package Domain.Store.Conditions;

public class ConditionDTO {
    private int conditionID;
    private String productID;
    private String category;
    private String minAmount;
    private String maxAmount;
    private String amount;
    private String price;
    private String discountType;
    private ConditionDTO conditionDTO1;
    private ConditionDTO conditionDTO2;
    private ConditionDTO policyConditionDTO;
    private String productName;
    private String relationType; // AND, OR, XOR

    public ConditionDTO(int conditionID, String productID, String productName, String category, String discountType, String amount, String minAmount, String maxAmount,String price, ConditionDTO conditionDTO1, ConditionDTO conditionDTO2, ConditionDTO policyConditionDTO, String relationType) {
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

    public String getProductID() {
        return productID;
    }

    public String getCategory() {
        return category;
    }

    public String getMinAmount() {
        return minAmount;
    }

    public String getMaxAmount() {
        return maxAmount;
    }

    public String getAmount() {
        return amount;
    }

    public String getPrice() {
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
