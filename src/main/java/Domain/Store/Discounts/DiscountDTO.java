package Domain.Store.Discounts;

import Domain.Store.Conditions.ConditionDTO;

public class DiscountDTO {
    private int discountID;
    private String storeID;
    private String discountType;
    private String percent;
    private String productID;
    private String productName;
    private String category;
    private DiscountDTO discountDTO1;
    private DiscountDTO discountDTO2;
    private ConditionDTO condition;


    public DiscountDTO(int discountID, String productID, String productName, String storeID, String discountType, String discountCategory, String discountPercentage, DiscountDTO discountDTO1, DiscountDTO discountDTO2, ConditionDTO condition) {
        this.discountID = discountID;
        this.productID = productID;
        this.productName = productName;
        this.storeID = storeID;
        this.discountType = discountType;
        this.percent = discountPercentage;
        this.category = discountCategory;
        this.discountDTO1 = discountDTO1;
        this.discountDTO2 = discountDTO2;
        this.condition = condition;
    }

    public Integer getDiscountID() {
        return discountID;
    }

    public String getStoreID() {
        return storeID;
    }

    public String getDiscountType() {
        return discountType;
    }

    public String getPercent() {
        return percent;
    }

    public String getProductID() {
        return productID;
    }

    public String getCategory() {
        return category;
    }

    public DiscountDTO getDiscountDTO1() {
        return discountDTO1;
    }

    public DiscountDTO getDiscountDTO2() {
        return discountDTO2;
    }

    public String getProductName() {
        return productName;
    }

    public ConditionDTO getCondition() {
        return condition;
    }

    public String getType() { //Simple / Complex / Condition
        if (discountDTO1 != null && discountDTO2 != null) {
            return "COMPLEX";
        }
        if (condition != null) {
            return "CONDITION";
        }
        return "SIMPLE";
    }




}
