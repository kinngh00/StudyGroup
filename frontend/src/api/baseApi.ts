import { createApi, type BaseQueryFn } from "@reduxjs/toolkit/query/react";
import type { AxiosError, AxiosRequestConfig } from "axios";
import apiClient from "@/api/axios";
import type { Application, BlacklistUser, Recruitment, Study, StudyMember, StudyNotice, StudyPermission, StudySchedule, UserRole } from "@/types/domain";

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
      const payload = result.data as {
        success?: boolean;
        status?: number;
        code?: string;
        message?: string;
        data?: unknown;
        error?: { code?: string; message?: string };
      };

      // Support both wrapped responses({success,data,error}) and raw payload responses.
      if (payload && typeof payload === "object" && "success" in payload) {
        if (payload.success === false) {
          return {
            error: {
              status: result.status,
              data: payload.error,
              message: payload.error?.message ?? "요청 처리에 실패했습니다."
            }
          };
        }
        return { data: payload.data };
      }

      // Support backend wrapper response({status,code,message,data})
      if (payload && typeof payload === "object" && "code" in payload && "data" in payload) {
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

export const studyGroupApi = createApi({
  reducerPath: "studyGroupApi",
  baseQuery: axiosBaseQuery(),
  tagTypes: ["Study", "Recruitment", "Application", "Member", "Schedule", "Role", "Blacklist", "Notice"],
  endpoints: (builder) => ({
    signup: builder.mutation<{ email: string; name: string }, { name: string; email: string; password: string }>({
      query: (body) => ({ url: "/api/users/signup", method: "POST", data: body })
    }),
    login: builder.mutation<{ email: string; name: string; accessToken: string }, { email: string; password: string }>({
      query: (body) => ({ url: "/api/users/login", method: "POST", data: body })
    }),
    getStudies: builder.query<Study[], { name?: string; finished?: boolean }>({
      query: (params) => ({ url: "/api/studies", method: "GET", params }),
      providesTags: ["Study"]
    }),
    getStudy: builder.query<Study, number>({
      query: (studyId) => ({ url: `/api/studies/${studyId}`, method: "GET" }),
      providesTags: (_r, _e, id) => [{ type: "Study", id }]
    }),
    createStudy: builder.mutation<Study, Partial<Study>>({
      query: (body) => ({ url: "/api/studies", method: "POST", data: body }),
      invalidatesTags: ["Study"]
    }),
    updateStudy: builder.mutation<Study, { studyId: number; body: Partial<Study> }>({
      query: ({ studyId, body }) => ({ url: `/api/studies/${studyId}`, method: "PUT", data: body }),
      invalidatesTags: (_r, _e, { studyId }) => [{ type: "Study", id: studyId }]
    }),
    getUserRole: builder.query<{ role: UserRole }, number>({
      query: (studyId) => ({ url: `/api/studies/${studyId}/role`, method: "GET" }),
      providesTags: ["Role"]
    }),
    getRecruitments: builder.query<Recruitment[], { studyId?: number; keyword?: string }>({
      query: (params) => ({ url: "/api/recruitment", method: "GET", params }),
      providesTags: ["Recruitment"]
    }),
    createRecruitment: builder.mutation<Recruitment, { studyId: number; title: string; content: string; deadline: string }>({
      query: ({ studyId, ...body }) => ({
        url: `/api/studies/${studyId}/recruitment`,
        method: "POST",
        data: body
      }),
      invalidatesTags: ["Recruitment"]
    }),
    updateRecruitment: builder.mutation<Recruitment, { studyId: number; recruitmentId: number; title: string; content: string; deadline: string; status?: "OPEN" | "CLOSED" }>({
      query: ({ studyId, recruitmentId, ...body }) => ({
        url: `/api/studies/${studyId}/recruitment/${recruitmentId}`,
        method: "PUT",
        data: body
      }),
      invalidatesTags: ["Recruitment"]
    }),
    deleteRecruitment: builder.mutation<void, { studyId: number; recruitmentId: number }>({
      query: ({ studyId, recruitmentId }) => ({
        url: `/api/studies/${studyId}/recruitment/${recruitmentId}`,
        method: "DELETE"
      }),
      invalidatesTags: ["Recruitment"]
    }),
    applyStudy: builder.mutation<void, { studyId: number; message?: string }>({
      query: ({ studyId, ...body }) => ({ url: `/api/studies/${studyId}/apply`, method: "POST", data: body }),
      invalidatesTags: ["Application"]
    }),
    getApplications: builder.query<Application[], number>({
      query: (studyId) => ({ url: `/api/studies/${studyId}/applications`, method: "GET" }),
      providesTags: ["Application"]
    }),
    processApplication: builder.mutation<void, { appId: number; approved: boolean }>({
      query: ({ appId, approved }) => ({ url: `/api/applications/${appId}`, method: "PUT", data: { approved } }),
      invalidatesTags: ["Application", "Member"]
    }),
    getStudyMembers: builder.query<StudyMember[], number>({
      query: (studyId) => ({ url: `/api/studies/${studyId}/members`, method: "GET" }),
      providesTags: ["Member"]
    }),
    kickMember: builder.mutation<void, { studyId: number; memberId: number }>({
      query: ({ studyId, memberId }) => ({
        url: `/api/studies/${studyId}/members/${memberId}`,
        method: "DELETE"
      }),
      invalidatesTags: ["Member"]
    }),
    addToBlacklist: builder.mutation<void, { studyId: number; memberId: number }>({
      query: ({ studyId, ...body }) => ({ url: `/api/studies/${studyId}/blacklist`, method: "POST", data: body }),
      invalidatesTags: ["Blacklist"]
    }),
    getBlacklistUsers: builder.query<BlacklistUser[], number>({
      query: (studyId) => ({ url: `/api/studies/${studyId}/blacklist`, method: "GET" }),
      providesTags: ["Blacklist"]
    }),
    removeFromBlacklist: builder.mutation<void, { studyId: number; userId: number }>({
      query: ({ studyId, userId }) => ({ url: `/api/studies/${studyId}/blacklist/${userId}`, method: "DELETE" }),
      invalidatesTags: ["Blacklist"]
    }),
    assignAdmin: builder.mutation<void, { studyId: number; memberId: number; permissions?: StudyPermission[] }>({
      query: ({ studyId, ...body }) => ({ url: `/api/studies/${studyId}/admins`, method: "POST", data: body }),
      invalidatesTags: ["Member"]
    }),
    getSchedules: builder.query<StudySchedule[], number>({
      query: (studyId) => ({ url: `/api/studies/${studyId}/schedules`, method: "GET" }),
      providesTags: ["Schedule"]
    }),
    createSchedule: builder.mutation<StudySchedule, { studyId: number; title: string; dateTime: string; description?: string }>({
      query: ({ studyId, ...body }) => ({ url: `/api/studies/${studyId}/schedules`, method: "POST", data: body }),
      invalidatesTags: ["Schedule"]
    }),
    updateSchedule: builder.mutation<StudySchedule, { studyId: number; scheduleId: number; title: string; dateTime: string; description?: string }>({
      query: ({ studyId, scheduleId, ...body }) => ({
        url: `/api/studies/${studyId}/schedules/${scheduleId}`,
        method: "PUT",
        data: body
      }),
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
      providesTags: ["Notice"]
    }),
    createNotice: builder.mutation<StudyNotice, { studyId: number; title: string; content: string }>({
      query: ({ studyId, ...body }) => ({ url: `/api/studies/${studyId}/notices`, method: "POST", data: body }),
      invalidatesTags: ["Notice"]
    }),
    reportUser: builder.mutation<void, { studyId: number; targetUserId: number; reason: string }>({
      query: ({ studyId, ...body }) => ({ url: `/api/studies/${studyId}/reports`, method: "POST", data: body })
    })
  })
});

export const {
  useSignupMutation,
  useLoginMutation,
  useGetStudiesQuery,
  useGetStudyQuery,
  useCreateStudyMutation,
  useUpdateStudyMutation,
  useGetUserRoleQuery,
  useGetRecruitmentsQuery,
  useCreateRecruitmentMutation,
  useUpdateRecruitmentMutation,
  useDeleteRecruitmentMutation,
  useApplyStudyMutation,
  useGetApplicationsQuery,
  useProcessApplicationMutation,
  useGetStudyMembersQuery,
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
