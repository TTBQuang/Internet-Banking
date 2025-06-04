import { createSlice, createAsyncThunk } from "@reduxjs/toolkit";
import apiClient from "../services/apiClient";

// Async thunk for fetching notifications
export const fetchNotifications = createAsyncThunk(
  "notifications/fetchNotifications",
  async (_, { rejectWithValue }) => {
    try {
      const response = await apiClient.get("/notifications");
      return response.data;
    } catch (error) {
      return rejectWithValue(error.message);
    }
  }
);

// Async thunk for marking a notification as read
export const markNotificationAsRead = createAsyncThunk(
  "notifications/markAsRead",
  async (notificationId, { dispatch }) => {
    // Update state immediately
    dispatch(markAsRead(notificationId));
    
    try {
      // Call API in background
      await apiClient.put(`/notifications/${notificationId}/read`);
    } catch (error) {
      // Ignore error as per requirement
      console.log("Error marking notification as read:", error);
    }
    return notificationId;
  }
);

// Async thunk for marking all notifications as read
export const markAllNotificationsAsRead = createAsyncThunk(
  "notifications/markAllAsRead",
  async (_, { dispatch }) => {
    // Update state immediately
    dispatch(markAllAsRead());
    
    try {
      // Call API in background
      await apiClient.put("/notifications/read-all");
    } catch (error) {
      // Ignore error as per requirement
      console.log("Error marking all notifications as read:", error);
    }
  }
);

const notificationsSlice = createSlice({
  name: "notifications",
  initialState: {
    items: [],
    loading: false,
    error: null,
  },
  reducers: {
    markAsRead: (state, action) => {
      const notification = state.items.find(
        (n) => n.notificationId === action.payload
      );
      if (notification) {
        notification.read = true;
      }
    },
    markAllAsRead: (state) => {
      state.items = state.items.map((notification) => ({
        ...notification,
        read: true,
      }));
    }
  },
  extraReducers: (builder) => {
    builder
      // Fetch notifications
      .addCase(fetchNotifications.pending, (state) => {
        state.loading = true;
        state.error = null;
      })
      .addCase(fetchNotifications.fulfilled, (state, action) => {
        state.loading = false;
        state.items = action.payload;
      })
      .addCase(fetchNotifications.rejected, (state, action) => {
        state.loading = false;
        state.error = action.payload;
      })
  },
});

export const { markAsRead, markAllAsRead } = notificationsSlice.actions;
export default notificationsSlice.reducer; 