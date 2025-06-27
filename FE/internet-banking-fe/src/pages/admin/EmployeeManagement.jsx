import React, { useEffect, useState } from "react";
import apiClient from "../../services/apiClient";
import { Button } from "../../components/ui/button";
import Pagination from "../../components/common/pagination";
import { ToastContainer, toast } from "react-toastify";
import "react-toastify/dist/ReactToastify.css";

export default function EmployeeManagement() {
  const [employees, setEmployees] = useState([]);
  const [form, setForm] = useState({ username: "", password: "", fullName: "", email: "", phone: "" });
  const [editing, setEditing] = useState(null);
  const [page, setPage] = useState(1);
  const [totalPages, setTotalPages] = useState(1);
  const [totalItems, setTotalItems] = useState(0);
  const [isLoading, setIsLoading] = useState(false);

  const fetchEmployees = async (pageNum = 1) => {
    setIsLoading(true);
    try {
      const res = await apiClient.get(`/api/employees?page=${pageNum - 1}&size=5`);
      console.log('API response:', JSON.stringify(res, null, 2));
      setEmployees(res.data.content || []);
      setTotalPages(res.data.page?.totalPages || 1);
      setTotalItems(res.data.page?.totalElements || 0);
    } catch (error) {
      console.error('Lỗi khi fetch employees:', error);
      setEmployees([]);
      setTotalPages(1);
      setTotalItems(0);
    } finally {
      setIsLoading(false);
    }
  };

  useEffect(() => {
    fetchEmployees(page);
  }, [page]);

  const handleSubmit = async (e) => {
    e.preventDefault();
    try {
      let res;
      if (editing) {
        res = await apiClient.put(`/api/employees/${editing.userId}`, form);
        if (res.message == "failed") {
          toast.error("Cập nhật nhân viên thất bại");
        } else {
          toast.success("Cập nhật nhân viên thành công");
          setForm({ username: "", password: "", fullName: "", email: "", phone: "" });
          setEditing(null);
          fetchEmployees(page);
        }
      } else {
        res = await apiClient.post("/api/employees/register", form);
        if (res.message) {
          toast.error(res.message);
        } else {
          toast.success("Tạo nhân viên thành công");
          setForm({ username: "", password: "", fullName: "", email: "", phone: "" });
          setEditing(null);
          fetchEmployees(page);
        }
      }
    } catch (error) {
      console.error('Lỗi khi submit form:', error);
      const errorMessage = error.response?.data?.message || "Đã xảy ra lỗi khi xử lý yêu cầu";
      toast.error(errorMessage);
    }
  };

  const handleDelete = async (userId) => {
    try {
      const res = await apiClient.delete(`/api/employees/${userId}`);
        if (res.message == "failed") {
        toast.error("Xóa nhân viên thất bại");
      } else {
        toast.success("Xóa nhân viên thành công");
        fetchEmployees(page);
      }
    } catch (error) {
      console.error('Lỗi khi xóa employee:', error);
      toast.error("Đã xảy ra lỗi khi xóa nhân viên");
    }
  };

  const handleEdit = (employee) => {
    setEditing(employee);
    setForm({ ...employee, password: "" }); // Reset password khi chỉnh sửa
  };

  return (
    <div className="max-w-6xl mx-auto p-6 space-y-8">
      <ToastContainer position="top-right" autoClose={3000} hideProgressBar={false} closeOnClick pauseOnHover />

      <div className="bg-white shadow-md rounded-xl p-6">
        <h2 className="text-2xl font-semibold mb-4">
          {editing ? "Edit Employee" : "Add New Employee"}
        </h2>
        <form onSubmit={handleSubmit} className="grid grid-cols-1 md:grid-cols-2 gap-4">
          <input
            className="border rounded px-4 py-2 disabled:bg-gray-100 disabled:cursor-not-allowed"
            value={form.username}
            onChange={(e) => setForm({ ...form, username: e.target.value })}
            placeholder="Username"
            required
            disabled={editing}
          />
          <input
            className="border rounded px-4 py-2 disabled:bg-gray-100 disabled:cursor-not-allowed"
            value={form.password}
            onChange={(e) => setForm({ ...form, password: e.target.value })}
            placeholder="Password"
            type="password"
            required={!editing}
            disabled={editing}
          />
          <input
            className="border rounded px-4 py-2"
            value={form.fullName}
            onChange={(e) => setForm({ ...form, fullName: e.target.value })}
            placeholder="Full Name"
            required
          />
          <input
            className="border rounded px-4 py-2"
            value={form.email}
            onChange={(e) => setForm({ ...form, email: e.target.value })}
            placeholder="Email"
            type="email"
            required
          />
          <input
            className="border rounded px-4 py-2"
            value={form.phone}
            onChange={(e) => setForm({ ...form, phone: e.target.value })}
            placeholder="Phone"
            required
          />
          <div className="flex gap-2 mt-2 col-span-full">
            <Button type="submit" className="px-6">
              {editing ? "Update" : "Add"}
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

      <div className="bg-white shadow-md rounded-xl p-6">
        <h2 className="text-2xl font-semibold mb-4">Employees</h2>
        {isLoading ? (
          <p>Loading...</p>
        ) : (
          <>
            <div className="overflow-x-auto">
              <table className="min-w-full text-sm text-left border">
                <thead className="bg-gray-100 text-gray-600 uppercase">
                  <tr>
                    <th className="px-4 py-2">Username</th>
                    <th className="px-4 py-2">Full Name</th>
                    <th className="px-4 py-2">Email</th>
                    <th className="px-4 py-2">Phone</th>
                    <th className="px-4 py-2 text-center">Action</th>
                  </tr>
                </thead>
                <tbody>
                  {employees.map((e) => (
                    <tr key={e.userId} className="border-t">
                      <td className="px-4 py-2">{e.username}</td>
                      <td className="px-4 py-2">{e.fullName}</td>
                      <td className="px-4 py-2">{e.email}</td>
                      <td className="px-4 py-2">{e.phone}</td>
                      <td className="px-4 py-2 text-center space-x-2">
                        <Button size="sm" onClick={() => handleEdit(e)}>Edit</Button>
                        <Button size="sm" variant="destructive" onClick={() => handleDelete(e.userId)}>Delete</Button>
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
            <div className="mt-4 flex justify-center">
              <Pagination
                currentPage={page}
                totalPages={totalPages}
                totalItems={totalItems}
                itemsPerPage={5}
                onPageChange={setPage}
              />
            </div>
          </>
        )}
      </div>
    </div>
  );
}