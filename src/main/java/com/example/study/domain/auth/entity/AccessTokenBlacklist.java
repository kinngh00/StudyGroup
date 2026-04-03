package com.example.study.domain.auth.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "access_token_blacklist")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AccessTokenBlacklist {

  @Id
  @Column(length = 500)
  private String token;

  @Column(nullable = false)
  private LocalDateTime expiresAt;

  private AccessTokenBlacklist(String token, LocalDateTime expiresAt) {
    this.token = token;
    this.expiresAt = expiresAt;
  }

  public static AccessTokenBlacklist create(String token, LocalDateTime expiresAt) {
    return new AccessTokenBlacklist(token, expiresAt);
  }
}
