import { useEffect, useState } from 'react';
import { useDispatch, useSelector } from 'react-redux';
import {
  fetchSentDebtReminders,
  fetchReceivedDebtReminders,
  setPage,
  deleteDebtReminder,
  deleteReceivedDebtReminder,
  clearError,
} from '@/redux/debtRemindersSlice';
import Pagination from '../common/pagination';
import {
  Card,
  CardContent,
  CardDescription,
  CardFooter,
  CardHeader,
  CardTitle,
} from '../ui/card';
import { Search, Trash2, CreditCard } from 'lucide-react';
import { Input } from '../ui/input';
import { useDebounce } from 'use-debounce';
import { toast } from 'react-toastify';
import DeleteDebtReminderDialog from './DeleteDebtReminderDialog';

const formatCurrency = (amount) => {
  return `₫ ${Number(amount).toLocaleString('en-US')}`;
};

const DebtRemindersListCard = ({ type }) => {
  const {
    debtReminders,
    currentPage,
    totalPages,
    totalElements,
    pageSize,
    loading: debtRemindersLoading,
  } = useSelector((state) => state.debtReminders);

  const dispatch = useDispatch();

  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);

  const [searchTerm, setSearchTerm] = useState('');
  const [debouncedSearchTerm] = useDebounce(searchTerm, 300);

  const [isDeleteDialogOpen, setIsDeleteDialogOpen] = useState(false);
  const [selectedDebtReminder, setSelectedDebtReminder] = useState(null);

  const [deleteError, setDeleteError] = useState(null);
  const [deleteContent, setDeleteContent] = useState('');
  // Reset page to 1 when search term or type changes
  useEffect(() => {
    dispatch(setPage(1));
  }, [debouncedSearchTerm, type, dispatch]);

  // Handle page change
  const handlePageChange = (page) => {
    dispatch(setPage(page));
  };

  // Fetch data
  useEffect(() => {
    const fetchData = async () => {
      const params = { page: currentPage, size: pageSize };
      if (debouncedSearchTerm) {
        params.query = debouncedSearchTerm;
      }
      try {
        setLoading(true);
        setError(null);
        if (type === 'sent') {
          await dispatch(fetchSentDebtReminders(params)).unwrap();
        } else if (type === 'received') {
          await dispatch(fetchReceivedDebtReminders(params)).unwrap();
        }
      } catch (err) {
        setError(err.message);
      } finally {
        setLoading(false);
      }
    };

    fetchData();
  }, [dispatch, currentPage, pageSize, debouncedSearchTerm, type]);

  // if (loading) {
  //   return <div>Loading...</div>;
  // }

  const handleDelete = (debtReminder) => {
    setSelectedDebtReminder(debtReminder);
    setIsDeleteDialogOpen(true);
  };

  const confirmDelete = async () => {
    try {
      if (type === 'sent') {
        await dispatch(
          deleteDebtReminder({
            debtReminderId: selectedDebtReminder.debtReminderId,
            content: deleteContent,
          })
        ).unwrap();
      } else if (type === 'received') {
        await dispatch(
          deleteReceivedDebtReminder({
            debtReminderId: selectedDebtReminder.debtReminderId,
            content: deleteContent,
          })
        ).unwrap();
      }
      // Refetch data
      if (type === 'sent') {
        await dispatch(
          fetchSentDebtReminders({ page: 1, size: pageSize })
        ).unwrap();
      } else if (type === 'received') {
        await dispatch(
          fetchReceivedDebtReminders({ page: 1, size: pageSize })
        ).unwrap();
      }
      toast.success('Debt reminder deleted successfully');

      setIsDeleteDialogOpen(false);
      setSelectedDebtReminder(null);
      setDeleteError(null);
      setDeleteContent('');
    } catch (err) {
      setDeleteError(err.message);
    }
  };

  const handlePay = (debtReminder) => {
    console.log(debtReminder);
  };

  if (error) {
    return <div>Error: {error}</div>;
  }

  return (
    <Card>
      <CardHeader>
        <div className="flex flex-col md:flex-row justify-between gap-4">
          <div>
            <CardTitle>
              {type === 'sent'
                ? 'Sent Debt Reminders'
                : 'Received Debt Reminders'}
            </CardTitle>
            <CardDescription>
              {type === 'sent'
                ? 'Reminder you sent to others'
                : 'Reminder you received from others'}
            </CardDescription>
          </div>
          <div className="relative">
            <Search className="absolute left-2.5 top-2.5 h-4 w-4 text-muted-foreground" />
            <Input
              type="search"
              placeholder="Search reminders..."
              className="w-full md:w-[300px] pl-8"
              value={searchTerm}
              onChange={(e) => setSearchTerm(e.target.value)}
            />
          </div>
        </div>
      </CardHeader>
      <CardContent>
        <div className="border border-gray-200 rounded-md overflow-hidden">
          <div className="overflow-x-auto">
            <table className="w-full text-sm">
              <thead className="bg-gray-50">
                <tr>
                  <th className="h-12 px-4 text-left font-medium text-gray-600">
                    {type === 'sent' ? 'Sent to' : 'Received from'}
                  </th>
                  <th className="h-12 px-4 text-left font-medium text-gray-600">
                    Amount
                  </th>
                  <th className="h-12 px-4 text-left font-medium text-gray-600">
                    Content
                  </th>
                  <th className="h-12 px-4 text-left font-medium text-gray-600">
                    Status
                  </th>
                  <th className="h-12 px-4 text-left font-medium text-gray-600">
                    Created At
                  </th>
                  <th className="h-12 px-4 text-left font-medium text-gray-600">
                    Paid At
                  </th>
                  <th className="h-12 px-4 text-left font-medium text-gray-600">
                    Actions
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
                ) : debtReminders.length > 0 ? (
                  debtReminders.map((debtReminder) => (
                    <tr
                      key={debtReminder.debtReminderId}
                      className="hover:bg-gray-50"
                    >
                      <td className="p-4">
                        <p className="font-medium">
                          {type === 'sent'
                            ? debtReminder.debtorAccount.user.fullName
                            : debtReminder.creditor.fullName}
                        </p>
                      </td>
                      <td className="p-4 whitespace-nowrap">
                        {formatCurrency(debtReminder.amount)}
                      </td>
                      <td className="p-4">{debtReminder.content}</td>
                      <td className="p-4">
                        <span
                          className={`px-2 py-1 rounded-full text-xs font-medium ${
                            debtReminder.status === 'PENDING'
                              ? 'bg-yellow-100 text-yellow-800'
                              : debtReminder.status === 'PAID'
                              ? 'bg-green-100 text-green-800'
                              : ''
                          }`}
                        >
                          {debtReminder.status}
                        </span>
                      </td>
                      <td className="p-4">
                        {new Date(debtReminder.createdAt).toLocaleDateString(
                          'en-GB'
                        )}
                      </td>
                      <td className="p-4">
                        {debtReminder.paidAt
                          ? new Date(debtReminder.paidAt).toLocaleDateString(
                              'en-GB'
                            )
                          : 'N/A'}
                      </td>
                      <td className="p-4">
                        <div className="flex items-center gap-2">
                          <button
                            onClick={() => handleDelete(debtReminder)}
                            className="p-2 text-gray-400 hover:text-red-600 hover:bg-red-50 rounded cursor-pointer"
                          >
                            <Trash2 className="h-4 w-4" />
                          </button>
                          {type === 'received' && (
                            <button
                              onClick={() => handlePay(debtReminder)}
                              className="p-2 text-gray-400 hover:text-green-600 hover:bg-green-50 rounded cursor-pointer"
                            >
                              <CreditCard className="h-4 w-4" />
                            </button>
                          )}
                        </div>
                      </td>
                    </tr>
                  ))
                ) : (
                  <tr>
                    <td colSpan={7} className="h-24 text-center text-gray-500">
                      No debt reminders found.
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
      <DeleteDebtReminderDialog
        isOpen={isDeleteDialogOpen}
        onClose={() => {
          setIsDeleteDialogOpen(false);
          setDeleteContent('');
          dispatch(clearError());
        }}
        onSubmit={confirmDelete}
        loading={debtRemindersLoading}
        error={deleteError}
        content={deleteContent}
        setContent={setDeleteContent}
      />
    </Card>
  );
};

export default DebtRemindersListCard;
