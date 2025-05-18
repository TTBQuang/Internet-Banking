import { configureStore } from "@reduxjs/toolkit";

import userReducer from "./userSlice";
import accountReducer from "./accountSlice";
import forgotPasswordReducer from "./forgotPasswordSlice";
import changePasswordReducer from "./changePasswordSlice";

export const store = configureStore({
  reducer: {
    user: userReducer,
    account: accountReducer,
    forgotPassword: forgotPasswordReducer,
    changePassword: changePasswordReducer,
  },
});
