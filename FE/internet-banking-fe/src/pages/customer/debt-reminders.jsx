import { useState } from 'react';
import { Tabs, TabsContent, TabsList, TabsTrigger } from '@/components/ui/tabs';

import DashboardLayout from '@/components/common/dashboard-layout';

import DebtRemindersList from '@/components/debt-reminders/DebtRemindersList';
import CreateDebtReminderDialog from '@/components/debt-reminders/CreateDebtReminderDialog';

// Mock debt reminder data
// const debtReminders = Array.from({ length: 30 }, (_, i) => ({
//   id: `debt-${i + 1}`,
//   date: new Date(2023, 4, 30 - i),
//   name: ['Tran Van B', 'Le Thi C', 'Pham Van D', 'Nguyen Thi E', 'Hoang Van F'][
//     Math.floor(Math.random() * 5)
//   ],
//   type: Math.random() > 0.5 ? 'received' : 'sent',
//   amount: Math.floor(Math.random() * 5000000) + 100000,
//   description: [
//     'Dinner payment',
//     'Shared taxi fare',
//     'Movie tickets',
//     'Group gift',
//     'Utility bill',
//     'Rent share',
//     'Grocery shopping',
//     'Concert tickets',
//   ][Math.floor(Math.random() * 8)],
//   status: ['pending', 'completed', 'rejected'][Math.floor(Math.random() * 3)],
// }));

export default function DebtRemindersPage() {
  const [activeTab, setActiveTab] = useState('sent');

  // Filter debt reminders based on selected filters
  // const filteredReminders = debtReminders.filter((reminder) => {
  //   // Filter by type
  //   if (activeTab === 'sent' && reminder.type !== 'sent') {
  //     return false;
  //   }

  //   if (activeTab === 'received' && reminder.type !== 'received') {
  //     return false;
  //   }

  //   // Filter by search term
  //   if (searchTerm) {
  //     const term = searchTerm.toLowerCase();
  //     return (
  //       reminder.name.toLowerCase().includes(term) ||
  //       reminder.description.toLowerCase().includes(term) ||
  //       reminder.amount.toString().includes(term)
  //     );
  //   }

  //   return true;
  // });

  // Calculate pagination
  // const totalPages = Math.ceil(filteredReminders.length / itemsPerPage);
  // const paginatedReminders = filteredReminders.slice(
  //   (currentPage - 1) * itemsPerPage,
  //   currentPage * itemsPerPage
  // );

  return (
    <DashboardLayout>
      <div className="space-y-6">
        <div className="flex flex-col md:flex-row justify-between gap-4">
          <div>
            <h1 className="text-3xl font-bold tracking-tight">
              Debt Reminders
            </h1>
            <p className="text-muted-foreground">
              Manage payment requests and reminders
            </p>
          </div>
          <div className="flex items-center gap-2">
            <CreateDebtReminderDialog activeTab={activeTab} />
          </div>
        </div>

        <Tabs
          defaultValue="sent"
          value={activeTab}
          onValueChange={setActiveTab}
        >
          <TabsList className="grid w-full grid-cols-2">
            <TabsTrigger value="sent" className="cursor-pointer">
              Sent
            </TabsTrigger>
            <TabsTrigger value="received" className="cursor-pointer">
              Received
            </TabsTrigger>
          </TabsList>
          <TabsContent value="sent" className="mt-4 space-y-4">
            <DebtRemindersList type="sent" />
          </TabsContent>
          <TabsContent value="received" className="mt-4 space-y-4">
            <DebtRemindersList type="received" />
          </TabsContent>
        </Tabs>
      </div>
    </DashboardLayout>
  );
}
