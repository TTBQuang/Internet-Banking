import { configureStore } from "@reduxjs/toolkit";

import userReducer from "./userSlice";
import accountReducer from "./accountSlice";
import forgotPasswordReducer from "./forgotPasswordSlice";
import changePasswordReducer from "./changePasswordSlice";
import recipientsReducer from "./recipientsSlice";
import transfersReducer from "./transferSlice";

export const store = configureStore({
  reducer: {
    user: userReducer,
    account: accountReducer,
    forgotPassword: forgotPasswordReducer,
    changePassword: changePasswordReducer,
    recipients: recipientsReducer,
    transfers: transfersReducer,
  },
});
