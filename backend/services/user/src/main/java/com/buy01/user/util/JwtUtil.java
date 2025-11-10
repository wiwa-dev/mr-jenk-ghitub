package com.buy01.user.util;

import com.buy01.user.security.CustomUserDetails;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Component
public class JwtUtil {

    // Properties injected from application.properties or application.yml
    @Value("${jwt.secret}")
    private String secret;

    // JWT token expiration time in milliseconds
    @Value("${jwt.expiration}")
    private Long expiration;

    // Generate the signing key from the secret
    private SecretKey getSigningKey() {
        byte[] keyBytes = secret.getBytes();
        return Keys.hmacShaKeyFor(keyBytes);
    }

    // Extract the token ID
    public String extractId(String token) {
        return extractClaim(token, Claims::getId);
    }

    // Extract the email (subject) from the token
    public String extractEmail(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    // ✅ Extract the role from the token
    public String extractRole(String token) {
        return extractClaim(token, claims -> claims.get("role", String.class));
    }

    // Extract expiration date from the token
    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    // Extract a specific claim using a resolver function
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    // Parse the token and extract all claims
    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    // Check if the token is expired
    private Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    // Generate a JWT token for the given UserDetails
    public String generateToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();

        if (userDetails instanceof CustomUserDetails) {
            CustomUserDetails customUserDetails = (CustomUserDetails) userDetails;
            claims.put("id", customUserDetails.getId());
            claims.put("role", customUserDetails.getAuthorities().iterator().next().getAuthority());
        } else {
            // Fallback for standard UserDetails
            claims.put("role", "CLIENT");
        }

        return createToken(claims, userDetails.getUsername());
    }

    // Create a JWT token with the given claims and subject
    private String createToken(Map<String, Object> claims, String subject) {
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    // Validate the token (checks signature, expiration, and subject)
    public Boolean isTokenValid(String token) {
        try {
            // ✅ 1. Verify SIGNATURE (automatic during parsing)
            // ✅ 2. Extract claims (fails if signature invalid)
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

            // ✅ 3. Check EXPIRATION
            System.out.println(claims.get("id"));
            Date expiration = claims.getExpiration();
            if (expiration.before(new Date())) {
                System.out.println("❌ Token expired");
                return false;
            }

            // ✅ 4. Check SUBJECT (email must exist)
            String email = claims.getSubject();
            if (email == null || email.isEmpty()) {
                System.out.println("❌ No email in token");
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

    // Validate the token against UserDetails
    public Boolean validateToken(String token, UserDetails userDetails) {
        final String email = extractEmail(token);
        return (email.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }
}
