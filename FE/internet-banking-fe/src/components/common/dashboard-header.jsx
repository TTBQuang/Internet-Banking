import { useState, useEffect } from "react";
import { Bell, ChevronDown, Search } from "lucide-react";
import { Avatar, AvatarFallback } from "@/components/ui/avatar";
import { Button } from "@/components/ui/button";
import {
  DropdownMenu,
  DropdownMenuContent,
  DropdownMenuItem,
  DropdownMenuLabel,
  DropdownMenuSeparator,
  DropdownMenuTrigger,
} from "@/components/ui/dropdown-menu";
import {
  Sheet,
  SheetContent,
  SheetDescription,
  SheetHeader,
  SheetTitle,
  SheetTrigger,
} from "@/components/ui/sheet";
import { Link, useNavigate } from "react-router-dom";
import { User } from "lucide-react";
import { useDispatch, useSelector } from "react-redux";
import { logout } from "../../redux/userSlice";
import {
  Dialog,
  DialogContent,
  DialogHeader,
  DialogTitle,
  DialogFooter,
} from "@/components/ui/dialog";
import { Input } from "@/components/ui/input";
import {
  changePassword,
  resetChangePasswordState,
} from "@/redux/changePasswordSlice";
import {
  fetchNotifications,
  markNotificationAsRead,
  markAllNotificationsAsRead,
} from "@/redux/notificationsSlice";
import { format } from "date-fns";

export default function DashboardHeader() {
  const dispatch = useDispatch();
  const navigate = useNavigate();
  const fullName = useSelector((state) => state.user.fullName);
  const {
    loading: changeLoading,
    error: changeError,
    success: changeSuccess,
  } = useSelector((state) => state.changePassword);
  
  const { items: notifications, loading: notificationsLoading } = useSelector(
    (state) => state.notifications
  );

  // Fetch notifications when component mounts
  useEffect(() => {
    dispatch(fetchNotifications());
  }, [dispatch]);

  const unreadCount = notifications.filter((n) => !n.read).length;

  const handleNotificationClick = async (notification) => {
    // Update state and call API in background
    dispatch(markNotificationAsRead(notification.notificationId));
    
    // Navigate to debt reminders tab within customer dashboard
    navigate("/customer/dashboard/debt-reminders");
  };

  const handleMarkAllAsRead = async () => {
    await dispatch(markAllNotificationsAsRead());
  };

  const [showChangePassword, setShowChangePassword] = useState(false);
  const [oldPassword, setOldPassword] = useState("");
  const [newPassword, setNewPassword] = useState("");

  const handleChangePassword = async () => {
    dispatch(changePassword({ oldPassword, newPassword }));
  };

  const handleDialogClose = (open) => {
    setShowChangePassword(open);
    if (!open) {
      setOldPassword("");
      setNewPassword("");
      dispatch(resetChangePasswordState());
    }
  };

  const formatNotificationTime = (dateString) => {
    const date = new Date(dateString);
    const now = new Date();
    const diffInMinutes = Math.floor((now - date) / (1000 * 60));
    
    if (diffInMinutes < 60) {
      return `${diffInMinutes} minutes ago`;
    } else if (diffInMinutes < 1440) {
      const hours = Math.floor(diffInMinutes / 60);
      return `${hours} hours ago`;
    } else {
      return format(date, "MMM d, yyyy");
    }
  };

  return (
    <>
      <header className="sticky top-0 z-30 flex h-16 items-center gap-4 border-b bg-white px-6">
        <div className="hidden md:flex md:flex-1 md:items-center md:gap-4 lg:gap-8"></div>
        <div className="flex items-center gap-4">
          <Sheet>
            <SheetTrigger asChild>
              <Button variant="outline" size="icon" className="relative">
                <Bell className="h-5 w-5" />
                {unreadCount > 0 && (
                  <span className="absolute -top-1 -right-1 flex h-5 w-5 items-center justify-center rounded-full bg-red-500 text-[10px] font-medium text-white">
                    {unreadCount}
                  </span>
                )}
              </Button>
            </SheetTrigger>
            <SheetContent className="w-[380px] sm:w-[400px]">
              <SheetHeader className="space-y-0.5 px-4 pb-1">
                <SheetTitle className="text-xl">Notifications</SheetTitle>
                <SheetDescription>
                  <Button
                    variant="link"
                    size="sm"
                    className="p-0 h-auto text-muted-foreground hover:text-primary"
                    onClick={handleMarkAllAsRead}
                  >
                    Mark all as read
                  </Button>
                </SheetDescription>
              </SheetHeader>
              <div className="mt-2 px-4">
                <div className="space-y-3">
                  {notificationsLoading ? (
                    <div className="text-center text-muted-foreground">
                      Loading notifications...
                    </div>
                  ) : notifications.length === 0 ? (
                    <div className="text-center text-muted-foreground">
                      No notifications
                    </div>
                  ) : (
                    notifications.map((notification) => (
                      <div
                        key={notification.notificationId}
                        className={`p-4 rounded-lg cursor-pointer transition-colors ${
                          notification.read ? "bg-white hover:bg-gray-50" : "bg-blue-50 hover:bg-blue-100"
                        }`}
                        onClick={() => handleNotificationClick(notification)}
                      >
                        <div className="font-semibold text-[15px]">{notification.title}</div>
                        <div className="mt-1 text-[14px] text-muted-foreground">
                          {notification.content.split('\n').map((line, idx) => (
                            <span key={idx}>
                              {line}
                              <br />
                            </span>
                          ))}
                        </div>
                        <div className="mt-2 text-[13px] text-muted-foreground">
                          {formatNotificationTime(notification.createdAt)}
                        </div>
                      </div>
                    ))
                  )}
                </div>
              </div>
            </SheetContent>
          </Sheet>
          <DropdownMenu modal={false}>
            <DropdownMenuTrigger asChild>
              <div className="flex items-center gap-2 cursor-pointer select-none">
                <Avatar className="h-8 w-8">
                  <AvatarFallback>
                    <User className="w-4 h-4 text-gray-500" />
                  </AvatarFallback>
                </Avatar>
                <div className="hidden md:block text-sm font-normal">
                  {fullName || "User"}
                </div>
                <ChevronDown className="h-4 w-4 text-muted-foreground" />
              </div>
            </DropdownMenuTrigger>

            <DropdownMenuContent align="end">
              <DropdownMenuLabel>My Account</DropdownMenuLabel>
              <DropdownMenuSeparator />
              <DropdownMenuItem onClick={() => setShowChangePassword(true)}>
                Change password
              </DropdownMenuItem>
              <DropdownMenuSeparator />
              <DropdownMenuItem asChild>
                <Link
                  to="#"
                  onClick={() => {
                    dispatch(logout());
                  }}
                  className="w-full"
                >
                  Logout
                </Link>
              </DropdownMenuItem>
            </DropdownMenuContent>
          </DropdownMenu>
        </div>
      </header>

      {/* Change Password Dialog */}
      <Dialog open={showChangePassword} onOpenChange={handleDialogClose}>
        <DialogContent className="max-w-sm">
          <DialogHeader className="text-center">
            <DialogTitle className="text-2xl font-bold text-center">
              Change Password
            </DialogTitle>
          </DialogHeader>
          <div className="space-y-4">
            <Input
              type="password"
              placeholder="Current password"
              value={oldPassword}
              onChange={(e) => setOldPassword(e.target.value)}
              disabled={changeLoading}
            />
            <Input
              type="password"
              placeholder="New password"
              value={newPassword}
              onChange={(e) => setNewPassword(e.target.value)}
              disabled={changeLoading}
            />
            {changeError && (
              <div className="p-3 bg-red-50 border border-red-200 text-red-700 rounded-md text-sm text-center">
                {changeError}
              </div>
            )}
            {changeSuccess && (
              <div className="p-3 bg-green-50 border border-green-200 text-green-700 rounded-md text-sm text-center">
                {changeSuccess}
              </div>
            )}
          </div>
          <DialogFooter className="flex flex-col gap-2 mt-4">
            <Button
              variant="outline"
              onClick={() => handleDialogClose(false)}
              disabled={changeLoading}
            >
              Cancel
            </Button>
            <Button
              variant="black"
              onClick={handleChangePassword}
              disabled={changeLoading}
            >
              {changeLoading ? "Changing..." : "Confirm"}
            </Button>
          </DialogFooter>
        </DialogContent>
      </Dialog>
    </>
  );
}
