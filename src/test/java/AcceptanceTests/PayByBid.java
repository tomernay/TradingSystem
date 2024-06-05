package AcceptanceTests;

import Domain.Store.Store;
import Domain.Users.Subscriber.Subscriber;
import Service.ServiceInitializer;
import org.junit.Before;
import org.junit.Test;
import Service.StoreService;
import Service.UserService;

public class PayByBid {
    ServiceInitializer serviceInitializer;
    Subscriber subscriber,buyer;
    Store store;
    UserService userService;
    StoreService storeService;
    @Before
    public void init(){
        serviceInitializer=new ServiceInitializer();
        userService=serviceInitializer.getUserService();
        storeService=serviceInitializer.getStoreService();
        userService.register("yair","Password123!");
        subscriber=userService.getUserFacade().getUserRepository().getUser("yair");

        userService.register("yair2","Password123!");
        buyer=userService.getUserFacade().getUserRepository().getUser("yair2");
        storeService.addStore("yairStore","yair",subscriber.getToken());
        store=storeService.getStoreFacade().getStoreRepository().getStore("0");
    }

//    @Test
//    public void payByBidEmptyProducts(){
//     // Response<String> result=  service.getPaymentService().sendPayByBid(subscriber.getToken(),store,buyer,50,new HashMap<Integer,Integer>());
//     // Assert.assertFalse(result.isSuccess());
//    }
//    @Test
//    public void payByBidProducts(){
//      //  Response<String> result=  service.getPaymentService().sendPayByBid(subscriber.getToken(),store,buyer,50,new HashMap<Integer,Integer>());
//      //  Assert.assertFalse(result.isSuccess());
//    }
}
