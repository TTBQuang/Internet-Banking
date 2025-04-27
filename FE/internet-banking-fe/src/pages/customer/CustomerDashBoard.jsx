import React, { useEffect } from "react";
import { useSelector, useDispatch } from "react-redux";
import { logout } from "../../redux/userSlice";
import { fetchAccount } from "../../redux/accountSlice";

const CustomerDashBoard = () => {
  const user = useSelector((state) => state.user);
  const accountState = useSelector((state) => state.account);
  const dispatch = useDispatch();

  useEffect(() => {
    dispatch(fetchAccount());
  }, [dispatch]);

  const handleLogout = () => {
    dispatch(logout());
  };

  return (
    <div>
      <h1>{user.username}</h1>
      <p>{user.fullName}</p>
      <p>{user.email}</p>
      <p>{user.role}</p>

      {accountState.loading && <p>Loading account...</p>}
      {accountState.account && (
        <div>
          <h2>Account Info</h2>
          <p>Account Number: {accountState.account.accountNumber}</p>
          <p>Balance: ${accountState.account.balance}</p>
        </div>
      )}
      {accountState.error && (
        <p style={{ color: "red" }}>{accountState.error.message}</p>
      )}

      <button onClick={handleLogout}>Logout</button>
    </div>
  );
};

export default CustomerDashBoard;
