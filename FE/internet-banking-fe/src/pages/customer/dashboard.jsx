import {
  Card,
  CardContent,
  CardDescription,
  CardHeader,
  CardTitle,
} from "../../components/ui/card";
import { Button } from "../../components/ui/button";
import { Separator } from "../../components/ui/separator";
import {
  User,
  Mail,
  Phone,
  Wallet,
  Eye,
  EyeOff,
  CreditCard,
  ArrowRightLeft,
} from "lucide-react";
import DashboardLayout from "../../components/common/dashboard-layout";
import { useSelector, useDispatch } from "react-redux";
import { useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import { fetchAccount } from "../../redux/accountSlice";

export default function CustomerDashboardPage() {
  const navigate = useNavigate();
  const dispatch = useDispatch();
  const [showBalance, setShowBalance] = useState(true);
  const [showFullAccount, setShowFullAccount] = useState(false);

  // Format currency
  const formatCurrency = (amount) => {
    return new Intl.NumberFormat("vi-VN").format(amount);
  };

  const { fullName, email, phone } = useSelector((state) => state.user);
  const { account, loading: accountLoading } = useSelector(
    (state) => state.account
  );

  useEffect(() => {
    dispatch(fetchAccount());
  }, [dispatch]);

  const maskedAccount = account
    ? `****-****-****-${account.accountNumber.slice(-4)}`
    : "****-****-****-****";
  const displayAccount = showFullAccount
    ? account?.accountNumber || "Not available"
    : maskedAccount;

  const userData = {
    profile: {
      fullname: fullName || "User",
      email: email || "user@email.com",
      phone: phone || "Not available",
    },
    account: {
      account_number: account?.accountNumber || "Not available",
      balance: account?.balance || 0,
    },
  };

  return (
    <DashboardLayout>
      <div className="space-y-6">
        <div>
          <h1 className="text-3xl font-bold tracking-tight">Dashboard</h1>
          <p className="text-muted-foreground">
            Welcome back, {userData.profile.fullname}
          </p>
        </div>

        <div className="grid gap-6 md:grid-cols-2">
          {/* Profile Information */}
          <Card>
            <CardHeader>
              <CardTitle className="flex items-center gap-2">
                <User className="h-5 w-5 text-blue-600" />
                Personal Information
              </CardTitle>
              <CardDescription>Your profile information</CardDescription>
            </CardHeader>
            <CardContent className="space-y-4">
              <div className="space-y-4">
                <div className="flex items-center justify-between p-3 rounded-lg bg-gray-50">
                  <div className="flex items-center gap-3">
                    <User className="h-4 w-4 text-muted-foreground" />
                    <span className="text-sm text-muted-foreground">
                      Full Name
                    </span>
                  </div>
                  <span className="font-medium">
                    {userData.profile.fullname}
                  </span>
                </div>

                <div className="flex items-center justify-between p-3 rounded-lg bg-gray-50">
                  <div className="flex items-center gap-3">
                    <Mail className="h-4 w-4 text-muted-foreground" />
                    <span className="text-sm text-muted-foreground">Email</span>
                  </div>
                  <span className="font-medium text-sm">
                    {userData.profile.email}
                  </span>
                </div>

                <div className="flex items-center justify-between p-3 rounded-lg bg-gray-50">
                  <div className="flex items-center gap-3">
                    <Phone className="h-4 w-4 text-muted-foreground" />
                    <span className="text-sm text-muted-foreground">
                      Phone Number
                    </span>
                  </div>
                  <span className="font-medium">{userData.profile.phone}</span>
                </div>
              </div>
            </CardContent>
          </Card>

          {/* Account Information */}
          <Card>
            <CardHeader>
              <CardTitle className="flex items-center gap-2">
                <Wallet className="h-5 w-5 text-green-600" />
                Account Information
              </CardTitle>
              <CardDescription>Your banking account details</CardDescription>
            </CardHeader>
            <CardContent className="space-y-4">
              {/* Balance Display */}
              <div className="rounded-lg bg-gradient-to-r from-green-600 to-green-700 p-6 text-white">
                <div className="flex justify-between items-start mb-4">
                  <div>
                    <p className="text-green-100 text-sm mb-1">
                      Account Balance
                    </p>
                    <p className="text-3xl font-bold">
                      {showBalance
                        ? formatCurrency(userData.account.balance)
                        : "••••••••"}
                    </p>
                  </div>
                  <Button
                    variant="ghost"
                    size="sm"
                    className="text-white hover:bg-green-500"
                    onClick={() => setShowBalance(!showBalance)}
                    disabled={accountLoading}
                  >
                    {showBalance ? (
                      <EyeOff className="h-4 w-4" />
                    ) : (
                      <Eye className="h-4 w-4" />
                    )}
                  </Button>
                </div>
                <div className="flex justify-between items-end">
                  <div>
                    <p className="text-green-100 text-xs mb-1">
                      Account Number
                    </p>
                    <p className="font-mono text-sm">{maskedAccount}</p>
                  </div>
                  <CreditCard className="h-6 w-6 text-green-200" />
                </div>
              </div>

              {/* Account Details */}
              <div className="space-y-4">
                <div className="flex items-center justify-between p-3 rounded-lg bg-gray-50">
                  <span className="text-sm text-muted-foreground">
                    Account Number
                  </span>
                  <div className="flex items-center gap-2">
                    <span className="font-mono text-sm">{displayAccount}</span>
                    <Button
                      variant="ghost"
                      size="sm"
                      className="h-6 w-6 p-0"
                      onClick={() => setShowFullAccount(!showFullAccount)}
                      disabled={accountLoading}
                    >
                      {showFullAccount ? (
                        <EyeOff className="h-3 w-3" />
                      ) : (
                        <Eye className="h-3 w-3" />
                      )}
                    </Button>
                  </div>
                </div>

                <div className="flex items-center justify-between p-3 rounded-lg bg-gray-50">
                  <span className="text-sm text-muted-foreground">Balance</span>
                  <div className="flex items-center gap-2">
                    <span className="font-bold text-lg text-green-600">
                      {showBalance
                        ? formatCurrency(userData.account.balance)
                        : "••••••••"}
                    </span>
                    <Button
                      variant="ghost"
                      size="sm"
                      className="h-6 w-6 p-0"
                      onClick={() => setShowBalance(!showBalance)}
                      disabled={accountLoading}
                    >
                      {showBalance ? (
                        <EyeOff className="h-3 w-3" />
                      ) : (
                        <Eye className="h-3 w-3" />
                      )}
                    </Button>
                  </div>
                </div>
              </div>

              <Separator />

              <Button
                className="w-full"
                onClick={() => navigate("/customer/dashboard/transfer")}
                disabled={accountLoading}
              >
                <ArrowRightLeft className="mr-2 h-4 w-4" />
                Transfer
              </Button>
            </CardContent>
          </Card>
        </div>
      </div>
    </DashboardLayout>
  );
}
