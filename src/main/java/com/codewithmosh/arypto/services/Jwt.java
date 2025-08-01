package com.codewithmosh.arypto.services;

import com.codewithmosh.arypto.entities.Role;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import lombok.Getter;
import lombok.Setter;

import javax.crypto.SecretKey;
import java.util.Date;

@Getter
@Setter
public class Jwt {
    private Claims claims;
    private SecretKey key;

    public Jwt(Claims claims, SecretKey key) {
        this.claims = claims;
        this.key = key;
    }

    public boolean isExpired(String token) {
        return claims.getExpiration().before(new Date());
    }
    public String getUserId(String token) {
        return Long.valueOf(claims.getSubject());
    }

    public Role getRole(String token) {
        return Role.valueOf(claims.get("role", String.class));
    }

    @Override
    public String toString() {
        return Jwts.builder()
                .claims(claims).signWith(key)
                .compact();
    }
}
