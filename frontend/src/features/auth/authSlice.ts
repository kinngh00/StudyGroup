import { createSlice, type PayloadAction } from "@reduxjs/toolkit";
import type { User } from "@/types/domain";

interface AuthState {
  user: User | null;
  isAuthenticated: boolean;
  token: string | null;
  refreshToken: string | null;
}

const initialToken = localStorage.getItem("accessToken");
const initialRefreshToken = localStorage.getItem("refreshToken");

const initialState: AuthState = {
  user: null,
  isAuthenticated: Boolean(initialToken),
  token: initialToken,
  refreshToken: initialRefreshToken
};

const authSlice = createSlice({
  name: "auth",
  initialState,
  reducers: {
    setCredentials: (state, action: PayloadAction<{ user: User; token: string; refreshToken?: string }>) => {
      state.user = action.payload.user;
      state.token = action.payload.token;
      state.isAuthenticated = true;
      localStorage.setItem("accessToken", action.payload.token);

      if (action.payload.refreshToken) {
        state.refreshToken = action.payload.refreshToken;
        localStorage.setItem("refreshToken", action.payload.refreshToken);
      }
    },
    logout: (state) => {
      state.user = null;
      state.token = null;
      state.refreshToken = null;
      state.isAuthenticated = false;
      localStorage.removeItem("accessToken");
      localStorage.removeItem("refreshToken");
    }
  }
});

export const { setCredentials, logout } = authSlice.actions;
export const authReducer = authSlice.reducer;
