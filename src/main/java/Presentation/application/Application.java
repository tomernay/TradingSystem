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
@EntityScan(basePackages = "Domain.Store, Domain.Users.Subscriber, Domain.Users.User")
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

            // Drop the database if it exists
            stmt.executeUpdate("DROP DATABASE IF EXISTS " + DATABASE_NAME);

            // Create a new database
            stmt.executeUpdate("CREATE DATABASE " + DATABASE_NAME);

            System.out.println("Database " + DATABASE_NAME + " has been reset successfully.");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}