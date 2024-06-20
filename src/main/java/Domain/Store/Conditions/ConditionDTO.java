package Domain.Store.Conditions;

public class ConditionDTO {
    private Integer conditionID;
    private Integer productID;
    private String category;
    private Double minAmount;
    private Double maxAmount;
    private Double amount;
    private Boolean price;
    private String discountType;
    private ConditionDTO conditionDTO1;
    private ConditionDTO conditionDTO2;
    private ConditionDTO policyConditionDTO;
    private String productName;
    private String relationType; // AND, OR, XOR

    public ConditionDTO(Integer conditionID, Integer productID, String productName, String category, String discountType, Double amount, Double minAmount, Double maxAmount,Boolean price, ConditionDTO conditionDTO1, ConditionDTO conditionDTO2, ConditionDTO policyConditionDTO, String relationType) {
        this.conditionID = conditionID;
        this.productID = productID;
        this.productName = productName;
        this.category = category;
        this.discountType = discountType;
        this.amount = amount;
        this.minAmount = minAmount;
        this.maxAmount = maxAmount;
        this.price = price;
        this.conditionDTO1 = conditionDTO1;
        this.conditionDTO2 = conditionDTO2;
        this.policyConditionDTO = policyConditionDTO;
        this.relationType = relationType;
    }

    public String getRelationType() {
        return relationType;
    }

    public int getConditionID() {
        return conditionID;
    }

    public Integer getProductID() {
        return productID;
    }

    public String getCategory() {
        return category;
    }

    public Double getMinAmount() {
        return minAmount;
    }

    public Double getMaxAmount() {
        return maxAmount;
    }

    public Double getAmount() {
        return amount;
    }

    public Boolean getPrice() {
        return price;
    }

    public ConditionDTO getConditionDTO1() {
        return conditionDTO1;
    }

    public ConditionDTO getConditionDTO2() {
        return conditionDTO2;
    }

    public ConditionDTO getPolicyConditionDTO() {
        return policyConditionDTO;
    }

    public String getDiscountType() {
        return discountType;
    }

    public String getProductName() {
        return productName;
    }
}
