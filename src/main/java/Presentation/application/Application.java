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

@Push
@SpringBootApplication
@ComponentScan(basePackages = {"Service", "Domain", "Presentation"})
@Theme(value = "my-app")
@EntityScan(basePackages = "Domain.Users.Subscriber,Domain.Users.User")
public class Application implements AppShellConfigurator {
    public static void main(String[] args) {
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
}