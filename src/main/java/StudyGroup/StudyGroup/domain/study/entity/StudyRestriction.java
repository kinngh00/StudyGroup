package StudyGroup.StudyGroup.domain.study.entity;

import StudyGroup.StudyGroup.domain.user.entity.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Table(
    name = "study_restrictions",
    uniqueConstraints = {
        @UniqueConstraint(name = "uk_study_restrictions_study_user", columnNames = {"study_group_id", "user_id"})
    }
)
public class StudyRestriction {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "study_group_id", nullable = false)
  private StudyGroup studyGroup;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id", nullable = false)
  private User user;

  @Column(nullable = false)
  private boolean blacklisted;

  @Column(nullable = false)
  private boolean blocked;

  @Column(length = 500)
  private String reason;

  private LocalDateTime blacklistedAt;
  private LocalDateTime blockedAt;

  @Column(nullable = false, updatable = false)
  @CreationTimestamp
  private LocalDateTime createdAt;

  @Column(nullable = false)
  @UpdateTimestamp
  private LocalDateTime updatedAt;

  public void addBlacklist(String reason) {
    this.blacklisted = true;
    this.reason = reason;
    this.blacklistedAt = LocalDateTime.now();
  }

  public void removeBlacklist() {
    this.blacklisted = false;
    this.blacklistedAt = null;
  }

  public void block(String reason) {
    this.blocked = true;
    this.reason = reason;
    this.blockedAt = LocalDateTime.now();
  }

  public void unblock() {
    this.blocked = false;
    this.blockedAt = null;
  }
}
