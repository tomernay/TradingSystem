package Domain.Store.Inventory;

import Utilities.Response;

public interface checkSupplyLegal {
    public boolean isSupplyLegal(Product product,String storeID);

    public Response<String> supply(Product product,int quantity,String storeID);
}
