import { useEffect, useState } from "react";

import {
  getAccountById,
  getAccounts,
  createAccount,
  deleteAccount,
} from "../api";

function AccountSection({ onSelectAccount }) {

  const [accounts, setAccounts] = useState([]);

  const [selectedAccount, setSelectedAccount] =
    useState(null);

  const [accountId, setAccountId] = useState("");

  const [accountNumber, setAccountNumber] = useState("");
  const [accountType, setAccountType] = useState("");
  const [balance, setBalance] = useState("");
  const [customerId, setCustomerId] = useState("");

  const [error, setError] = useState("");
  const [message, setMessage] = useState("");

  // =========================
  // LOAD ACCOUNTS
  // =========================

  const loadAccounts = async () => {

    try {

      setError("");

      const data = await getAccounts();

      setAccounts(data);

    } catch (error) {

      setError(error.message);

    }
  };

  useEffect(() => {
    loadAccounts();
  }, []);


  // =========================
  // VIEW ACCOUNT
  // =========================

  const handleViewAccount = async () => {

    if (!accountId) {
      setError("Enter an account ID.");
      return;
    }

    try {

      setError("");

      const data = await getAccountById(accountId);

      setSelectedAccount(data);

      onSelectAccount(data.id);

    } catch (error) {

      setError(error.message);

      setSelectedAccount(null);

    }
  };


  // =========================
  // ADD ACCOUNT
  // =========================

  const handleAddAccount = async (event) => {

    event.preventDefault();

    setError("");
    setMessage("");

    if (
      !accountNumber.trim() ||
      !accountType.trim() ||
      !balance ||
      !customerId
    ) {
      setError("All account fields are required.");
      return;
    }

    try {

      await createAccount({
        accountNumber,
        accountType,
        balance: Number(balance),
        customerId: Number(customerId),
      });

      setMessage("Account created successfully.");

      // Clear form
      setAccountNumber("");
      setAccountType("");
      setBalance("");
      setCustomerId("");

      // Refresh account list
      await loadAccounts();

    } catch (error) {

      setError(error.message);

    }
  };


  // =========================
  // DELETE ACCOUNT
  // =========================

  const handleDeleteAccount = async (id) => {

    const confirmed = window.confirm(
      "Are you sure you want to delete this account?"
    );

    if (!confirmed) {
      return;
    }

    try {

      setError("");
      setMessage("");

      await deleteAccount(id);

      setMessage("Account deleted successfully.");

      // If deleted account was selected
      if (selectedAccount?.id === id) {

        setSelectedAccount(null);
        onSelectAccount(null);

      }

      await loadAccounts();

    } catch (error) {

      setError(error.message);

    }
  };


  return (

    <section className="section">

      <h2>Accounts</h2>


      {/* =========================
          ADD ACCOUNT FORM
      ========================= */}

      <form
        onSubmit={handleAddAccount}
        className="form"
      >

        <h3>Add Account</h3>

        <input
          type="text"
          placeholder="Account Number"
          value={accountNumber}
          onChange={(e) =>
            setAccountNumber(e.target.value)
          }
        />

        <input
          type="text"
          placeholder="Account Type (SAVINGS / CURRENT)"
          value={accountType}
          onChange={(e) =>
            setAccountType(e.target.value)
          }
        />

        <input
          type="number"
          placeholder="Initial Balance"
          value={balance}
          onChange={(e) =>
            setBalance(e.target.value)
          }
        />

        <input
          type="number"
          placeholder="Customer ID"
          value={customerId}
          onChange={(e) =>
            setCustomerId(e.target.value)
          }
        />

        <button type="submit">
          Add Account
        </button>

      </form>


      {/* =========================
          MESSAGES
      ========================= */}

      {message && (
        <p className="success">
          {message}
        </p>
      )}

      {error && (
        <p className="error">
          {error}
        </p>
      )}


      {/* =========================
          VIEW ACCOUNT
      ========================= */}

      <div className="account-search">

        <input
          type="number"
          placeholder="Enter Account ID"
          value={accountId}
          onChange={(e) =>
            setAccountId(e.target.value)
          }
        />

        <button onClick={handleViewAccount}>
          View Account
        </button>

      </div>


      {selectedAccount && (

        <div className="card">

          <h3>Account Details</h3>

          <p>
            <strong>ID:</strong>{" "}
            {selectedAccount.id}
          </p>

          <p>
            <strong>Account Number:</strong>{" "}
            {selectedAccount.accountNumber}
          </p>

          <p>
            <strong>Type:</strong>{" "}
            {selectedAccount.accountType}
          </p>

          <p>
            <strong>Balance:</strong>{" "}
            ₹{selectedAccount.balance}
          </p>

          <p>
            <strong>Customer ID:</strong>{" "}
            {selectedAccount.customerId}
          </p>

        </div>

      )}


      {/* =========================
          ACCOUNT LIST
      ========================= */}

      <h3>All Accounts</h3>

      {accounts.length === 0 ? (

        <p>No accounts found.</p>

      ) : (

        <div className="list">

          {accounts.map((account) => (

            <div
              className="card"
              key={account.id}
            >

              <h3>
                Account #{account.id}
              </h3>

              <p>
                <strong>Number:</strong>{" "}
                {account.accountNumber}
              </p>

              <p>
                <strong>Type:</strong>{" "}
                {account.accountType}
              </p>

              <p>
                <strong>Balance:</strong>{" "}
                ₹{account.balance}
              </p>

              <p>
                <strong>Customer:</strong>{" "}
                {account.customerId}
              </p>


              <button
                onClick={() => {

                  setSelectedAccount(account);

                  onSelectAccount(account.id);

                }}
              >
                View Details
              </button>


              <button
                className="delete-button"
                onClick={() =>
                  handleDeleteAccount(account.id)
                }
              >
                Delete
              </button>

            </div>

          ))}

        </div>

      )}

    </section>

  );
}

export default AccountSection;