package Domain.Users;

import Domain.Users.Subscriber.Cart.ShoppingCart;
import Domain.Users.Subscriber.Messages.Message;
import Utilities.Response;

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
