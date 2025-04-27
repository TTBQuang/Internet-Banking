import { configureStore } from "@reduxjs/toolkit";

import userReducer from "./userSlice";
import accountReducer from "./accountSlice";

export const store = configureStore({
  reducer: {
    user: userReducer,
    account: accountReducer,
  },
});
