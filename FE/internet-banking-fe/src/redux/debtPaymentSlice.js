import { createAsyncThunk, createSlice } from '@reduxjs/toolkit';
import apiClient from '@/services/apiClient';

export const initiateDebtPayment = createAsyncThunk(
  'debtPayment/initiateDebtPayment',
  async ({ creditorAccountNumber, content, debtReminderId }) => {
    const response = await apiClient.post('transactions/debt-payment', {
      creditorAccountNumber,
      content,
      debtReminderId,
    });
    return response.data;
  }
);

export const confirmDebtPayment = createAsyncThunk(
  'debtPayment/confirmDebtPayment',
  async ({ transactionId, debtReminderId, otpCode }) => {
    const response = await apiClient.post(
      `transactions/debt-payment/${transactionId}/confirm`,
      {
        debtReminderId,
        otpCode,
      }
    );
    return response.data;
  }
);

const initialState = {
  debtReminder: null,
  transactionId: null,
  loading: false,
  error: null,
};

const debtPaymentSlice = createSlice({
  name: 'debtPayment',
  initialState,
  reducers: {
    setDebtReminder: (state, action) => {
      state.debtReminder = action.payload;
    },
    clearState: (state) => {
      state.debtReminder = null;
      state.transactionId = null;
      state.error = null;
    },
    clearError: (state) => {
      state.error = null;
    },
  },
  extraReducers: (builder) => {
    builder
      .addCase(initiateDebtPayment.pending, (state) => {
        state.loading = true;
        state.error = null;
      })
      .addCase(initiateDebtPayment.fulfilled, (state, action) => {
        state.loading = false;
        state.transactionId = action.payload;
      })
      .addCase(initiateDebtPayment.rejected, (state, action) => {
        state.loading = false;
        state.error = action.error.message;
      });
  },
});

export const { setDebtReminder, clearError, clearState } =
  debtPaymentSlice.actions;
export default debtPaymentSlice.reducer;
