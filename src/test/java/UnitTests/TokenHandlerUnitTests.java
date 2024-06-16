package UnitTests;

import Domain.Externals.Security.PasswordEncoderUtil;
import Domain.Externals.Security.TokenHandler;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

public class TokenHandlerUnitTests {
    static String token;
    static String encodedPassword;
    @BeforeClass
    public static void init(){
        token= TokenHandler.generateJWT("yair");
        encodedPassword= PasswordEncoderUtil.encode("Password123!");
    }

    @Test
    public void checkToken(){
        Assert.assertTrue(TokenHandler.isValidJWT(token,"yair"));
        Assert.assertFalse(TokenHandler.isValidJWT(token,"yairby"));

    }

    @Test
    public void decodePassword(){
        Assert.assertTrue(PasswordEncoderUtil.matches("Password123!",encodedPassword));
        Assert.assertFalse(PasswordEncoderUtil.matches("Password1234!",encodedPassword));

    }
}
