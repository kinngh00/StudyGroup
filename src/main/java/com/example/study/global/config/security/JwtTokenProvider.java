package com.example.study.global.config.security;

import com.example.study.domain.user.entity.User;
import com.example.study.domain.user.entity.UserRole;
import com.example.study.global.config.security.dto.AuthUserPrincipal;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

@Component
public class JwtTokenProvider {

  private static final String CLAIM_USER_ID = "uid";
  private static final String CLAIM_ROLE = "role";
  private static final String CLAIM_TOKEN_TYPE = "tokenType";

  private static final String TOKEN_TYPE_ACCESS = "ACCESS";
  private static final String TOKEN_TYPE_REFRESH = "REFRESH";

  private final Key key;
  private final long accessTokenExpiration;
  private final long refreshTokenExpiration;

  public JwtTokenProvider(
      @Value("${jwt.secret}") String secretKey,
      @Value("${jwt.access-token-expiration}") long accessTokenExpiration,
      @Value("${jwt.refresh-token-expiration}") long refreshTokenExpiration
  ) {
    this.key = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
    this.accessTokenExpiration = accessTokenExpiration;
    this.refreshTokenExpiration = refreshTokenExpiration;
  }

  public String createAccessToken(User user) {
    Date issuedAt = new Date();
    Date expiresAt = new Date(issuedAt.getTime() + accessTokenExpiration);

    return Jwts.builder()
        .setSubject(user.getEmail())
        .claim(CLAIM_USER_ID, user.getId())
        .claim(CLAIM_ROLE, user.getRole().name())
        .claim(CLAIM_TOKEN_TYPE, TOKEN_TYPE_ACCESS)
        .setIssuedAt(issuedAt)
        .setExpiration(expiresAt)
        .signWith(key, SignatureAlgorithm.HS256)
        .compact();
  }

  public String createRefreshToken(User user) {
    Date issuedAt = new Date();
    Date expiresAt = new Date(issuedAt.getTime() + refreshTokenExpiration);

    return Jwts.builder()
        .setSubject(user.getEmail())
        .claim(CLAIM_USER_ID, user.getId())
        .claim(CLAIM_TOKEN_TYPE, TOKEN_TYPE_REFRESH)
        .setIssuedAt(issuedAt)
        .setExpiration(expiresAt)
        .signWith(key, SignatureAlgorithm.HS256)
        .compact();
  }

  public Authentication getAuthentication(String accessToken) {
    Claims claims = parseClaims(accessToken);

    Long userId = claims.get(CLAIM_USER_ID, Long.class);
    String email = claims.getSubject();
    String userRoleValue = claims.get(CLAIM_ROLE, String.class);

    UserRole userRole = UserRole.valueOf(userRoleValue);
    AuthUserPrincipal authUserPrincipal = new AuthUserPrincipal(userId, email, userRole);

    String authority = "ROLE_" + userRole.name();
    return new UsernamePasswordAuthenticationToken(
        authUserPrincipal,
        "",
        List.of(new SimpleGrantedAuthority(authority))
    );
  }

  public boolean validateToken(String token) {
    try {
      Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
      return true;
    } catch (io.jsonwebtoken.security.SecurityException | MalformedJwtException exception) {
      return false;
    } catch (ExpiredJwtException exception) {
      return false;
    } catch (UnsupportedJwtException exception) {
      return false;
    } catch (IllegalArgumentException exception) {
      return false;
    }
  }

  public boolean isAccessToken(String token) {
    return TOKEN_TYPE_ACCESS.equals(parseClaims(token).get(CLAIM_TOKEN_TYPE, String.class));
  }

  public boolean isRefreshToken(String token) {
    return TOKEN_TYPE_REFRESH.equals(parseClaims(token).get(CLAIM_TOKEN_TYPE, String.class));
  }

  public Long getUserId(String token) {
    return parseClaims(token).get(CLAIM_USER_ID, Long.class);
  }

  public String getEmail(String token) {
    return parseClaims(token).getSubject();
  }

  public LocalDateTime getExpiration(String token) {
    Date expiration = parseClaims(token).getExpiration();
    return LocalDateTime.ofInstant(Instant.ofEpochMilli(expiration.getTime()), ZoneId.systemDefault());
  }

  private Claims parseClaims(String token) {
    return Jwts.parserBuilder()
        .setSigningKey(key)
        .build()
        .parseClaimsJws(token)
        .getBody();
  }
}
