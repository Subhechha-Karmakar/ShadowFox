package com.banking.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

/**
 * JwtUtils — TIER 2: JWT Token Management
 *
 * JWT Structure: header.payload.signature
 *   Header:    {"alg": "HS256", "typ": "JWT"}
 *   Payload:   {"sub": "alice", "iat": 1234567890, "exp": 1234654290}
 *   Signature: HMACSHA256(base64(header) + "." + base64(payload), secret)
 *
 * Flow:
 *   1. User logs in → server creates JWT → returns to client
 *   2. Client stores JWT (localStorage or cookie)
 *   3. Every request: client sends "Authorization: Bearer <token>"
 *   4. Server validates signature → extracts username → processes request
 *
 * Security properties:
 *   - Stateless: server doesn't store sessions, scales horizontally
 *   - Tamper-proof: any payload modification invalidates the signature
 *   - Expiring: tokens auto-expire after configured duration
 */
@Component
@Slf4j
public class JwtUtils {

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.expiration.ms}")
    private long jwtExpirationMs;

    /**
     * Generate JWT from authenticated user.
     * The "subject" (sub claim) stores the username.
     */
    public String generateToken(Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        return buildToken(userDetails.getUsername());
    }

    public String generateToken(String username) {
        return buildToken(username);
    }

    private String buildToken(String username) {
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + jwtExpirationMs))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public String extractUsername(String token) {
        return parseClaims(token).getSubject();
    }

    public boolean validateToken(String token) {
        try {
            parseClaims(token); // throws if invalid
            return true;
        } catch (MalformedJwtException e) {
            log.error("Invalid JWT token: {}", e.getMessage());
        } catch (ExpiredJwtException e) {
            log.error("JWT token is expired: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            log.error("JWT token is unsupported: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            log.error("JWT claims string is empty: {}", e.getMessage());
        }
        return false;
    }

    private Claims parseClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private Key getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(jwtSecret);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
