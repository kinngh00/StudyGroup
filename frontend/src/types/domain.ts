export type UserRole = "Owner" | "Admin" | "Member" | "None";
export type StudyPermission = "RECRUITMENT_WRITE" | "APPLICATION_APPROVE" | "MEMBER_MANAGE" | "SCHEDULE_MANAGE";

export interface User {
  id: number;
  name: string;
  email: string;
  profileImageUrl?: string;
}

export interface Study {
  id: number;
  name: string;
  description: string;
  period: string;
  maxMembers: number;
  ownerName: string;
  isFinished: boolean;
}

export interface Recruitment {
  id: number;
  studyId: number;
  title: string;
  content: string;
  status: "OPEN" | "CLOSED";
  deadline: string;
}

export interface StudyMember {
  id: number;
  name: string;
  role: UserRole;
  permissions?: StudyPermission[];
}

export interface StudySchedule {
  id: number;
  title: string;
  dateTime: string;
  description?: string;
}

export interface Application {
  id: number;
  applicantName: string;
  status: "PENDING" | "APPROVED" | "REJECTED";
}

export interface BlacklistUser {
  userId: number;
  name: string;
  reason?: string;
  blockedAt?: string;
}

export interface StudyNotice {
  id: number;
  title: string;
  content: string;
  createdAt: string;
  authorName: string;
}
