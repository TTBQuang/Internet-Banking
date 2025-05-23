import { useState } from "react";
import { Link } from "react-router-dom";
import {
  Shield,
  LayoutDashboard,
  ArrowRightLeft,
  Users,
  Clock,
  Settings,
  HelpCircle,
  LogOut,
  ChevronLeft,
  ChevronRight,
  Menu,
  AlertCircle,
  User,
} from "lucide-react";
import { Button } from "@/components/ui/button";
import { useMobile } from "../../hooks/use-mobile";
import { useDispatch } from "react-redux";
import { logout } from "../../redux/userSlice";

export default function DashboardSidebar() {
  const dispatch = useDispatch();
  const isMobile = useMobile();
  const [collapsed, setCollapsed] = useState(false);
  const [mobileOpen, setMobileOpen] = useState(false);

  // Get current path
  const pathname = window.location.pathname;

  const toggleSidebar = () => {
    setCollapsed(!collapsed);
  };

  const toggleMobileSidebar = () => {
    setMobileOpen(!mobileOpen);
  };

  const navItems = [
    {
      title: "Dashboard",
      href: "/customer/dashboard",
      icon: <LayoutDashboard className="h-5 w-5" />,
    },
    {
      title: "Transfer",
      href: "/customer/dashboard/transfer",
      icon: <ArrowRightLeft className="h-5 w-5" />,
    },
    {
      title: "History",
      href: "/customer/dashboard/history",
      icon: <Clock className="h-5 w-5" />,
    },
    {
      title: "Debt Reminders",
      href: "/customer/dashboard/debt-reminders",
      icon: <AlertCircle className="h-5 w-5" />,
    },
    {
      title: "Recipients",
      href: "/customer/dashboard/recipients",
      icon: <Users className="h-5 w-5" />,
    },
    {
      title: "Profile",
      href: "/customer/dashboard/profile",
      icon: <User className="h-5 w-5" />,
    },
  ];

  const sidebarClasses = isMobile
    ? `fixed inset-y-0 left-0 z-50 w-64 bg-white border-r transform transition-transform duration-300 ease-in-out ${
        mobileOpen ? "translate-x-0" : "-translate-x-full"
      }`
    : `bg-white border-r h-screen transition-all duration-300 ${
        collapsed ? "w-[70px]" : "w-64"
      }`;

  return (
    <>
      {isMobile && (
        <Button
          variant="outline"
          size="icon"
          className="fixed top-4 left-4 z-50 md:hidden"
          onClick={toggleMobileSidebar}
        >
          <Menu className="h-5 w-5" />
        </Button>
      )}

      <div className={sidebarClasses}>
        <div className="flex flex-col h-full">
          <div className="p-4 border-b flex items-center justify-between">
            <Link to="/dashboard" className="flex items-center">
              <Shield className="h-6 w-6 text-blue-600" />
              {!collapsed && !isMobile && (
                <span className="ml-2 font-bold text-lg text-blue-600">
                  SecureBank
                </span>
              )}
            </Link>
            {!isMobile && (
              <Button
                variant="ghost"
                size="icon"
                onClick={toggleSidebar}
                className="h-8 w-8"
              >
                {collapsed ? (
                  <ChevronRight className="h-4 w-4" />
                ) : (
                  <ChevronLeft className="h-4 w-4" />
                )}
              </Button>
            )}
          </div>

          <div className="flex-1 py-6 px-3 space-y-1 overflow-y-auto">
            {navItems.map((item) => (
              <Link
                key={item.href}
                to={item.href}
                className={`flex items-center rounded-md px-3 py-2 text-sm transition-colors ${
                  pathname === item.href
                    ? "bg-blue-50 text-blue-600 font-medium"
                    : "text-gray-600 hover:bg-gray-100"
                }`}
              >
                <span className="flex items-center justify-center w-5">
                  {item.icon}
                </span>
                {!collapsed && <span className="ml-3">{item.title}</span>}
              </Link>
            ))}
          </div>

          <div className="p-4 border-t mt-auto">
            <div className="space-y-1">
              <Link
                to="#"
                onClick={() => dispatch(logout())}
                className="flex items-center rounded-md px-3 py-2 text-sm text-gray-600 hover:bg-gray-100 transition-colors cursor-pointer"
              >
                <LogOut className="h-5 w-5" />
                {!collapsed && <span className="ml-3">Logout</span>}
              </Link>
            </div>
          </div>
        </div>
      </div>

      {isMobile && mobileOpen && (
        <div
          className="fixed inset-0 bg-black/20 z-40"
          onClick={toggleMobileSidebar}
        />
      )}
    </>
  );
}
