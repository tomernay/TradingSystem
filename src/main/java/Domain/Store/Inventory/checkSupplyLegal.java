package src.main.java.Domain.Store.Inventory;

import src.main.java.Utilities.Response;

public interface checkSupplyLegal {
    public boolean isSupplyLegal(Product product,String storeID);

    public Response<String> supply(Product product,int quantity,String storeID);
}
