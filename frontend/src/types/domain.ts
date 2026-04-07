export type UserRole = "OWNER" | "ADMIN" | "MEMBER" | "NONE";

export type StudyPermission =
  | "RECRUITMENT_WRITE"
  | "APPLICATION_APPROVE"
  | "MEMBER_MANAGE"
  | "SCHEDULE_MANAGE"
  | "NOTICE_MANAGE";

export interface User {
  id: number;
  name: string;
  email: string;
  profileImageUrl?: string;
  role?: string;
  provider?: string;
}

export interface Study {
  id: number;
  name: string;
  description: string;
  maxMembers: number;
  currentMembers: number;
  status: "RECRUITING" | "RECRUITMENT_CLOSED";
  ownerUserId?: number;
  ownerName?: string;
  isFinished: boolean;
}

export interface Recruitment {
  id: number;
  studyId: number;
  authorUserId: number;
  title: string;
  content: string;
  status: "OPEN" | "CLOSED";
  createdAt: string;
  updatedAt: string;
}

export interface StudyMember {
  id: number;
  name: string;
  role: UserRole;
  permissions?: StudyPermission[];
}

export interface StudySchedule {
  id: number;
  studyId: number;
  title: string;
  dateTime: string;
  description: string;
}

export interface Application {
  id: number;
  recruitmentPostId: number;
  studyId: number;
  applicantUserId: number;
  applicantName: string;
  motivation: string;
  status: "PENDING" | "APPROVED" | "REJECTED";
  decidedAt: string | null;
  createdAt: string;
}

export interface BlacklistUser {
  userId: number;
  name: string;
  reason?: string;
  blacklistedAt?: string | null;
  blockedAt?: string | null;
}

export interface StudyNotice {
  id: number;
  studyId: number;
  authorUserId: number;
  authorName: string;
  title: string;
  content: string;
  pinned: boolean;
  createdAt: string;
  updatedAt: string;
}
