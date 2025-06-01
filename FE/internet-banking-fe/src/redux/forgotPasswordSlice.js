import { createSlice, createAsyncThunk } from "@reduxjs/toolkit";
import apiClient from "../services/apiClient";

// Gửi email để nhận OTP
export const sendResetEmail = createAsyncThunk(
  "forgotPassword/sendResetEmail",
  async (email, { rejectWithValue }) => {
    try {
      const res = await apiClient.post("/auth/password-reset/initiate", {
        email,
      });
      // API trả về userId trong res.data.data.userId (giả định)
      return res.data || null;
    } catch (err) {
      return rejectWithValue(err.message);
    }
  }
);

// Xác thực OTP và đổi mật khẩu
export const verifyOtp = createAsyncThunk(
  "forgotPassword/verifyOtp",
  async ({ userId, otp, newPassword }, { rejectWithValue }) => {
    try {
      const res = await apiClient.post("/auth/password-reset/verify", {
        userId,
        otp,
        newPassword,
      });
      return res;
    } catch (err) {
      return rejectWithValue(err.message);
    }
  }
);

const forgotPasswordSlice = createSlice({
  name: "forgotPassword",
  initialState: {
    loading: false,
    error: null,
    success: null,
    userId: null,
  },
  reducers: {
    resetForgotPasswordState: (state) => {
      state.loading = false;
      state.error = null;
      state.success = null;
      state.userId = null;
    },
    setError: (state, action) => {
      state.error = action.payload;
    },
  },
  extraReducers: (builder) => {
    builder
      // sendResetEmail
      .addCase(sendResetEmail.pending, (state) => {
        state.loading = true;
        state.error = null;
        state.success = null;
      })
      .addCase(sendResetEmail.fulfilled, (state, action) => {
        state.loading = false;
        state.userId = action.payload;
        state.success = "A verification code has been sent to your email";
      })
      .addCase(sendResetEmail.rejected, (state, action) => {
        state.loading = false;
        state.error = action.payload || "Failed to send verification code";
      })
      // verifyOtp
      .addCase(verifyOtp.pending, (state) => {
        state.loading = true;
        state.error = null;
        state.success = null;
      })
      .addCase(verifyOtp.fulfilled, (state) => {
        state.loading = false;
        state.success = "Password has been reset successfully";
      })
      .addCase(verifyOtp.rejected, (state, action) => {
        state.loading = false;
        state.error = action.payload || "Failed to reset password";
      });
  },
});

export const { resetForgotPasswordState, setError } =
  forgotPasswordSlice.actions;
export default forgotPasswordSlice.reducer;
