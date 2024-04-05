package com.group10.util;

import com.group10.model.LoginUser;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.extern.slf4j.Slf4j;

import java.util.Date;


@Slf4j
public class JWTUtil {


    private static final long EXPIRE = 1000 * 60 * 60 * 24 * 7 * 10;

    private static final String SECRET = "xdclass.net666";

    private static final String TOKEN_PREFIX = "xdclass1024shop";

    private static final String SUBJECT = "xdclass";


    public static String geneJsonWebToken(LoginUser loginUser) {

        if (loginUser == null) {
            throw new NullPointerException("Login user is null.");
        }

        String token = Jwts.builder().setSubject(SUBJECT)
                //payload
                .claim("avatar", loginUser.getAvatar())
                .claim("id", loginUser.getId())
                .claim("name", loginUser.getName())
                .claim("email", loginUser.getEmail())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRE))
                .signWith(SignatureAlgorithm.HS256, SECRET).compact();

        token = TOKEN_PREFIX + token;
        return token;
    }


    public static Claims checkJWT(String token) {

        try {

            final Claims claims = Jwts.parser()
                    .setSigningKey(SECRET)
                    .parseClaimsJws(token.replace(TOKEN_PREFIX, "")).getBody();

            return claims;

        } catch (Exception e) {
            log.info("jwt token decode failure");
            return null;
        }

    }


}
