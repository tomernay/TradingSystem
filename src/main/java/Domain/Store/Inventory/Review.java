package Domain.Store.Inventory;

import Domain.Users.User;

public class Review {
    private int rate;
    private int orderID;
    private int productID;
    private int userID;

    public Review(int userID, int productID, int orderID, int rate) {
            this.userID = userID;
            this.productID = productID;
            this.orderID = orderID;
            this.setRate(rate);
    }

    public void setRate(int rate) {
        if (rate >= 0 && rate <= 10) {
            this.rate = rate;
        } else {
            throw new IllegalArgumentException("Rate must be between 0 and 10.");
        }
    }

    public int getRate() {
        return rate;
    }

    public int getOrderID() {
        return orderID;
    }

    public int getProductID() {
        return productID;
    }

}
