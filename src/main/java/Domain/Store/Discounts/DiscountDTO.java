package Domain.Store.Discounts;

import Domain.Store.Conditions.ConditionDTO;

public class DiscountDTO {
    private Integer discountID;
    private Integer storeID;
    private String discountType;
    private Double percent;
    private Integer productID;
    private String productName;
    private String category;
    private DiscountDTO discountDTO1;
    private DiscountDTO discountDTO2;
    private ConditionDTO condition;


    public DiscountDTO(Integer discountID, Integer productID, String productName, Integer storeID, String discountType, String discountCategory, Double discountPercentage, DiscountDTO discountDTO1, DiscountDTO discountDTO2, ConditionDTO condition) {
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

    public Integer getStoreID() {
        return storeID;
    }

    public String getDiscountType() {
        return discountType;
    }

    public Double getPercent() {
        return percent;
    }

    public Integer getProductID() {
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
