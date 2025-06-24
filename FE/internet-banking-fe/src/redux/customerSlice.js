import { createSlice, createAsyncThunk } from '@reduxjs/toolkit';
import apiClient from '../services/apiClient';

// Async thunks
export const fetchCustomers = createAsyncThunk(
  'customers/fetchCustomers',
  async ({ page = 0, size = 10 }) => {
    const response = await apiClient.get(`/api/customers?page=${page}&size=${size}`);
    return response.data;
  }
);

export const createCustomer = createAsyncThunk(
  'customers/createCustomer',
  async (customerData) => {
    const response = await apiClient.post('/api/customers/register', customerData);
    return response.data;
  }
);

export const updateCustomer = createAsyncThunk(
  'customers/updateCustomer',
  async ({ userId, customerData }) => {
    const response = await apiClient.put(`/api/customers/${userId}`, customerData);
    return response.data;
  }
);

export const deleteCustomer = createAsyncThunk(
  'customers/deleteCustomer',
  async (userId) => {
    await apiClient.delete(`/api/customers/${userId}`);
    return userId;
  }
);

export const depositMoney = createAsyncThunk(
  'customers/depositMoney',
  async ({ accountNumber, amount }) => {
    const response = await apiClient.post('/api/customers/deposit', { accountNumber, amount });
    return response.data;
  }
);

const customerSlice = createSlice({
  name: 'customers',
  initialState: {
    customers: [],
    totalPages: 0,
    currentPage: 0,
    loading: false,
    error: null,
    depositLoading: false,
    depositMessage: null,
  },
  reducers: {
    clearDepositMessage: (state) => {
      state.depositMessage = null;
    },
    clearError: (state) => {
      state.error = null;
    },
  },
  extraReducers: (builder) => {
    builder
      // Fetch customers
      .addCase(fetchCustomers.pending, (state) => {
        state.loading = true;
        state.error = null;
      })
      .addCase(fetchCustomers.fulfilled, (state, action) => {
        state.loading = false;
        state.customers = action.payload.content;
        state.totalPages = action.payload.totalPages;
        state.currentPage = action.payload.number;
      })
      .addCase(fetchCustomers.rejected, (state, action) => {
        state.loading = false;
        state.error = action.error.message;
      })
      // Create customer
      .addCase(createCustomer.pending, (state) => {
        state.loading = true;
      })
      .addCase(createCustomer.fulfilled, (state) => {
        state.loading = false;
      })
      .addCase(createCustomer.rejected, (state, action) => {
        state.loading = false;
        state.error = action.error.message;
      })
      // Update customer
      .addCase(updateCustomer.pending, (state) => {
        state.loading = true;
      })
      .addCase(updateCustomer.fulfilled, (state) => {
        state.loading = false;
      })
      .addCase(updateCustomer.rejected, (state, action) => {
        state.loading = false;
        state.error = action.error.message;
      })
      // Delete customer
      .addCase(deleteCustomer.fulfilled, (state, action) => {
        state.customers = state.customers.filter(customer => customer.userId !== action.payload);
      })
      // Deposit money
      .addCase(depositMoney.pending, (state) => {
        state.depositLoading = true;
        state.depositMessage = null;
      })
      .addCase(depositMoney.fulfilled, (state) => {
        state.depositLoading = false;
        state.depositMessage = { type: 'success', text: 'Deposit successful!' };
      })
      .addCase(depositMoney.rejected, (state, action) => {
        state.depositLoading = false;
        state.depositMessage = { type: 'error', text: `Deposit failed: ${action.error.message}` };
      });
  },
});

export const { clearDepositMessage, clearError } = customerSlice.actions;
export default customerSlice.reducer;