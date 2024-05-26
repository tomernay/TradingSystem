package Facades;

import Domain.Externals.Suppliers.SupplierAdapter;
import Domain.Repo.SupplyRepository;

public class SupplyFacade {
    private SupplyRepository supplyRepository;

    public SupplyFacade() {
        supplyRepository = new SupplyRepository();
    }

    public void addSupplierAdapter(SupplierAdapter supplierAdapter, String name){
        supplyRepository.addSupplierAdapter(supplierAdapter, name);
    }
}
