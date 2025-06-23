import { createSlice, createAsyncThunk } from '@reduxjs/toolkit';
import apiClient from '../services/apiClient';

// Async thunks
export const fetchRecipients = createAsyncThunk(
  'recipients/fetchRecipients',
  async ({ page = 1, size = 10, nickname }) => {
    let url = `/recipients?page=${page - 1}&size=${size}`;
    if (nickname) {
      url += `&nickname=${encodeURIComponent(nickname)}`;
    }
    const response = await apiClient.get(url);
    return response.data;
  }
);

export const fetchAllRecipients = createAsyncThunk(
  'recipients/fetchAllRecipients',
  async (searchTerm = "") => {
    const response = await apiClient.get('/recipients/all');
    const allRecipients = response.data || [];

    if (!searchTerm) return allRecipients;

    const lower = searchTerm.toLowerCase();

    const filtered = allRecipients.filter(
      (r) =>
        r.nickname?.toLowerCase().includes(lower) ||
        r.accountNumber?.toLowerCase().includes(searchTerm.toLowerCase())
    );

    console.log(filtered);

    return filtered;
  }
);

export const fetchAllInternalRecipients = createAsyncThunk(
  'recipients/fetchAllInternalRecipients',
  async (nickname) => {
    let url = '/recipients/all/internal';
    if (nickname) {
      url += `?nickname=${encodeURIComponent(nickname)}`;
    }
    const response = await apiClient.get(url);
    return response.data;
  }
);

export const addRecipient = createAsyncThunk(
  'recipients/addRecipient',
  async (recipientData) => {
    const response = await apiClient.post('/recipients', recipientData);
    return response.data;
  }
);

export const updateRecipient = createAsyncThunk(
  'recipients/updateRecipient',
  async ({ id, recipientData }) => {
    const response = await apiClient.put(`/recipients/${id}`, recipientData);
    return response.data;
  }
);

export const deleteRecipient = createAsyncThunk(
  'recipients/deleteRecipient',
  async (id) => {
    const response = await apiClient.delete(`/recipients/${id}`);
    return { id, message: response.message };
  }
);

const initialState = {
  recipients: [],
  currentPage: 1,
  totalPages: 0,
  totalElements: 0,
  pageSize: 10,
  loading: false,
  error: null,
};

const recipientsSlice = createSlice({
  name: 'recipients',
  initialState,
  reducers: {
    clearError: (state) => {
      state.error = null;
    },
    setPage: (state, action) => {
      state.currentPage = action.payload;
    },
  },
  extraReducers: (builder) => {
    builder
      // Fetch recipients
      .addCase(fetchRecipients.pending, (state) => {
        state.loading = true;
        state.error = null;
      })
      .addCase(fetchRecipients.fulfilled, (state, action) => {
        state.loading = false;
        state.recipients = action.payload.content;
        state.currentPage = action.payload.page.number + 1; // Convert to 1-based index
        state.totalPages = action.payload.page.totalPages;
        state.totalElements = action.payload.page.totalElements;
        state.pageSize = action.payload.page.size;
      })
      .addCase(fetchRecipients.rejected, (state, action) => {
        state.loading = false;
        state.error = action.error.message;
      })
      // Fetch all recipients
      .addCase(fetchAllRecipients.pending, (state) => {
        state.loading = true;
        state.error = null;
      })
      .addCase(fetchAllRecipients.fulfilled, (state, action) => {
        state.loading = false;
        state.recipients = action.payload;
      })
      .addCase(fetchAllRecipients.rejected, (state, action) => {
        state.loading = false;
        state.error = action.error.message;
      })
      // Fetch all internal recipients
      .addCase(fetchAllInternalRecipients.pending, (state) => {
        state.loading = true;
        state.error = null;
      })
      .addCase(fetchAllInternalRecipients.fulfilled, (state, action) => {
        state.loading = false;
        state.recipients = action.payload;
      })
      .addCase(fetchAllInternalRecipients.rejected, (state, action) => {
        state.loading = false;
        state.error = action.error.message;
      })
      // Add recipient
      .addCase(addRecipient.pending, (state) => {
        state.loading = true;
        state.error = null;
      })
      .addCase(addRecipient.fulfilled, (state, action) => {
        state.loading = false;
        state.recipients.unshift(action.payload);
        state.totalElements += 1;
      })
      .addCase(addRecipient.rejected, (state, action) => {
        state.loading = false;
        state.error = action.error.message;
      })
      // Update recipient
      .addCase(updateRecipient.pending, (state) => {
        state.loading = true;
        state.error = null;
      })
      .addCase(updateRecipient.fulfilled, (state, action) => {
        state.loading = false;
        const index = state.recipients.findIndex(
          (r) => r.recipientId === action.payload.recipientId
        );
        if (index !== -1) {
          state.recipients[index] = action.payload;
        }
      })
      .addCase(updateRecipient.rejected, (state, action) => {
        state.loading = false;
        state.error = action.error.message;
      })
      // Delete recipient
      .addCase(deleteRecipient.pending, (state) => {
        state.loading = true;
        state.error = null;
      })
      .addCase(deleteRecipient.fulfilled, (state, action) => {
        state.loading = false;
        state.recipients = state.recipients.filter(
          (r) => r.recipientId !== action.payload.id
        );
        state.totalElements -= 1;
      })
      .addCase(deleteRecipient.rejected, (state, action) => {
        state.loading = false;
        state.error = action.error.message;
      });
  },
});

export const { clearError, setPage } = recipientsSlice.actions;
export default recipientsSlice.reducer;
