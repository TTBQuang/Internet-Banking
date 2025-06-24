import React from "react";
import DashboardLayout from "../../components/common/dashboard-layout-no-sidebar";
import EmployeeManagement from "./EmployeeManagement";

const AdminDashBoard = () => {
  return (
    <DashboardLayout>
      <EmployeeManagement />
    </DashboardLayout>
  );
};

export default AdminDashBoard;