package Service;

import Facades.OrderFacade;
import Utilities.Response;

import java.util.Map;

public class OrderService {
    private OrderFacade orderFacade;



    public OrderService() {
        orderFacade = new OrderFacade();
    }

    public Response<Map<String, String>> getOrderHistorty(String storeID) {
        return orderFacade.getOrdersHistory(storeID);
    }
}
