import { createSlice, createAsyncThunk } from "@reduxjs/toolkit";
import { getAccount } from "../services/accountService";

export const fetchAccount = createAsyncThunk(
  "account/fetchAccount",
  async (_, { rejectWithValue }) => {
    try {
      const data = await getAccount();
      return data.data;
    } catch (error) {
      return rejectWithValue(error.message);
    }
  }
);

const accountSlice = createSlice({
  name: "account",
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
      });
  },
});

export default accountSlice.reducer;
