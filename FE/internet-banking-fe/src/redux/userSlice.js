import { createSlice } from "@reduxjs/toolkit";

const initialState = {
  userId: "",
  username: "",
  fullName: "",
  email: "",
  phone: "",
  role: "",
};

const userSlice = createSlice({
  name: "user",
  initialState,
  reducers: {
    setUser: (state, action) => {
      const { userId, username, fullName, email, phone, role } = action.payload;
      state.userId = userId;
      state.username = username;
      state.fullName = fullName;
      state.email = email;
      state.phone = phone;
      state.role = role;
    },
    logout: (state) => {
      state.userId = "";
      state.username = "";
      state.fullName = "";
      state.email = "";
      state.phone = "";
      state.role = "";
    },
  },
});

export const { setUser, logout } = userSlice.actions;
export default userSlice.reducer;
