package Domain.Externals.Suppliers;

import Domain.Externals.Payment.CreditCard;
import Domain.Externals.Payment.PaymentAdapter;
import Domain.Store.Inventory.Product;
import Domain.Store.Inventory.checkSupplyLegal;
import Domain.Store.Store;
import Utilities.Response;
import Utilities.SystemLogger;

public class DefaultSupplier extends SupplierAdapter {
    public DefaultSupplier(String name) {
        super(name);
    }

    @Override
    public Response<String> supply(Product product, String s, CreditCard buyer,CreditCard supplier,double fee, PaymentAdapter paymentAdapter, int amount, checkSupplyLegal checkSupplyLegal) {
        if(amount<=0){
            SystemLogger.error("[ERROR] Can't supply amount of 0 or less");
            return Response.error("Amount must be greater than 0", null);
        }
        else if(product==null){
            SystemLogger.error("[ERROR] Product cannot be null");
            return Response.error("Product cannot be null", null);
        }
        else if(!checkSupplyLegal.isSupplyLegal(product,s)){
            SystemLogger.error("[ERROR] Supply is not legal");
            return Response.error("Supply is not legal", null);
        }
        Response<String> res=paymentAdapter.pay(buyer,supplier,fee);
        if(!res.isSuccess()){
            return res;
        }
       res=checkSupplyLegal.supply(product,amount,s);
        return res;
    }





}
