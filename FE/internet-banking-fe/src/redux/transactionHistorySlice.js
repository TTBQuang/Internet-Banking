import { createAsyncThunk, createSlice } from '@reduxjs/toolkit';
import apiClient from "../services/apiClient";

// Lấy accountNumber từ userId
export const fetchAccountNumber = createAsyncThunk(
  'transactionHistory/fetchAccountNumber',
  async (userId, { rejectWithValue }) => {
    try {
      const response = await apiClient.get(`/account/account-number?userId=${userId}`);
      return response.data; // Response trả về { "data": "ACCBEC89A2638" }
    } catch (err) {
      console.error('Lỗi khi lấy accountNumber:', err.message);
      return rejectWithValue(err.message || 'Không thể lấy số tài khoản');
    }
  }
);

// Lấy lịch sử giao dịch từ accountNumber
export const fetchTransactionHistory = createAsyncThunk(
  'transactionHistory/fetchTransactionHistory',
  async (accountNumber, { rejectWithValue }) => {
    try {
      const response = await apiClient.get(`/transactions/histories?accountNumber=${accountNumber}`);
      return response.data; // Response trả về { "data": [ { transactionId, ... } ] }
    } catch (err) {
      console.error('Lỗi khi lấy lịch sử giao dịch:', err.message);
      return rejectWithValue(err.message || 'Không thể lấy lịch sử giao dịch');
    }
  }
);

const transactionHistorySlice = createSlice({
  name: 'transactionHistory',
  initialState: {
    accountNumber: null,
    transactions: [],
    loading: false,
    error: null,
  },
  reducers: {
    resetTransactionHistory: (state) => {
      state.transactions = [];
      state.accountNumber = null;
      state.error = null;
    },
  },
  extraReducers: (builder) => {
    builder
      // Xử lý fetchAccountNumber
      .addCase(fetchAccountNumber.pending, (state) => {
        state.loading = true;
        state.error = null;
      })
      .addCase(fetchAccountNumber.fulfilled, (state, action) => {
        state.loading = false;
        state.accountNumber = action.payload; // Lưu accountNumber
      })
      .addCase(fetchAccountNumber.rejected, (state, action) => {
        state.loading = false;
        state.error = action.payload;
      })
      // Xử lý fetchTransactionHistory
      .addCase(fetchTransactionHistory.pending, (state) => {
        state.loading = true;
        state.error = null;
      })
      .addCase(fetchTransactionHistory.fulfilled, (state, action) => {
        state.loading = false;
        state.transactions = action.payload; // Lưu danh sách giao dịch
      })
      .addCase(fetchTransactionHistory.rejected, (state, action) => {
        state.loading = false;
        state.error = action.payload;
      });
  },
});

export const { resetTransactionHistory } = transactionHistorySlice.actions;
export default transactionHistorySlice.reducer;