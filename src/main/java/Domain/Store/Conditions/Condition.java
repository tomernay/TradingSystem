package Domain.Store.Conditions;

import Domain.Store.Inventory.ProductDTO;

import java.util.Map;

public interface Condition {
    boolean isValid(Map<ProductDTO,Integer> products);
    public int getConditionID();
    public Integer getProductID();
    public String getCategory();
    public String getMinAmount();
    public String getMaxAmount();
    public String getAmount();
    public Condition getCondition1();
    public Condition getCondition2();
    public String getPrice();
}
