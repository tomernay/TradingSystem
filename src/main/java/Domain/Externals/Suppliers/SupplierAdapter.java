package src.main.java.Domain.Externals.Suppliers;

import src.main.java.Domain.Externals.Payment.CreditCard;
import src.main.java.Domain.Externals.Payment.PaymentAdapter;
import src.main.java.Domain.Store.Inventory.Product;
import src.main.java.Domain.Store.Inventory.checkSupplyLegal;
import src.main.java.Domain.Store.Store;
import src.main.java.Utilities.Response;

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
