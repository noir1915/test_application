package com.example.test_application.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class JwtService {

    static final Key key = Keys.secretKeyFor(SignatureAlgorithm.HS256);

    private static final String PREFIX = "Bearer ";

    private static final Integer EXPIRATION_TIME = 86400000; // 1 день в миллисекундах

    public String getToken(UserDetails user) {
        String username = user.getUsername();

        List<String> authorities = user.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        Map<String, Object> claims = new HashMap<>(Map.of("authority", authorities));

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(key)
                .compact();
    }

    public String getAuthUser(String token) {
        Claims claims = getAllClaimsFromToken(token.replace(PREFIX, ""));

        return claims.getSubject(); // Предполагается, что имя пользователя хранится в поле "subject"
    }

    public List<GrantedAuthority> getAuthority(String token) {
        List<String> authority = (List<String>) getAllClaimsFromToken(token.replace(PREFIX, "")).get("authority");

        return authority.stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
    }

    private Claims getAllClaimsFromToken(String token) {
        return Jwts.parser()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}