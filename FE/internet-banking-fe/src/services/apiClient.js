import axios from "axios";
import { store } from "../redux/store";
import { logout } from "../redux/userSlice";

const BASE_URL = "http://localhost:8080";

// Create an axios instance with common configuration
const apiClient = axios.create({
  baseURL: BASE_URL,
  headers: {
    "Content-Type": "application/json",
  },
});

// Request interceptor - automatically add token to headers
apiClient.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem("accessToken");

    // Check if token is needed
    if (config.withToken !== false && token) {
      config.headers.Authorization = `Bearer ${token}`;
    }

    return config;
  },
  (error) => Promise.reject(error)
);

// Response interceptor
apiClient.interceptors.response.use(
  (response) => {
    if (response.data) {
      if (response.data.data) {
        return response.data.data;
      }
      if (response.data.message) {
        return { message: response.data.message };
      }
    }

    return response;
  },
  async (error) => {
    const originalRequest = error.config;

    // If 401 error and not yet retried
    if (error.response?.status === 401 && !originalRequest._retry) {
      originalRequest._retry = true;

      try {
        const refreshToken = localStorage.getItem("refreshToken");
        if (!refreshToken) {
          handleLogout();
          return Promise.reject(new Error("Session expired"));
        }

        // Call refresh API (still using axios to avoid nested interceptors)
        const refreshRes = await axios.post(
          `${BASE_URL}/auth/refresh`,
          {
            refreshToken,
          },
          { withToken: false }
        );
        const { accessToken, refreshToken: newRefreshToken } =
          refreshRes.data.data;

        if (accessToken && newRefreshToken) {
          localStorage.setItem("accessToken", accessToken);
          localStorage.setItem("refreshToken", newRefreshToken);

          // Add header and retry the original request
          originalRequest.headers.Authorization = `Bearer ${accessToken}`;
          return apiClient(originalRequest);
        } else {
          handleLogout();
          return Promise.reject(new Error("Invalid refresh response"));
        }
      } catch (refreshError) {
        console.error("Refresh token error:", refreshError);
        handleLogout();
        return Promise.reject(new Error("Session expired, please login again"));
      }
    }

    // Handle other errors
    const errorMessage =
      error.response?.data?.message || error.message || "An error occurred";
    return Promise.reject(new Error(errorMessage));
  }
);

// Helper function to handle logout consistently
const handleLogout = () => {
  store.dispatch(logout());
  localStorage.removeItem("accessToken");
  localStorage.removeItem("refreshToken");
};

export default {
  get: (endpoint, config = {}) =>
    apiClient.get(endpoint, { ...config, withToken: true }),
  post: (endpoint, data = {}, config = {}) =>
    apiClient.post(endpoint, data, { ...config, withToken: true }),
  put: (endpoint, data = {}, config = {}) =>
    apiClient.put(endpoint, data, { ...config, withToken: true }),
  delete: (endpoint, config = {}) =>
    apiClient.delete(endpoint, { ...config, withToken: true }),
  patch: (endpoint, data = {}, config = {}) =>
    apiClient.patch(endpoint, data, { ...config, withToken: true }),
};
