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
        encodedPassword= PasswordEncoderUtil.encode("yairby");
    }

    @Test
    public void checkToken(){
        Assert.assertTrue(Security.isValidJWT(token,"yair"));
        Assert.assertFalse(Security.isValidJWT(token,"yairby"));

    }

    @Test
    public void decodePassword(){
        Assert.assertTrue(PasswordEncoderUtil.matches("yairby",encodedPassword));
        Assert.assertFalse(PasswordEncoderUtil.matches("yairbyy",encodedPassword));

    }
}
