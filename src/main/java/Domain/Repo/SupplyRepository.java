package Domain.Repo;

import Domain.Externals.Suppliers.SupplierAdapter;
import Utilities.Response;

import java.util.HashMap;

public class SupplyRepository {
    private HashMap<String, SupplierAdapter> suppliers;

    public SupplyRepository() {
        this.suppliers = new HashMap<>();
    }

    public Response<String> addSupplierAdapter(SupplierAdapter supplierAdapter, String name){
        suppliers.put(name, supplierAdapter);
        return new Response<>(true,"supplier added successfully");
    }
}
