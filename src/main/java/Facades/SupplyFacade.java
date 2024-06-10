package Facades;

import Domain.Externals.Suppliers.SupplierAdapter;
import Domain.Repo.SupplyRepository;
import Utilities.Response;

public class SupplyFacade {
    private SupplyRepository supplyRepository;

    public SupplyFacade() {
        supplyRepository = new SupplyRepository();
    }

    public Response<String> addSupplierAdapter(SupplierAdapter supplierAdapter, String name){
        return supplyRepository.addSupplierAdapter(supplierAdapter, name);
    }
}
