package src.main.java.Domain.Users;

import src.main.java.Domain.Users.Subscriber.Cart.ShoppingCart;

public class User {
    private ShoppingCart shoppingCart = null;

    public boolean loginAsGuest() {
        if(shoppingCart == null){
            shoppingCart = new ShoppingCart();
            return true;
        }
        return false;
    }

    public boolean logoutAsGuest(){
        if(shoppingCart != null){
            shoppingCart = null;
            return true;
        }
        return false;
    }

    public ShoppingCart getShoppingCart() {
        return shoppingCart;
    }


}