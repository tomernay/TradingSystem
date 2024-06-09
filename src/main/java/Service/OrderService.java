package Service;

import Facades.OrderFacade;
import Utilities.Response;
import Utilities.SystemLogger;

import java.util.Map;

public class OrderService {
    private OrderFacade orderFacade;
    private UserService userService ;



    public OrderService() {
        orderFacade = new OrderFacade();
    }
    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    public Response<Map<String, String>> getOrderHistorty(String storeID) {
        return orderFacade.getOrdersHistory(storeID);
    }

    public OrderFacade getOrderFacade() {
        return orderFacade;
    }

    public Response<String> CreatOrder(String username, String token) {
        SystemLogger.info("[START] User: " + username + " is trying to purchase the shopping cart");
        Response<Map<String, Map<String, Integer>>> resShoppSingCartContents = userService.getShoppingCartContents(username, token);
        if (userService.isValidToken(token, username)) {

        }
        SystemLogger.error("[ERROR] User: " + username + " tried to purchase the shopping cart but the token was invalid");
        return Response.error("invalid token", null);
    }


}
