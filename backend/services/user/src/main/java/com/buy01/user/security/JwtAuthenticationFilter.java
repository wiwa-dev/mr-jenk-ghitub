package com.buy01.user.security;

import com.buy01.user.util.JwtUtil;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.SignatureException;
import org.springframework.http.HttpStatus;
import org.springframework.lang.NonNull;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import java.util.Collections;
import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain) throws ServletException, IOException {

        // 1️⃣ Extract Authorization header
        final String authorizationHeader = request.getHeader("Authorization");

        // No token → continue without authentication (for public endpoints)
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }
        
        try {
            // 2️⃣ Extract JWT token (remove "Bearer ")
            String jwt = authorizationHeader.substring(7);

            // 3️⃣ 🔥 Quick validation of the token (without UserDetails)
            // Checks: signature, expiration, format
            if (!jwtUtil.isTokenValid(jwt)) {
                logger.warn("Invalid or expired token");
                SecurityConfig.writeJsonError(
                    response,
                    HttpStatus.UNAUTHORIZED,
                    "Invalid token",
                    "Your session has expired or the token is invalid",
                    request.getRequestURI()
                );
                return;
            }

            // 4️⃣ Extract email from the token
            String email = jwtUtil.extractEmail(jwt);

            // 5️⃣ Skip if already authenticated
            if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {

                // 6️⃣ Load user details from the database
                UserDetails userDetails = this.userDetailsService.loadUserByUsername(email);

                // 7️⃣ Full validation with UserDetails
                if (jwtUtil.validateToken(jwt, userDetails)) {
                    
                    // 8️⃣ Extract role from the token
                    String role = jwtUtil.extractRole(jwt);

                    // 9️⃣ Create Spring Security authentication
                    UsernamePasswordAuthenticationToken authToken = 
                        new UsernamePasswordAuthenticationToken(
                            userDetails,
                            null,
                            Collections.singletonList(new SimpleGrantedAuthority(role))
                        );

                    authToken.setDetails(
                        new WebAuthenticationDetailsSource().buildDetails(request)
                    );

                    // 🔟 Set authentication in context
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                    
                    logger.debug("✅ Authenticated user: " + email + " | Role: " + role);
                }
            }

            // ✅ Continue filter chain
            filterChain.doFilter(request, response);

        } catch (SignatureException e) {
            // Invalid JWT signature (forged or wrong key)
            logger.error("❌ Invalid JWT signature: " + e.getMessage());
            SecurityConfig.writeJsonError(
                response,
                HttpStatus.UNAUTHORIZED,
                "Invalid signature",
                "The token has been modified or is invalid",
                request.getRequestURI()
            );

        } catch (ExpiredJwtException e) {
            // Expired token
            logger.error("❌ Expired JWT token: " + e.getMessage());
            SecurityConfig.writeJsonError(
                response,
                HttpStatus.UNAUTHORIZED,
                "Session expired",
                "Your session has expired. Please log in again",
                request.getRequestURI()
            );

        } catch (MalformedJwtException e) {
            // Malformed token
            logger.error("❌ Malformed JWT token: " + e.getMessage());
            SecurityConfig.writeJsonError(
                response,
                HttpStatus.BAD_REQUEST,
                "Malformed token",
                "The token format is invalid",
                request.getRequestURI()
            );

        } catch (IllegalArgumentException e) {
            // Empty or null token
            logger.error("❌ Empty JWT token: " + e.getMessage());
            SecurityConfig.writeJsonError(
                response,
                HttpStatus.BAD_REQUEST,
                "Missing token",
                "The JWT token is empty or missing",
                request.getRequestURI()
            );

        } catch (Exception e) {
            // Other errors (e.g. user not found)
            logger.error("❌ JWT authentication error: " + e.getMessage(), e);
            SecurityConfig.writeJsonError(
                response,
                HttpStatus.UNAUTHORIZED,
                "Authentication error",
                "An error occurred during authentication",
                request.getRequestURI()
            );
        }
    }
}
