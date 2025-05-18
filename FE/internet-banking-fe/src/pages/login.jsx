import React, { useState, useEffect, useRef } from "react";
import { useNavigate, Link } from "react-router-dom";
import { useDispatch, useSelector } from "react-redux";
import { loginUser } from "../redux/userSlice";
import ReCAPTCHA from "react-google-recaptcha";
import { Shield, ArrowRight } from "lucide-react";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import {
  Card,
  CardContent,
  CardDescription,
  CardHeader,
  CardTitle,
} from "@/components/ui/card";

const LoginPage = () => {
  const RECAPTCHA_SITE_KEY = import.meta.env.VITE_RECAPTCHA_SITE_KEY;

  const [username, setUsername] = useState("");
  const [password, setPassword] = useState("");
  const [error, setError] = useState("");
  const [captchaToken, setCaptchaToken] = useState("");
  const recaptchaRef = useRef(null);

  const dispatch = useDispatch();
  const navigate = useNavigate();

  const {
    loading,
    error: loginError,
    role,
  } = useSelector((state) => state.user);

  // Check if user is already logged in and redirect accordingly
  useEffect(() => {
    if (role) {
      const rolePaths = {
        CUSTOMER: "/customer/dashboard",
        EMPLOYEE: "/employee/dashboard",
        ADMIN: "/admin/dashboard",
      };

      const path = rolePaths[role] || "/";
      navigate(path);
    }
  }, [role, navigate]);

  // Handle reCAPTCHA change
  const handleCaptchaChange = (token) => {
    setCaptchaToken(token);
  };

  // Reset reCAPTCHA
  const resetCaptcha = () => {
    if (recaptchaRef.current) {
      recaptchaRef.current.reset();
    }
    setCaptchaToken("");
  };

  // Handler when the user submits the login form
  const handleLogin = async (event) => {
    event.preventDefault();
    setError("");

    // Validate input fields
    if (!username.trim() || !password.trim()) {
      setError("Please enter both username and password");
      return;
    }

    // Validate reCAPTCHA
    if (!captchaToken) {
      setError("Please complete the reCAPTCHA verification");
      return;
    }

    // Call the login action
    try {
      const resultAction = await dispatch(
        loginUser({
          username,
          password,
          recaptchaToken: captchaToken,
        })
      );

      if (loginUser.rejected.match(resultAction)) {
        setError(resultAction.payload || "Login failed");
        resetCaptcha();
      }
    } catch (e) {
      setError(e.message || "An unexpected error occurred");
      resetCaptcha();
    }
  };

  return (
    <div className="min-h-screen flex flex-col items-center justify-center bg-gradient-to-b from-blue-50 to-white p-4">
      <Link to="/" className="flex items-center space-x-2 mb-8">
        <Shield className="h-8 w-8 text-blue-600" />
        <span className="font-bold text-xl text-blue-600">SecureBank</span>
      </Link>

      <Card className="w-full max-w-md">
        <CardHeader className="space-y-1">
          <CardTitle className="text-2xl font-bold text-center">
            Login to your account
          </CardTitle>
          <CardDescription className="text-center">
            Enter your credentials to access your account
          </CardDescription>
        </CardHeader>
        <CardContent>
          <form onSubmit={handleLogin} className="space-y-4">
            {(error || loginError) && (
              <div className="p-3 bg-red-50 border border-red-200 text-red-700 rounded-md text-sm">
                {error || loginError}
              </div>
            )}

            <div className="space-y-2">
              <Label htmlFor="username">Username</Label>
              <Input
                id="username"
                placeholder="Enter your username"
                value={username}
                onChange={(e) => setUsername(e.target.value)}
                required
                disabled={loading}
              />
            </div>
            <div className="space-y-2">
              <div className="flex items-center justify-between">
                <Label htmlFor="password">Password</Label>
                <Link
                  to="/forgot-password"
                  className="text-sm text-blue-600 hover:underline"
                >
                  Forgot password?
                </Link>
              </div>
              <Input
                id="password"
                type="password"
                placeholder="Enter your password"
                value={password}
                onChange={(e) => setPassword(e.target.value)}
                required
                disabled={loading}
              />
            </div>

            <div className="recaptcha-container flex justify-center">
              <ReCAPTCHA
                ref={recaptchaRef}
                sitekey={RECAPTCHA_SITE_KEY}
                onChange={handleCaptchaChange}
                hl="en"
              />
            </div>

            <Button type="submit" className="w-full" disabled={loading}>
              {loading ? (
                <span className="flex items-center justify-center">
                  <svg
                    className="animate-spin -ml-1 mr-3 h-5 w-5 text-white"
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
                  Logging in...
                </span>
              ) : (
                <span className="flex items-center justify-center">
                  Login <ArrowRight className="ml-2 h-4 w-4" />
                </span>
              )}
            </Button>
          </form>
        </CardContent>
      </Card>
    </div>
  );
};

export default LoginPage;
