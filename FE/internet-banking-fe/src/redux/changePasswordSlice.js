import { createSlice, createAsyncThunk } from "@reduxjs/toolkit";
import apiClient from "../services/apiClient";

export const changePassword = createAsyncThunk(
  "changePassword/changePassword",
  async ({ oldPassword, newPassword }, { rejectWithValue }) => {
    try {
      const res = await apiClient.post("/auth/change-password", {
        oldPassword,
        newPassword,
      });
      return res.message || "Password changed successfully";
    } catch (err) {
      return rejectWithValue(err.message || "Failed to change password");
    }
  }
);

const changePasswordSlice = createSlice({
  name: "changePassword",
  initialState: {
    loading: false,
    error: null,
    success: null,
  },
  reducers: {
    resetChangePasswordState: (state) => {
      state.loading = false;
      state.error = null;
      state.success = null;
    },
  },
  extraReducers: (builder) => {
    builder
      .addCase(changePassword.pending, (state) => {
        state.loading = true;
        state.error = null;
        state.success = null;
      })
      .addCase(changePassword.fulfilled, (state, action) => {
        state.loading = false;
        state.success = action.payload;
      })
      .addCase(changePassword.rejected, (state, action) => {
        state.loading = false;
        state.error = action.payload;
      });
  },
});

export const { resetChangePasswordState } = changePasswordSlice.actions;
export default changePasswordSlice.reducer;
