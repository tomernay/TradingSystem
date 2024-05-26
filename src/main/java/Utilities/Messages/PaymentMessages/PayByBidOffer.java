package Utilities.Messages.PaymentMessages;

import DataBase.FireBaseConstants;
import Domain.Externals.Payment.CreditCard;
import Domain.Externals.Payment.DefaultPay;
import Domain.Externals.Payment.PaymentAdapter;
import Domain.Store.PurchasePolicy.PaymentTypes.PayByBid;
import Domain.Store.Store;
import Utilities.Messages.Message;
import Utilities.Messages.NormalMessage;
import Domain.Users.Subscriber.Subscriber;
import Utilities.Response;

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
    public Response<Message> response(boolean answer) {
         if (answer){
             payByBid.acceptCreator(creator);
             for(PayByBid.PayByBidStatus payByBidStatus:payByBid.getIsAgreed().values()){
                 if(payByBidStatus.compareTo(PayByBid.PayByBidStatus.Accept)!=0){
                     return null;
                 }
             }
             PaymentAdapter paymentAdapter=new DefaultPay(user.getUsername());
             paymentAdapter.pay(new CreditCard(user.getCredit()),new CreditCard(FireBaseConstants.visa),fee);
             user.addMessage(new NormalMessage("payment was successful"));
         }
         else{
             store.removePayByBid(user.getUsername());
             Message message=new NormalMessage("canceled pay by bid");
             user.addMessage(message);
         }
         return null;
    }
}
