package Service;

import Domain.Market.Market;

public class Service {
    private UserService userService;
    private PaymentService paymentService;
    private StoreService storeService;
    private Market market;
    public Service(){
        market=new Market();
        userService=new UserService(market);
        paymentService=new PaymentService(market);
          storeService=new StoreService(market);
    }

    public PaymentService getPaymentService() {
        return paymentService;
    }

    public UserService getUserService() {
        return userService;
    }

    public StoreService getStoreService() {
        return storeService;
    }
}
