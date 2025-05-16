import React, { useState, useEffect, useRef } from "react";
import { useNavigate } from "react-router-dom";
import { useDispatch, useSelector } from "react-redux";
import { loginUser } from "../redux/userSlice";
import ReCAPTCHA from "react-google-recaptcha";

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
    <div className="login-container">
      <h1>Login</h1>
      <form onSubmit={handleLogin}>
        {(error || loginError) && (
          <div className="error-message">{error || loginError}</div>
        )}

        <div className="form-group">
          <label htmlFor="username">Username</label>
          <input
            id="username"
            type="text"
            placeholder="Enter your username"
            value={username}
            onChange={(e) => setUsername(e.target.value)}
            disabled={loading}
          />
        </div>

        <div className="form-group">
          <label htmlFor="password">Password</label>
          <input
            id="password"
            type="password"
            placeholder="Enter your password"
            value={password}
            onChange={(e) => setPassword(e.target.value)}
            disabled={loading}
            autoComplete="current-password"
          />
        </div>

        <div className="recaptcha-container">
          <ReCAPTCHA
            ref={recaptchaRef}
            sitekey={RECAPTCHA_SITE_KEY}
            onChange={handleCaptchaChange}
            hl="en"
          />
        </div>

        <button type="submit" className="login-button" disabled={loading}>
          {loading ? "Logging in..." : "Login"}
        </button>
      </form>
    </div>
  );
};

export default LoginPage;
