import React, { useEffect, useState } from "react";
import apiClient from "../../services/apiClient";
import { Button } from "../../components/ui/button";
import Pagination from "../../components/common/pagination";
import { ToastContainer, toast } from "react-toastify";
import "react-toastify/dist/ReactToastify.css";
import moment from "moment";

export default function LinkedBankTransactionManagement() {
  const [transactions, setTransactions] = useState([]);
  const [banks, setBanks] = useState([]);
  const [filters, setFilters] = useState({
    bankId: "",
    startDate: "",
    endDate: "",
  });
  const [page, setPage] = useState(1);
  const [totalPages, setTotalPages] = useState(1);
  const [totalItems, setTotalItems] = useState(0);
  const [isLoading, setIsLoading] = useState(false);

  const fetchBanks = async () => {
    try {
      const res = await apiClient.get("/api/linked-banks");
      setBanks(res.data || []);
    } catch (error) {
      console.error("Lỗi khi lấy danh sách ngân hàng:", error);
      toast.error("Đã xảy ra lỗi khi lấy danh sách ngân hàng");
    }
  };

  const fetchTransactions = async (pageNum = 1) => {
    setIsLoading(true);
    try {
      const params = new URLSearchParams({
        page: pageNum - 1,
        size: 5,
        ...(filters.bankId && { bankId: filters.bankId }),
        ...(filters.startDate && { startDate: moment(filters.startDate).startOf("day").format("YYYY-MM-DD[T]HH:mm:ss") }),
        ...(filters.endDate && { endDate: moment(filters.endDate).endOf("day").format("YYYY-MM-DD[T]HH:mm:ss") })
      });
      const res = await apiClient.get(`/api/linked-banks/transactions?${params}`);
      setTransactions(res.data.content || []);
      setTotalPages(res.data.page.totalPages || 1);
      setTotalItems(res.data.page.totalElements || 0);
    } catch (error) {
      console.error("Lỗi khi lấy danh sách giao dịch:", error);
      toast.error(error.response?.data?.message || "Đã xảy ra lỗi khi lấy danh sách giao dịch");
    } finally {
      setIsLoading(false);
    }
  };

  useEffect(() => {
    fetchBanks();
    fetchTransactions(page);
  }, [page, filters]);

  const handleFilterChange = (e) => {
    const { name, value } = e.target;
    setFilters((prev) => ({ ...prev, [name]: value }));
    setPage(1); // Reset về trang 1 khi thay đổi bộ lọc
  };

  const handleResetFilters = () => {
    setFilters({ bankId: "", startDate: "", endDate: "" });
    setPage(1);
  };

  return (
    <div className="max-w-5xl mx-auto p-6 space-y-8">
      <ToastContainer position="top-right" autoClose={3000} hideProgressBar={false} closeOnClick pauseOnHover />

      <div className="bg-white shadow-md rounded-xl p-6">
        <h2 className="text-2xl font-semibold mb-4">Bộ lọc giao dịch</h2>
        <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
          <select
            name="bankId"
            value={filters.bankId}
            onChange={handleFilterChange}
            className="border rounded px-4 py-2 focus:outline-none focus:ring-2 focus:ring-blue-500"
          >
            <option value="">Tất cả ngân hàng</option>
            {banks.map((bank) => (
              <option key={bank.linkedBankId} value={bank.linkedBankId}>
                {bank.bankName} ({bank.bankCode})
              </option>
            ))}
          </select>
          <input
            type="date"
            name="startDate"
            value={filters.startDate}
            onChange={handleFilterChange}
            className="border rounded px-4 py-2 focus:outline-none focus:ring-2 focus:ring-blue-500"
            placeholder="Ngày bắt đầu"
          />
          <input
            type="date"
            name="endDate"
            value={filters.endDate}
            onChange={handleFilterChange}
            className="border rounded px-4 py-2 focus:outline-none focus:ring-2 focus:ring-blue-500"
            placeholder="Ngày kết thúc"
          />
        </div>
        <div className="mt-4">
          <Button variant="outline" onClick={handleResetFilters}>
            Xóa bộ lọc
          </Button>
        </div>
      </div>

      <div className="bg-white shadow-md rounded-xl p-6">
        <h2 className="text-2xl font-semibold mb-4">Danh sách giao dịch liên ngân hàng</h2>
        {isLoading ? (
          <p>Đang tải...</p>
        ) : (
          <>
            <div className="overflow-x-auto">
              <table className="min-w-full text-sm text-left border">
                <thead className="bg-gray-100 text-gray-600 uppercase">
                  <tr>
                    <th className="px-4 py-2">Số TK Gửi</th>
                    <th className="px-4 py-2">Ngân hàng gửi</th>
                    <th className="px-4 py-2">Số TK Nhận</th>
                    <th className="px-4 py-2">Ngân hàng nhận</th>
                    <th className="px-4 py-2">Số tiền</th>
                    <th className="px-4 py-2">Phí</th>
                    <th className="px-4 py-2">Người trả phí</th>
                    <th className="px-4 py-2">Nội dung</th>
                    <th className="px-4 py-2">Loại</th>
                    <th className="px-4 py-2">Trạng thái</th>
                    <th className="px-4 py-2">Ngày tạo</th>
                    <th className="px-4 py-2">Ngày xác nhận</th>
                  </tr>
                </thead>
                <tbody>
                  {transactions.map((t) => (
                    <tr key={t.transactionId} className="border-t">
                      <td className="px-4 py-2">{t.senderAccountNumber}</td>
                      <td className="px-4 py-2">{t.senderBank?.bankName ?? "Secure Bank"}</td>
                      <td className="px-4 py-2">{t.receiverAccountNumber}</td>
                      <td className="px-4 py-2">{t.receiverBank?.bankName ?? "Secure Bank"}</td>
                      <td className="px-4 py-2">{t.amount.toLocaleString()}</td>
                      <td className="px-4 py-2">{t.fee.toLocaleString()}</td>
                      <td className="px-4 py-2">{t.feePayer}</td>
                      <td className="px-4 py-2">{t.content}</td>
                      <td className="px-4 py-2">{t.type}</td>
                      <td className="px-4 py-2">{t.status}</td>
                      <td className="px-4 py-2">{new Date(t.createdAt).toLocaleString()}</td>
                      <td className="px-4 py-2">{t.confirmedAt ? new Date(t.confirmedAt).toLocaleString() : "Chưa xác nhận"}</td>
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