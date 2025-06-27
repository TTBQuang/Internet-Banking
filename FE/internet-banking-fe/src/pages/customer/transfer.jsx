import DashboardLayout from '../../components/common/dashboard-layout';
import { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { Button } from '../../components/ui/button';
import {
  Card,
  CardContent,
  CardDescription,
  CardFooter,
  CardHeader,
  CardTitle,
} from '../../components/ui/card';
import { Input } from '../../components/ui/input';
import { Label } from '../../components/ui/label';
import {
  Tabs,
  TabsContent,
  TabsList,
  TabsTrigger,
} from '../../components/ui/tabs';
import { Textarea } from '../../components/ui/textarea';
import { ArrowRight, Loader2, Search, User, CheckCircle2 } from 'lucide-react';
import { useDispatch, useSelector } from 'react-redux';
import { fetchAllRecipients, addRecipient } from '../../redux/recipientsSlice';
import {
  initiateTransfer,
  confirmInternalTransfer,
} from '../../redux/transferSlice';
import {
  fetchAccountByAccountNumber,
  fetchAccountNumberByUserId,
} from '../../redux/accountSlice';
import {
  fetchAllLinkedBanks,
  fetchAccountInfo,
} from '../../redux/linkedBankSlice';
import {
  clearState,
  confirmDebtPayment,
  initiateDebtPayment,
} from '@/redux/debtPaymentSlice';

export default function TransferPage() {
  const navigate = useNavigate();

  const dispatch = useDispatch();
  const recipients = useSelector((state) => state.recipients.recipients);
  const bankOptions = useSelector((state) => state.linkedBanks.linkedBanks);
  const transactionId = useSelector((state) => state.transfers.transactionId);
  const debtPaymentTransactionId = useSelector(
    (state) => state.debtPayment.transactionId
  );

  const [step, setStep] = useState(1);
  const [transferType, setTransferType] = useState('recipient');
  const [receiverInfo, setReceiverInfo] = useState(null);
  const [isSearching, setIsSearching] = useState(false);
  const [amount, setAmount] = useState('');
  const [description, setDescription] = useState('');
  const [otp, setOtp] = useState(['', '', '', '', '', '']);
  const [isConfirming, setIsConfirming] = useState(false);
  const [isVerifying, setIsVerifying] = useState(false);
  const [isTransferComplete, setIsTransferComplete] = useState(false);
  const [error, setError] = useState('');
  const [searchTerm, setSearchTerm] = useState('');
  const [accountNumber, setAccountNumber] = useState('');
  const [selectedBankCode, setSelectedBankCode] = useState('');
  const [showSaveModal, setShowSaveModal] = useState(false);
  const [nickname, setNickname] = useState('');
  const [isRecipientSaved, setIsRecipientSaved] = useState(false);
  const [toastMessage, setToastMessage] = useState('');
  const [isSavingRecipient, setIsSavingRecipient] = useState(false);

  useEffect(() => {
    dispatch(fetchAllRecipients(searchTerm));
  }, [dispatch, searchTerm]);

  useEffect(() => {
    setReceiverInfo(null);
  }, [searchTerm]);

  const setStepAndClearError = (step) => {
    setError('');
    setStep(step);
  };

  // Handle if debt payment is in progress
  const { debtReminder } = useSelector((state) => state.debtPayment);
  useEffect(() => {
    if (!debtReminder) {
      return;
    }

    const handleDebtPayment = async () => {
      const creditorAccountNumber = await dispatch(
        fetchAccountNumberByUserId(debtReminder.creditor.userId)
      ).unwrap();

      setReceiverInfo({
        accountNumber: creditorAccountNumber,
        fullName: debtReminder.creditor.fullName,
      });
      setStep(2);
      setDescription(`Debt payment to ${debtReminder.creditor.fullName}`);
      setAmount(debtReminder.amount);
    };

    handleDebtPayment();
  }, [debtReminder, dispatch]);

  // Fetch recipients data
  useEffect(() => {
    dispatch(fetchAllRecipients());
  }, [dispatch]);

  // Fetch linked banks data
  useEffect(() => {
    dispatch(fetchAllLinkedBanks());
  }, [dispatch]);

  // Format currency
  const formatCurrency = (value) => {
    if (!value) return '';
    // Remove non-digit characters
    const digits = value.toString().replace(/\D/g, '');
    // Format with thousand separators
    return new Intl.NumberFormat('vi-VN').format(Number(digits));
  };

  // Handle amount input change
  const handleAmountChange = (e) => {
    setError('');
    const value = e.target.value.replace(/\D/g, '');
    setAmount(value);
  };

  // Handle account number search
  const handleAccountSearch = async () => {
    try {
      setReceiverInfo(null);
      setError('');
      setIsSearching(true);

      // Simulate API call
      if (selectedBankCode === 'SCB') {
        await dispatch(fetchAccountByAccountNumber(accountNumber))
          .unwrap()
          .then((recipient) => {
            setReceiverInfo(recipient);
            setIsSearching(false);
          })
          .catch((err) => {
            setIsSearching(false);
            setError(err);
          });
      } else {
        await dispatch(fetchAccountInfo({ selectedBankCode, accountNumber }))
          .unwrap()
          .then((account) => {
            setReceiverInfo(account);
            setIsSearching(false);
          })
          .catch((err) => {
            setIsSearching(false);
            setError(err);
          });
      }
    } catch (err) {
      console.log(err);
      setError(err);
    } finally {
      setIsSearching(false);
    }
  };

  // Handle OTP input
  const handleOtpChange = (index, value) => {
    setError('');
    if (value.length > 1) {
      value = value.slice(0, 1);
    }

    const newOtp = [...otp];
    newOtp[index] = value;
    setOtp(newOtp);

    // Auto-focus next input
    if (value && index < 5) {
      document.getElementById(`otp-${index + 1}`).focus();
    }
  };

  // Handle OTP verification
  const handleVerifyOtp = async () => {
    try {
      setError('');
      setIsVerifying(true);

      // Handle debt payment
      if (debtReminder) {
        await dispatch(
          confirmDebtPayment({
            transactionId: debtPaymentTransactionId,
            debtReminderId: debtReminder.debtReminderId,
            otpCode: otp.join(''),
          })
        ).unwrap();

        // Clear debt reminder state after successful payment
        dispatch(clearState());
      } else {
        await dispatch(
          confirmInternalTransfer({ transactionId, otp: otp.join('') })
        ).unwrap();
      }

      setIsTransferComplete(true);
    } catch (err) {
      setError(err);
    } finally {
      setIsVerifying(false);
    }
  };

  // Handle recipient selection
  const handleSelectRecipient = (recipient) => {
    setError('');
    setReceiverInfo(recipient);
  };

  // Reset the form
  const resetForm = () => {
    setSearchTerm('');
    setError('');
    setToastMessage('');
    setStep(1);
    setTransferType('recipient');
    setReceiverInfo(null);
    setAmount('');
    setDescription('');
    setOtp(['', '', '', '', '', '']);
    setIsTransferComplete(false);
    setIsConfirming(false);
    setAccountNumber('');
    setSelectedBankCode('');
    setShowSaveModal(false);
    setIsRecipientSaved(false);
    setIsSavingRecipient(false);

    // Clear debt reminder state
    dispatch(clearState());
  };

  // Handle confirm transfer
  const handleConfirmTransfer = async () => {
    try {
      setError('');
      setIsConfirming(true);

      let payload = {
        receiverAccountNumber: receiverInfo.accountNumber,
        amount: parseFloat(amount),
        content: description,
      };
      const receiverBankCode =
        transferType !== 'account'
          ? receiverInfo.bank?.bankCode || null
          : selectedBankCode;
      if (receiverBankCode != null) {
        payload = { receiverBankCode, ...payload };
      }

      // Handle debt payment
      if (debtReminder) {
        await dispatch(
          initiateDebtPayment({
            creditorAccountNumber: receiverInfo.accountNumber,
            content: description,
            debtReminderId: debtReminder.debtReminderId,
          })
        ).unwrap();

        setStep(3);
        return;
      }

      await dispatch(initiateTransfer(payload)).unwrap();
      setStep(3);
    } catch (err) {
      console.log(err);
      setError(err);
    } finally {
      setIsConfirming(false);
    }
  };

  // Handle save recipient
  const handleSaveRecipient = async () => {
    try {
      const bankId =
        bankOptions.find((bank) => bank.bankCode === selectedBankCode)
          ?.linkedBankId || null;
      await dispatch(
        addRecipient({
          accountNumber: receiverInfo.accountNumber,
          fullName: receiverInfo.fullName || receiverInfo.nickname,
          bankId: bankId,
          nickname: nickname.trim(),
        })
      ).unwrap();

      setToastMessage('Recipient saved successfully!');
    } catch (err) {
      setToastMessage(err?.message || 'An error occurred');
    } finally {
      setShowSaveModal(false);
      setIsSavingRecipient(false);
    }
  };

  return (
    <DashboardLayout>
      <div className="space-y-6">
        <div>
          <h1 className="text-3xl font-bold tracking-tight">Transfer Money</h1>
          <p className="text-muted-foreground">Send money to other accounts</p>
        </div>

        <Card className="max-w-2xl mx-auto">
          {!isTransferComplete ? (
            <>
              <CardHeader>
                <CardTitle>Money Transfer</CardTitle>
                <CardDescription>
                  {step === 1
                    ? 'Enter recipient details and amount'
                    : step === 2
                    ? 'Confirm transfer details'
                    : 'Verify with OTP'}
                </CardDescription>
              </CardHeader>
              <CardContent>
                {step === 1 && (
                  <div className="space-y-6">
                    <Tabs
                      value={transferType}
                      onValueChange={(value) => {
                        setTransferType(value);
                        setReceiverInfo(null);
                      }}
                    >
                      <TabsList className="grid w-full grid-cols-2">
                        <TabsTrigger value="recipient">
                          Select Recipient
                        </TabsTrigger>
                        <TabsTrigger value="account">
                          Enter Account Number
                        </TabsTrigger>
                      </TabsList>
                      <TabsContent value="recipient" className="space-y-4 mt-4">
                        <div className="space-y-4">
                          <Label>Select a recipient</Label>
                          <div className="space-y-4">
                            <Input
                              type="text"
                              placeholder="Search by nickname or account number"
                              value={searchTerm}
                              onChange={(e) => setSearchTerm(e.target.value)}
                            />
                          </div>
                          <div className="space-y-2 max-h-72 overflow-y-auto pr-1">
                            {recipients && recipients.length > 0 ? (
                              recipients.map((recipient) => (
                                <div
                                  key={recipient.recipientId}
                                  className={`p-4 border rounded-lg cursor-pointer transition-colors ${
                                    receiverInfo?.recipientId ===
                                    recipient.recipientId
                                      ? 'border-blue-500 bg-blue-50'
                                      : 'hover:bg-gray-50'
                                  }`}
                                  onClick={() =>
                                    handleSelectRecipient(recipient)
                                  }
                                >
                                  <div className="flex items-center justify-between">
                                    <div className="flex items-center gap-3">
                                      <div className="bg-blue-100 p-2 rounded-full">
                                        <User className="h-4 w-4 text-blue-600" />
                                      </div>
                                      <div>
                                        <p className="font-medium">
                                          {recipient.bank?.bankName ||
                                            'Secure Bank'}
                                        </p>
                                        <p className="font-medium">
                                          {recipient.nickname} -{' '}
                                          <span className="text-sm text-muted-foreground">
                                            {recipient.accountNumber}
                                          </span>
                                        </p>
                                      </div>
                                    </div>
                                    {receiverInfo?.recipientId ===
                                      recipient.recipientId && (
                                      <CheckCircle2 className="h-5 w-5 text-blue-600" />
                                    )}
                                  </div>
                                </div>
                              ))
                            ) : (
                              <p>No recipients found</p>
                            )}
                          </div>
                        </div>
                      </TabsContent>

                      <TabsContent value="account" className="space-y-4 mt-4">
                        <div className="space-y-2">
                          <div className="space-y-2">
                            <Label htmlFor="bank">Select Bank</Label>
                            <select
                              id="bank"
                              className="w-full border rounded-md p-2"
                              value={selectedBankCode}
                              onChange={(e) =>
                                setSelectedBankCode(e.target.value)
                              }
                            >
                              <option value="">-- Choose a bank --</option>
                              <option key="SCB" value="SCB">
                                Secure Bank
                              </option>
                              {bankOptions.map((bank) => (
                                <option
                                  key={bank.bankCode}
                                  value={bank.bankCode}
                                >
                                  {bank.bankName}
                                </option>
                              ))}
                            </select>
                          </div>
                          <Label htmlFor="account-number">Account Number</Label>
                          <div className="flex gap-2">
                            <Input
                              id="account-number"
                              placeholder="Enter account number"
                              className="flex-1"
                              value={accountNumber}
                              onChange={(e) => setAccountNumber(e.target.value)}
                            />
                            <Button
                              type="button"
                              onClick={() => handleAccountSearch(accountNumber)}
                              disabled={
                                !accountNumber.trim() ||
                                isSearching ||
                                !selectedBankCode.trim()
                              }
                            >
                              {isSearching ? (
                                <Loader2 className="h-4 w-4 animate-spin" />
                              ) : (
                                <Search className="h-4 w-4" />
                              )}
                            </Button>
                          </div>
                        </div>

                        {receiverInfo && (
                          <div className="p-4 border rounded-lg bg-blue-50">
                            <div className="flex items-center gap-3">
                              <div className="bg-blue-100 p-2 rounded-full">
                                <User className="h-4 w-4 text-blue-600" />
                              </div>
                              <div>
                                <p className="font-medium">
                                  {receiverInfo.name}
                                </p>
                                <p className="text-sm text-muted-foreground">
                                  {receiverInfo.accountNumber} -{' '}
                                  {receiverInfo.fullName}
                                </p>
                              </div>
                            </div>
                          </div>
                        )}
                      </TabsContent>
                    </Tabs>

                    <div className="space-y-2">
                      <Label htmlFor="amount">Amount</Label>
                      <div className="relative">
                        <span className="absolute inset-y-0 left-0 flex items-center pl-3 text-muted-foreground">
                          ₫
                        </span>
                        <Input
                          id="amount"
                          value={formatCurrency(amount)}
                          onChange={handleAmountChange}
                          className="pl-8"
                          placeholder="0"
                        />
                      </div>
                    </div>

                    <div className="space-y-2">
                      <Label htmlFor="description">Description</Label>
                      <Textarea
                        id="description"
                        value={description}
                        onChange={(e) => setDescription(e.target.value)}
                        placeholder="Enter transfer description"
                        rows={3}
                      />
                    </div>
                  </div>
                )}

                {step === 2 && receiverInfo && (
                  <div className="space-y-6">
                    <div className="space-y-4">
                      <div className="space-y-2">
                        <p className="text-sm text-muted-foreground">
                          Receiver Account
                        </p>
                        <div className="p-4 border rounded-lg">
                          <div className="flex items-center gap-3">
                            <div className="bg-blue-100 p-2 rounded-full">
                              <User className="h-4 w-4 text-blue-600" />
                            </div>
                            <div>
                              <p className="font-medium">
                                {receiverInfo.nickname || receiverInfo.fullName}
                              </p>
                              <p className="text-sm text-muted-foreground">
                                {receiverInfo.accountNumber}
                              </p>
                            </div>
                          </div>
                        </div>
                      </div>

                      {transferType === 'account' && selectedBankCode && (
                        <div className="space-y-2">
                          <p className="text-sm text-muted-foreground">Bank</p>
                          <div className="p-4 border rounded-lg">
                            <p className="font-medium">
                              {bankOptions.find(
                                (bank) => bank.bankCode === selectedBankCode
                              )?.bankName || 'Secure Bank'}
                            </p>
                          </div>
                        </div>
                      )}

                      {transferType !== 'account' && (
                        <div className="space-y-2">
                          <p className="text-sm text-muted-foreground">Bank</p>
                          <div className="p-4 border rounded-lg">
                            <p className="font-medium">
                              {bankOptions.find(
                                (bank) =>
                                  bank.bankCode === receiverInfo?.bank?.bankCode
                              )?.bankName || 'Secure Bank'}
                            </p>
                          </div>
                        </div>
                      )}

                      <div className="space-y-2">
                        <p className="text-sm text-muted-foreground">Amount</p>
                        <div className="p-4 border rounded-lg">
                          <p className="text-xl font-bold">
                            ₫{formatCurrency(amount)}
                          </p>
                        </div>
                      </div>

                      <div className="space-y-2">
                        <p className="text-sm text-muted-foreground">
                          Description
                        </p>
                        <div className="p-4 border rounded-lg">
                          <p>{description || 'No description provided'}</p>
                        </div>
                      </div>

                      <div className="space-y-2">
                        <p className="text-sm text-muted-foreground">Fee</p>
                        <div className="p-4 border rounded-lg">
                          <p className="font-medium">₫0</p>
                          <p className="text-sm text-muted-foreground">
                            No transfer fee
                          </p>
                        </div>
                      </div>
                    </div>
                  </div>
                )}

                {step === 3 && (
                  <div className="space-y-6">
                    <div className="text-center">
                      <p className="mb-4">
                        Enter the 6-digit OTP sent to your registered email
                      </p>
                      <div className="flex justify-center gap-2">
                        {otp.map((digit, index) => (
                          <Input
                            key={index}
                            id={`otp-${index}`}
                            type="text"
                            inputMode="numeric"
                            pattern="[0-9]*"
                            maxLength={1}
                            className="w-12 h-12 text-center text-lg"
                            value={digit}
                            onChange={(e) =>
                              handleOtpChange(index, e.target.value)
                            }
                          />
                        ))}
                      </div>
                    </div>
                  </div>
                )}

                {error && (
                  <div className="p-3 mt-3 bg-red-50 border border-red-200 text-red-700 rounded-md text-sm">
                    {error}
                  </div>
                )}
              </CardContent>
              <CardFooter className="flex justify-between">
                {step > 1 ? (
                  <Button
                    variant="outline"
                    onClick={() => {
                      if (step === 2 && debtReminder) {
                        dispatch(clearState());
                        resetForm();
                        return;
                      }
                      setOtp(['', '', '', '', '', '']);
                      setStepAndClearError(step - 1);
                    }}
                  >
                    Back
                  </Button>
                ) : (
                  <Button variant="outline" onClick={resetForm}>
                    Cancel
                  </Button>
                )}
                {step === 1 ? (
                  <Button
                    onClick={() => setStepAndClearError(2)}
                    disabled={!receiverInfo || !amount}
                  >
                    Continue
                    <ArrowRight className="ml-2 h-4 w-4" />
                  </Button>
                ) : step === 2 ? (
                  <Button
                    onClick={handleConfirmTransfer}
                    disabled={isConfirming}
                  >
                    Confirm Transfer
                    <ArrowRight className="ml-2 h-4 w-4" />
                  </Button>
                ) : (
                  <Button
                    onClick={handleVerifyOtp}
                    disabled={otp.some((digit) => !digit) || isVerifying}
                  >
                    {isVerifying ? (
                      <>
                        <Loader2 className="mr-2 h-4 w-4 animate-spin" />
                        Verifying...
                      </>
                    ) : (
                      <>
                        Complete Transfer
                        <ArrowRight className="ml-2 h-4 w-4" />
                      </>
                    )}
                  </Button>
                )}
              </CardFooter>
            </>
          ) : (
            <div className="p-8 text-center">
              <div className="mx-auto w-16 h-16 bg-green-100 rounded-full flex items-center justify-center mb-4">
                <CheckCircle2 className="h-8 w-8 text-green-600" />
              </div>
              <h2 className="text-2xl font-bold mb-2">Transfer Successful!</h2>
              <p className="text-muted-foreground mb-6">
                You have successfully transferred ₫ {formatCurrency(amount)} to{' '}
                {receiverInfo.nickname || receiverInfo.fullName}
              </p>
              <div className="space-y-4">
                <Button className="w-full" onClick={resetForm}>
                  Make Another Transfer
                </Button>
                <Button
                  variant="outline"
                  className="w-full"
                  onClick={() => navigate('/customer/dashboard/history')}
                >
                  View Receipt
                </Button>
                {transferType === 'account' && (
                  <Button
                    variant="outline"
                    className="w-full"
                    disabled={isRecipientSaved}
                    onClick={() => {
                      setNickname(
                        receiverInfo.nickname || receiverInfo.fullName
                      );
                      setIsRecipientSaved(true);
                      setShowSaveModal(true);
                    }}
                  >
                    Save as Recipient
                  </Button>
                )}
                {isRecipientSaved && (
                  <p
                    className={`mt-4 text-sm text-center rounded-md p-2 ${
                      toastMessage.toLowerCase().includes('successfully')
                        ? 'bg-green-100 text-green-700'
                        : 'bg-red-100 text-red-700'
                    }`}
                  >
                    {toastMessage}
                  </p>
                )}
              </div>
            </div>
          )}
        </Card>
      </div>

      {showSaveModal && (
        <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/20 backdrop-blur-sm">
          <div className="bg-white rounded-lg shadow-lg p-6 w-[90%] max-w-md mx-auto space-y-4">
            <h2 className="text-lg font-semibold">Save as Recipient</h2>
            <div className="space-y-2">
              <Label htmlFor="nickname">Nickname</Label>
              <Input
                id="nickname"
                value={nickname}
                onChange={(e) => setNickname(e.target.value)}
              />
            </div>
            <div className="flex justify-end gap-2 pt-4">
              <Button
                variant="outline"
                onClick={() => {
                  setShowSaveModal(false);
                  setIsSavingRecipient(false);
                  setIsRecipientSaved(false);
                }}
              >
                Cancel
              </Button>
              <Button
                disabled={isSavingRecipient}
                onClick={() => {
                  setIsSavingRecipient(true);
                  handleSaveRecipient();
                }}
              >
                Save
              </Button>
            </div>
          </div>
        </div>
      )}
    </DashboardLayout>
  );
}
