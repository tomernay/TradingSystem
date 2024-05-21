package src.main.java.Domain.Externals.Payment;

import src.main.java.Utilities.Response;

public abstract class PaymentAdapter {
   String paymentName;
   public PaymentAdapter(String name){
      paymentName=name;
   }

   public String getPaymentName() {
      return paymentName;
   }

   /**
    * creates a new payment
    * @param buyer
    * @param receiver
    * @param fee
    * @return
    */
   public abstract Response<String> pay(CreditCard buyer, CreditCard receiver, double fee);
}
