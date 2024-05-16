package Domain.Externals.Security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

import javax.crypto.SecretKey;
import java.security.Key;
import java.util.Date;

import java.util.Date;
import java.util.Random;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

import java.security.Key;

public class Security {

<<<<<<< Updated upstream
    private static final SecretKey SECRET_KEY = Keys.secretKeyFor(SignatureAlgorithm.HS256); // Replace with your secret key
=======
    private static final String SECRET_KEY = "your_secret_key_here"; // Replace with your secret key
>>>>>>> Stashed changes
    private static final long EXPIRATION_TIME_MS = 86400000; // 24 hours

    // Method to generate a JWT for a user
    public static String generateJWT(String userId) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + EXPIRATION_TIME_MS);

<<<<<<< Updated upstream
=======
        Key key = Keys.hmacShaKeyFor(SECRET_KEY.getBytes());

>>>>>>> Stashed changes
        return Jwts.builder()
                .setSubject(userId)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
<<<<<<< Updated upstream
                .signWith(SECRET_KEY)
                .compact();
    }

    // Method to check if a JWT is valid and belongs to a specific username
    public static boolean isValidJWT(String jwt, String username) {
        try {
            Jws<Claims> claimsJws = Jwts.parser().setSigningKey(SECRET_KEY).build().parseClaimsJws(jwt);
            String subject = claimsJws.getBody().getSubject();
            return subject.equals(username);
=======
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    // Method to check if a JWT is valid
    public static boolean isValidJWT(String jwt) {
        try {
            Key key = Keys.hmacShaKeyFor(SECRET_KEY.getBytes());
            Jwts.parser().setSigningKey(key).build().parseClaimsJws(jwt);
            return true;
>>>>>>> Stashed changes
        } catch (Exception e) {
            return false;
        }
    }

}
