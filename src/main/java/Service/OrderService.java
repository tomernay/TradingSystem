package Service;

import Facades.OrderFacade;
import Utilities.Response;
import Utilities.SystemLogger;

import java.util.Map;

public class OrderService {
    private OrderFacade orderFacade;



    public OrderService() {
        orderFacade = new OrderFacade();
    }

    public Response<Map<String, String>> getOrderHistorty(String storeID) {
        return orderFacade.getOrdersHistory(storeID);
    }

    public OrderFacade getOrderFacade() {
        return orderFacade;
    }

//    public Response<String> CreatOrder(String username, String token) {
//        SystemLogger.info("[START] User: " + username + " is trying to purchase the shopping cart");
//        Response<Map<String, Map<String, Integer>>> resShoppSingCartContents = userFacade.getShoppingCartContents(username);
//        if (isValidToken(token, username)) {
//            return orderService.CreatOrder(username, resShoppSingCartContents.getData());
//        }
//        SystemLogger.error("[ERROR] User: " + username + " tried to purchase the shopping cart but the token was invalid");
//        return Response.error("invalid token", null);
//


}
