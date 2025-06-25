import { createSlice } from '@reduxjs/toolkit';

const uiSlice = createSlice({
  name: 'ui',
  initialState: {
    activeTab: 'customers',
    editingCustomer: null,
    showDeleteConfirm: false,
    customerToDelete: null,
  },
  reducers: {
    setActiveTab: (state, action) => {
      state.activeTab = action.payload;
    },
    setEditingCustomer: (state, action) => {
      state.editingCustomer = action.payload;
    },
    showDeleteConfirmation: (state, action) => {
      state.showDeleteConfirm = true;
      state.customerToDelete = action.payload;
    },
    hideDeleteConfirmation: (state) => {
      state.showDeleteConfirm = false;
      state.customerToDelete = null;
    },
  },
});

export const { setActiveTab, setEditingCustomer, showDeleteConfirmation, hideDeleteConfirmation } = uiSlice.actions;
export default uiSlice.reducer;