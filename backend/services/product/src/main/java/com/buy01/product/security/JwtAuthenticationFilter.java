package com.buy01.product.security;

import com.buy01.product.util.JwtUtil; // ← Shared JWT utility class

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
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.SignatureException;

import java.util.Collections;
import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    // ⚠️ No UserDetailsService here! Product service does not access the User DB

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain) throws ServletException, IOException {

        // 1️⃣ Extract the Authorization header
        final String authorizationHeader = request.getHeader("Authorization");

        // No token → continue without authentication
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            // 2️⃣ Extract the JWT token (remove "Bearer ")
            String jwt = authorizationHeader.substring(7);

            // 3️⃣ 🔥 Validate the token
            if (jwtUtil.isTokenValid(jwt)) {
                // System.out.println("validee");
                // 4️⃣ Extract information from the token
                String email = jwtUtil.extractEmail(jwt);
                String role = jwtUtil.extractRole(jwt);
                String id = jwtUtil.extractId(jwt);
                // 5️⃣ Check if the user is not already authenticated
                if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {

                    // 6️⃣ Create the Spring Security authentication object
                    // ⚠️ Difference with User Service: here we don’t use UserDetails, just the email
                    UsernamePasswordAuthenticationToken authToken = 
                        new UsernamePasswordAuthenticationToken(
                            email,  // ← Principal = email (not a UserDetails)
                            id,   // ← No credentials
                            Collections.singletonList(new SimpleGrantedAuthority(role))
                        );
                    authToken.setDetails(
                        new WebAuthenticationDetailsSource().buildDetails(request)
                    );

                    // 7️⃣ Store authentication in the SecurityContext
                    SecurityContextHolder.getContext().setAuthentication(authToken);

                    logger.debug("✅ Authenticated user: " + email + " | Role: " + role);
                }
            } else {
                logger.warn("❌ Invalid or expired token");
                SecurityConfig.writeJsonError(
                    response,
                    HttpStatus.UNAUTHORIZED,
                    "Invalid signature",
                    "The token has been modified or is invalid",
                    request.getRequestURI()
                );
                return;
            }

            // ✅ Continue the filter chain
            filterChain.doFilter(request, response);

        } catch (SignatureException e) {
            logger.error("❌ Invalid JWT signature: " + e.getMessage());
            SecurityConfig.writeJsonError(
                response,
                HttpStatus.UNAUTHORIZED,
                "Invalid signature",
                "The token has been modified or is invalid",
                request.getRequestURI()
            );

        } catch (ExpiredJwtException e) {
            logger.error("❌ Expired JWT token: " + e.getMessage());
            SecurityConfig.writeJsonError(
                response,
                HttpStatus.UNAUTHORIZED,
                "Session expired",
                "Your session has expired. Please log in again.",
                request.getRequestURI()
            );

        } catch (MalformedJwtException e) {
            logger.error("❌ Malformed JWT token: " + e.getMessage());
            SecurityConfig.writeJsonError(
                response,
                HttpStatus.BAD_REQUEST,
                "Malformed token",
                "The token format is invalid",
                request.getRequestURI()
            );

        } catch (Exception e) {
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
