import React, { useState } from "react";
import apiClient from "../../services/apiClient";
import { Button } from "../../components/ui/button";

export default function DepositMoney() {
  const [accountNumber, setAccountNumber] = useState("");
  const [amount, setAmount] = useState("");
  const [message, setMessage] = useState("");
  const [isSuccess, setIsSuccess] = useState(null);

  const handleDeposit = async (e) => {
    e.preventDefault();
    try {
      await apiClient.post("/api/customers/deposit", {
        accountNumber,
        amount: parseFloat(amount),
      });
      setMessage("Deposit successful!");
      setIsSuccess(true);
      setAccountNumber("");
      setAmount("");
    } catch (err) {
      setMessage("Deposit failed: " + err.message);
      setIsSuccess(false);
    }
  };

  return (
    <div className="max-w-md mx-auto p-6 bg-white rounded-xl shadow-md space-y-4">
      <h2 className="text-2xl font-semibold text-center">Deposit Money</h2>
      <form onSubmit={handleDeposit} className="space-y-4">
        <input
          className="w-full border rounded px-4 py-2 focus:outline-none focus:ring-2 focus:ring-blue-500"
          value={accountNumber}
          onChange={(e) => setAccountNumber(e.target.value)}
          placeholder="Account Number"
          required
        />
        <input
          className="w-full border rounded px-4 py-2 focus:outline-none focus:ring-2 focus:ring-blue-500"
          value={amount}
          onChange={(e) => setAmount(e.target.value)}
          placeholder="Amount"
          type="number"
          required
        />
        <Button type="submit" className="w-full">Deposit</Button>
      </form>
      {message && (
        <div
          className={`mt-2 text-sm p-2 rounded ${
            isSuccess ? "bg-green-100 text-green-700" : "bg-red-100 text-red-700"
          }`}
        >
          {message}
        </div>
      )}
    </div>
  );
}
