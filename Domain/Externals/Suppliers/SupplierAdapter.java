package Domain.Externals.Suppliers;

import Domain.Store.Inventory.Product;
import Domain.Store.Store;

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

    public abstract boolean supply(Product product, Store s);
}
