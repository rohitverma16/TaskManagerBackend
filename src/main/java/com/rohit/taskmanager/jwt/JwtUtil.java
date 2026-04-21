package com.rohit.taskmanager.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

@Component
public class JwtUtil {

    private final String SECRET = "mySuperSecretKeyForTaskManagerApp12345";
    private final SecretKey SIGN_KEY= Keys.hmacShaKeyFor(SECRET.getBytes());
    private final long EXPIRATION_TIME = 1000*60*30; //30 minutes

    public String generateToken(String username, String role) {
        return Jwts
                .builder()
                .setSubject(username)
                .claim("role",role)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis()+EXPIRATION_TIME))
                .signWith(SIGN_KEY, SignatureAlgorithm.HS256)
                .compact();
    }

    public String extractUsername(String token) {
        Claims claim = getClaim(token);
        return claim.getSubject();
    }

    public String extractRole(String token) {
        return getClaim(token).get("role",String.class);
    }

    private Claims getClaim(String token) {
        return Jwts
                .parserBuilder()
                .setSigningKey(SIGN_KEY)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public boolean validateToken(String token, String username) {
        return extractUsername(token).equals(username) && !isTokenExpired(token);
    }

    public boolean isTokenExpired(String token) {
        return getClaim(token).getExpiration().before(new Date());
    }
}
