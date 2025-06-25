import React, { useEffect, useState } from "react";
import apiClient from "../../services/apiClient";
import { Button } from "../../components/ui/button";
import Pagination from "../../components/common/pagination";

export default function EmployeeManagement() {
  const [employees, setEmployees] = useState([]);
  const [form, setForm] = useState({ username: "", password: "", fullName: "", email: "", phone: "" });
  const [editing, setEditing] = useState(null);
  const [page, setPage] = useState(1);
  const [totalPages, setTotalPages] = useState(1);

  // Fetch employees with pagination
  const fetchEmployees = async (pageNum = 1) => {
    const res = await apiClient.get(`/api/employees?page=${pageNum - 1}&size=10`);
    setEmployees(res.data.content);
    setTotalPages(res.data.totalPages);
  };

  useEffect(() => {
    fetchEmployees(page);
  }, [page]);

  // Create or update employee
  const handleSubmit = async (e) => {
    e.preventDefault();
    if (editing) {
      await apiClient.put(`/api/employees/${editing.userId}`, form);
    } else {
      await apiClient.post("/api/employees/register", form);
    }
    setForm({ username: "", password: "", fullName: "", email: "", phone: "" });
    setEditing(null);
    fetchEmployees(page);
  };

  // Delete employee
  const handleDelete = async (userId) => {
    await apiClient.delete(`/api/employees/${userId}`);
    fetchEmployees(page);
  };

  // Edit employee
  const handleEdit = (employee) => {
    setEditing(employee);
    setForm(employee);
  };

  return (
    <div>
      <h2 className="text-xl font-bold mb-4">{editing ? "Edit Employee" : "Add Employee"}</h2>
      <form onSubmit={handleSubmit} className="space-y-2 mb-6">
        <input value={form.username} onChange={e => setForm({ ...form, username: e.target.value })} placeholder="Username" required />
        <input value={form.password} onChange={e => setForm({ ...form, password: e.target.value })} placeholder="Password" type="password" required={!editing} />
        <input value={form.fullName} onChange={e => setForm({ ...form, fullName: e.target.value })} placeholder="Full Name" required />
        <input value={form.email} onChange={e => setForm({ ...form, email: e.target.value })} placeholder="Email" required />
        <input value={form.phone} onChange={e => setForm({ ...form, phone: e.target.value })} placeholder="Phone" required />
        <Button type="submit">{editing ? "Update" : "Create"}</Button>
        {editing && <Button type="button" onClick={() => { setEditing(null); setForm({ username: "", password: "", fullName: "", email: "", phone: "" }); }}>Cancel</Button>}
      </form>
      <h2 className="text-xl font-bold mb-2">Employee List</h2>
      <table className="w-full mb-4">
        <thead>
          <tr>
            <th>Username</th><th>Full Name</th><th>Email</th><th>Phone</th><th>Actions</th>
          </tr>
        </thead>
        <tbody>
          {employees.map(e => (
            <tr key={e.userId}>
              <td>{e.username}</td>
              <td>{e.fullName}</td>
              <td>{e.email}</td>
              <td>{e.phone}</td>
              <td>
                <Button onClick={() => handleEdit(e)}>Edit</Button>
                <Button variant="destructive" onClick={() => handleDelete(e.userId)}>Delete</Button>
              </td>
            </tr>
          ))}
        </tbody>
      </table>
      <Pagination currentPage={page} totalPages={totalPages} onPageChange={setPage} />
    </div>
  );
}