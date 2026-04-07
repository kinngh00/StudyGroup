import { createSlice, type PayloadAction } from "@reduxjs/toolkit";
import type { Study, UserRole } from "@/types/domain";

interface StudyState {
  currentStudy: Study | null;
  userRoleInCurrentStudy: UserRole;
}

const initialState: StudyState = {
  currentStudy: null,
  userRoleInCurrentStudy: "NONE"
};

const studySlice = createSlice({
  name: "study",
  initialState,
  reducers: {
    setCurrentStudy: (state, action: PayloadAction<Study | null>) => {
      state.currentStudy = action.payload;
    },
    setUserRoleInCurrentStudy: (state, action: PayloadAction<UserRole>) => {
      state.userRoleInCurrentStudy = action.payload;
    }
  }
});

export const { setCurrentStudy, setUserRoleInCurrentStudy } = studySlice.actions;
export const studyReducer = studySlice.reducer;
