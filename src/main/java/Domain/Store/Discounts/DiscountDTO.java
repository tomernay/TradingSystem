package Domain.Store.Discounts;

public class DiscountDTO {
    private int discountID;
    private String storeID;
    private DiscountType discountType;
    private String percent;
    private String productID;
    private String category;
    private DiscountDTO discountDTO1;
    private DiscountDTO discountDTO2;


    public DiscountDTO(int discountID, String storeID, DiscountType discountType, String discountcategory, String discountPercentage, String discountOperator) {
        this.discountID = discountID;
        this.storeID = storeID;
        this.discountType = discountType;
        this.percent = discountPercentage;
        this.category = discountcategory;
        this.discountDTO1 = null;
        this.discountDTO2 = null;
    }

    public int getDiscountID() {
        return discountID;
    }

    public String getStoreID() {
        return storeID;
    }


}
