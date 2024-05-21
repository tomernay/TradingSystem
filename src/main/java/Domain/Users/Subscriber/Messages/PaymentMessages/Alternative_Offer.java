package Domain.Users.Subscriber.Messages.PaymentMessages;

import DataBase.FireBaseConstants;
import Domain.Externals.Payment.DefaultPay;
import Domain.Store.PurchasePolicy.PaymentTypes.ImmediatePay;
import Domain.Store.Store;
import Domain.Users.Subscriber.Messages.Message;
import Domain.Users.Subscriber.Messages.NormalMessage;
import Domain.Users.Subscriber.Subscriber;

import java.util.HashMap;

public class Alternative_Offer extends Message {
    HashMap<Integer,Integer> products;
    double fee;
    Store store;
    Subscriber subscriber;
    public Alternative_Offer(double fee, HashMap<Integer,Integer> products, Store store, Subscriber subscriber){
        super("you got an alternative offer");
        this.products=products;
        this.fee=fee;
        this.store=store;
        this.subscriber=subscriber;
    }
    @Override
    public void response(boolean answer) {
        if(answer){
            ImmediatePay immediatePay=new ImmediatePay(fee, FireBaseConstants.visa,subscriber.getCredit());
            immediatePay.pay(new DefaultPay(subscriber.getUsername()));
            subscriber.addMessage(new NormalMessage("payment was successful"));
        }

    }

    public double getFee() {
        return fee;
    }
}