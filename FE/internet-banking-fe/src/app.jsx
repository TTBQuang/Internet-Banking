import React from "react";
import {
  BrowserRouter as Router,
  Route,
  Routes,
  Navigate,
} from "react-router-dom";
import { useSelector } from "react-redux";
import LoginPage from "./pages/LoginPage";
import CustomerDashBoard from "./pages/customer/CustomerDashBoard";
import EmployeeDashBoard from "./pages/employee/EmployeeDashBoard";
import AdminDashBoard from "./pages/admin/AdminDashBoard";

function App() {
  const user = useSelector((state) => state.user);

  return (
    <Router>
      <Routes>
        <Route path="/" element={<LoginPage />} />

        <Route
          path="/customer/dashboard"
          element={
            user.role === "CUSTOMER" ? (
              <CustomerDashBoard />
            ) : (
              <Navigate to="/" />
            )
          }
        />

        <Route
          path="/employee/dashboard"
          element={
            user.role === "EMPLOYEE" ? (
              <EmployeeDashBoard />
            ) : (
              <Navigate to="/" />
            )
          }
        />

        <Route
          path="/admin/dashboard"
          element={
            user.role === "ADMIN" ? <AdminDashBoard /> : <Navigate to="/" />
          }
        />
      </Routes>
    </Router>
  );
}

export default App;
