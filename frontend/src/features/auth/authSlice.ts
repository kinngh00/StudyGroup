import { createSlice, type PayloadAction } from "@reduxjs/toolkit";
import type { User } from "@/types/domain";

interface AuthState {
  user: User | null;
  isAuthenticated: boolean;
  token: string | null;
}

const initialToken = localStorage.getItem("accessToken");

const initialState: AuthState = {
  user: null,
  isAuthenticated: Boolean(initialToken),
  token: initialToken
};

const authSlice = createSlice({
  name: "auth",
  initialState,
  reducers: {
    setCredentials: (state, action: PayloadAction<{ user: User; token: string }>) => {
      state.user = action.payload.user;
      state.token = action.payload.token;
      state.isAuthenticated = true;
      localStorage.setItem("accessToken", action.payload.token);
    },
    logout: (state) => {
      state.user = null;
      state.token = null;
      state.isAuthenticated = false;
      localStorage.removeItem("accessToken");
    }
  }
});

export const { setCredentials, logout } = authSlice.actions;
export const authReducer = authSlice.reducer;
