import ActionDialog from '../common/action-dialog';

const PayDebtConfirmDialog = ({
  isOpen,
  onClose,
  onConfirm,
  loading,
  error,
  debtReminder,
  formatCurrency,
}) => {
  return (
    <ActionDialog
      isOpen={isOpen}
      onClose={() => {
        onClose();
      }}
      title="Pay Debt Confirmation"
      description={
        <div className="space-y-4">
          <div className="flex flex-col items-start gap-0 mt-4">
            {error && (
              <div className="p-3 bg-red-50 border border-red-200 text-red-700 rounded-md text-sm mb-4">
                {error}
              </div>
            )}
            <div>
              Do you want to pay{' '}
              <span className="font-semibold">
                {formatCurrency(debtReminder?.amount)}
              </span>{' '}
              to{' '}
              <span className="font-semibold">
                {debtReminder?.creditor?.fullName}
              </span>
              ?
            </div>
          </div>
        </div>
      }
      confirmText="Proceed Payment"
      variant="default"
      onConfirm={onConfirm}
      isLoading={loading}
    />
  );
};

export default PayDebtConfirmDialog;
