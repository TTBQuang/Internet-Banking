import React, { useState } from "react";
import apiClient from "../../services/apiClient";
import { Button } from "../../components/ui/button";

export default function DepositMoney() {
  const [accountNumber, setAccountNumber] = useState("");
  const [amount, setAmount] = useState("");
  const [message, setMessage] = useState("");

  const handleDeposit = async (e) => {
    e.preventDefault();
    try {
      await apiClient.post("/api/customers/deposit", { accountNumber, amount: parseFloat(amount) });
      setMessage("Deposit successful!");
    } catch (err) {
      setMessage("Deposit failed: " + err.message);
    }
  };

  return (
    <div>
      <h2 className="text-xl font-bold mb-4">Deposit Money</h2>
      <form onSubmit={handleDeposit} className="space-y-2">
        <input value={accountNumber} onChange={e => setAccountNumber(e.target.value)} placeholder="Account Number" required />
        <input value={amount} onChange={e => setAmount(e.target.value)} placeholder="Amount" type="number" required />
        <Button type="submit">Deposit</Button>
      </form>
      {message && <div className="mt-2">{message}</div>}
    </div>
  );
}