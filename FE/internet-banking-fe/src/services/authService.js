import apiClient from "./apiClient";

export const login = async (credentials) => {
  const response = await apiClient.post(
    "/auth/login",
    {
      username: credentials.username,
      password: credentials.password,
      recaptchaToken: credentials.recaptchaToken,
    },
    {
      withToken: false,
    }
  );

  if (
    response.token &&
    response.token.accessToken &&
    response.token.refreshToken
  ) {
    localStorage.setItem("accessToken", response.token.accessToken);
    localStorage.setItem("refreshToken", response.token.refreshToken);
  }

  return response;
};

export const logout = async () => {
  try {
    const refreshToken = localStorage.getItem("refreshToken");
    if (refreshToken) {
      await apiClient.post("/auth/logout", { refreshToken });
    }
  } catch (error) {
    console.error("Logout error:", error);
  } finally {
    localStorage.removeItem("accessToken");
    localStorage.removeItem("refreshToken");
  }
};
