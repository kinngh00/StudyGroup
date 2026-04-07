import { createApi, type BaseQueryFn } from "@reduxjs/toolkit/query/react";
import type { AxiosError, AxiosRequestConfig } from "axios";
import apiClient from "@/api/axios";
import type {
  Application,
  BlacklistUser,
  Recruitment,
  Study,
  StudyMember,
  StudyNotice,
  StudyPermission,
  StudySchedule,
  User,
  UserRole
} from "@/types/domain";

type AxiosQueryArgs = {
  url: string;
  method: AxiosRequestConfig["method"];
  data?: unknown;
  params?: Record<string, unknown>;
};

type AxiosBaseQueryError = {
  status?: number;
  data?: unknown;
  message?: string;
};

const axiosBaseQuery =
  (): BaseQueryFn<AxiosQueryArgs, unknown, AxiosBaseQueryError> =>
  async ({ url, method, data, params }) => {
    try {
      const result = await apiClient({ url, method, data, params });
      const payload = result.data as { data?: unknown };
      if (payload && typeof payload === "object" && "data" in payload) {
        return { data: payload.data };
      }
      return { data: result.data };
    } catch (axiosError) {
      const err = axiosError as AxiosError;
      const responseData = err.response?.data as { message?: string } | undefined;
      return {
        error: {
          status: err.response?.status,
          data: err.response?.data,
          message: responseData?.message ?? err.message
        }
      };
    }
  };

const toStudySummary = (source: {
  studyGroupId: number;
  name: string;
  maxMemberCount: number;
  status: "RECRUITING" | "RECRUITMENT_CLOSED";
}): Study => ({
  id: source.studyGroupId,
  name: source.name,
  description: "",
  maxMembers: source.maxMemberCount,
  currentMembers: 0,
  status: source.status,
  ownerUserId: undefined,
  ownerName: undefined,
  isFinished: source.status === "RECRUITMENT_CLOSED"
});

const toStudyDetail = (source: {
  studyGroupId: number;
  name: string;
  description: string;
  maxMemberCount: number;
  currentMemberCount: number;
  status: "RECRUITING" | "RECRUITMENT_CLOSED";
  ownerUserId: number;
  ownerName: string;
}): Study => ({
  id: source.studyGroupId,
  name: source.name,
  description: source.description,
  maxMembers: source.maxMemberCount,
  currentMembers: source.currentMemberCount,
  status: source.status,
  ownerUserId: source.ownerUserId,
  ownerName: source.ownerName,
  isFinished: source.status === "RECRUITMENT_CLOSED"
});

const toRecruitment = (source: {
  recruitmentPostId: number;
  studyGroupId: number;
  authorUserId: number;
  title: string;
  content: string;
  status: "OPEN" | "CLOSED";
  createdAt: string;
  updatedAt: string;
}): Recruitment => ({
  id: source.recruitmentPostId,
  studyId: source.studyGroupId,
  authorUserId: source.authorUserId,
  title: source.title,
  content: source.content,
  status: source.status,
  createdAt: source.createdAt,
  updatedAt: source.updatedAt
});

const toApplication = (source: {
  applicationId: number;
  recruitmentPostId: number;
  studyGroupId: number;
  applicantUserId: number;
  applicantName: string;
  motivation: string;
  status: "PENDING" | "APPROVED" | "REJECTED";
  decidedAt: string | null;
  createdAt: string;
}): Application => ({
  id: source.applicationId,
  recruitmentPostId: source.recruitmentPostId,
  studyId: source.studyGroupId,
  applicantUserId: source.applicantUserId,
  applicantName: source.applicantName,
  motivation: source.motivation,
  status: source.status,
  decidedAt: source.decidedAt,
  createdAt: source.createdAt
});

const toMember = (source: {
  userId: number;
  userName: string;
  role: UserRole;
  recruitmentWrite: boolean;
  recruitmentApprove: boolean;
  memberManage: boolean;
  scheduleManage: boolean;
  noticeManage: boolean;
}): StudyMember => {
  const permissions: StudyPermission[] = [];
  if (source.recruitmentWrite) permissions.push("RECRUITMENT_WRITE");
  if (source.recruitmentApprove) permissions.push("APPLICATION_APPROVE");
  if (source.memberManage) permissions.push("MEMBER_MANAGE");
  if (source.scheduleManage) permissions.push("SCHEDULE_MANAGE");
  if (source.noticeManage) permissions.push("NOTICE_MANAGE");

  return {
    id: source.userId,
    name: source.userName,
    role: source.role,
    permissions
  };
};

const toSchedule = (source: {
  studyScheduleId: number;
  studyGroupId: number;
  title: string;
  description: string;
  scheduledAt: string;
}): StudySchedule => ({
  id: source.studyScheduleId,
  studyId: source.studyGroupId,
  title: source.title,
  description: source.description,
  dateTime: source.scheduledAt
});

const toNotice = (source: {
  studyNoticeId: number;
  studyGroupId: number;
  authorUserId: number;
  authorName: string;
  title: string;
  content: string;
  pinned: boolean;
  createdAt: string;
  updatedAt: string;
}): StudyNotice => ({
  id: source.studyNoticeId,
  studyId: source.studyGroupId,
  authorUserId: source.authorUserId,
  authorName: source.authorName,
  title: source.title,
  content: source.content,
  pinned: source.pinned,
  createdAt: source.createdAt,
  updatedAt: source.updatedAt
});

export const studyGroupApi = createApi({
  reducerPath: "studyGroupApi",
  baseQuery: axiosBaseQuery(),
  tagTypes: ["Study", "Recruitment", "Application", "Member", "Schedule", "Role", "Blacklist", "Notice", "Auth"],
  endpoints: (builder) => ({
    signup: builder.mutation<{ email: string; name: string }, { name: string; email: string; password: string }>({
      query: (body) => ({ url: "/api/auth/local/signup", method: "POST", data: body })
    }),
    login: builder.mutation<{ email: string; name: string; accessToken: string; refreshToken: string }, { email: string; password: string }>({
      query: (body) => ({ url: "/api/auth/local/login", method: "POST", data: body })
    }),
    googleLogin: builder.mutation<{ email: string; name: string; accessToken: string; refreshToken: string }, { idToken: string }>({
      query: (body) => ({ url: "/api/auth/google/login", method: "POST", data: body })
    }),
    reissue: builder.mutation<{ email: string; name: string; accessToken: string; refreshToken: string }, { refreshToken: string }>({
      query: (body) => ({ url: "/api/auth/local/reissue", method: "POST", data: body })
    }),
    logout: builder.mutation<void, { refreshToken: string }>({
      query: (body) => ({ url: "/api/auth/logout", method: "POST", data: body }),
      invalidatesTags: ["Auth"]
    }),
    getMe: builder.query<User, void>({
      query: () => ({ url: "/api/auth/me", method: "GET" }),
      transformResponse: (response: { userId: number; email: string; name: string; role: string; provider: string }) => ({
        id: response.userId,
        email: response.email,
        name: response.name,
        role: response.role,
        provider: response.provider
      }),
      providesTags: ["Auth"]
    }),

    getStudies: builder.query<Study[], { name?: string; finished?: boolean; status?: "RECRUITING" | "RECRUITMENT_CLOSED" }>({
      query: (params) => ({
        url: "/api/studies",
        method: "GET",
        params: {
          nameKeyword: params.name?.trim() ? params.name.trim() : undefined,
          status: params.status,
          isClosed: typeof params.finished === "boolean" ? params.finished : undefined
        }
      }),
      transformResponse: (response: Array<{ studyGroupId: number; name: string; maxMemberCount: number; status: "RECRUITING" | "RECRUITMENT_CLOSED" }>) =>
        response.map(toStudySummary),
      providesTags: ["Study"]
    }),
    getStudy: builder.query<Study, number>({
      query: (studyId) => ({ url: `/api/studies/${studyId}`, method: "GET" }),
      transformResponse: (response: {
        studyGroupId: number;
        name: string;
        description: string;
        maxMemberCount: number;
        currentMemberCount: number;
        status: "RECRUITING" | "RECRUITMENT_CLOSED";
        ownerUserId: number;
        ownerName: string;
      }) => toStudyDetail(response),
      providesTags: (_r, _e, id) => [{ type: "Study", id }]
    }),
    createStudy: builder.mutation<Study, { name: string; description: string; maxMemberCount: number }>({
      query: (body) => ({ url: "/api/studies", method: "POST", data: body }),
      transformResponse: (response: {
        studyGroupId: number;
        name: string;
        description: string;
        maxMemberCount: number;
        status: "RECRUITING" | "RECRUITMENT_CLOSED";
      }) => ({
        id: response.studyGroupId,
        name: response.name,
        description: response.description,
        maxMembers: response.maxMemberCount,
        currentMembers: 1,
        status: response.status,
        isFinished: response.status === "RECRUITMENT_CLOSED"
      }),
      invalidatesTags: ["Study"]
    }),
    updateStudy: builder.mutation<Study, { studyId: number; body: { name: string; description: string; maxMemberCount: number; status?: "RECRUITING" | "RECRUITMENT_CLOSED" } }>({
      query: ({ studyId, body }) => ({ url: `/api/studies/${studyId}`, method: "PATCH", data: body }),
      transformResponse: (response: {
        studyGroupId: number;
        name: string;
        description: string;
        maxMemberCount: number;
        currentMemberCount: number;
        status: "RECRUITING" | "RECRUITMENT_CLOSED";
        ownerUserId: number;
        ownerName: string;
      }) => toStudyDetail(response),
      invalidatesTags: (_r, _e, { studyId }) => [{ type: "Study", id: studyId }]
    }),
    getUserRole: builder.query<{ role: UserRole }, number>({
      query: (studyId) => ({ url: `/api/studies/${studyId}/role`, method: "GET" }),
      transformResponse: (response: { role: UserRole }) => response,
      providesTags: ["Role"]
    }),
    getStudyMembers: builder.query<StudyMember[], number>({
      query: (studyId) => ({ url: `/api/studies/${studyId}/members`, method: "GET" }),
      transformResponse: (response: Array<{
        userId: number;
        userName: string;
        role: UserRole;
        recruitmentWrite: boolean;
        recruitmentApprove: boolean;
        memberManage: boolean;
        scheduleManage: boolean;
        noticeManage: boolean;
      }>) => response.map(toMember),
      providesTags: ["Member"]
    }),

    getRecruitments: builder.query<Recruitment[], { studyId: number }>({
      query: ({ studyId }) => ({ url: `/api/studies/${studyId}/recruitment-posts`, method: "GET" }),
      transformResponse: (response: Array<{
        recruitmentPostId: number;
        studyGroupId: number;
        authorUserId: number;
        title: string;
        content: string;
        status: "OPEN" | "CLOSED";
        createdAt: string;
        updatedAt: string;
      }>) => response.map(toRecruitment),
      providesTags: ["Recruitment"]
    }),
    createRecruitment: builder.mutation<Recruitment, { studyId: number; title: string; content: string }>({
      query: ({ studyId, title, content }) => ({
        url: `/api/studies/${studyId}/recruitment-posts`,
        method: "POST",
        data: { title, content }
      }),
      transformResponse: (response: {
        recruitmentPostId: number;
        studyGroupId: number;
        authorUserId: number;
        title: string;
        content: string;
        status: "OPEN" | "CLOSED";
        createdAt: string;
        updatedAt: string;
      }) => toRecruitment(response),
      invalidatesTags: ["Recruitment"]
    }),
    updateRecruitment: builder.mutation<Recruitment, { studyId: number; recruitmentId: number; title: string; content: string; status?: "OPEN" | "CLOSED" }>({
      query: ({ studyId, recruitmentId, ...body }) => ({
        url: `/api/studies/${studyId}/recruitment-posts/${recruitmentId}`,
        method: "PATCH",
        data: body
      }),
      transformResponse: (response: {
        recruitmentPostId: number;
        studyGroupId: number;
        authorUserId: number;
        title: string;
        content: string;
        status: "OPEN" | "CLOSED";
        createdAt: string;
        updatedAt: string;
      }) => toRecruitment(response),
      invalidatesTags: ["Recruitment"]
    }),
    deleteRecruitment: builder.mutation<void, { studyId: number; recruitmentId: number }>({
      query: ({ studyId, recruitmentId }) => ({
        url: `/api/studies/${studyId}/recruitment-posts/${recruitmentId}`,
        method: "DELETE"
      }),
      invalidatesTags: ["Recruitment", "Application"]
    }),
    applyStudy: builder.mutation<Application, { studyId: number; recruitmentPostId: number; motivation: string }>({
      query: ({ studyId, recruitmentPostId, motivation }) => ({
        url: `/api/studies/${studyId}/recruitment-posts/${recruitmentPostId}/applications`,
        method: "POST",
        data: { motivation }
      }),
      transformResponse: (response: {
        applicationId: number;
        recruitmentPostId: number;
        studyGroupId: number;
        applicantUserId: number;
        applicantName: string;
        motivation: string;
        status: "PENDING" | "APPROVED" | "REJECTED";
        decidedAt: string | null;
        createdAt: string;
      }) => toApplication(response),
      invalidatesTags: ["Application"]
    }),
    getApplications: builder.query<Application[], { studyId: number; recruitmentPostId: number }>({
      query: ({ studyId, recruitmentPostId }) => ({
        url: `/api/studies/${studyId}/recruitment-posts/${recruitmentPostId}/applications`,
        method: "GET"
      }),
      transformResponse: (response: Array<{
        applicationId: number;
        recruitmentPostId: number;
        studyGroupId: number;
        applicantUserId: number;
        applicantName: string;
        motivation: string;
        status: "PENDING" | "APPROVED" | "REJECTED";
        decidedAt: string | null;
        createdAt: string;
      }>) => response.map(toApplication),
      providesTags: ["Application"]
    }),
    approveApplication: builder.mutation<Application, { studyId: number; recruitmentPostId: number; applicationId: number }>({
      query: ({ studyId, recruitmentPostId, applicationId }) => ({
        url: `/api/studies/${studyId}/recruitment-posts/${recruitmentPostId}/applications/${applicationId}/approve`,
        method: "PATCH"
      }),
      transformResponse: (response: {
        applicationId: number;
        recruitmentPostId: number;
        studyGroupId: number;
        applicantUserId: number;
        applicantName: string;
        motivation: string;
        status: "PENDING" | "APPROVED" | "REJECTED";
        decidedAt: string | null;
        createdAt: string;
      }) => toApplication(response),
      invalidatesTags: ["Application", "Member"]
    }),
    rejectApplication: builder.mutation<Application, { studyId: number; recruitmentPostId: number; applicationId: number }>({
      query: ({ studyId, recruitmentPostId, applicationId }) => ({
        url: `/api/studies/${studyId}/recruitment-posts/${recruitmentPostId}/applications/${applicationId}/reject`,
        method: "PATCH"
      }),
      transformResponse: (response: {
        applicationId: number;
        recruitmentPostId: number;
        studyGroupId: number;
        applicantUserId: number;
        applicantName: string;
        motivation: string;
        status: "PENDING" | "APPROVED" | "REJECTED";
        decidedAt: string | null;
        createdAt: string;
      }) => toApplication(response),
      invalidatesTags: ["Application"]
    }),

    kickMember: builder.mutation<void, { studyId: number; targetUserId: number; addToBlacklist?: boolean; reason?: string }>({
      query: ({ studyId, targetUserId, addToBlacklist = false, reason }) => ({
        url: `/api/studies/${studyId}/members/${targetUserId}/kick`,
        method: "PATCH",
        data: { addToBlacklist, reason }
      }),
      invalidatesTags: ["Member", "Blacklist"]
    }),
    addToBlacklist: builder.mutation<void, { studyId: number; targetUserId: number; reason?: string }>({
      query: ({ studyId, targetUserId, reason }) => ({
        url: `/api/studies/${studyId}/blacklist/${targetUserId}`,
        method: "POST",
        data: { reason }
      }),
      invalidatesTags: ["Blacklist"]
    }),
    getBlacklistUsers: builder.query<BlacklistUser[], number>({
      query: (studyId) => ({ url: `/api/studies/${studyId}/blacklist`, method: "GET" }),
      transformResponse: (response: Array<{
        userId: number;
        userName: string;
        blacklisted: boolean;
        blocked: boolean;
        reason: string;
        blacklistedAt: string | null;
        blockedAt: string | null;
      }>) =>
        response.map((item) => ({
          userId: item.userId,
          name: item.userName,
          reason: item.reason,
          blacklistedAt: item.blacklistedAt,
          blockedAt: item.blockedAt
        })),
      providesTags: ["Blacklist"]
    }),
    removeFromBlacklist: builder.mutation<void, { studyId: number; userId: number }>({
      query: ({ studyId, userId }) => ({ url: `/api/studies/${studyId}/blacklist/${userId}`, method: "DELETE" }),
      invalidatesTags: ["Blacklist"]
    }),
    assignAdmin: builder.mutation<void, { studyId: number; memberId: number; permissions?: StudyPermission[] }>({
      query: ({ studyId, memberId, permissions = [] }) => ({
        url: `/api/studies/${studyId}/admins`,
        method: "POST",
        data: {
          userId: memberId,
          recruitmentWrite: permissions.includes("RECRUITMENT_WRITE"),
          recruitmentApprove: permissions.includes("APPLICATION_APPROVE"),
          memberManage: permissions.includes("MEMBER_MANAGE"),
          scheduleManage: permissions.includes("SCHEDULE_MANAGE"),
          noticeManage: permissions.includes("NOTICE_MANAGE")
        }
      }),
      invalidatesTags: ["Member"]
    }),

    getSchedules: builder.query<StudySchedule[], number>({
      query: (studyId) => ({ url: `/api/studies/${studyId}/schedules`, method: "GET" }),
      transformResponse: (response: Array<{
        studyScheduleId: number;
        studyGroupId: number;
        title: string;
        description: string;
        scheduledAt: string;
      }>) => response.map(toSchedule),
      providesTags: ["Schedule"]
    }),
    createSchedule: builder.mutation<StudySchedule, { studyId: number; title: string; dateTime: string; description?: string }>({
      query: ({ studyId, title, dateTime, description }) => ({
        url: `/api/studies/${studyId}/schedules`,
        method: "POST",
        data: { title, description: description ?? "-", scheduledAt: dateTime }
      }),
      transformResponse: (response: {
        studyScheduleId: number;
        studyGroupId: number;
        title: string;
        description: string;
        scheduledAt: string;
      }) => toSchedule(response),
      invalidatesTags: ["Schedule"]
    }),
    updateSchedule: builder.mutation<StudySchedule, { studyId: number; scheduleId: number; title: string; dateTime: string; description?: string }>({
      query: ({ studyId, scheduleId, title, dateTime, description }) => ({
        url: `/api/studies/${studyId}/schedules/${scheduleId}`,
        method: "PATCH",
        data: { title, description: description ?? "-", scheduledAt: dateTime }
      }),
      transformResponse: (response: {
        studyScheduleId: number;
        studyGroupId: number;
        title: string;
        description: string;
        scheduledAt: string;
      }) => toSchedule(response),
      invalidatesTags: ["Schedule"]
    }),
    deleteSchedule: builder.mutation<void, { studyId: number; scheduleId: number }>({
      query: ({ studyId, scheduleId }) => ({
        url: `/api/studies/${studyId}/schedules/${scheduleId}`,
        method: "DELETE"
      }),
      invalidatesTags: ["Schedule"]
    }),

    getNotices: builder.query<StudyNotice[], number>({
      query: (studyId) => ({ url: `/api/studies/${studyId}/notices`, method: "GET" }),
      transformResponse: (response: Array<{
        studyNoticeId: number;
        studyGroupId: number;
        authorUserId: number;
        authorName: string;
        title: string;
        content: string;
        pinned: boolean;
        createdAt: string;
        updatedAt: string;
      }>) => response.map(toNotice),
      providesTags: ["Notice"]
    }),
    createNotice: builder.mutation<StudyNotice, { studyId: number; title: string; content: string; pinned?: boolean }>({
      query: ({ studyId, title, content, pinned = false }) => ({
        url: `/api/studies/${studyId}/notices`,
        method: "POST",
        data: { title, content, pinned }
      }),
      transformResponse: (response: {
        studyNoticeId: number;
        studyGroupId: number;
        authorUserId: number;
        authorName: string;
        title: string;
        content: string;
        pinned: boolean;
        createdAt: string;
        updatedAt: string;
      }) => toNotice(response),
      invalidatesTags: ["Notice"]
    }),
    reportUser: builder.mutation<void, { studyId: number; targetUserId: number; reason: string }>({
      query: ({ studyId, targetUserId, reason }) => ({
        url: `/api/studies/${studyId}/reports`,
        method: "POST",
        data: { targetUserId, content: reason }
      })
    })
  })
});

export const {
  useSignupMutation,
  useLoginMutation,
  useGoogleLoginMutation,
  useReissueMutation,
  useLogoutMutation,
  useGetMeQuery,
  useGetStudiesQuery,
  useGetStudyQuery,
  useCreateStudyMutation,
  useUpdateStudyMutation,
  useGetUserRoleQuery,
  useGetStudyMembersQuery,
  useGetRecruitmentsQuery,
  useCreateRecruitmentMutation,
  useUpdateRecruitmentMutation,
  useDeleteRecruitmentMutation,
  useApplyStudyMutation,
  useGetApplicationsQuery,
  useApproveApplicationMutation,
  useRejectApplicationMutation,
  useKickMemberMutation,
  useAddToBlacklistMutation,
  useGetBlacklistUsersQuery,
  useRemoveFromBlacklistMutation,
  useAssignAdminMutation,
  useGetSchedulesQuery,
  useCreateScheduleMutation,
  useUpdateScheduleMutation,
  useDeleteScheduleMutation,
  useGetNoticesQuery,
  useCreateNoticeMutation,
  useReportUserMutation
} = studyGroupApi;
