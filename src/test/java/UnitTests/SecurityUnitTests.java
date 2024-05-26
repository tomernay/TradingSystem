package UnitTests;

import Domain.Externals.Security.PasswordEncoderUtil;
import Domain.Externals.Security.Security;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

public class SecurityUnitTests {
    static String token;
    static String encodedPassword;
    @BeforeClass
    public static void init(){
        token= Security.generateJWT("yair");
        encodedPassword= PasswordEncoderUtil.encode("Password123!");
    }

    @Test
    public void checkToken(){
        Assert.assertTrue(Security.isValidJWT(token,"yair"));
        Assert.assertFalse(Security.isValidJWT(token,"yairby"));

    }

    @Test
    public void decodePassword(){
        Assert.assertTrue(PasswordEncoderUtil.matches("Password123!",encodedPassword));
        Assert.assertFalse(PasswordEncoderUtil.matches("Password1234!",encodedPassword));

    }
}
