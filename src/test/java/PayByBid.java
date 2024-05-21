import Domain.Store.Store;
import Domain.Users.Subscriber.Subscriber;
import org.junit.Before;
import org.junit.Test;
import Service.StoreService;
import Service.UserService;

public class PayByBid {
    Subscriber subscriber,buyer;
    Store store;
    UserService userService;
    StoreService storeService;
    @Before
    public void init(){
        userService=new UserService();
        storeService=new StoreService();
        userService.register("yair","by");
        subscriber=userService.getUser("yair");

        userService.register("yair2","by2");
        buyer=userService.getUser("yair2");
        storeService.addStore("yairStore","yair",subscriber.getToken());
        store=storeService.getStore("0");
    }

    @Test
    public void payByBidEmptyProducts(){
     // Response<String> result=  service.getPaymentService().sendPayByBid(subscriber.getToken(),store,buyer,50,new HashMap<Integer,Integer>());
     // Assert.assertFalse(result.isSuccess());
    }
    @Test
    public void payByBidProducts(){
      //  Response<String> result=  service.getPaymentService().sendPayByBid(subscriber.getToken(),store,buyer,50,new HashMap<Integer,Integer>());
      //  Assert.assertFalse(result.isSuccess());
    }
}
