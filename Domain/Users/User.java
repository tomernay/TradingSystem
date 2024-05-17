package Domain.Users;

import Domain.Users.Subscriber.Cart.ShoppingCart;
import Domain.Users.Subscriber.Messages.Message;
import Utilities.Response;

public class User {
    public ShoppingCart shoppingCart = new ShoppingCart();
    public static Response<String> loginAsGuest() {
        return Response.success("You signed in as a GUEST",null);
    }
    public void logout(){
        shoppingCart = null;
    }
}
