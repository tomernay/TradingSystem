package Presentation.application;

import Domain.Externals.InitFile.Configuration;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vaadin.flow.component.page.AppShellConfigurator;
import com.vaadin.flow.component.page.Push;
import com.vaadin.flow.theme.Theme;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ComponentScan;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

@Push
@SpringBootApplication
@ComponentScan(basePackages = {"Service", "Domain", "Presentation"})
@Theme(value = "my-app")
@EntityScan(basePackages = "Domain.Users.Subscriber,Domain.Users.User")
public class Application implements AppShellConfigurator {
    private static final String DATABASE_NAME = "TradingSystem";
    private static final String USERNAME = "admin";
    private static final String PASSWORD = "Password123!";
    private static final String CONNECTION_STRING = "jdbc:mysql://trading-system-db.c3w048kga84z.eu-north-1.rds.amazonaws.com:3306/";

    public static void main(String[] args) {
        resetDatabase();
        try {
            ObjectMapper mapper = new ObjectMapper();
            ApplicationContext context = SpringApplication.run(Application.class, args);
            Configuration configuration = context.getBean(Configuration.class);
            JsonNode configNode = mapper.readTree(new File("src/main/java/Domain/Externals/InitFile/Configuration.json"));
            configuration.init(configNode);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private static void resetDatabase() {
        try (Connection conn = DriverManager.getConnection(CONNECTION_STRING, USERNAME, PASSWORD);
             Statement stmt = conn.createStatement()) {

            // Select the database
            stmt.executeUpdate("USE " + DATABASE_NAME);

            // Delete all subscribers
            stmt.executeUpdate("DELETE FROM subscribers");

            // Reset the auto-increment value of the ID column to 1
            stmt.executeUpdate("ALTER TABLE subscribers AUTO_INCREMENT = 1");

            System.out.println("Subscribers table has been reset successfully, including the ID column.");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}