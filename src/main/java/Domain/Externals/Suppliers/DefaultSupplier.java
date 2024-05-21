package src.main.java.Domain.Externals.Suppliers;

import src.main.java.Domain.Externals.Payment.CreditCard;
import src.main.java.Domain.Externals.Payment.PaymentAdapter;
import src.main.java.Domain.Store.Inventory.Product;
import src.main.java.Domain.Store.Inventory.checkSupplyLegal;
import src.main.java.Domain.Store.Store;
import src.main.java.Utilities.Response;

public class DefaultSupplier extends SupplierAdapter {
    public DefaultSupplier(String name) {
        super(name);
    }

    @Override
    public Response<String> supply(Product product, String s, CreditCard buyer,CreditCard supplier,double fee, PaymentAdapter paymentAdapter, int amount, checkSupplyLegal checkSupplyLegal) {
        if(amount<=0){
            return new Response<String>(false,"Amount must be greater than 0");
        }
        else if(product==null){
            return new Response<String>(false,"Product cannot be null");
        }
        else if(!checkSupplyLegal.isSupplyLegal(product,s)){
            return new Response<String>(false,"product cannot be added");
        }
        Response<String> res=paymentAdapter.pay(buyer,supplier,fee);
        if(!res.isSuccess()){
            return res;
        }
       res=checkSupplyLegal.supply(product,amount,s);
        return res;
    }





}
