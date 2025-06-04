import { configureStore } from '@reduxjs/toolkit';

import userReducer from './userSlice';
import accountReducer from './accountSlice';
import forgotPasswordReducer from './forgotPasswordSlice';
import changePasswordReducer from './changePasswordSlice';
import recipientsReducer from './recipientsSlice';
import debtRemindersReducer from './debtRemindersSlice';
import transactionsReducer from './transactionsSlice';
import notificationsReducer from './notificationsSlice';
import transfersReducer from "./transferSlice";

export const store = configureStore({
  reducer: {
    user: userReducer,
    account: accountReducer,
    forgotPassword: forgotPasswordReducer,
    changePassword: changePasswordReducer,
    recipients: recipientsReducer,
    debtReminders: debtRemindersReducer,
    transactions: transactionsReducer,
    notifications: notificationsReducer,
    transfers: transfersReducer,
  },
});
