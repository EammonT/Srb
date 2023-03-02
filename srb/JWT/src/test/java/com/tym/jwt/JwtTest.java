package com.tym.jwt;

import io.jsonwebtoken.*;
import org.junit.Test;

import java.util.Date;
import java.util.UUID;

public class JwtTest {

        String tokenSignKey = "tym123";
        long tokenExpiration = 1000 * 60 * 60 * 24;
    @Test
    public void testCreatedToken(){
        JwtBuilder builder = Jwts.builder();
        String token = builder
                .setHeaderParam("alg", "HS256")
                .setHeaderParam("typ", "JWT")
                .claim("nickname", "zhangsan")
                .claim("avatar", "1.jpg")
                .claim("role", "admin")
                .setSubject("srb")
                .setIssuer("tym")
                .setAudience("lisi")
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + tokenExpiration))
                .setNotBefore(new Date(System.currentTimeMillis() + 1000 * 20))
                .setId(UUID.randomUUID().toString())
                .signWith(SignatureAlgorithm.HS256, tokenSignKey)
                .compact();
        System.out.println(token);
    }

    @Test
    public void testGetUserInfo(){
        String token = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJuaWNrbmFtZSI6InpoYW5nc2FuIiwiYXZhdGFyIjoiMS5qcGciLCJyb2xlIjoiYWRtaW4iLCJzdWIiOiJzcmIiLCJpc3MiOiJ0eW0iLCJhdWQiOiJsaXNpIiwiaWF0IjoxNjcyOTA4MjUwLCJleHAiOjE2NzI5OTQ2NTAsIm5iZiI6MTY3MjkwODI3MCwianRpIjoiYjY4OWQwNTktOTcwNS00N2M2LWIxOTItMTM0YmI1ZGFhMmU4In0.dQObBKERwvXED1PNKCv2A783gnAHMhaa2DCKLyDBeyo";
        JwtParser parser = Jwts.parser();
        Jws<Claims> claimsJws = parser.setSigningKey(tokenSignKey).parseClaimsJws(token);
        Claims body = claimsJws.getBody();
        String nickname = (String) body.get("nickname");
        String avatar = (String) body.get("avatar");
        String role = (String) body.get("role");
        System.out.println(nickname+avatar+role);
        String id = body.getId();
        System.out.println(id);
    }

}
