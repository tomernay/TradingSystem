package Domain.Repo;

import Domain.Externals.Suppliers.DefaultSupplier;
import Domain.Externals.Suppliers.SupplierAdapter;
import Utilities.Response;

import java.util.HashMap;

public class SupplyRepository {
    private final HashMap<String, SupplierAdapter> suppliers;
    private final HashMap<String, SupplierAdapter> suppliersAdapterHashMap;

    public SupplyRepository() {
        this.suppliers = new HashMap<>();
        suppliersAdapterHashMap=new HashMap<>();
        suppliersAdapterHashMap.put("DefaultSupply",new DefaultSupplier(""));
    }

    public Response<String> addSupplierAdapter(String supplierAdapter, String name){
        suppliers.put(name, suppliersAdapterHashMap.get(supplierAdapter));
        return new Response<>(true,"supplier added successfully");
    }
}
