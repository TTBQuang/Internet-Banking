import React from "react";
import { Provider } from 'react-redux';
import { store } from '../store';
import { Tabs, TabsList, TabsTrigger, TabsContent } from "../../components/ui/tabs";
import CustomerManagement from "./CustomerManagement";
import DepositMoney from "./DepositMoney";
import TransactionHistory from "./TransactionHistory";
import DashboardLayout from "../../components/common/dashboard-layout";
import { useAppDispatch, useAppSelector } from '../hooks/redux';
import { setActiveTab } from '../store/slices/uiSlice';

const EmployeeDashboardContent = () => {
  const dispatch = useAppDispatch();
  const activeTab = useAppSelector(state => state.ui.activeTab);

  return (
    <DashboardLayout>
      <div className="bg-white rounded-lg shadow-sm">
        <div className="border-b border-gray-200 px-6 py-4">
          <h1 className="text-2xl font-bold text-gray-900">Employee Dashboard</h1>
          <p className="text-gray-600 mt-1">Manage customers, deposits, and view transaction history</p>
        </div>
        
        <Tabs value={activeTab} onValueChange={(tab) => dispatch(setActiveTab(tab))} className="w-full">
          <div className="px-6 pt-4">
            <TabsList className="grid w-full grid-cols-3 bg-gray-100">
              <TabsTrigger value="customers" className="data-[state=active]:bg-white data-[state=active]:shadow-sm">
                <div className="flex items-center space-x-2">
                  <svg className="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M12 4.354a4 4 0 110 5.292M15 21H3v-1a6 6 0 0112 0v1zm0 0h6v-1a6 6 0 00-9-5.197m13.5-9a2.5 2.5 0 11-5 0 2.5 2.5 0 015 0z" />
                  </svg>
                  <span>Customer Management</span>
                </div>
              </TabsTrigger>
              <TabsTrigger value="deposit" className="data-[state=active]:bg-white data-[state=active]:shadow-sm">
                <div className="flex items-center space-x-2">
                  <svg className="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M12 6v6m0 0v6m0-6h6m-6 0H6" />
                  </svg>
                  <span>Deposit Money</span>
                </div>
              </TabsTrigger>
              <TabsTrigger value="history" className="data-[state=active]:bg-white data-[state=active]:shadow-sm">
                <div className="flex items-center space-x-2">
                  <svg className="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M9 12h6m-6 4h6m2 5H7a2 2 0 01-2-2V5a2 2 0 012-2h5.586a1 1 0 01.707.293l5.414 5.414a1 1 0 01.293.707V19a2 2 0 01-2 2z" />
                  </svg>
                  <span>Transaction History</span>
                </div>
              </TabsTrigger>
            </TabsList>
          </div>
          
          <div className="p-6">
            <TabsContent value="customers" className="mt-0">
              <CustomerManagement />
            </TabsContent>
            <TabsContent value="deposit" className="mt-0">
              <DepositMoney />
            </TabsContent>
            <TabsContent value="history" className="mt-0">
              <TransactionHistory />
            </TabsContent>
          </div>
        </Tabs>
      </div>
    </DashboardLayout>
  );
};

const EmployeeDashboard = () => {
  return (
    <Provider store={store}>
      <EmployeeDashboardContent />
    </Provider>
  );
};

export default EmployeeDashboard;