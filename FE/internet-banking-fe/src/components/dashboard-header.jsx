import { useState } from "react";
import { Bell, ChevronDown, Search } from "lucide-react";
import { Avatar, AvatarFallback } from "@/components/ui/avatar";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
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
import { Link } from "react-router-dom";
import { User } from "lucide-react";
import { useDispatch } from "react-redux";
import { logout } from "../redux/userSlice";

export default function DashboardHeader() {
  const dispatch = useDispatch();
  const [notifications, setNotifications] = useState([
    {
      id: 1,
      title: "Money Received",
      description: "You received ₫15,000,000 from Tran Van B",
      time: "10 minutes ago",
      read: false,
    },
    {
      id: 2,
      title: "Transfer Successful",
      description: "Your transfer of ₫5,000,000 to Le Thi C was successful",
      time: "2 hours ago",
      read: false,
    },
    {
      id: 3,
      title: "Security Alert",
      description: "New login detected from Ho Chi Minh City",
      time: "Yesterday",
      read: true,
    },
  ]);

  const unreadCount = notifications.filter((n) => !n.read).length;

  const markAsRead = (id) => {
    setNotifications(
      notifications.map((notification) =>
        notification.id === id ? { ...notification, read: true } : notification
      )
    );
  };

  const markAllAsRead = () => {
    setNotifications(
      notifications.map((notification) => ({ ...notification, read: true }))
    );
  };

  return (
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
          <SheetContent>
            <SheetHeader>
              <SheetTitle>Notifications</SheetTitle>
              <SheetDescription>
                <Button
                  variant="link"
                  size="sm"
                  className="p-0 h-auto"
                  onClick={markAllAsRead}
                >
                  Mark all as read
                </Button>
              </SheetDescription>
            </SheetHeader>
            <div className="mt-4 space-y-4">
              {notifications.map((notification) => (
                <div
                  key={notification.id}
                  className={`rounded-lg p-3 ${
                    notification.read ? "bg-white" : "bg-blue-50"
                  }`}
                  onClick={() => markAsRead(notification.id)}
                >
                  <div className="font-medium">{notification.title}</div>
                  <div className="text-sm text-muted-foreground">
                    {notification.description}
                  </div>
                  <div className="mt-1 text-xs text-muted-foreground">
                    {notification.time}
                  </div>
                </div>
              ))}
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
                Nguyen Van A
              </div>
              <ChevronDown className="h-4 w-4 text-muted-foreground" />
            </div>
          </DropdownMenuTrigger>

          <DropdownMenuContent align="end">
            <DropdownMenuLabel>My Account</DropdownMenuLabel>
            <DropdownMenuSeparator />
            <DropdownMenuItem>
              <Link to="/customer/dashboard/profile" className="w-full">
                Profile
              </Link>
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
  );
}
