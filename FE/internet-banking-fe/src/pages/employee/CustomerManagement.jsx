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

  const fetchCustomers = async (pageNum = 1) => {
    const res = await apiClient.get(`/api/customers?page=${pageNum - 1}&size=10`);
    setCustomers(res.data.content);
    setTotalPages(res.data.totalPages);
  };

  useEffect(() => {
    fetchCustomers(page);
  }, [page]);

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

  const handleDelete = async (userId) => {
    await apiClient.delete(`/api/customers/${userId}`);
    fetchCustomers(page);
  };

  const handleEdit = (customer) => {
    setEditing(customer);
    setForm(customer);
  };

  return (
    <div className="max-w-5xl mx-auto p-6 space-y-8">
      {/* Form Section */}
      <div className="bg-white shadow-md rounded-xl p-6">
        <h2 className="text-2xl font-semibold mb-4">
          {editing ? "Edit Customer" : "Add New Customer"}
        </h2>
        <form onSubmit={handleSubmit} className="grid grid-cols-1 md:grid-cols-2 gap-4">
          <input
            className="border rounded px-4 py-2"
            value={form.username}
            onChange={e => setForm({ ...form, username: e.target.value })}
            placeholder="Username"
            required
          />
          <input
            className="border rounded px-4 py-2"
            value={form.password}
            onChange={e => setForm({ ...form, password: e.target.value })}
            placeholder="Password"
            type="password"
            required={!editing}
          />
          <input
            className="border rounded px-4 py-2"
            value={form.fullName}
            onChange={e => setForm({ ...form, fullName: e.target.value })}
            placeholder="Full Name"
            required
          />
          <input
            className="border rounded px-4 py-2"
            value={form.email}
            onChange={e => setForm({ ...form, email: e.target.value })}
            placeholder="Email"
            required
          />
          <input
            className="border rounded px-4 py-2"
            value={form.phone}
            onChange={e => setForm({ ...form, phone: e.target.value })}
            placeholder="Phone"
            required
          />
          <div className="flex gap-2 mt-2 col-span-full">
            <Button type="submit" className="px-6">
              {editing ? "Update" : "Create"}
            </Button>
            {editing && (
              <Button
                type="button"
                variant="outline"
                onClick={() => {
                  setEditing(null);
                  setForm({ username: "", password: "", fullName: "", email: "", phone: "" });
                }}
              >
                Cancel
              </Button>
            )}
          </div>
        </form>
      </div>

      {/* Customer List Section */}
      <div className="bg-white shadow-md rounded-xl p-6">
        <h2 className="text-2xl font-semibold mb-4">Customer List</h2>
        <div className="overflow-x-auto">
          <table className="min-w-full text-sm text-left border">
            <thead className="bg-gray-100 text-gray-600 uppercase">
              <tr>
                <th className="px-4 py-2">Username</th>
                <th className="px-4 py-2">Full Name</th>
                <th className="px-4 py-2">Email</th>
                <th className="px-4 py-2">Phone</th>
                <th className="px-4 py-2 text-center">Actions</th>
              </tr>
            </thead>
            <tbody>
              {customers.map((c) => (
                <tr key={c.userId} className="border-t">
                  <td className="px-4 py-2">{c.username}</td>
                  <td className="px-4 py-2">{c.fullName}</td>
                  <td className="px-4 py-2">{c.email}</td>
                  <td className="px-4 py-2">{c.phone}</td>
                  <td className="px-4 py-2 text-center space-x-2">
                    <Button size="sm" onClick={() => handleEdit(c)}>Edit</Button>
                    <Button size="sm" variant="destructive" onClick={() => handleDelete(c.userId)}>Delete</Button>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>

        <div className="mt-4 flex justify-center">
          <Pagination currentPage={page} totalPages={totalPages} onPageChange={setPage} />
        </div>
      </div>
    </div>
  );
}
