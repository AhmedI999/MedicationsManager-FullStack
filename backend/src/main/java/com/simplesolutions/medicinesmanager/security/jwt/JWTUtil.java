package com.simplesolutions.medicinesmanager.security.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Encoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.time.Instant;

import java.util.Date;
import java.util.List;
import java.util.Map;

import static java.time.temporal.ChronoUnit.DAYS;
import static java.time.temporal.ChronoUnit.MINUTES;

@Service
public class JWTUtil {
    private static final SecretKey SECRET_KEY = Jwts.SIG.HS256.key().build();
    @Value("#{'${jwt.token-expiration}'}")
    private Integer DAYS_UNTIL_TOKEN_EXPIRATION;
    @Value("#{'${jwt.email.confirmationToken.expiresAt}'}")
    private Integer MINUTES_UNTIL_CONFIRMATION_EXPIRATION;

    public String issueToken(
            String subject,
            Map<String, Object> claims) {

        return Jwts.builder()
                .claims(claims)
                .subject(subject)
                .issuer("AhmedIbrahim")
                .issuedAt(Date.from(Instant.now()))
                .expiration(Date.from(Instant.now().plus(DAYS_UNTIL_TOKEN_EXPIRATION, DAYS)))
                .signWith(getSigningKey())
                .compact();
    }
    public String issueToken( String subject ) {
        return issueToken(subject, Map.of());
    }
    public String issueToken( String subject, String ...scopes ) {
        return issueToken(subject, Map.of("scopes", scopes));
    }
    public String issueToken( String subject, List<String> scopes ) {
        return issueToken(subject, Map.of("scopes", scopes));
    }
    public String issueEmailToken( String subject ) {

        return Jwts.builder()
                .subject(subject)
                .issuer("AhmedIbrahim")
                .issuedAt(Date.from(Instant.now()))
                .expiration(Date.from(Instant.now().plus(MINUTES_UNTIL_CONFIRMATION_EXPIRATION, MINUTES)))
                .compact();
    }
    private SecretKey getSigningKey() {
        String secretString = Encoders.BASE64.encode(SECRET_KEY.getEncoded());
        return Keys.hmacShaKeyFor(secretString.getBytes());
    }
    public String getSubject( String token ) {
        return getClaims(token).getSubject();
    }
    private Claims getClaims(String token) {
        return Jwts
                .parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
    private boolean isTokenExpired(String jwtToken) {
       return getClaims(jwtToken).getExpiration().before(Date.from(Instant.now()));
    }

    public boolean isTokenValid(String jwtToken, String username) {
        String subject = getSubject(jwtToken);
        return subject.equals(username) && !isTokenExpired(jwtToken);
    }


}
