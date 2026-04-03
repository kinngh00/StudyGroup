package com.example.study.domain.auth.entity;

import com.example.study.domain.user.entity.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "refresh_token")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RefreshToken {

  @Id
  private Long userId;

  @MapsId
  @OneToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id")
  private User user;

  @Column(nullable = false, length = 500)
  private String tokenHash;

  @Column(nullable = false)
  private LocalDateTime expiresAt;

  private RefreshToken(User user, String tokenHash, LocalDateTime expiresAt) {
    this.user = user;
    this.tokenHash = tokenHash;
    this.expiresAt = expiresAt;
  }

  public static RefreshToken create(User user, String tokenHash, LocalDateTime expiresAt) {
    return new RefreshToken(user, tokenHash, expiresAt);
  }

  public void update(String tokenHash, LocalDateTime expiresAt) {
    this.tokenHash = tokenHash;
    this.expiresAt = expiresAt;
  }
}
