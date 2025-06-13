import { createSlice, createAsyncThunk } from '@reduxjs/toolkit';
import apiClient from '../services/apiClient';

export const fetchAccount = createAsyncThunk(
  'account/fetchAccount',
  async (_, { rejectWithValue }) => {
    try {
      const response = await apiClient.get('/account');
      return response.data;
    } catch (error) {
      return rejectWithValue(error.message);
    }
  }
);

export const fetchAccountByAccountNumber = createAsyncThunk(
  'account/fetchAccountByAccountNumber',
  async (accountNumber, { rejectWithValue }) => {
    try {
      const response = await apiClient.get(`/account/${accountNumber}`);
      return response.data;
    } catch (error) {
      return rejectWithValue(error.message);
    }
  }
);

export const fetchAccountNumberByUserId = createAsyncThunk(
  'account/fetchAccountNumberByUserId',
  async (userId, { rejectWithValue }) => {
    try {
      const response = await apiClient.get(
        `/account/account-number?userId=${userId}`
      );
      return response.data;
    } catch (error) {
      return rejectWithValue(error.message);
    }
  }
);

const accountSlice = createSlice({
  name: 'account',
  initialState: {
    account: null,
    loading: false,
    error: null,
  },
  reducers: {},
  extraReducers: (builder) => {
    builder
      .addCase(fetchAccount.pending, (state) => {
        state.loading = true;
        state.error = null;
      })
      .addCase(fetchAccount.fulfilled, (state, action) => {
        state.loading = false;
        state.account = action.payload;
      })
      .addCase(fetchAccount.rejected, (state, action) => {
        state.loading = false;
        state.error = action.payload;
      })
      .addCase(fetchAccountByAccountNumber.pending, (state) => {
        state.loading = true;
        state.error = null;
      })
      .addCase(fetchAccountByAccountNumber.fulfilled, (state, action) => {
        state.loading = false;
        state.account = action.payload;
      })
      .addCase(fetchAccountByAccountNumber.rejected, (state, action) => {
        state.loading = false;
        state.error = action.payload;
      })
      .addCase(fetchAccountNumberByUserId.pending, (state) => {
        state.loading = true;
        state.error = null;
      })
      .addCase(fetchAccountNumberByUserId.fulfilled, (state) => {
        state.loading = false;
      })
      .addCase(fetchAccountNumberByUserId.rejected, (state, action) => {
        state.loading = false;
        state.error = action.payload;
      });
  },
});

export default accountSlice.reducer;
