import { useEffect, useState } from 'react';
import {
  Dialog,
  DialogContent,
  DialogDescription,
  DialogFooter,
  DialogHeader,
  DialogTitle,
  DialogTrigger,
} from '@/components/ui/dialog';
import { Button } from '@/components/ui/button';
import { Input } from '@/components/ui/input';
import { Label } from '@/components/ui/label';
import { Textarea } from '@/components/ui/textarea';
import { Plus, Search } from 'lucide-react';
import { ChevronsUpDown } from 'lucide-react';
import { useDispatch, useSelector } from 'react-redux';
import { useDebounce } from 'use-debounce';
import { fetchRecipients } from '@/redux/recipientsSlice';
import {
  clearError,
  createDebtReminder,
  fetchReceivedDebtReminders,
  fetchSentDebtReminders,
} from '@/redux/debtRemindersSlice';
import { toast } from 'react-toastify';

const CreateDebtReminderDialog = ({ activeTab }) => {
  const [recipientOpen, setRecipientOpen] = useState(false);
  const [selectedRecipient, setSelectedRecipient] = useState(null);
  const [searchTerm, setSearchTerm] = useState('');
  const [debouncedSearchTerm] = useDebounce(searchTerm, 300);

  const [isDialogOpen, setIsDialogOpen] = useState(false);

  const {
    recipients,
    currentPage,
    totalPages,
    totalElements,
    pageSize: recipientsPageSize,
    loading: recipientsLoading,
    error: recipientsError,
  } = useSelector((state) => state.recipients);

  const { loading, pageSize } = useSelector((state) => state.debtReminders);
  const [error, setError] = useState(null);

  const dispatch = useDispatch();

  useEffect(() => {
    const params = { page: currentPage, size: recipientsPageSize };
    if (debouncedSearchTerm) {
      params.nickname = debouncedSearchTerm;
    }
    dispatch(fetchRecipients(params));
  }, [dispatch, currentPage, recipientsPageSize, debouncedSearchTerm]);

  // Form data
  const [formData, setFormData] = useState({
    debtorAccountNumber: '',
    amount: '',
    content: '',
  });

  const handleChange = (e) => {
    const { id, value } = e.target;
    setFormData((prev) => ({
      ...prev,
      [id]: value,
    }));
  };

  const handleRecipientSelect = (recipient) => {
    setSelectedRecipient(recipient);
    setFormData((prev) => ({
      ...prev,
      debtorAccountNumber: recipient.accountNumber,
    }));
    setRecipientOpen(false);
  };

  const handleOpenChange = () => {
    setIsDialogOpen(!isDialogOpen);
    if (!isDialogOpen) {
      // Reset all states when dialog closes
      setRecipientOpen(false);
      setSelectedRecipient(null);
      setSearchTerm('');
      setError(null);
      setFormData({
        debtorAccountNumber: '',
        amount: '',
        content: '',
      });
    }
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    // TODO: Add your submit logic here
    console.log('Form submitted:', formData);

    try {
      await dispatch(createDebtReminder(formData)).unwrap();
      toast.success('Debt reminder created successfully');

      // Refetch data
      if (activeTab === 'sent') {
        dispatch(fetchSentDebtReminders({ page: 1, size: pageSize }));
      } else if (activeTab === 'received') {
        dispatch(fetchReceivedDebtReminders({ page: 1, size: pageSize }));
      }
      // Close dialog
      setIsDialogOpen(false);
    } catch (err) {
      setError(err.message);
      dispatch(clearError());
    }
  };

  return (
    <Dialog open={isDialogOpen} onOpenChange={handleOpenChange}>
      <DialogTrigger asChild>
        <Button className="cursor-pointer">
          <Plus className="mr-2 h-4 w-4" />
          New Reminder
        </Button>
      </DialogTrigger>
      <DialogContent className="sm:max-w-[425px]">
        <DialogHeader>
          <DialogTitle>Create Debt Reminder</DialogTitle>
          <DialogDescription>
            Send a payment request to another user.
          </DialogDescription>
        </DialogHeader>
        <form onSubmit={handleSubmit}>
          <div className="grid gap-4 py-4">
            <div className="grid grid-cols-4 items-center gap-4">
              <Label htmlFor="debtorAccountNumber" className="text-right">
                Recipient
              </Label>
              <div className="col-span-3 space-y-2">
                <div className="flex gap-2">
                  <div className="relative w-full">
                    <Button
                      type="button"
                      variant="outline"
                      role="combobox"
                      aria-expanded={recipientOpen}
                      className="w-full justify-between"
                      onClick={() => setRecipientOpen(!recipientOpen)}
                    >
                      Choose from recipient list
                      <ChevronsUpDown className="ml-2 h-4 w-4 shrink-0 opacity-50" />
                    </Button>
                    {recipientOpen && (
                      <div className="absolute z-50 mt-1 w-full rounded-md border bg-popover text-popover-foreground shadow-md">
                        <div className="flex items-center border-b px-3">
                          <Search className="mr-2 h-4 w-4 shrink-0 opacity-50" />
                          <input
                            className="flex h-10 w-full rounded-md bg-transparent py-3 text-sm outline-none placeholder:text-muted-foreground disabled:cursor-not-allowed disabled:opacity-50"
                            placeholder="Search recipient..."
                            value={searchTerm}
                            onChange={(e) => setSearchTerm(e.target.value)}
                          />
                        </div>
                        <div className="max-h-[300px] overflow-auto p-1">
                          {recipients.length === 0 ? (
                            <div className="py-6 text-center text-sm">
                              No recipient found.
                            </div>
                          ) : (
                            <div className="space-y-1">
                              {recipients.map((recipient) => (
                                <div
                                  key={recipient.recipientId}
                                  className="flex cursor-pointer items-center rounded-sm px-2 py-1.5 text-sm outline-none hover:bg-accent hover:text-accent-foreground"
                                  onClick={() =>
                                    handleRecipientSelect(recipient)
                                  }
                                >
                                  <div className="flex flex-col">
                                    <span>{recipient.nickname}</span>
                                    <span className="text-sm text-muted-foreground">
                                      {recipient.accountNumber}
                                    </span>
                                  </div>
                                </div>
                              ))}
                            </div>
                          )}
                        </div>
                      </div>
                    )}
                  </div>
                </div>
                <Input
                  id="debtorAccountNumber"
                  placeholder="Account number"
                  value={formData.debtorAccountNumber}
                  onChange={handleChange}
                  className="col-span-3"
                />
              </div>
            </div>
            <div className="grid grid-cols-4 items-center gap-4">
              <Label htmlFor="amount" className="text-right">
                Amount
              </Label>
              <div className="relative col-span-3">
                <span className="absolute inset-y-0 left-0 flex items-center pl-3 text-muted-foreground">
                  ₫
                </span>
                <Input
                  id="amount"
                  placeholder="0"
                  className="pl-8"
                  value={formData.amount}
                  onChange={handleChange}
                />
              </div>
            </div>
            <div className="grid grid-cols-4 items-center gap-4">
              <Label htmlFor="content" className="text-right">
                Content
              </Label>
              <Textarea
                id="content"
                placeholder="Reason for the request"
                className="col-span-3"
                value={formData.content}
                onChange={handleChange}
              />
            </div>
          </div>
          {error && (
            <div className="p-3 bg-red-50 border border-red-200 text-red-700 rounded-md text-sm mb-4">
              {error}
            </div>
          )}
          <DialogFooter>
            <Button className="cursor-pointer" type="submit" disabled={loading}>
              {loading ? 'Sending...' : 'Send Request'}
            </Button>
          </DialogFooter>
        </form>
      </DialogContent>
    </Dialog>
  );
};

export default CreateDebtReminderDialog;
