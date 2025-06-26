import React, { useEffect, useState } from "react";
import { useDispatch, useSelector } from "react-redux";
import apiClient from "../../services/apiClient";
import { Button } from "../../components/ui/button";
import Pagination from "../../components/common/pagination";
import { fetchAccountNumber, fetchTransactionHistory, resetTransactionHistory } from "../../redux/transactionHistorySlice";
import { ToastContainer, toast } from "react-toastify";
import "react-toastify/dist/ReactToastify.css";

export default function CustomerManagement() {
  const [customers, setCustomers] = useState([]);
  const [form, setForm] = useState({ username: "", password: "", fullName: "", email: "", phone: "" });
  const [editing, setEditing] = useState(null);
  const [page, setPage] = useState(1);
  const [totalPages, setTotalPages] = useState(1);
  const [totalItems, setTotalItems] = useState(0);
  const [isLoading, setIsLoading] = useState(false);
  const [showHistoryModal, setShowHistoryModal] = useState(false);

  const dispatch = useDispatch();
  const { transactions, loading: historyLoading, error: historyError } = useSelector((state) => state.transactionHistory);

  const fetchCustomers = async (pageNum = 1) => {
    setIsLoading(true);
    try {
      const res = await apiClient.get(`/api/customers?page=${pageNum - 1}&size=5`);
      console.log('API response:', JSON.stringify(res, null, 2));
      setCustomers(res.data.content || []);
      setTotalPages(res.data.page?.totalPages || 1);
      setTotalItems(res.data.page?.totalElements || 0);
    } catch (error) {
      console.error('Lỗi khi fetch customers:', error);
      setCustomers([]);
      setTotalPages(1);
      setTotalItems(0);
    } finally {
      setIsLoading(false);
    }
  };

  useEffect(() => {
    fetchCustomers(page);
  }, [page]);

  const handleSubmit = async (e) => {
    e.preventDefault();
    try {
      let res;
      if (editing) {
        res = await apiClient.put(`/api/customers/${editing.userId}`, form);
        toast.success("Cập nhật khách hàng thành công");
      } else {
        res = await apiClient.post("/api/customers/register", form);
        if (res.message) {
          toast.error(res.message);
        } else {
          toast.success("Tạo khách hàng thành công");
          setForm({ username: "", password: "", fullName: "", email: "", phone: "" });
          setEditing(null);
          fetchCustomers(page);
        }
      }
    } catch (error) {
      console.error('Lỗi khi submit form:', error);
      const errorMessage = error.response?.message || "Đã xảy ra lỗi khi xử lý yêu cầu";
      toast.error(errorMessage);
    }
  };

  const handleDelete = async (userId) => {
    try {
      await apiClient.delete(`/api/customers/${userId}`);
      toast.success("Xóa khách hàng thành công");
      fetchCustomers(page);
    } catch (error) {
      console.error('Lỗi khi xóa customer:', error);
      toast.error("Đã xảy ra lỗi khi xóa khách hàng");
    }
  };

  const handleEdit = (customer) => {
    setEditing(customer);
    setForm(customer);
  };

  const handleViewHistory = async (userId) => {
    dispatch(resetTransactionHistory());
    try {
      const accountNumber = await dispatch(fetchAccountNumber(userId)).unwrap();
      await dispatch(fetchTransactionHistory(accountNumber)).unwrap();
      setShowHistoryModal(true);
    } catch (error) {
      console.error('Lỗi khi lấy lịch sử giao dịch:', error);
      toast.error("Đã xảy ra lỗi khi lấy lịch sử giao dịch");
    }
  };

  const closeHistoryModal = () => {
    setShowHistoryModal(false);
    dispatch(resetTransactionHistory());
  };

  return (
    <div className="max-w-5xl mx-auto p-6 space-y-8">
      {/* Toast Container */}
      <ToastContainer position="top-right" autoClose={3000} hideProgressBar={false} closeOnClick pauseOnHover />

      {/* Form Section */}
      <div className="bg-white shadow-md rounded-xl p-6">
        <h2 className="text-2xl font-semibold mb-4">
          {editing ? "Edit Customer" : "Add New Customer"}
        </h2>
        <form onSubmit={handleSubmit} className="grid grid-cols-1 md:grid-cols-2 gap-4">
          <input
            className="border rounded px-4 py-2"
            value={form.username}
            onChange={(e) => setForm({ ...form, username: e.target.value })}
            placeholder="Username"
            required
          />
          <input
            className="border rounded px-4 py-2"
            value={form.password}
            onChange={(e) => setForm({ ...form, password: e.target.value })}
            placeholder="Password"
            type="password"
            required={!editing}
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
              {editing ? "Cập nhật" : "Tạo"}
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
                Hủy
              </Button>
            )}
          </div>
        </form>
      </div>

      {/* Customer List Section */}
      <div className="bg-white shadow-md rounded-xl p-6">
        <h2 className="text-2xl font-semibold mb-4">Customers</h2>
        {isLoading ? (
          <p>Đang tải...</p>
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
                  {customers.map((c) => (
                    <tr key={c.userId} className="border-t">
                      <td className="px-4 py-2">{c.username}</td>
                      <td className="px-4 py-2">{c.fullName}</td>
                      <td className="px-4 py-2">{c.email}</td>
                      <td className="px-4 py-2">{c.phone}</td>
                      <td className="px-4 py-2 text-center space-x-2">
                        {/* <Button size="sm" onClick={() => handleEdit(c)}>Sửa</Button> */}
                        {/* <Button size="sm" variant="destructive" onClick={() => handleDelete(c.userId)}>Xóa</Button> */}
                        <Button size="sm" variant="outline" onClick={() => handleViewHistory(c.userId)}>Xem Lịch Sử</Button>
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

      {/* Transaction History Modal */}
      {showHistoryModal && (
        <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50">
          <div className="bg-white rounded-xl p-6 max-w-4xl w-full max-h-[80vh] overflow-y-auto">
            <h2 className="text-2xl font-semibold mb-4">Lịch Sử G Ascending Giao Dịch</h2>
            {historyLoading ? (
              <p>Đang tải lịch sử giao dịch...</p>
            ) : historyError ? (
              <p className="text-red-500">Lỗi: {historyError}</p>
            ) : transactions.length === 0 ? (
              <p>Không có giao dịch nào.</p>
            ) : (
              <div className="overflow-x-auto">
                <table className="min-w-full text-sm text-left border">
                  <thead className="bg-gray-100 text-gray-600 uppercase">
                    <tr>
                      <th className="px-4 py-2">ID Giao Dịch</th>
                      <th className="px-4 py-2">Số TK Gửi</th>
                      <th className="px-4 py-2">Số TK Nhận</th>
                      <th className="px-4 py-2">Số Tiền</th>
                      <th className="px-4 py-2">Loại</th>
                      <th className="px-4 py-2">Trạng Thái</th>
                      <th className="px-4 py-2">Ngày Tạo</th>
                      <th className="px-4 py-2">Ngày Xác Nhận</th>
                    </tr>
                  </thead>
                  <tbody>
                    {transactions.map((t) => (
                      <tr key={t.transactionId} className="border-t">
                        <td className="px-4 py-2">{t.transactionId}</td>
                        <td className="px-4 py-2">{t.senderAccountNumber}</td>
                        <td className="px-4 py-2">{t.receiverAccountNumber}</td>
                        <td className="px-4 py-2">{t.amount.toLocaleString()}</td>
                        <td className="px-4 py-2">{t.type}</td>
                        <td className="px-4 py-2">{t.status}</td>
                        <td className="px-4 py-2">{new Date(t.createdAt).toLocaleString()}</td>
                        <td className="px-4 py-2">{t.confirmedAt ? new Date(t.confirmedAt).toLocaleString() : 'Chưa xác nhận'}</td>
                      </tr>
                    ))}
                  </tbody>
                </table>
              </div>
            )}
            <div className="mt-4 flex justify-end">
              <Button variant="outline" onClick={closeHistoryModal}>Đóng</Button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
}