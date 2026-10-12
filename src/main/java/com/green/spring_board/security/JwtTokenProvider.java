package com.green.spring_board.security;


import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Value;
import java.security.Key;
import java.util.Date;
import java.util.UUID;

@Component
public class JwtTokenProvider {
    private final Key key;
    //엑세스 토큰 유효 기간 (단위: 밀리초) : 1시간
    private static final long ACCESS_TOKEN_VALIDITY = 1000*60*60;
    //리프레시 토큰 유효 기간 : 7일
    private static final long REFRESH_TOKEN_VALIDITY = 1000*60*60*24*7;
    public JwtTokenProvider(
            @Value("${spring.jwt.secret}") String secretKey
    ) {
        this.key = Keys.hmacShaKeyFor(secretKey.getBytes());
    }
    //엑세스 토큰 생성
    public String generateAccessToken(String username){
        Date now = new Date();
        Date expiry = new Date(now.getTime()+ACCESS_TOKEN_VALIDITY);

        return Jwts.builder()
                .setSubject(username)
                .setId(UUID.randomUUID().toString())
                .claim("tokenType", "ACCESS")
                .setIssuedAt(now)
                .setExpiration(expiry)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }
    //리프레시 토큰 생성
    public String generateRefreshToken(String username){
        Date now = new Date();
        Date expiry = new Date(now.getTime()+ REFRESH_TOKEN_VALIDITY);

        return Jwts.builder()
                .setSubject(username)
                .setId(UUID.randomUUID().toString())
                .claim("tokenType", "REFRESH")
                .setIssuedAt(now)
                .setExpiration(expiry)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    //액세스 토큰 유효성(위/변조 여부) 검증
    public boolean validateAccessToken(String token){
        try{
            String tokenType = Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody()
                    .get("tokenType", String.class);

            return tokenType.equals("ACCESS");
        }catch (JwtException |IllegalArgumentException e){
            return false;
        }
    }


    //리프레시 토큰 유효성(위/변조 여부) 검증
    public boolean validateRefreshToken(String token){
        try{
            String tokenType = Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody()
                    .get("tokenType", String.class);

            return tokenType.equals("REFRESH");
        }catch (JwtException |IllegalArgumentException e){
            return false;
        }
    }

    //JWT 토큰에서 username추출
    public String getUsernameFromToken(String token){
        return Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody()
                    .getSubject();
    }

    //JWT토큰에서 만료일자 추출
    public Date getExpiration (String token){
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getExpiration();
    }
}