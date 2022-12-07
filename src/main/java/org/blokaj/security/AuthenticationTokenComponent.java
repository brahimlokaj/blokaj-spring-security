package org.blokaj.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.blokaj.models.entities.User;
import org.blokaj.models.responses.ResponseToken;
import org.blokaj.utiles.DateUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Component
public class AuthenticationTokenComponent {

    static final String CLAIM_KEY_ID = "id";
    static final String CLAIM_KEY_ROLE = "role";
    static final String USERNAME = "username";
    static final String EMAIL = "email";
    static final String ACCESS_TOKEN_EXP = "exp";
    static final String REFRESH_TOKEN_EXP = "exp";

    @Value("${app.jwt.secret}")
    private String secret;

    /*
     * Access token expiration in hour
     */
    @Value("${app.jwt.access-token.expiration}")
    private Integer accessTokenExpiration;

    /*
     * Refresh token expiration in days
     */
    @Value("${app.jwt.refresh-token.expiration}")
    private Integer refreshTokenExpiration;

    public Boolean validateToken(String token, UserDetails userDetails) {
        AuthenticationUser authenticationUser = (AuthenticationUser) userDetails;

        final String username = getUsernameFromToken(token);
        return (username.equals(authenticationUser.getUsername()) || username.equals(authenticationUser.getEmail()));
    }

    public String getUsernameFromToken(String token) {
        return getClaimFromToken(token, Claims::getSubject);
    }

    public <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = Jwts.parser().setSigningKey(secret).parseClaimsJws(token).getBody();
        return claimsResolver.apply(claims);
    }

    public Object getClaimFromToken(String token, String claim) {
        final Claims claims = Jwts.parser().setSigningKey(secret).parseClaimsJws(token).getBody();
        return claims.get(claim);
    }

    public String generateAccessToken(User user) {
        Map<String, Object> claims = new HashMap<>();
        claims.put(CLAIM_KEY_ID, user.getId());
        claims.put(CLAIM_KEY_ROLE, user.getRole().getName());
        claims.put(USERNAME, user.getUsername());
        claims.put(EMAIL, user.getEmail());
        claims.put(ACCESS_TOKEN_EXP, accessTokenExpiration);

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(user.getEmail())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(getAccessTokenExpiration())
                .signWith(SignatureAlgorithm.HS512, secret).compact();
    }

    private Date getAccessTokenExpiration() {
        LocalDateTime now = LocalDateTime.now();
        now = now.plusHours(accessTokenExpiration);

        return DateUtil.localDateTimeToDate(now);
    }

    public String generateRefreshToken(User user) {
        Map<String, Object> claims = new HashMap<>();
        claims.put(EMAIL, user.getEmail());
        claims.put(REFRESH_TOKEN_EXP, refreshTokenExpiration);

        return Jwts.builder().setClaims(claims)
                .setSubject(user.getEmail())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(getRefreshTokenExpiration())
                .signWith(SignatureAlgorithm.HS512, secret).compact();
    }

    private Date getRefreshTokenExpiration() {
        LocalDateTime now = LocalDateTime.now();
        now = now.plusDays(refreshTokenExpiration);

        return DateUtil.localDateTimeToDate(now);
    }

    public ResponseToken getResponseToken(User user) {
        ResponseToken responseToken = new ResponseToken();
        responseToken.setAccessToken(generateAccessToken(user));
        responseToken.setRefreshToken(generateRefreshToken(user));

        return responseToken;
    }

}

