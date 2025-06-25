import React, { useState } from "react";
import apiClient from "../../services/apiClient";
import { Button } from "../../components/ui/button";

export default function TransactionHistory() {
  const [accountNumber, setAccountNumber] = useState("");
  const [transactions, setTransactions] = useState([]);
  const [error, setError] = useState("");

  const handleSearch = async (e) => {
    e.preventDefault();
    setError("");
    try {
      const res = await apiClient.get(`/api/transactions/histories?accountNumber=${accountNumber}`);
      setTransactions(res.data);
    } catch (err) {
      setError("Failed to fetch transactions: " + err.message);
    }
  };

  return (
    <div>
      <h2 className="text-xl font-bold mb-4">Transaction History</h2>
      <form onSubmit={handleSearch} className="space-y-2 mb-4">
        <input value={accountNumber} onChange={e => setAccountNumber(e.target.value)} placeholder="Account Number" required />
        <Button type="submit">Search</Button>
      </form>
      {error && <div className="text-red-500">{error}</div>}
      <table className="w-full">
        <thead>
          <tr>
            <th>ID</th><th>Sender</th><th>Receiver</th><th>Amount</th><th>Date</th>
          </tr>
        </thead>
        <tbody>
          {transactions.map(tx => (
            <tr key={tx.transactionId}>
              <td>{tx.transactionId}</td>
              <td>{tx.senderAccountNumber}</td>
              <td>{tx.receiverAccountNumber}</td>
              <td>{tx.amount}</td>
              <td>{tx.createdAt}</td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
}