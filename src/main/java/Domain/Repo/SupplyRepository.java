package Domain.Repo;

import Domain.Externals.Suppliers.SupplierAdapter;

import java.util.HashMap;

public class SupplyRepository {
    private HashMap<String, SupplierAdapter> suppliers;

    public SupplyRepository() {
        this.suppliers = new HashMap<>();
    }

    public void addSupplierAdapter(SupplierAdapter supplierAdapter, String name){
        suppliers.put(name, supplierAdapter);
    }
}
