package com.example.study.domain.auth.repository;

import com.example.study.domain.auth.entity.AccessTokenBlacklist;
import java.time.LocalDateTime;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccessTokenBlacklistRepository extends JpaRepository<AccessTokenBlacklist, String> {

  void deleteByExpiresAtBefore(LocalDateTime now);
}
