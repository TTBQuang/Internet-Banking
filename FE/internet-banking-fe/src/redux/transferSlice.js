// transferSlice.js
import { createAsyncThunk, createSlice } from '@reduxjs/toolkit';
import apiClient from "../services/apiClient";

// Send internal transfer request
export const initiateInternalTransfer = createAsyncThunk(
  'transfer/initiateInternalTransfer',
  async (payload, { rejectWithValue }) => {
    try {
      const response = await apiClient.post('/transactions/internal-transfers', payload);
      return response.data; // UUID from backend
    } catch (err) {
      console.log(err.message);
      return rejectWithValue(err.message || 'Transfer failed');
    }
  }
);

export const confirmInternalTransfer = createAsyncThunk(
  'transaction/confirmInternalTransfer',
  async ({ transactionId, otp }, { rejectWithValue }) => {
    try {
      const response = await apiClient.post(
        `/transactions/internal-transfers/${transactionId}/confirm`,
        { otpCode: otp }
      );
      return response.message;
    } catch (err) {
      console.log(err.message);
      return rejectWithValue(err.message || 'Failed to confirm transaction');
    }
  }
);

const transferSlice = createSlice({
  name: 'transfer',
  initialState: {
    transactionId: null,
    loading: false,
    error: null,
  },
  reducers: {},
  extraReducers: (builder) => {
    builder
      .addCase(initiateInternalTransfer.pending, (state) => {
        state.loading = true;
        state.error = null;
      })
      .addCase(initiateInternalTransfer.fulfilled, (state, action) => {
        state.loading = false;
        state.transactionId = action.payload;
      })
      .addCase(initiateInternalTransfer.rejected, (state, action) => {
        state.loading = false;
        state.error = action.error.message;
      })
      .addCase(confirmInternalTransfer.pending, (state) => {
        state.loading = true;
        state.error = null;
      })
      .addCase(confirmInternalTransfer.fulfilled, (state) => {
        state.loading = false;
      })
      .addCase(confirmInternalTransfer.rejected, (state, action) => {
        state.loading = false;
        state.error = action.error.message;
      });;
  },
});

export default transferSlice.reducer;
