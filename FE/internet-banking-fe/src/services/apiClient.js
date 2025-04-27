import axios from "axios";
import { store } from "../redux/store";
import { logout } from "../redux/userSlice";

const BASE_URL = "http://localhost:8080";

// Tạo instance axios với cấu hình chung
const apiClient = axios.create({
  baseURL: BASE_URL,
  headers: {
    "Content-Type": "application/json",
  },
});

// Request interceptor - tự động thêm token vào header
apiClient.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem("accessToken");
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => Promise.reject(error)
);

// Response interceptor - xử lý refresh token và lỗi chung
apiClient.interceptors.response.use(
  (response) => {
    // Chỉ trả về data.data từ response để đơn giản hoá
    return response.data?.data || response.data;
  },
  async (error) => {
    const originalRequest = error.config;

    // Nếu lỗi 401 (Unauthorized) và chưa thử refresh token
    if (error.response?.status === 401 && !originalRequest._retry) {
      originalRequest._retry = true;

      try {
        // Thử refresh token
        const refreshToken = localStorage.getItem("refreshToken");
        if (!refreshToken) {
          // Nếu không có refresh token, logout
          store.dispatch(logout());
          localStorage.removeItem("accessToken");
          localStorage.removeItem("refreshToken");
          return Promise.reject(new Error("Phiên đăng nhập hết hạn"));
        }

        // Gọi API refresh token
        const response = await axios.post(`${BASE_URL}/auth/refresh`, {
          refreshToken,
        });

        if (response.data?.data?.token) {
          const { accessToken, refreshToken: newRefreshToken } =
            response.data.data.token;
          // Lưu token mới
          localStorage.setItem("accessToken", accessToken);
          localStorage.setItem("refreshToken", newRefreshToken);

          // Cập nhật header cho request ban đầu và thử lại
          originalRequest.headers.Authorization = `Bearer ${accessToken}`;
          return axios(originalRequest);
        }
      } catch {
        // Nếu refresh token thất bại, logout
        store.dispatch(logout());
        localStorage.removeItem("accessToken");
        localStorage.removeItem("refreshToken");
        return Promise.reject(
          new Error("Phiên đăng nhập hết hạn, vui lòng đăng nhập lại")
        );
      }
    }

    // Xử lý thông báo lỗi
    const errorMessage =
      error.response?.data?.message || error.message || "Đã có lỗi xảy ra";
    return Promise.reject(new Error(errorMessage));
  }
);

export default {
  get: (endpoint, config = {}) => apiClient.get(endpoint, config),
  post: (endpoint, data = {}, config = {}) =>
    apiClient.post(endpoint, data, config),
  put: (endpoint, data = {}, config = {}) =>
    apiClient.put(endpoint, data, config),
  delete: (endpoint, config = {}) => apiClient.delete(endpoint, config),
  patch: (endpoint, data = {}, config = {}) =>
    apiClient.patch(endpoint, data, config),
};
