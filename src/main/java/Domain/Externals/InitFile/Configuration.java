package Domain.Externals.InitFile;

import Domain.Externals.Payment.DefaultPaymentGateway;
import Domain.Externals.Payment.PaymentGateway;
import Domain.Externals.Payment.ProxyPaymentGateway;
import Domain.Externals.Suppliers.DefaultSupplySystem;
import Domain.Externals.Suppliers.ProxySupplySystem;
import Domain.Externals.Suppliers.SupplySystem;
import Domain.Users.Subscriber.Subscriber;
import Facades.AdminFacade;
import Facades.OrderFacade;
import Facades.StoreFacade;
import Facades.UserFacade;
import Service.OrderService;
import Service.ServiceInitializer;
import Service.StoreService;
import Service.UserService;
import Utilities.Response;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@org.springframework.context.annotation.Configuration
@EnableTransactionManagement
@EnableJpaRepositories(basePackages = {"Domain"})
public class Configuration {
    private PaymentGateway paymentGateway;
    private SupplySystem supplySystem;
    private String adminUser;
    private String adminPassword;
    private List<FunctionCall> initSequence;
    @Autowired
    private @Lazy UserService userService;
    @Autowired
    private @Lazy StoreService storeService;


    public Configuration() {
    }

    public Configuration(JsonNode jsonNode) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();

        JsonNode paymentServiceNode = jsonNode.get("payments service");
        if (paymentServiceNode.has("ProxyPaymentGateway")) {
            paymentGateway = mapper.treeToValue(paymentServiceNode.get("ProxyPaymentGateway"), ProxyPaymentGateway.class);
        } else if (paymentServiceNode.has("DefaultPaymentGateway")) {
            paymentGateway = mapper.treeToValue(paymentServiceNode.get("DefaultPaymentGateway"), DefaultPaymentGateway.class);
        }
        else{
            throw new IllegalArgumentException("Invalid payment service configuration");
        }
        JsonNode supplierServiceNode = jsonNode.get("supplier service");
        if (supplierServiceNode.has("ProxySupplyGateway")) {
            supplySystem = mapper.treeToValue(supplierServiceNode.get("ProxySupplyGateway"), ProxySupplySystem.class);
        } else if (supplierServiceNode.has("DefaultSupplySystem")) {
            supplySystem = mapper.treeToValue(supplierServiceNode.get("DefaultSupplySystem"), DefaultSupplySystem.class);
        }
        else{
            throw new IllegalArgumentException("Invalid supplier service configuration");
        }
        JsonNode adminDetails = jsonNode.get("Admin details");
        adminUser = adminDetails.get("user").asText();
        adminPassword = adminDetails.get("password").asText();

        initSequence = new ArrayList<>();
        for (JsonNode functionNode : jsonNode.get("init sequence")) {
            initSequence.add(new FunctionCall(functionNode.get("function").asText(), functionNode.get("parameters")));
        }
    }

    public PaymentGateway getPaymentGateway() {
        return paymentGateway;
    }

    public SupplySystem getSupplySystem() {
        return supplySystem;
    }

    public String getAdminUser() {
        return adminUser;
    }

    public String getAdminPassword() {
        return adminPassword;
    }

    public List<FunctionCall> getInitSequence() {
        return initSequence;
    }

    public static class FunctionCall {
        private String function;
        private JsonNode parameters;

        public FunctionCall(String function, JsonNode parameters) {
            this.function = function;
            this.parameters = parameters;
        }

        public String getFunction() {
            return function;
        }

        public JsonNode getParameters() {
            return parameters;
        }
    }

    @Bean(name = "UserFacade")
    public UserFacade userFacade() {
        return new UserFacade();
    }

    @Bean(name = "StoreFacade")
    public StoreFacade storeFacade() {
        return new StoreFacade();
    }

    @Bean(name = "AdminFacade")
    public AdminFacade adminFacade() {
        return new AdminFacade();
    }

    @Bean(name = "OrderFacade")
    public OrderFacade orderFacade() {
        return new OrderFacade();
    }


    public void init(JsonNode configNode) throws JsonProcessingException {
        Configuration config = new Configuration(configNode);
        ServiceInitializer.reset();

        Subscriber subscriber = null;
        for (FunctionCall functionCall : config.getInitSequence()) {
            try {


                JsonNode params = functionCall.getParameters();
                switch (functionCall.getFunction()) {
                    case "register":
                        userService.register(params.get("username").asText(), params.get("password").asText());

                        break;
                    case "loginAsSubscriber":
                        userService.loginAsSubscriber(params.get("username").asText(), params.get("password").asText());
                        break;
                    case "addStore":
                        userService.loginAsSubscriber(params.get("ownerUsername").asText(), "Password123!"); // Assuming all passwords are the same
                        subscriber = userService.getUserFacade().getUserRepository().getSubscriber(params.get("ownerUsername").asText());
                        storeService.addStore(params.get("storeName").asText(), params.get("ownerUsername").asText(), subscriber.getToken());
                        break;
                    case "addProductToStore":
                        ArrayList<String> categories = new ArrayList<>();
                        JsonNode categoriesNode = params.get("categories");
                        if (categoriesNode != null && categoriesNode.isArray()) {
                            Iterator<JsonNode> elements = categoriesNode.elements();
                            while (elements.hasNext()) {
                                categories.add(elements.next().asText());
                            }
                        }
                        storeService.addProductToStore(
                                params.get("storeIndex").asInt(),
                                params.get("productName").asText(),
                                params.get("description").asText(),
                                params.get("price").asDouble(),
                                params.get("quantity").asInt(),
                               categories
                                ,
                                params.get("ownerUsername").asText(),
                                subscriber.getToken()
                        );
                        break;
                    case "SendManagerNominationRequest":
                        Response<Integer> managerRes = userService.SendManagerNominationRequest(
                                params.get("storeIndex").asInt(),
                                params.get("ownerUsername").asText(),
                                params.get("managerUsername").asText(),
                                new ArrayList<>(params.get("permissions").findValuesAsText("")),
                                subscriber.getToken()
                        );
                        userService.managerNominationResponse(managerRes.getData(), params.get("managerUsername").asText(), true, "Password123!"); // Assuming all passwords are the same
                        break;
                    case "managerNominationResponse":
                        userService.managerNominationResponse(params.get("requestId").asInt(), params.get("managerUsername").asText(), params.get("accepted").asBoolean(), "Password123!"); // Assuming all passwords are the same
                        break;
                    case "SendOwnerNominationRequest":
                        Response<Integer> ownerRes = userService.SendOwnerNominationRequest(
                                params.get("storeIndex").asInt(),
                                params.get("ownerUsername").asText(),
                                params.get("newOwnerUsername").asText(),
                                subscriber.getToken()
                        );
                        userService.ownerNominationResponse(ownerRes.getData(), params.get("newOwnerUsername").asText(), true, "Password123!"); // Assuming all passwords are the same
                        break;
                    case "ownerNominationResponse":
                        userService.ownerNominationResponse(params.get("requestId").asInt(), params.get("newOwnerUsername").asText(), params.get("accepted").asBoolean(), "Password123!"); // Assuming all passwords are the same
                        break;

                    case "addProductToShoppingCart":
                        userService.addProductToShoppingCart(
                                params.get("storeIndex").asInt(),
                                params.get("productId").asInt(),
                                params.get("quantity").asInt(),
                                params.get("username").asText(),
                                subscriber.getToken()
                        );
                        break;
                    case "logoutAsSubscriber":
                        userService.logoutAsSubscriber(params.get("username").asText());
                        break;
                    default:
                       throw new IllegalArgumentException();

                }
            }
            catch (Exception e) {
                e.printStackTrace();
           // throw new IllegalArgumentException("Illegal external file");
            }
        }
    }

    public static void main(String[] args) throws JsonProcessingException {
        ProxyPaymentGateway proxyPaymentGateway=new ProxyPaymentGateway();
        System.out.println(proxyPaymentGateway);
    }

}
