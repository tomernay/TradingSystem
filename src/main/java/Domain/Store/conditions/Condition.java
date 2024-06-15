package Domain.Store.conditions;

import Domain.Store.Inventory.ProductDTO;

import java.util.List;
import java.util.Map;

public interface Condition {
    boolean isValid(Map<ProductDTO,Integer> products);

    int getConditionID();
}
