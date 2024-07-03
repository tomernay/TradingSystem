package ConfigurationFileTests;

import Domain.Externals.InitFile.Configuration;
import Domain.Externals.Payment.DefaultPaymentGateway;
import Domain.Externals.Payment.ProxyPaymentGateway;
import Domain.Users.Subscriber.Subscriber;
import Presentation.application.Application;
import Service.OrderService;
import Service.ServiceInitializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.boot.SpringApplication;

import java.io.File;
import java.io.IOException;

public class ConfigurationFileTests {

   static ServiceInitializer serviceInitializer;
    @BeforeClass
    public static void setUp() throws Exception {

            Configuration configuration = null;

                // Read the configuration file
                ObjectMapper mapper = new ObjectMapper();
                JsonNode configNode = mapper.readTree(new File("src/main/java/Domain/Externals/InitFile/proxyConfig.json"));

                // Initialize the Configuration object
                Configuration.init(configNode);
                serviceInitializer=ServiceInitializer.getInstance(configuration);

            // Pass the configuration to SpringApplication.run


    }

    @Test
    public void testConfiguration(){
        OrderService orderService=ServiceInitializer.getInstance().getOrderService();
        Assert.assertTrue(orderService.getPaymentGateway() instanceof DefaultPaymentGateway);
        boolean isMiaExist=ServiceInitializer.getInstance().getUserService().userExists("mia");
        Assert.assertTrue(isMiaExist);
    }

    @Test
    public void testFileNotExist()  {
        try {


            Configuration configuration = null;

            // Read the configuration file
            ObjectMapper mapper = new ObjectMapper();
            JsonNode configNode = mapper.readTree(new File("src/main/java/Domain/Externals/InitFile/p.json"));

            // Initialize the Configuration object
            Configuration.init(configNode);
            serviceInitializer = ServiceInitializer.getInstance(configuration);
            OrderService orderService = ServiceInitializer.getInstance().getOrderService();
            Assert.assertTrue(false);
            boolean isMiaExist = ServiceInitializer.getInstance().getUserService().userExists("mia");
            Assert.assertTrue(false);
        }catch (Exception e){
            Assert.assertTrue(true);
        }
    }

    @Test
    public void testFileError()  {
        try {


            Configuration configuration = null;

            // Read the configuration file
            ObjectMapper mapper = new ObjectMapper();
            JsonNode configNode = mapper.readTree(new File("src/main/java/Domain/Externals/InitFile/FailedTestFile.json"));

            // Initialize the Configuration object
            Configuration.init(configNode);
            serviceInitializer = ServiceInitializer.getInstance(configuration);
            OrderService orderService = ServiceInitializer.getInstance().getOrderService();
            Assert.assertTrue(false);
            boolean isMiaExist = ServiceInitializer.getInstance().getUserService().userExists("mia");
            Assert.assertTrue(false);
        }catch (Exception e){
            Assert.assertTrue(true);
        }
    }
}
