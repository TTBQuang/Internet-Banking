import React, { useState } from "react";
import apiClient from "../../services/apiClient";
import { Button } from "../../components/ui/button";
import { ToastContainer, toast } from "react-toastify";
import "react-toastify/dist/ReactToastify.css";

export default function DepositMoney() {
  const [accountNumber, setAccountNumber] = useState("");
  const [amount, setAmount] = useState("");

  const handleDeposit = async (e) => {
    e.preventDefault();
    try {
      const res = await apiClient.post("/api/customers/deposit", {
        accountNumber,
        amount: parseFloat(amount),
      });
      if (res.message) {
        toast.error(res.message);
      } else {
        toast.success("Nạp tiền thành công!");
      }
      // setAccountNumber("");
      // setAmount("");
    } catch (err) {
      const errorMessage = err.response?.data?.message || "Đã xảy ra lỗi khi nạp tiền";
      toast.error(errorMessage);
    }
  };

  return (
    <div className="max-w-md mx-auto p-6 bg-white rounded-xl shadow-md space-y-4">
      <ToastContainer position="top-right" autoClose={3000} hideProgressBar={false} closeOnClick pauseOnHover />
      <h2 className="text-2xl font-semibold text-center">Nạp Tiền</h2>
      <form onSubmit={handleDeposit} className="space-y-4">
        <input
          className="w-full border rounded px-4 py-2 focus:outline-none focus:ring-2 focus:ring-blue-500"
          value={accountNumber}
          onChange={(e) => setAccountNumber(e.target.value)}
          placeholder="Số tài khoản/Tên người dùng"
          required
        />
        <input
          className="w-full border rounded px-4 py-2 focus:outline-none focus:ring-2 focus:ring-blue-500"
          value={amount}
          onChange={(e) => setAmount(e.target.value)}
          placeholder="Số tiền"
          type="number"
          min="1"
          required
        />
        <Button type="submit" className="w-full">Nạp tiền</Button>
      </form>
    </div>
  );
}