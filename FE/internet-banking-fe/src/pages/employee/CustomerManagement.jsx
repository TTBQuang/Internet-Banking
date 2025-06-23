import React, { useEffect, useState } from "react";
import apiClient from "../../services/apiClient";
import { Button } from "../../components/ui/button";
import Pagination from "../../components/common/pagination";

export default function CustomerManagement() {
  const [customers, setCustomers] = useState([]);
  const [form, setForm] = useState({ username: "", password: "", fullName: "", email: "", phone: "" });
  const [editing, setEditing] = useState(null);
  const [page, setPage] = useState(1);
  const [totalPages, setTotalPages] = useState(1);

  // Fetch customers with pagination
  const fetchCustomers = async (pageNum = 1) => {
    const res = await apiClient.get(`/api/customers?page=${pageNum - 1}&size=10`);
    setCustomers(res.data.content);
    setTotalPages(res.data.totalPages);
  };

  useEffect(() => {
    fetchCustomers(page);
  }, [page]);

  // Create or update customer
  const handleSubmit = async (e) => {
    e.preventDefault();
    if (editing) {
      await apiClient.put(`/api/customers/${editing.userId}`, form);
    } else {
      await apiClient.post("/api/customers/register", form);
    }
    setForm({ username: "", password: "", fullName: "", email: "", phone: "" });
    setEditing(null);
    fetchCustomers(page);
  };

  // Delete customer
  const handleDelete = async (userId) => {
    await apiClient.delete(`/api/customers/${userId}`);
    fetchCustomers(page);
  };

  // Edit customer
  const handleEdit = (customer) => {
    setEditing(customer);
    setForm(customer);
  };

  return (
    <div>
      <h2 className="text-xl font-bold mb-4">{editing ? "Edit Customer" : "Add Customer"}</h2>
      <form onSubmit={handleSubmit} className="space-y-2 mb-6">
        <input value={form.username} onChange={e => setForm({ ...form, username: e.target.value })} placeholder="Username" required />
        <input value={form.password} onChange={e => setForm({ ...form, password: e.target.value })} placeholder="Password" type="password" required={!editing} />
        <input value={form.fullName} onChange={e => setForm({ ...form, fullName: e.target.value })} placeholder="Full Name" required />
        <input value={form.email} onChange={e => setForm({ ...form, email: e.target.value })} placeholder="Email" required />
        <input value={form.phone} onChange={e => setForm({ ...form, phone: e.target.value })} placeholder="Phone" required />
        <Button type="submit">{editing ? "Update" : "Create"}</Button>
        {editing && <Button type="button" onClick={() => { setEditing(null); setForm({ username: "", password: "", fullName: "", email: "", phone: "" }); }}>Cancel</Button>}
      </form>
      <h2 className="text-xl font-bold mb-2">Customer List</h2>
      <table className="w-full mb-4">
        <thead>
          <tr>
            <th>Username</th><th>Full Name</th><th>Email</th><th>Phone</th><th>Actions</th>
          </tr>
        </thead>
        <tbody>
          {customers.map(c => (
            <tr key={c.userId}>
              <td>{c.username}</td>
              <td>{c.fullName}</td>
              <td>{c.email}</td>
              <td>{c.phone}</td>
              <td>
                <Button onClick={() => handleEdit(c)}>Edit</Button>
                <Button variant="destructive" onClick={() => handleDelete(c.userId)}>Delete</Button>
              </td>
            </tr>
          ))}
        </tbody>
      </table>
      <Pagination currentPage={page} totalPages={totalPages} onPageChange={setPage} />
    </div>
  );
}