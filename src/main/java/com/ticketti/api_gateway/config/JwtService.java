package com.ticketti.api_gateway.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Set;
import java.util.function.Function;

@Slf4j
@Service
public class JwtService {

    @Value("${jwt.secret}")
    private String secretKey;

    @Value("${jwt.issuer:}")
    private String issuer;

    @Value("${jwt.audience:}")
    private String audience;

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
    }

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    public boolean validateToken(String token) {
        try {
            Claims claims = extractAllClaims(token);
            if (isTokenExpired(token)) {
                return false;
            }
            return isValidIssuer(claims) && isValidAudience(claims);
        } catch (Exception e) {
            log.error("Token validation error: {}", e.getMessage());
            return false;
        }
    }

    private boolean isValidIssuer(Claims claims) {
        if (issuer == null || issuer.isEmpty()) {
            return true;
        }

        return issuer.equals(claims.getIssuer());
    }

    private boolean isValidAudience(Claims claims) {
        if (audience == null || audience.isEmpty()) {
            return true;
        }

        Object tokenAudience = claims.get("aud");
        if (tokenAudience instanceof String audienceValue) {
            return audience.equals(audienceValue);
        }
        if (tokenAudience instanceof Set<?> audienceValues) {
            return audienceValues.contains(audience);
        }

        return false;
    }

    @SuppressWarnings("unchecked")
    public Set<String> extractRoles(String token) {
        Claims claims = extractAllClaims(token);
        return claims.get("roles", Set.class);
    }
}
