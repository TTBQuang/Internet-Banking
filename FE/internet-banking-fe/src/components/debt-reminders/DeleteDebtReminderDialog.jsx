import ActionDialog from '../common/action-dialog';

const DeleteDebtReminderDialog = ({
  isOpen,
  onClose,
  loading,
  error,
  onSubmit,
  content,
  setContent,
}) => {
  return (
    <ActionDialog
      isOpen={isOpen}
      onClose={() => {
        onClose();
      }}
      title="Delete Debt Reminder"
      description={
        <div className="space-y-4">
          <div className="flex items-center gap-0 mt-4">
            {error && (
              <div className="p-3 bg-red-50 border border-red-200 text-red-700 rounded-md text-sm mb-4">
                {error}
              </div>
            )}
            <label className="block w-24 text-sm font-medium text-gray-700">
              Content
            </label>
            <input
              type="text"
              className="flex-1 px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
              value={content}
              onChange={(e) => setContent(e.target.value)}
              placeholder="Enter content"
            />
          </div>
        </div>
      }
      confirmText="Confirm Delete"
      variant="default"
      onConfirm={onSubmit}
      isLoading={loading}
    />
  );
};

export default DeleteDebtReminderDialog;
