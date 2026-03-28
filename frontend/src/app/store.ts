import { configureStore } from "@reduxjs/toolkit";
import { authReducer } from "@/features/auth/authSlice";
import { studyReducer } from "@/features/study/studySlice";
import { uiReducer } from "@/features/ui/uiSlice";
import { studyGroupApi } from "@/api/baseApi";

export const store = configureStore({
  reducer: {
    auth: authReducer,
    study: studyReducer,
    ui: uiReducer,
    [studyGroupApi.reducerPath]: studyGroupApi.reducer
  },
  middleware: (getDefaultMiddleware) => getDefaultMiddleware().concat(studyGroupApi.middleware)
});

export type RootState = ReturnType<typeof store.getState>;
export type AppDispatch = typeof store.dispatch;
