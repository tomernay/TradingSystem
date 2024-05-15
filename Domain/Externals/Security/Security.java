package Domain.Externals.Security;

import Domain.Users.Subscriber.Subscriber;

import java.util.Date;
import java.util.Random;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

import java.security.Key;

public class Security {

    private static final String SECRET_KEY = "your_secret_key_here"; // Replace with your secret key
    private static final long EXPIRATION_TIME_MS = 86400000; // 24 hours

    // Method to generate a JWT for a user
    public static String generateJWT(String userId) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + EXPIRATION_TIME_MS);

        Key key = Keys.hmacShaKeyFor(SECRET_KEY.getBytes());

        return Jwts.builder()
                .setSubject(userId)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    // Method to check if a JWT is valid
    public static boolean isValidJWT(String jwt) {
        try {
            Key key = Keys.hmacShaKeyFor(SECRET_KEY.getBytes());
            Jwts.parser().setSigningKey(key).build().parseClaimsJws(jwt);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

}
