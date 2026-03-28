import { createSlice, type PayloadAction } from "@reduxjs/toolkit";

interface ModalState {
  type: string | null;
  data?: unknown;
}

interface UiState {
  sidebarOpen: boolean;
  modal: ModalState;
}

const initialState: UiState = {
  sidebarOpen: true,
  modal: { type: null }
};

const uiSlice = createSlice({
  name: "ui",
  initialState,
  reducers: {
    setSidebarOpen: (state, action: PayloadAction<boolean>) => {
      state.sidebarOpen = action.payload;
    },
    openModal: (state, action: PayloadAction<ModalState>) => {
      state.modal = action.payload;
    },
    closeModal: (state) => {
      state.modal = { type: null };
    }
  }
});

export const { setSidebarOpen, openModal, closeModal } = uiSlice.actions;
export const uiReducer = uiSlice.reducer;
