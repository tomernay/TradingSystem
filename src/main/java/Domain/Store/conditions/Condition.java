package Domain.Store.conditions;

import Domain.Store.Inventory.ProductDTO;

import java.util.List;

public interface Condition {
    boolean isValid(List<ProductDTO> products);

    int getConditionID();
}
