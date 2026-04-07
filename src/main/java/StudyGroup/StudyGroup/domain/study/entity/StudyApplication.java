package StudyGroup.StudyGroup.domain.study.entity;

import StudyGroup.StudyGroup.domain.user.entity.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
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
@Table(name = "study_applications")
public class StudyApplication {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "recruitment_post_id", nullable = false)
  private RecruitmentPost recruitmentPost;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "study_group_id", nullable = false)
  private StudyGroup studyGroup;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "applicant_user_id", nullable = false)
  private User applicantUser;

  @Column(nullable = false, length = 500)
  private String motivation;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 20)
  private ApplicationStatus status;

  @Column
  private LocalDateTime decidedAt;

  @Column(nullable = false, updatable = false)
  @CreationTimestamp
  private LocalDateTime createdAt;

  @Column(nullable = false)
  @UpdateTimestamp
  private LocalDateTime updatedAt;

  public void approve() {
    this.status = ApplicationStatus.APPROVED;
    this.decidedAt = LocalDateTime.now();
  }

  public void reject() {
    this.status = ApplicationStatus.REJECTED;
    this.decidedAt = LocalDateTime.now();
  }

  public void reapply(RecruitmentPost recruitmentPost, String motivation) {
    this.recruitmentPost = recruitmentPost;
    this.motivation = motivation;
    this.status = ApplicationStatus.PENDING;
    this.decidedAt = null;
  }
}
