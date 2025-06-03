import DashboardLayout from '../../components/common/dashboard-layout';
import TransactionListCard from '../../components/history/TransactionsListCard';

export default function HistoryPage() {
  return (
    <DashboardLayout>
      <div className="space-y-6">
        <div className="flex flex-col md:flex-row justify-between gap-4">
          <div>
            <h1 className="text-3xl font-bold tracking-tight">
              Transaction History
            </h1>
            <p className="text-muted-foreground">
              View and search your transaction history
            </p>
          </div>
        </div>
        <TransactionListCard />
      </div>
    </DashboardLayout>
  );
}
