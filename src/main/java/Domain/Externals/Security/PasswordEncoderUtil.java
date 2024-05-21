package Domain.Externals.Security;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

public class PasswordEncoderUtil {
    static PasswordEncoder passwordEncoder=new BCryptPasswordEncoder();

    /**
     * encode password
     * @param password
     * @return
     */
    public static String encode(String password){
        return passwordEncoder.encode(password);
    }

    /**
     * check password matches the encoded password
     * @param password
     * @param encodedPassword
     * @return
     */
    public static boolean matches(String password,String encodedPassword){
        return passwordEncoder.matches(password,encodedPassword);
    }
}
