// linkedBankSlice.js
import { createAsyncThunk, createSlice } from '@reduxjs/toolkit';
import apiClient from "../services/apiClient";

// Send fetch all link banks request
export const fetchAllLinkedBanks = createAsyncThunk(
  'linked-banks',
  async (_, { rejectWithValue }) => {
    try {
      const response = await apiClient.get('/api/linked-banks');
      return response.data;
    } catch (err) {
      console.log(err.message);
      return rejectWithValue(err.message || 'Fetch linked banks failed');
    }
  }
);

// Send fetch account info request
export const fetchAccountInfo = createAsyncThunk(
  'linked-banks/account-info',
  async ({selectedBankCode, accountNumber}, { rejectWithValue }) => {
    try {
      const url = `/api/linked-banks/account-info?bankCode=${selectedBankCode}&accountNumber=${accountNumber}`;
      const response = await apiClient.get(url);
      return response.data;
    } catch (err) {
      return rejectWithValue(err.message || 'Fetch account info failed');
    }
  }
);


const linkedBankSlice = createSlice({
  name: 'linkedBanks',
  initialState: {
    linkedBanks: [],
    account: null,
    loading: false,
    error: null,
  },
  reducers: {},
  extraReducers: (builder) => {
    builder
      .addCase(fetchAllLinkedBanks.pending, (state) => {
        state.loading = true;
        state.error = null;
      })
      .addCase(fetchAllLinkedBanks.fulfilled, (state, action) => {
        state.loading = false;
        state.linkedBanks = action.payload;
      })
      .addCase(fetchAllLinkedBanks.rejected, (state, action) => {
        state.loading = false;
        state.error = action.error.message;
      })
      .addCase(fetchAccountInfo.pending, (state) => {
        state.loading = true;
        state.error = null;
      })
      .addCase(fetchAccountInfo.fulfilled, (state, action) => {
        state.loading = false;
        state.account = action.payload;
      })
      .addCase(fetchAccountInfo.rejected, (state, action) => {
        state.loading = false;
        state.error = action.error.message;
      });;
  },
});

export default linkedBankSlice.reducer;
