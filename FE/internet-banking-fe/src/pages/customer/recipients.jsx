import { useState, useEffect } from "react";
import { Search, Edit, Trash2, UserPlus } from "lucide-react";
import { useDispatch, useSelector } from "react-redux";
import DashboardLayout from "../../components/common/dashboard-layout";
import Pagination from "../../components/common/pagination";
import ConfirmationDialog from "../../components/common/confirmation-dialog";
import {
  fetchRecipients,
  addRecipient,
  deleteRecipient,
  clearError,
  setPage,
  updateRecipient,
} from "../../redux/recipientsSlice";
import { Avatar, AvatarFallback } from "../../components/ui/avatar";
import { User } from "lucide-react";

// --- Main Page Component ---
export default function RecipientsPage() {
  const dispatch = useDispatch();
  const {
    recipients,
    currentPage,
    totalPages,
    totalElements,
    pageSize,
    loading,
    error: globalError,
  } = useSelector((state) => state.recipients);
  const [searchTerm, setSearchTerm] = useState("");
  const [isAddOpen, setIsAddOpen] = useState(false);
  const [isDeleteOpen, setIsDeleteOpen] = useState(false);
  const [selectedRecipient, setSelectedRecipient] = useState(null);
  const [isEditOpen, setIsEditOpen] = useState(false);
  const [editRecipient, setEditRecipient] = useState(null);
  const [editNickname, setEditNickname] = useState("");
  const [addError, setAddError] = useState("");
  const [editError, setEditError] = useState("");

  // Fetch recipients on component mount, page, pageSize, or searchTerm change
  useEffect(() => {
    const params = { page: currentPage, size: pageSize };
    if (searchTerm) {
      params.nickname = searchTerm;
    }
    dispatch(fetchRecipients(params));
  }, [dispatch, currentPage, pageSize, searchTerm]);

  // Reset page to 1 if search results in fewer pages than currentPage
  useEffect(() => {
    if (currentPage > totalPages) {
      dispatch(setPage(1));
    }
    // eslint-disable-next-line
  }, [searchTerm, totalPages]);

  // Clear error when component unmounts
  useEffect(() => {
    return () => {
      dispatch(clearError());
    };
  }, [dispatch]);

  // Handle page change
  const handlePageChange = (page) => {
    dispatch(setPage(page));
  };

  // Handle delete recipient
  const handleDelete = (recipient) => {
    setSelectedRecipient(recipient);
    setIsDeleteOpen(true);
  };

  const confirmDelete = async () => {
    try {
      await dispatch(deleteRecipient(selectedRecipient.recipientId)).unwrap();
      setIsDeleteOpen(false);
      setSelectedRecipient(null);
    } catch (err) {
      console.error("Failed to delete recipient:", err);
    }
  };

  // Handle edit recipient
  const handleEdit = (recipient) => {
    setEditRecipient(recipient);
    setEditNickname(recipient.nickname || "");
    setEditError("");
    setIsEditOpen(true);
  };

  const confirmEdit = async () => {
    setEditError("");
    try {
      await dispatch(
        updateRecipient({
          id: editRecipient.recipientId,
          recipientData: { nickname: editNickname },
        })
      ).unwrap();
      setIsEditOpen(false);
      setEditRecipient(null);
      setEditNickname("");
    } catch (err) {
      setEditError(err?.message || "An error occurred");
    }
  };

  // Add recipient submit handler
  const handleAddRecipient = async (formData, resetForm) => {
    setAddError("");
    try {
      await dispatch(addRecipient(formData)).unwrap();
      setIsAddOpen(false);
      resetForm();
    } catch (err) {
      setAddError(err?.message || "An error occurred");
    }
  };

  return (
    <DashboardLayout>
      <div className="max-w-7xl mx-auto p-6 space-y-6">
        <div className="flex flex-col md:flex-row justify-between gap-4">
          <div>
            <h1 className="text-3xl font-bold tracking-tight">Recipients</h1>
            <p className="text-gray-600">Manage your transfer recipients</p>
          </div>
          <div className="flex items-center gap-2">
            <button
              onClick={() => setIsAddOpen(true)}
              className="inline-flex items-center px-4 py-2 bg-black text-white text-sm font-medium rounded-md hover:bg-gray-800 focus:outline-none focus:ring-2 focus:ring-black"
            >
              <UserPlus className="mr-2 h-4 w-4" />
              Add Recipient
            </button>
          </div>
        </div>

        {globalError && !isAddOpen && !isEditOpen && (
          <div className="p-4 bg-red-50 border border-red-200 text-red-700 rounded-md">
            {globalError}
          </div>
        )}

        <div className="bg-white shadow rounded-lg">
          <div className="p-6">
            <div className="flex flex-col md:flex-row justify-between gap-4 mb-6">
              <div>
                <h2 className="text-lg font-semibold">Your Recipients</h2>
                <p className="text-sm text-gray-600">
                  You have {totalElements} saved recipients
                </p>
              </div>
              <div className="relative">
                <Search className="absolute left-2.5 top-2.5 h-4 w-4 text-gray-400" />
                <input
                  type="search"
                  placeholder="Search recipients..."
                  className="w-full md:w-[300px] pl-8 pr-4 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
                  value={searchTerm}
                  onChange={(e) => setSearchTerm(e.target.value)}
                />
              </div>
            </div>

            <div className="border border-gray-200 rounded-md overflow-hidden">
              <div className="overflow-x-auto">
                <table className="w-full text-sm">
                  <thead className="bg-gray-50">
                    <tr>
                      <th className="h-12 px-4 text-left font-medium text-gray-600">
                        Name
                      </th>
                      <th className="h-12 px-4 text-left font-medium text-gray-600">
                        Account Number
                      </th>
                      <th className="h-12 px-4 text-left font-medium text-gray-600">
                        Bank
                      </th>
                      <th className="h-12 px-4 text-left font-medium text-gray-600">
                        Created At
                      </th>
                      <th className="h-12 px-4 text-left font-medium text-gray-600">
                        Actions
                      </th>
                    </tr>
                  </thead>
                  <tbody className="divide-y divide-gray-200">
                    {loading ? (
                      <tr>
                        <td colSpan={5} className="h-24 text-center">
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
                    ) : recipients.length > 0 ? (
                      recipients.map((recipient) => (
                        <tr
                          key={recipient.recipientId}
                          className="hover:bg-gray-50"
                        >
                          <td className="p-4">
                            <div className="flex items-center gap-3">
                              <Avatar className="h-9 w-9">
                                <AvatarFallback>
                                  <User className="w-4 h-4 text-gray-500" />
                                </AvatarFallback>
                              </Avatar>
                              <div>
                                <div className="font-medium">
                                  {recipient.fullName}
                                </div>
                                <div className="text-xs text-gray-500">
                                  {recipient.nickname}
                                </div>
                              </div>
                            </div>
                          </td>
                          <td className="p-4">{recipient.accountNumber}</td>
                          <td className="p-4">
                            {recipient.bank?.bankName || "SecureBank"}
                          </td>
                          <td className="p-4">
                            {new Date(recipient.createdAt).toLocaleDateString()}
                          </td>
                          <td className="p-4">
                            <div className="flex items-center gap-2">
                              <button
                                className="p-2 text-gray-400 hover:text-gray-600 hover:bg-gray-100 rounded"
                                onClick={() => handleEdit(recipient)}
                              >
                                <Edit className="h-4 w-4" />
                              </button>
                              <button
                                onClick={() => handleDelete(recipient)}
                                className="p-2 text-gray-400 hover:text-red-600 hover:bg-red-50 rounded"
                              >
                                <Trash2 className="h-4 w-4" />
                              </button>
                            </div>
                          </td>
                        </tr>
                      ))
                    ) : (
                      <tr>
                        <td
                          colSpan={5}
                          className="h-24 text-center text-gray-500"
                        >
                          No recipients found.
                        </td>
                      </tr>
                    )}
                  </tbody>
                </table>
              </div>
            </div>
          </div>

          {totalElements > 0 && (
            <div className="border-t border-gray-200 p-4">
              <Pagination
                currentPage={currentPage}
                totalPages={totalPages}
                totalItems={totalElements}
                itemsPerPage={pageSize}
                onPageChange={handlePageChange}
              />
            </div>
          )}
        </div>

        <AddRecipientDialog
          isOpen={isAddOpen}
          onClose={() => {
            setIsAddOpen(false);
            setAddError("");
            dispatch(clearError());
          }}
          loading={loading}
          error={addError}
          onSubmit={handleAddRecipient}
        />

        <EditRecipientDialog
          isOpen={isEditOpen}
          onClose={() => {
            setIsEditOpen(false);
            setEditRecipient(null);
            setEditNickname("");
            setEditError("");
            dispatch(clearError());
          }}
          loading={loading}
          nickname={editNickname}
          setNickname={setEditNickname}
          onSubmit={confirmEdit}
          error={editError}
        />

        <DeleteRecipientDialog
          isOpen={isDeleteOpen}
          onClose={() => {
            setIsDeleteOpen(false);
            setSelectedRecipient(null);
          }}
          loading={loading}
          recipient={selectedRecipient}
          onConfirm={confirmDelete}
        />
      </div>
    </DashboardLayout>
  );
}

// --- Dialog Components ---
function AddRecipientDialog({ isOpen, onClose, loading, error, onSubmit }) {
  const [formData, setFormData] = useState({
    accountNumber: "",
    nickname: "",
  });

  const handleSubmit = async () => {
    await onSubmit(formData, () =>
      setFormData({ accountNumber: "", nickname: "" })
    );
  };

  return (
    <ConfirmationDialog
      isOpen={isOpen}
      onClose={onClose}
      title="Add New Recipient"
      subtitle="Add a new recipient to your list for quick transfers."
      description={
        <div className="space-y-4">
          {error && (
            <div className="p-3 bg-red-50 border border-red-200 text-red-700 rounded-md text-sm mb-4">
              {error}
            </div>
          )}
          <div className="flex items-center gap-0">
            <label className="block w-24 text-sm font-medium text-gray-700">
              Account No.
            </label>
            <input
              type="text"
              value={formData.accountNumber}
              onChange={(e) =>
                setFormData((prev) => ({
                  ...prev,
                  accountNumber: e.target.value,
                }))
              }
              placeholder="Account number"
              className="flex-1 px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
            />
          </div>
          <div className="flex items-center gap-0">
            <label className="block w-24 text-sm font-medium text-gray-700">
              Nickname
            </label>
            <input
              type="text"
              value={formData.nickname}
              onChange={(e) =>
                setFormData((prev) => ({ ...prev, nickname: e.target.value }))
              }
              placeholder="Optional nickname"
              className="flex-1 px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
            />
          </div>
        </div>
      }
      confirmText="Save Recipient"
      cancelText="Cancel"
      variant="black"
      onConfirm={handleSubmit}
      isLoading={loading}
    />
  );
}

function EditRecipientDialog({
  isOpen,
  onClose,
  loading,
  nickname,
  setNickname,
  onSubmit,
  error,
}) {
  return (
    <ConfirmationDialog
      isOpen={isOpen}
      onClose={onClose}
      title="Edit Recipient"
      description={
        <div className="space-y-4">
          {error && (
            <div className="p-3 bg-red-50 border border-red-200 text-red-700 rounded-md text-sm mb-4">
              {error}
            </div>
          )}
          <div className="flex items-center gap-0 mt-4">
            <label className="block w-24 text-sm font-medium text-gray-700">
              Nickname
            </label>
            <input
              type="text"
              className="flex-1 px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
              value={nickname}
              onChange={(e) => setNickname(e.target.value)}
              placeholder="Enter new nickname"
            />
          </div>
        </div>
      }
      confirmText="Save"
      variant="default"
      onConfirm={onSubmit}
      isLoading={loading}
    />
  );
}

function DeleteRecipientDialog({
  isOpen,
  onClose,
  loading,
  recipient,
  onConfirm,
}) {
  return (
    <ConfirmationDialog
      isOpen={isOpen}
      onClose={onClose}
      title="Delete Recipient"
      description={`Are you sure you want to delete ${recipient?.fullName}? This action cannot be undone.`}
      confirmText="Delete"
      variant="destructive"
      onConfirm={onConfirm}
      isLoading={loading}
    />
  );
}
