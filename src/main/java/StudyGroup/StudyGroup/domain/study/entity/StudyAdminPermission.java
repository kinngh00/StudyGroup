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
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Table(
    name = "study_admin_permissions",
    uniqueConstraints = {
        @UniqueConstraint(name = "uk_study_admin_permission", columnNames = {"study_group_id", "user_id"})
    }
)
public class StudyAdminPermission {

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
  private boolean recruitmentWrite;

  @Column(nullable = false)
  private boolean recruitmentApprove;

  @Column(nullable = false)
  private boolean memberManage;

  @Column(nullable = false)
  private boolean scheduleManage;

  @Column(nullable = false)
  private boolean noticeManage;

  public void update(
      boolean recruitmentWrite,
      boolean recruitmentApprove,
      boolean memberManage,
      boolean scheduleManage,
      boolean noticeManage
  ) {
    this.recruitmentWrite = recruitmentWrite;
    this.recruitmentApprove = recruitmentApprove;
    this.memberManage = memberManage;
    this.scheduleManage = scheduleManage;
    this.noticeManage = noticeManage;
  }
}
