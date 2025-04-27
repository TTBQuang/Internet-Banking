import apiClient from "./apiClient";
import { store } from "../redux/store";
import { setUser, logout } from "../redux/userSlice";

export const loginUser = async (username, password) => {
  const data = await apiClient.post("/auth/login", { username, password });

  localStorage.setItem("accessToken", data.token.accessToken);
  localStorage.setItem("refreshToken", data.token.refreshToken);

  store.dispatch(setUser(data.user));

  return data;
};

export const logoutUser = () => {
  apiClient.post("/auth/logout");
  localStorage.removeItem("accessToken");
  localStorage.removeItem("refreshToken");
  store.dispatch(logout());
};
