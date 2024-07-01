package Domain.Externals.InitFile;

import Domain.Externals.Payment.PaymentGateway;
import Domain.Externals.Payment.ProxyPaymentGateway;
import Domain.Externals.Suppliers.ProxySupplySystem;
import Domain.Externals.Suppliers.SupplySystem;
import Domain.Users.Subscriber.Subscriber;
import Service.OrderService;
import Service.ServiceInitializer;
import Service.StoreService;
import Service.UserService;
import Utilities.Response;
import com.fasterxml.jackson.databind.JsonNode;

import java.util.ArrayList;
import java.util.List;

public class Configuration {
    private PaymentGateway paymentGateway;
    private SupplySystem supplySystem;
    private String adminUser;
    private String adminPassword;
    private List<FunctionCall> initSequence;

    public Configuration(JsonNode jsonNode) {
        if (jsonNode.get("payments service").asText().equals("ProxyPaymentGateway"))
            paymentGateway = new ProxyPaymentGateway();
        if (jsonNode.get("supplier service").asText().equals("ProxySupplyGateway"))
            supplySystem = new ProxySupplySystem();

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


    public static void init(JsonNode configNode) {
        Configuration config = new Configuration(configNode);
        ServiceInitializer.reset();
        ServiceInitializer serviceInitializer = ServiceInitializer.getInstance();
       UserService userService = serviceInitializer.getUserService();
      StoreService storeService = serviceInitializer.getStoreService();
     OrderService orderService = serviceInitializer.getOrderService();
        Subscriber subscriber=null;
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
                        storeService.addProductToStore(
                                params.get("storeIndex").asInt(),
                                params.get("productName").asText(),
                                params.get("description").asText(),
                                params.get("price").asDouble(),
                                params.get("quantity").asInt(),
                                new ArrayList<>(params.get("categories").findValuesAsText("")),
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
                        System.out.println("4");

                }
            }
            catch (Exception e) {}
        }
    }

}
