package com.buy01.media.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.function.Function;

@Component
public class JwtUtil {

    // These properties are injected from application.properties or application.yml
    @Value("${jwt.secret}")
    private String secret;

    // This defines the JWT token expiration time in milliseconds
    @Value("${jwt.expiration}")
    private Long expiration;

    // Generate the signing key from the secret string
    private SecretKey getSigningKey() {
        byte[] keyBytes = secret.getBytes();
        return Keys.hmacShaKeyFor(keyBytes);
    }

    // Extract the token ID
    public String extractId(String token) {
        return extractClaim(token,  claims -> claims.get("id", String.class));
    }

    // Extract the email (subject) from the token
    public String extractEmail(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    // ✅ Extract the role
    public String extractRole(String token) {
        return extractClaim(token, claims -> claims.get("role", String.class));
    }
    

    // Extract expiration date from the token
    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    // Generic method to extract any claim from the token
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    // Parse the token and get all claims
    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    // Check if the token has expired
    private Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    // Validate the token (signature, expiration, format, etc.)
    public Boolean isTokenValid(String token) {
        try {
            // ✅ 1. Verify SIGNATURE (automatically checked during parsing)
            // ✅ 2. Extract claims (fails if signature is invalid)
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

            // ✅ 3. Check EXPIRATION
            Date expiration = claims.getExpiration();
            if (expiration.before(new Date())) {
                System.out.println("❌ Token expired");
                return false;
            }

            // ✅ 4. Check SUBJECT (email must exist)
            String email = claims.getSubject();
            if (email == null || email.isEmpty()) {
                System.out.println("❌ Missing email in token");
                return false;
            }

            System.out.println("✅ Valid token for: " + email);
            return true;

        } catch (SignatureException e) {
            System.out.println("❌ Invalid signature: " + e.getMessage());
            return false;
        } catch (ExpiredJwtException e) {
            System.out.println("❌ Token expired: " + e.getMessage());
            return false;
        } catch (MalformedJwtException e) {
            System.out.println("❌ Malformed token: " + e.getMessage());
            return false;
        } catch (UnsupportedJwtException e) {
            System.out.println("❌ Unsupported token: " + e.getMessage());
            return false;
        } catch (IllegalArgumentException e) {
            System.out.println("❌ Empty or null token: " + e.getMessage());
            return false;
        }
    }

    // Validate the token against the provided UserDetails
    // Checks that the email in the token matches and the token is not expired
    public Boolean validateToken(String token, UserDetails userDetails) {
        final String email = extractEmail(token);
        return (email.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }
}
