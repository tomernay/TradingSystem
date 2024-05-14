package Domain.Externals.Security;

import Domain.Users.Subscriber.Subscriber;

public class Security {
    public boolean checkToken(String Token, Subscriber user){
        return user.getToken().equals(Token);
    }
}
