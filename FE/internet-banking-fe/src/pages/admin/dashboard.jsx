import React, { useState } from "react";
import { Tabs, TabsList, TabsTrigger, TabsContent } from "../../components/ui/tabs";
import DashboardLayout from "../../components/common/dashboard-layout-no-sidebar";
import EmployeeManagement from "./EmployeeManagement";
import LinkedBankTransactionManagement from "./TransactionHistory";

const AdminDashBoard = () => {
  const [tab, setTab] = useState("employees");
  return (
    <DashboardLayout>
      <Tabs value={tab} onValueChange={setTab}>
        <TabsList>
          <TabsTrigger value="employees">Employee Management</TabsTrigger>
          <TabsTrigger value="history">Interbank Transaction</TabsTrigger>
        </TabsList>
        <TabsContent value="employees">
          <EmployeeManagement />
        </TabsContent>
        <TabsContent value="history">
          <LinkedBankTransactionManagement />
        </TabsContent>
      </Tabs>
    </DashboardLayout>
  );
};

export default AdminDashBoard;