import { createAsyncThunk, createSlice } from '@reduxjs/toolkit';
import apiClient from '@/services/apiClient';

export const fetchCurrentUserTransferTransactions = createAsyncThunk(
  'transactions/fetchCurrentUserTransferTransactions',
  async ({ page = 1, size = 10 }) => {
    const response = await apiClient.get(
      `/transactions/transfer/me?page=${page - 1}&size=${size}`
    );
    return response.data;
  }
);

export const fetchCurrentUserReceivedTransactions = createAsyncThunk(
  'transactions/fetchCurrentUserReceivedTransactions',
  async ({ page = 1, size = 10 }) => {
    const response = await apiClient.get(
      `/transactions/received/me?page=${page - 1}&size=${size}`
    );
    return response.data;
  }
);

export const fetchCurrentUserDebtPaymentTransactions = createAsyncThunk(
  'transactions/fetchCurrentUserDebtPaymentTransactions',
  async ({ page = 1, size = 10 }) => {
    const response = await apiClient.get(
      `/transactions/debt-payment/me?page=${page - 1}&size=${size}`
    );
    return response.data;
  }
);

const initialState = {
  transactions: [],
  currentPage: 1,
  totalPages: 0,
  totalElements: 0,
  pageSize: 10,
  loading: false,
  error: null,
};

const transactionsSlice = createSlice({
  name: 'transactions',
  initialState,
  reducers: {
    setPage: (state, action) => {
      state.currentPage = action.payload;
    },
  },
  extraReducers: (builder) => {
    builder
      // Fetch current user transfer transactions
      .addCase(fetchCurrentUserTransferTransactions.pending, (state) => {
        state.loading = true;
        state.error = null;
      })
      .addCase(
        fetchCurrentUserTransferTransactions.fulfilled,
        (state, action) => {
          state.loading = false;
          state.transactions = action.payload.content;
          state.currentPage = action.payload.page.number + 1;
          state.totalPages = action.payload.page.totalPages;
          state.totalElements = action.payload.page.totalElements;
          state.pageSize = action.payload.page.size;
        }
      )
      .addCase(
        fetchCurrentUserTransferTransactions.rejected,
        (state, action) => {
          state.loading = false;
          state.error = action.error.message;
        }
      )
      // Fetch current user received transactions
      .addCase(fetchCurrentUserReceivedTransactions.pending, (state) => {
        state.loading = true;
        state.error = null;
      })
      .addCase(
        fetchCurrentUserReceivedTransactions.fulfilled,
        (state, action) => {
          state.loading = false;
          state.transactions = action.payload.content;
          state.currentPage = action.payload.page.number + 1;
          state.totalPages = action.payload.page.totalPages;
          state.totalElements = action.payload.page.totalElements;
          state.pageSize = action.payload.page.size;
        }
      )
      .addCase(
        fetchCurrentUserReceivedTransactions.rejected,
        (state, action) => {
          state.loading = false;
          state.error = action.error.message;
        }
      )
      // Fetch current user debt payment transactions
      .addCase(fetchCurrentUserDebtPaymentTransactions.pending, (state) => {
        state.loading = true;
        state.error = null;
      })
      .addCase(
        fetchCurrentUserDebtPaymentTransactions.fulfilled,
        (state, action) => {
          state.loading = false;
          state.transactions = action.payload.content;
          state.currentPage = action.payload.page.number + 1;
          state.totalPages = action.payload.page.totalPages;
          state.totalElements = action.payload.page.totalElements;
          state.pageSize = action.payload.page.size;
        }
      )
      .addCase(
        fetchCurrentUserDebtPaymentTransactions.rejected,
        (state, action) => {
          state.loading = false;
          state.error = action.error.message;
        }
      );
  },
});

export const { setPage } = transactionsSlice.actions;
export default transactionsSlice.reducer;
