package Domain.Externals.Suppliers;

import Domain.Store.Inventory.Product;
import Domain.Store.Store;

public class DefaultSupplier extends SupplierAdapter {
    public DefaultSupplier(String name) {
        super(name);
    }

    @Override
    public boolean supply(Product product, Store s) {
        try {
            s.getInventory().addProduct(product);
            return true;
        }
        catch (Exception e){
            return false;
        }
    }
}
