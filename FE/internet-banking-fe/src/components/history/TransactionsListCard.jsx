import { useEffect, useState } from 'react';
import { cn } from '@/lib/utils';
import { format } from 'date-fns';
import {
  Card,
  CardHeader,
  CardTitle,
  CardDescription,
  CardContent,
  CardFooter,
} from '@/components/ui/card';
import { Tabs, TabsList, TabsTrigger } from '@/components/ui/tabs';
import { Calendar } from '@/components/ui/calendar';
import {
  Popover,
  PopoverContent,
  PopoverTrigger,
} from '@/components/ui/popover';
import { Button } from '@/components/ui/button';
import { ArrowDownLeft, ArrowUpRight, CalendarIcon } from 'lucide-react';
import { useDispatch, useSelector } from 'react-redux';
import { setPage } from '@/redux/transactionsSlice';
import {
  fetchCurrentUserReceivedTransactions,
  fetchCurrentUserTransferTransactions,
  fetchCurrentUserDebtPaymentTransactions,
} from '@/redux/transactionsSlice';
import Pagination from '../common/pagination';

const TransactionListCard = () => {
  const formatCurrency = (amount) => {
    return `₫ ${Number(amount).toLocaleString('en-US')}`;
  };

  const [filterType, setFilterType] = useState('received');
  const [dateFrom, setDateFrom] = useState(undefined);
  const [dateTo, setDateTo] = useState(undefined);

  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);

  const { transactions, currentPage, totalPages, totalElements, pageSize } =
    useSelector((state) => state.transactions);

  const dispatch = useDispatch();

  // Reset page when filter type changes
  useEffect(() => {
    dispatch(setPage(1));
  }, [filterType, dispatch]);

  const handlePageChange = (page) => {
    dispatch(setPage(page));
  };

  // Fetch data
  useEffect(() => {
    const fetchData = async () => {
      const params = { page: currentPage, size: pageSize };
      try {
        setLoading(true);
        setError(null);

        if (filterType === 'received') {
          await dispatch(fetchCurrentUserReceivedTransactions(params)).unwrap();
        } else if (filterType === 'transfer') {
          await dispatch(fetchCurrentUserTransferTransactions(params)).unwrap();
        } else if (filterType === 'debt-payment') {
          await dispatch(
            fetchCurrentUserDebtPaymentTransactions(params)
          ).unwrap();
        }
      } catch (err) {
        setError(err.message);
      } finally {
        setLoading(false);
      }
    };

    fetchData();
  }, [dispatch, currentPage, pageSize, filterType]);

  return (
    <Card>
      <CardHeader>
        <div className="flex flex-col md:flex-row justify-between gap-4">
          <div>
            <CardTitle>Transactions</CardTitle>
            <CardDescription>
              {totalElements} transactions found
            </CardDescription>
          </div>
        </div>
      </CardHeader>
      <CardContent className="space-y-4">
        <div className="flex flex-col sm:flex-row gap-4 pb-4">
          <Tabs
            defaultValue="all"
            value={filterType}
            onValueChange={setFilterType}
            className="w-full sm:w-auto"
          >
            <TabsList className="grid w-full grid-cols-3">
              <TabsTrigger value="received" className="cursor-pointer">
                Received
              </TabsTrigger>
              <TabsTrigger value="transfer" className="cursor-pointer">
                Transfer
              </TabsTrigger>
              <TabsTrigger value="debt-payment" className="cursor-pointer">
                Debt Payment
              </TabsTrigger>
            </TabsList>
          </Tabs>

          <div className="flex flex-col sm:flex-row gap-2">
            <div className="grid w-full max-w-sm items-center gap-1.5">
              <Popover>
                <PopoverTrigger asChild>
                  <Button
                    variant={'outline'}
                    className={cn(
                      'w-full justify-start text-left font-normal cursor-pointer',
                      !dateFrom && 'text-muted-foreground'
                    )}
                  >
                    <CalendarIcon className="mr-2 h-4 w-4" />
                    {dateFrom ? format(dateFrom, 'PPP') : 'From Date'}
                  </Button>
                </PopoverTrigger>
                <PopoverContent className="w-auto p-0">
                  <Calendar
                    mode="single"
                    selected={dateFrom}
                    onSelect={setDateFrom}
                    initialFocus
                  />
                </PopoverContent>
              </Popover>
            </div>

            <div className="grid w-full max-w-sm items-center gap-1.5">
              <Popover>
                <PopoverTrigger asChild>
                  <Button
                    variant={'outline'}
                    className={cn(
                      'w-full justify-start text-left font-normal cursor-pointer',
                      !dateTo && 'text-muted-foreground'
                    )}
                  >
                    <CalendarIcon className="mr-2 h-4 w-4" />
                    {dateTo ? format(dateTo, 'PPP') : 'To Date'}
                  </Button>
                </PopoverTrigger>
                <PopoverContent className="w-auto p-0">
                  <Calendar
                    mode="single"
                    selected={dateTo}
                    onSelect={setDateTo}
                    initialFocus
                  />
                </PopoverContent>
              </Popover>
            </div>
          </div>
        </div>
        <div className="border border-gray-200 rounded-md overflow-hidden">
          <div className="overflow-x-auto">
            <table className="w-full text-sm">
              <thead className="bg-gray-50">
                <tr>
                  <th className="h-12 px-4 text-left font-medium text-gray-600">
                    Date
                  </th>
                  <th className="h-12 px-4 text-left font-medium text-gray-600">
                    Content
                  </th>
                  <th className="h-12 px-4 text-left font-medium text-gray-600">
                    {filterType === 'received'
                      ? 'Sender Accout Number'
                      : 'Receiver Account Number'}
                  </th>
                  <th className="h-12 px-4 text-left font-medium text-gray-600">
                    {filterType === 'received'
                      ? 'Sender Bank'
                      : 'Receiver Bank'}
                  </th>
                  <th className="h-12 px-4 text-left font-medium text-gray-600">
                    Amount
                  </th>
                  <th className="h-12 px-4 text-left font-medium text-gray-600">
                    Status
                  </th>
                  <th className="h-12 px-4 text-left font-medium text-gray-600">
                    Confirmed At
                  </th>
                </tr>
              </thead>
              <tbody className="divide-y divide-gray-200">
                {loading ? (
                  <tr>
                    <td colSpan={7} className="h-24 text-center">
                      <div className="flex items-center justify-center">
                        <svg
                          className="animate-spin h-5 w-5 text-blue-600"
                          xmlns="http://www.w3.org/2000/svg"
                          fill="none"
                          viewBox="0 0 24 24"
                        >
                          <circle
                            className="opacity-25"
                            cx="12"
                            cy="12"
                            r="10"
                            stroke="currentColor"
                            strokeWidth="4"
                          ></circle>
                          <path
                            className="opacity-75"
                            fill="currentColor"
                            d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"
                          ></path>
                        </svg>
                        <span className="ml-2">Loading...</span>
                      </div>
                    </td>
                  </tr>
                ) : transactions.length > 0 ? (
                  transactions.map((transaction) => (
                    <tr
                      key={transaction.transactionId}
                      className="hover:bg-gray-50"
                    >
                      <td className="p-4">
                        {new Date(transaction.createdAt).toLocaleDateString(
                          'en-GB'
                        )}
                      </td>
                      <td className="p-4 ">{transaction.content}</td>
                      <td className="p-4 font-medium">
                        {transaction.receiverAccountNumber}
                      </td>
                      <td className="p-4">
                        {filterType === 'received'
                          ? (transaction.senderBank &&
                              transaction.senderBank.bankName) ||
                            'Internal'
                          : (transaction.receiverBank &&
                              transaction.receiverBank.bankName) ||
                            'Internal'}
                      </td>
                      <td className="p-4 whitespace-nowrap">
                        <div className="flex items-center gap-2">
                          {filterType === 'received' ? (
                            <ArrowDownLeft className="h-4 w-4 text-green-600" />
                          ) : (
                            <ArrowUpRight className="h-4 w-4 text-red-600" />
                          )}
                          <span
                            className={
                              filterType === 'received'
                                ? 'text-green-600'
                                : 'text-red-600'
                            }
                          >
                            {filterType === 'received' ? '+' : '-'}
                            {formatCurrency(transaction.amount)}
                          </span>
                        </div>
                      </td>
                      <td className="p-4">
                        <span
                          className={`px-2 py-1 rounded-full text-xs font-medium ${
                            transaction.status === 'PENDING'
                              ? 'bg-yellow-100 text-yellow-800'
                              : transaction.status === 'COMPLETED'
                              ? 'bg-green-100 text-green-800'
                              : transaction.status === 'FAILED'
                              ? 'bg-red-100 text-red-800'
                              : ''
                          }`}
                        >
                          {transaction.status}
                        </span>
                      </td>
                      <td className="p-4">
                        {transaction.confirmedAt
                          ? new Date(
                              transaction.confirmedAt
                            ).toLocaleDateString('en-GB')
                          : 'N/A'}
                      </td>
                    </tr>
                  ))
                ) : (
                  <tr>
                    <td colSpan={7} className="h-24 text-center text-gray-500">
                      No transactions found.
                    </td>
                  </tr>
                )}
              </tbody>
            </table>
          </div>
        </div>
      </CardContent>
      {totalElements > 0 && (
        <CardFooter className="border-t p-4">
          <div className="w-full">
            <Pagination
              currentPage={currentPage}
              totalPages={totalPages}
              totalItems={totalElements}
              itemsPerPage={pageSize}
              onPageChange={handlePageChange}
            />
          </div>
        </CardFooter>
      )}
    </Card>
  );
};

export default TransactionListCard;
