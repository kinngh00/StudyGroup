package StudyGroup.StudyGroup.global.auth;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;
import javax.crypto.SecretKey;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

@Component
public class JwtTokenProvider {

  @Value("${jwt.secret}")
  private String secret;

  @Value("${jwt.access-token-expiration}")
  private long accessTokenExpiration;

  @Value("${jwt.refresh-token-expiration}")
  private long refreshTokenExpiration;

  private SecretKey secretKey;

  @PostConstruct
  public void init() {
    this.secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
  }

  public String createToken(Long userId, String email, String role) {
    return createAccessToken(userId, email, role);
  }

  public String createAccessToken(Long userId, String email, String role) {
    Date now = new Date();
    Date expiredAt = new Date(now.getTime() + accessTokenExpiration);

    return Jwts.builder()
        .subject(String.valueOf(userId))
        .claim("email", email)
        .claim("role", role)
        .issuedAt(now)
        .expiration(expiredAt)
        .signWith(secretKey)
        .compact();
  }

  public String createRefreshToken(Long userId, String email, String role) {
    Date now = new Date();
    Date expiredAt = new Date(now.getTime() + refreshTokenExpiration);

    return Jwts.builder()
        .subject(String.valueOf(userId))
        .claim("email", email)
        .claim("role", role)
        .claim("tokenType", "REFRESH")
        .issuedAt(now)
        .expiration(expiredAt)
        .signWith(secretKey)
        .compact();
  }

  public boolean validateToken(String token) {
    try {
      Jwts.parser()
          .verifyWith(secretKey)
          .build()
          .parseSignedClaims(token);
      return true;
    } catch (Exception e) {
      return false;
    }
  }

  public Authentication getAuthentication(String token) {
    Claims claims = Jwts.parser()
        .verifyWith(secretKey)
        .build()
        .parseSignedClaims(token)
        .getPayload();

    Long userId = Long.parseLong(claims.getSubject());
    String email = claims.get("email", String.class);
    String role = claims.get("role", String.class);
    AuthenticatedUserPrincipal authenticatedUserPrincipal = new AuthenticatedUserPrincipal(userId, email, role);
    List<SimpleGrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_" + role));

    return new UsernamePasswordAuthenticationToken(authenticatedUserPrincipal, token, authorities);
  }

  public boolean isRefreshToken(String token) {
    Claims claims = parseClaims(token);
    String tokenType = claims.get("tokenType", String.class);
    return "REFRESH".equals(tokenType);
  }

  public Long getUserId(String token) {
    return Long.parseLong(parseClaims(token).getSubject());
  }

  public LocalDateTime getExpiredAt(String token) {
    Date expiration = parseClaims(token).getExpiration();
    return LocalDateTime.ofInstant(expiration.toInstant(), ZoneId.systemDefault());
  }

  private Claims parseClaims(String token) {
    return Jwts.parser()
        .verifyWith(secretKey)
        .build()
        .parseSignedClaims(token)
        .getPayload();
  }
}
