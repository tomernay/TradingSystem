package Domain.Store.Conditions;

import Domain.Store.Inventory.ProductDTO;

import java.util.List;
import java.util.Map;

public interface Condition {
    boolean isValid(Map<ProductDTO, Integer> products);
    public Integer getConditionID();
    public Integer getProductID();
    public String getCategory();
    public Double getMinAmount();
    public Double getMaxAmount();
    public Double getAmount();
    public Condition getCondition1();
    public Condition getCondition2();
    public Boolean getPriceIndicator();
    public String toString();
}

