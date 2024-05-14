package Domain.Externals.Payment;

import Domain.Store.Store;

public abstract class PaymentAdapter {
   String paymentName;
   public PaymentAdapter(String name){
      paymentName=name;
   }

   public String getPaymentName() {
      return paymentName;
   }


   /**
    * create payment
    * @param credit
    * @param s
    * @param fee
    */
   public abstract boolean pay(String credit, Store s, double fee);
}
