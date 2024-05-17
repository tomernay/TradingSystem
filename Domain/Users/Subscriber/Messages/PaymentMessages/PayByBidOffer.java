package Domain.Users.Subscriber.Messages.PaymentMessages;

import Domain.Externals.Payment.DefaultPay;
import Domain.Externals.Payment.PaymentAdapter;
import Domain.Store.PurchasePolicy.PaymentTypes.PayByBid;
import Domain.Store.Store;
import Domain.Users.Subscriber.Messages.Message;
import Domain.Users.Subscriber.Messages.NormalMessage;
import Domain.Users.Subscriber.Subscriber;

import java.util.HashMap;

public class PayByBidOffer extends Message {

    HashMap<Integer,Integer> products;
    PayByBid payByBid;
    double fee;
    Subscriber user;
    String creator;
    Store store;
    public PayByBidOffer(Store store, String creator, Subscriber s, double fee, HashMap<Integer,Integer> products){
        super("you got an offer");
        this.products=products;
        this.fee=fee;
        payByBid=new PayByBid(products,fee,store);
        this.creator=creator;
        user=s;
        this.store=store;
    }
    @Override
    public void response(boolean answer) {
         if (answer){
             payByBid.acceptCreator(creator);
             for(PayByBid.PayByBidStatus payByBidStatus:payByBid.getIsAgreed().values()){
                 if(payByBidStatus.compareTo(PayByBid.PayByBidStatus.Accept)!=0){
                     return;
                 }
             }
             PaymentAdapter paymentAdapter=new DefaultPay(user.getUsername());
             paymentAdapter.pay(user.getCredit(),store,fee);
         }
         else{
             store.removePayByBid(user.getUsername());
             Message message=new NormalMessage("canceled pay by bid");
             user.addMessage(message);
         }
    }
}