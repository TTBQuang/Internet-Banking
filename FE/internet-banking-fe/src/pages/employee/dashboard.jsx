import React, { useState } from "react";
import { Tabs, TabsList, TabsTrigger, TabsContent } from "../../components/ui/tabs";
import CustomerManagement from "./CustomerManagement";
import DepositMoney from "./DepositMoney";
import TransactionHistory from "./TransactionHistory";
import DashboardLayout from "../../components/common/dashboard-layout";

const EmployeeDashBoard = () => {
  const [tab, setTab] = useState("customers");
  return (
    <DashboardLayout>
      <Tabs value={tab} onValueChange={setTab}>
        <TabsList>
          <TabsTrigger value="customers">Customer Management</TabsTrigger>
          <TabsTrigger value="deposit">Deposit Money</TabsTrigger>
          <TabsTrigger value="history">Transaction History</TabsTrigger>
        </TabsList>
        <TabsContent value="customers">
          <CustomerManagement />
        </TabsContent>
        <TabsContent value="deposit">
          <DepositMoney />
        </TabsContent>
        <TabsContent value="history">
          <TransactionHistory />
        </TabsContent>
      </Tabs>
    </DashboardLayout>
  );
};

export default EmployeeDashBoard;