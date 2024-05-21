package Domain.Externals.Suppliers;

import Domain.Externals.Payment.CreditCard;
import Domain.Externals.Payment.PaymentAdapter;
import Domain.Store.Inventory.Product;
import Domain.Store.Inventory.checkSupplyLegal;
import Domain.Store.Store;
import Utilities.Response;

public abstract class SupplierAdapter {
    String name;
    public SupplierAdapter(String name){
        this.name=name;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public abstract Response<String> supply(Product product, String s, CreditCard buyer, CreditCard supplier, double fee, PaymentAdapter paymentAdapter, int amount, checkSupplyLegal checkSupplyLegal);
}
