import {
  Card,
  CardContent,
  CardDescription,
  CardHeader,
  CardTitle,
} from "@/components/ui/card";
import { Button } from "@/components/ui/button";
import { Tabs, TabsContent, TabsList, TabsTrigger } from "@/components/ui/tabs";
import {
  ArrowRight,
  ArrowUpRight,
  ArrowDownRight,
  Clock,
  Users,
  AlertCircle,
} from "lucide-react";
import DashboardLayout from "../../components/common/dashboard-layout";
import { useSelector } from "react-redux";

export default function CustomerDashboardPage() {
  // Format currency
  const formatCurrency = (amount) => {
    return new Intl.NumberFormat("vi-VN").format(amount);
  };

  const fullName = useSelector((state) => state.user.fullName);

  // Mock data
  const accountBalance = 85000000;
  const recentTransactions = [
    {
      id: 1,
      type: "incoming",
      amount: 5000000,
      description: "Salary payment",
      date: "2023-05-10T08:30:00",
      from: "ABC Company",
    },
    {
      id: 2,
      type: "outgoing",
      amount: 1500000,
      description: "Electricity bill",
      date: "2023-05-08T14:15:00",
      to: "Electricity Company",
    },
    {
      id: 3,
      type: "outgoing",
      amount: 2000000,
      description: "Rent payment",
      date: "2023-05-05T10:00:00",
      to: "Landlord",
    },
    {
      id: 4,
      type: "incoming",
      amount: 3000000,
      description: "Client payment",
      date: "2023-05-03T16:45:00",
      from: "Client XYZ",
    },
  ];

  const debtReminders = [
    {
      id: 1,
      amount: 500000,
      description: "Dinner payment",
      date: "2023-05-15",
      from: "Nguyen Van B",
    },
    {
      id: 2,
      amount: 1000000,
      description: "Shared taxi fare",
      date: "2023-05-20",
      from: "Tran Thi C",
    },
  ];

  return (
    <DashboardLayout>
      <div className="space-y-6">
        <div>
          <h1 className="text-3xl font-bold tracking-tight">Dashboard</h1>
          <p className="text-muted-foreground">
            Welcome back, {fullName || "User"}
          </p>
        </div>

        <div className="grid gap-4 md:grid-cols-2 lg:grid-cols-4">
          <Card>
            <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
              <CardTitle className="text-sm font-medium">
                Account Balance
              </CardTitle>
              <svg
                xmlns="http://www.w3.org/2000/svg"
                viewBox="0 0 24 24"
                fill="none"
                stroke="currentColor"
                strokeLinecap="round"
                strokeLinejoin="round"
                strokeWidth="2"
                className="h-4 w-4 text-muted-foreground"
              >
                <path d="M12 2v20M17 5H9.5a3.5 3.5 0 0 0 0 7h5a3.5 3.5 0 0 1 0 7H6" />
              </svg>
            </CardHeader>
            <CardContent>
              <div className="text-2xl font-bold">
                ₫{formatCurrency(accountBalance)}
              </div>
              <p className="text-xs text-muted-foreground">
                Account ending in 1234
              </p>
            </CardContent>
          </Card>
          <Card>
            <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
              <CardTitle className="text-sm font-medium">
                Recent Transactions
              </CardTitle>
              <Clock className="h-4 w-4 text-muted-foreground" />
            </CardHeader>
            <CardContent>
              <div className="text-2xl font-bold">
                {recentTransactions.length}
              </div>
              <p className="text-xs text-muted-foreground">
                +2 since last week
              </p>
            </CardContent>
          </Card>
          <Card>
            <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
              <CardTitle className="text-sm font-medium">
                Debt Reminders
              </CardTitle>
              <AlertCircle className="h-4 w-4 text-muted-foreground" />
            </CardHeader>
            <CardContent>
              <div className="text-2xl font-bold">{debtReminders.length}</div>
              <p className="text-xs text-muted-foreground">Pending requests</p>
            </CardContent>
          </Card>
          <Card>
            <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
              <CardTitle className="text-sm font-medium">Recipients</CardTitle>
              <Users className="h-4 w-4 text-muted-foreground" />
            </CardHeader>
            <CardContent>
              <div className="text-2xl font-bold">12</div>
              <p className="text-xs text-muted-foreground">Saved recipients</p>
            </CardContent>
          </Card>
        </div>

        <Tabs defaultValue="transactions">
          <TabsList>
            <TabsTrigger value="transactions">Recent Transactions</TabsTrigger>
            <TabsTrigger value="debt-reminders">Debt Reminders</TabsTrigger>
          </TabsList>
          <TabsContent value="transactions" className="space-y-4">
            <Card>
              <CardHeader>
                <CardTitle>Recent Transactions</CardTitle>
                <CardDescription>Your recent account activity</CardDescription>
              </CardHeader>
              <CardContent>
                <div className="space-y-4">
                  {recentTransactions.map((transaction) => (
                    <div key={transaction.id} className="flex items-center">
                      <div
                        className={`mr-4 rounded-full p-2 ${
                          transaction.type === "incoming"
                            ? "bg-green-100"
                            : "bg-red-100"
                        }`}
                      >
                        {transaction.type === "incoming" ? (
                          <ArrowDownRight
                            className={`h-4 w-4 text-green-600`}
                          />
                        ) : (
                          <ArrowUpRight className={`h-4 w-4 text-red-600`} />
                        )}
                      </div>
                      <div className="flex-1 space-y-1">
                        <p className="text-sm font-medium leading-none">
                          {transaction.description}
                        </p>
                        <p className="text-xs text-muted-foreground">
                          {transaction.type === "incoming"
                            ? `From: ${transaction.from}`
                            : `To: ${transaction.to}`}
                        </p>
                        <p className="text-xs text-muted-foreground">
                          {new Date(transaction.date).toLocaleString()}
                        </p>
                      </div>
                      <div
                        className={`font-medium ${
                          transaction.type === "incoming"
                            ? "text-green-600"
                            : "text-red-600"
                        }`}
                      >
                        {transaction.type === "incoming" ? "+" : "-"}₫
                        {formatCurrency(transaction.amount)}
                      </div>
                    </div>
                  ))}
                </div>
                <div className="mt-4 flex justify-center">
                  <Button variant="outline" size="sm" className="gap-1">
                    View All Transactions
                    <ArrowRight className="h-4 w-4" />
                  </Button>
                </div>
              </CardContent>
            </Card>
          </TabsContent>
          <TabsContent value="debt-reminders" className="space-y-4">
            <Card>
              <CardHeader>
                <CardTitle>Debt Reminders</CardTitle>
                <CardDescription>
                  Payment requests from your contacts
                </CardDescription>
              </CardHeader>
              <CardContent>
                <div className="space-y-4">
                  {debtReminders.map((reminder) => (
                    <div
                      key={reminder.id}
                      className="flex items-center justify-between"
                    >
                      <div className="flex items-center gap-4">
                        <div className="rounded-full bg-blue-100 p-2">
                          <AlertCircle className="h-4 w-4 text-blue-600" />
                        </div>
                        <div>
                          <p className="text-sm font-medium">{reminder.from}</p>
                          <p className="text-xs text-muted-foreground">
                            {reminder.description}
                          </p>
                          <p className="text-xs text-muted-foreground">
                            Due: {reminder.date}
                          </p>
                        </div>
                      </div>
                      <div className="flex flex-col items-end gap-2">
                        <p className="font-medium">
                          ₫{formatCurrency(reminder.amount)}
                        </p>
                        <Button size="sm">Pay Now</Button>
                      </div>
                    </div>
                  ))}
                </div>
                <div className="mt-4 flex justify-center">
                  <Button variant="outline" size="sm" className="gap-1">
                    View All Reminders
                    <ArrowRight className="h-4 w-4" />
                  </Button>
                </div>
              </CardContent>
            </Card>
          </TabsContent>
        </Tabs>
      </div>
    </DashboardLayout>
  );
}
