import axios from "axios";

const baseURL = import.meta.env.VITE_API_BASE_URL ?? "http://localhost:8080";

const apiClient = axios.create({
  baseURL,
  withCredentials: true
});

apiClient.interceptors.request.use((config) => {
  const token = localStorage.getItem("accessToken");
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

apiClient.interceptors.response.use(
  (response) => response,
  async (error) => {
    const originalRequest = error.config as (typeof error.config & { _retry?: boolean }) | undefined;
    if (error.response?.status !== 401 || originalRequest?._retry) {
      return Promise.reject(error);
    }

    const refreshToken = localStorage.getItem("refreshToken");
    if (!refreshToken) {
      return Promise.reject(error);
    }

    originalRequest._retry = true;

    try {
      const refreshResponse = await axios.post(
        `${baseURL}/api/auth/local/reissue`,
        { refreshToken },
        { withCredentials: true }
      );

      const reissueData = refreshResponse.data?.data as
        | { accessToken: string; refreshToken: string }
        | undefined;

      if (!reissueData?.accessToken || !reissueData.refreshToken) {
        throw new Error("Reissue response is invalid");
      }

      localStorage.setItem("accessToken", reissueData.accessToken);
      localStorage.setItem("refreshToken", reissueData.refreshToken);

      if (!originalRequest) {
        throw new Error("Original request is missing");
      }
      originalRequest.headers = originalRequest.headers ?? {};
      originalRequest.headers.Authorization = `Bearer ${reissueData.accessToken}`;
      return apiClient(originalRequest);
    } catch (reissueError) {
      localStorage.removeItem("accessToken");
      localStorage.removeItem("refreshToken");
      return Promise.reject(reissueError);
    }
  }
);

export default apiClient;
