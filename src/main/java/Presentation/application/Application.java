package Presentation.application;

import Domain.Externals.InitFile.Configuration;

import Domain.Users.Subscriber.Subscriber;
import Service.OrderService;
import Service.ServiceInitializer;
import Service.StoreService;
import Service.UserService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vaadin.flow.component.page.AppShellConfigurator;
import com.vaadin.flow.component.page.Push;
import com.vaadin.flow.theme.Theme;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;

import java.io.File;
import java.io.IOException;


/**
 * The entry point of the Spring Boot application.
 *
 * Use the @PWA annotation make the application installable on phones, tablets
 * and some desktop browsers.
 *
 */
/**
 * The entry point of the Spring Boot application.
 *
 * Use the @PWA annotation make the application installable on phones, tablets
 * and some desktop browsers.
 *
 */
@Push
@SpringBootApplication
@ComponentScan(basePackages = {"Service","Domain","Presentation"})
@Theme(value = "my-app")
@EntityScan(basePackages = "Domain.Users")
public class Application implements AppShellConfigurator {

    @Autowired
    ServiceInitializer serviceInitializer;

    public static void main(String[] args) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            ConfigurableApplicationContext context = SpringApplication.run(Application.class, args);
            Configuration configuration = context.getBean(Configuration.class);
            JsonNode configNode = mapper.readTree(new File("src/main/java/Domain/Externals/InitFile/Configuration.json"));
            configuration.init(configNode);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
