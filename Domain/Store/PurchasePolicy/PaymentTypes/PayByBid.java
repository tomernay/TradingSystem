package Domain.Store.PurchasePolicy.PaymentTypes;

import Domain.Externals.Payment.PaymentAdapter;
import Domain.Store.Store;
import Domain.Users.StateOfSubscriber.StoreCreator;
import Domain.Users.StateOfSubscriber.StoreOwner;
import Domain.Users.StateOfSubscriber.SubscriberState;

import java.util.HashMap;
import java.util.Map;

public class PayByBid extends PurchaseType {
    HashMap<Integer,Integer> products;
    double fee;
    Store store;
    public enum PayByBidStatus{
        Waiting, Accept,Deny,Alternative_Offer
    }
    HashMap<String, PayByBidStatus> isAgreed;
    public PayByBid(HashMap<Integer,Integer> products, double fee, Store store){
     this.products=products;
     this.fee=fee;
     this.store=store;
     isAgreed=new HashMap<>();
        for (Map.Entry<String, SubscriberState> entry : store.getSubscribers().entrySet()) {
            String key = entry.getKey();
            SubscriberState subscriberState=entry.getValue();
            if(subscriberState instanceof StoreOwner ||subscriberState instanceof StoreCreator){
                isAgreed.put(key,PayByBidStatus.Waiting);
            }
        }
    }

    public void sendAlternative(){

    }
    @Override
    public boolean pay( PaymentAdapter paymentAdapter) {
        return false;
    }

    public void acceptCreator(String username){
        isAgreed.put(username,PayByBidStatus.Accept);
    }

    public HashMap<String, PayByBidStatus> getIsAgreed() {
        return isAgreed;
    }
}
