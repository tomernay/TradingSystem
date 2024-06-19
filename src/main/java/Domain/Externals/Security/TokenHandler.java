package Domain.Externals.Security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;

import javax.crypto.SecretKey;
import java.util.Date;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.SecurityException;

public class TokenHandler {

    private static final SecretKey SECRET_KEY = Keys.secretKeyFor(SignatureAlgorithm.HS256); // Replace with your secret key

    private static final long EXPIRATION_TIME_MS = 5 * 60 * 1000; // 24 hours

    // Method to generate a JWT for a user
    public static String generateJWT(String userId) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + EXPIRATION_TIME_MS);
        return Jwts.builder()
                .setSubject(userId)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(SECRET_KEY)
                .compact();
    }

    // Method to check if a JWT is valid and belongs to a specific username
    public static boolean isValidJWT(String jwt, String username) {
        try {
            Jws<Claims> claimsJws = Jwts.parser().setSigningKey(SECRET_KEY).build().parseClaimsJws(jwt);
            String subject = claimsJws.getBody().getSubject();
            return subject.equals(username);
        } catch (ExpiredJwtException e) {
            return false;
        } catch (UnsupportedJwtException e) {
            return false;
        } catch (MalformedJwtException e) {
            return false;
        } catch (SecurityException e) {
            return false;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }


}
