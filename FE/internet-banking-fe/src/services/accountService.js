import apiClient from "./apiClient";

export const getAccount = async () => {
  return await apiClient.get("/account");
};

export const examplePost = async (data) => {
  return await apiClient.post("/account", data);
};
