import React from "react";
import {
  BrowserRouter as Router,
  Route,
  Routes,
  Navigate,
} from "react-router-dom";
import { useSelector } from "react-redux";
import LoginPage from "./pages/login";
import CustomerDashBoard from "./pages/customer/dashboard";
import EmployeeDashBoard from "./pages/employee/dashboard";
import AdminDashBoard from "./pages/admin/dashboard";
import Transfer from "./pages/customer/transfer";
import History from "./pages/customer/history";
import DebtReminders from "./pages/customer/debt-reminders";
import Recipients from "./pages/customer/recipients";
import ForgotPasswordPage from "./pages/forgot-password";
import { ToastContainer } from "react-toastify";
import "react-toastify/dist/ReactToastify.css";

function App() {
  const user = useSelector((state) => state.user);

  // Protected Route component
  const ProtectedRoute = ({ children, allowedRoles }) => {
    if (!user.role) {
      return <Navigate to="/" />;
    }

    if (allowedRoles && !allowedRoles.includes(user.role)) {
      return <Navigate to="/" />;
    }

    return children;
  };

  return (
    <Router>
      <Routes>
        <Route path="/" element={<LoginPage />} />
        <Route path="/forgot-password" element={<ForgotPasswordPage />} />

        {/* Customer Routes */}
        <Route
          path="/customer/dashboard"
          element={
            <ProtectedRoute allowedRoles={["CUSTOMER"]}>
              <CustomerDashBoard />
            </ProtectedRoute>
          }
        />
        <Route
          path="/customer/dashboard/transfer"
          element={
            <ProtectedRoute allowedRoles={["CUSTOMER"]}>
              <Transfer />
            </ProtectedRoute>
          }
        />
        <Route
          path="/customer/dashboard/history"
          element={
            <ProtectedRoute allowedRoles={["CUSTOMER"]}>
              <History />
            </ProtectedRoute>
          }
        />
        <Route
          path="/customer/dashboard/debt-reminders"
          element={
            <ProtectedRoute allowedRoles={["CUSTOMER"]}>
              <DebtReminders />
            </ProtectedRoute>
          }
        />
        <Route
          path="/customer/dashboard/recipients"
          element={
            <ProtectedRoute allowedRoles={["CUSTOMER"]}>
              <Recipients />
            </ProtectedRoute>
          }
        />

        {/* Employee Routes */}
        <Route
          path="/employee/dashboard"
          element={
            <ProtectedRoute allowedRoles={["EMPLOYEE"]}>
              <EmployeeDashBoard />
            </ProtectedRoute>
          }
        />

        {/* Admin Routes */}
        <Route
          path="/admin/dashboard"
          element={
            <ProtectedRoute allowedRoles={["ADMIN"]}>
              <AdminDashBoard />
            </ProtectedRoute>
          }
        />

        {/* Catch all route */}
        <Route path="*" element={<Navigate to="/" />} />
      </Routes>
      <ToastContainer position="top-right" />
    </Router>
  );
}

export default App;
