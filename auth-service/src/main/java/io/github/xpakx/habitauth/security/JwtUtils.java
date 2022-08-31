package io.github.xpakx.habitauth.security;

import io.github.xpakx.habitauth.user.UserAccount;
import io.github.xpakx.habitauth.user.UserRole;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;

import java.util.Date;
import java.util.stream.Collectors;

public class JwtUtils {
    public static final long JWT_TOKEN_VALIDITY = 5 * 60 * 60;

    @Value("${jwt.secret}")
    private String secret;

    public String generateToken(UserAccount userDetails) {
        Claims claims = Jwts.claims().setSubject(userDetails.getUsername());
        claims.put(
                "roles",
                userDetails.getRoles().stream()
                        .map(UserRole::getAuthority)
                        .collect(Collectors.toList())
        );
        return doGenerateToken(claims, userDetails.getUsername());
    }

    private String doGenerateToken(Claims claims, String subject) {
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + JWT_TOKEN_VALIDITY * 1000))
                .signWith(SignatureAlgorithm.HS512, secret)
                .compact();
    }
}
