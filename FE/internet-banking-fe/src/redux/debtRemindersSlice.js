import { createSlice, createAsyncThunk } from '@reduxjs/toolkit';
import apiClient from '../services/apiClient';

export const fetchDebtReminders = createAsyncThunk(
  'debtReminders/fetchDebtReminders',
  async ({ page = 1, size = 10, query }) => {
    let url = `/debt-reminders?page=${page - 1}&size=${size}`;
    if (query) {
      url += `&query=${query}`;
    }
    const response = await apiClient.get(url);
    return response.data;
  }
);

export const fetchSentDebtReminders = createAsyncThunk(
  'debtReminders/fetchSentDebtReminders',
  async ({ page = 1, size = 10, query }) => {
    let url = `/debt-reminders/sent?page=${page - 1}&size=${size}`;
    if (query) {
      url += `&query=${query}`;
    }
    const response = await apiClient.get(url);
    return response.data;
  }
);

export const fetchReceivedDebtReminders = createAsyncThunk(
  'debtReminders/fetchReceivedDebtReminders',
  async ({ page = 1, size = 10, query }) => {
    let url = `/debt-reminders/received?page=${page - 1}&size=${size}`;
    if (query) {
      url += `&query=${query}`;
    }
    const response = await apiClient.get(url);
    return response.data;
  }
);

export const createDebtReminder = createAsyncThunk(
  'debtReminders/createDebtReminder',
  async (data) => {
    const response = await apiClient.post('/debt-reminders', data);
    return response.data;
  }
);

export const deleteDebtReminder = createAsyncThunk(
  'debtReminders/deleteDebtReminder',
  async ({ debtReminderId, content }) => {
    const response = await apiClient.delete(
      `/debt-reminders/${debtReminderId}`,
      {
        data: {
          content,
        },
      }
    );
    return response.data;
  }
);

export const deleteReceivedDebtReminder = createAsyncThunk(
  'debtReminders/deleteReceivedDebtReminder',
  async ({ debtReminderId, content }) => {
    const response = await apiClient.delete(
      `/debt-reminders/received/${debtReminderId}`,
      {
        data: {
          content,
        },
      }
    );
    return response.data;
  }
);

const initialState = {
  debtReminders: [],
  currentPage: 1,
  totalPages: 0,
  totalElements: 0,
  pageSize: 10,
  loading: false,
  error: null,
};

const debtRemindersSlice = createSlice({
  name: 'debtReminders',
  initialState,
  reducers: {
    setPage: (state, action) => {
      state.currentPage = action.payload;
    },
    clearError: (state) => {
      state.error = null;
    },
  },
  extraReducers: (builder) => {
    builder
      // Fetch debt reminders
      .addCase(fetchDebtReminders.pending, (state) => {
        state.loading = true;
        state.error = null;
      })
      .addCase(fetchDebtReminders.fulfilled, (state, action) => {
        state.loading = false;
        state.debtReminders = action.payload.content;
        state.currentPage = action.payload.page.number + 1;
        state.totalPages = action.payload.page.totalPages;
        state.totalElements = action.payload.page.totalElements;
        state.pageSize = action.payload.page.size;
      })
      .addCase(fetchDebtReminders.rejected, (state, action) => {
        state.loading = false;
        state.error = action.error.message;
      })
      // Fetch sent debt reminders
      .addCase(fetchSentDebtReminders.pending, (state) => {
        state.loading = true;
        state.error = null;
      })
      .addCase(fetchSentDebtReminders.fulfilled, (state, action) => {
        state.loading = false;
        state.debtReminders = action.payload.content;
        state.currentPage = action.payload.page.number + 1;
        state.totalPages = action.payload.page.totalPages;
        state.totalElements = action.payload.page.totalElements;
        state.pageSize = action.payload.page.size;
      })
      .addCase(fetchSentDebtReminders.rejected, (state, action) => {
        state.loading = false;
        state.error = action.error.message;
      })
      // Fetch received debt reminders
      .addCase(fetchReceivedDebtReminders.pending, (state) => {
        state.loading = true;
        state.error = null;
      })
      .addCase(fetchReceivedDebtReminders.fulfilled, (state, action) => {
        state.loading = false;
        state.debtReminders = action.payload.content;
        state.currentPage = action.payload.page.number + 1;
        state.totalPages = action.payload.page.totalPages;
        state.totalElements = action.payload.page.totalElements;
        state.pageSize = action.payload.page.size;
      })
      .addCase(fetchReceivedDebtReminders.rejected, (state, action) => {
        state.loading = false;
        state.error = action.error.message;
      })
      // Create debt reminder
      .addCase(createDebtReminder.pending, (state) => {
        1;
        state.loading = true;
        state.error = null;
      })
      .addCase(createDebtReminder.fulfilled, (state) => {
        state.loading = false;
      })
      .addCase(createDebtReminder.rejected, (state, action) => {
        state.loading = false;
        state.error = action.error.message;
      })
      // Delete debt reminder
      .addCase(deleteDebtReminder.pending, (state) => {
        state.loading = true;
        state.error = null;
      })
      .addCase(deleteDebtReminder.fulfilled, (state) => {
        state.loading = false;
      })
      .addCase(deleteDebtReminder.rejected, (state, action) => {
        state.loading = false;
        state.error = action.error.message;
      })
      // Delete received debt reminder
      .addCase(deleteReceivedDebtReminder.pending, (state) => {
        state.loading = true;
        state.error = null;
      })
      .addCase(deleteReceivedDebtReminder.fulfilled, (state) => {
        state.loading = false;
      })
      .addCase(deleteReceivedDebtReminder.rejected, (state, action) => {
        state.loading = false;
        state.error = action.error.message;
      });
  },
});

export const { setPage, clearError } = debtRemindersSlice.actions;
export default debtRemindersSlice.reducer;
