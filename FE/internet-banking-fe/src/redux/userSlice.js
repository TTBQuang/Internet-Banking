import { createSlice, createAsyncThunk } from "@reduxjs/toolkit";
import apiClient from "../services/apiClient";

// Async thunk for login
export const loginUser = createAsyncThunk(
  "user/login",
  async (credentials, { rejectWithValue }) => {
    try {
      const response = await apiClient.post(
        "/auth/login",
        {
          username: credentials.username,
          password: credentials.password,
          recaptchaToken: credentials.recaptchaToken,
        },
        {
          withToken: false,
        }
      );
      if (
        response.data.token &&
        response.data.token.accessToken &&
        response.data.token.refreshToken
      ) {
        localStorage.setItem("accessToken", response.data.token.accessToken);
        localStorage.setItem("refreshToken", response.data.token.refreshToken);
      }
      return response.data;
    } catch (error) {
      return rejectWithValue(error.message);
    }
  }
);

// Async thunk for logout
export const logout = createAsyncThunk("user/logout", async () => {
  try {
    const refreshToken = localStorage.getItem("refreshToken");
    if (refreshToken) {
      await apiClient.post("/auth/logout", { refreshToken });
    }
  } catch (error) {
    console.error("Logout error:", error);
  } finally {
    localStorage.removeItem("accessToken");
    localStorage.removeItem("refreshToken");
  }
  return true;
});

const userSlice = createSlice({
  name: "user",
  initialState: {
    username: null,
    fullName: null,
    email: null,
    phone: null,
    role: null,
    loading: false,
    error: null,
  },
  reducers: {},
  extraReducers: (builder) => {
    builder
      // Login
      .addCase(loginUser.pending, (state) => {
        state.loading = true;
        state.error = null;
      })
      .addCase(loginUser.fulfilled, (state, action) => {
        state.loading = false;
        state.username = action.payload.user.username;
        state.fullName = action.payload.user.fullName;
        state.email = action.payload.user.email;
        state.phone = action.payload.user.phone;
        state.role = action.payload.user.role;
      })
      .addCase(loginUser.rejected, (state, action) => {
        state.loading = false;
        state.error = action.payload;
      })

      // Logout
      .addCase(logout.pending, (state) => {
        state.loading = true;
      })
      .addCase(logout.fulfilled, () => {
        return {
          username: null,
          fullName: null,
          email: null,
          phone: null,
          role: null,
          loading: false,
          error: null,
        };
      })
      .addCase(logout.rejected, (state, action) => {
        state.loading = false;
        state.error = action.payload;
        state.username = null;
        state.fullName = null;
        state.email = null;
        state.phone = null;
        state.role = null;
      });
  },
});

export default userSlice.reducer;
