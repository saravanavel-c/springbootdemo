import { useEffect, useState } from "react";

import {
  getAccountById,
  getAccounts,
  createAccount,
  deleteAccount,
} from "../api";

function AccountSection({
  role,
  selectedAccountId,
  onSelectAccount,
}) {

  const [accounts, setAccounts] = useState([]);

  const [selectedAccount, setSelectedAccount] =
    useState(null);

  const [accountId, setAccountId] =
    useState("");

  const [accountNumber, setAccountNumber] =
    useState("");

  const [accountType, setAccountType] =
    useState("");

  const [balance, setBalance] =
    useState("");

  const [customerId, setCustomerId] =
    useState("");

  const [error, setError] =
    useState("");

  const [message, setMessage] =
    useState("");

  const isAdmin = role === "ADMIN";


  // =========================
  // LOAD ACCOUNTS
  // =========================

  const loadAccounts = async () => {
    try {

      setError("");

      const data = await getAccounts();

      setAccounts(data || []);

    } catch (error) {

      setError(error.message);

    }
  };

  useEffect(() => {
    loadAccounts();
  }, []);


  // =========================
  // SYNC SELECTED ACCOUNT
  // =========================

  useEffect(() => {

    if (!selectedAccountId || accounts.length === 0) {
      return;
    }

    const account = accounts.find(
      (item) => item.id === Number(selectedAccountId)
    );

    if (account) {

      setSelectedAccount(account);

      setAccountId(String(account.id));

    }

  }, [selectedAccountId, accounts]);

  // =========================
  // SELECT ACCOUNT
  // =========================

  const handleSelectAccount = (id) => {

    if (!id) {
      setSelectedAccount(null);
      onSelectAccount(null);
      return;
    }

    const account =
      accounts.find(
        (item) => item.id === Number(id)
      );

    if (account) {

      setSelectedAccount(account);

      setAccountId(String(account.id));

      onSelectAccount(account.id);
    }
  };


  // =========================
  // VIEW ACCOUNT BY ID
  // =========================

  const handleViewAccount = async () => {

    if (!accountId) {

      setError("Enter an account ID.");

      return;
    }

    try {

      setError("");

      const data =
        await getAccountById(accountId);

      setSelectedAccount(data);
      setAccountId(String(data.id));
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

      setError(
        "All account fields are required."
      );

      return;
    }

    try {

      await createAccount({
        accountNumber,
        accountType,
        balance: Number(balance),
        customerId: Number(customerId),
      });

      setMessage(
        "Account created successfully."
      );

      setAccountNumber("");
      setAccountType("");
      setBalance("");
      setCustomerId("");

      await loadAccounts();

    } catch (error) {

      setError(error.message);

    }
  };


  // =========================
  // DELETE ACCOUNT
  // =========================

  const handleDeleteAccount = async (id) => {

    const confirmed =
      window.confirm(
        "Are you sure you want to delete this account?"
      );

    if (!confirmed) {
      return;
    }

    try {

      setError("");
      setMessage("");

      await deleteAccount(id);

      setMessage(
        "Account deleted successfully."
      );

      if (
        selectedAccount?.id === id
      ) {

        setSelectedAccount(null);
        onSelectAccount(null);
        setAccountId("");

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
          ADMIN ADD ACCOUNT
      ========================= */}

      {isAdmin && (

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

      )}


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
          ACCOUNT SELECTOR
      ========================= */}

      <div className="account-selector">

        <h3>Select Account</h3>

        <select
          value={accountId}
          onChange={(e) =>
            handleSelectAccount(e.target.value)
          }
        >

          <option value="">
            Select an account
          </option>

          {accounts.map((account) => (

            <option
              key={account.id}
              value={account.id}
            >
              Account #{account.id} -
              {` ${account.accountType} - ₹${account.balance}`}
            </option>

          ))}

        </select>

      </div>


      {/* =========================
          VIEW ACCOUNT BY ID
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

        <button
          onClick={handleViewAccount}
        >
          View Account
        </button>

      </div>


      {/* =========================
          SELECTED ACCOUNT
      ========================= */}
      {selectedAccount && (
        <div className="account-detail-panel">

          <div className="account-detail-header">

            <div>
              <span className="account-detail-label">
                {selectedAccount.accountType}
              </span>

              <h3>
                ••••{" "}
                {String(
                  selectedAccount.accountNumber
                ).slice(-4)}
              </h3>
            </div>

            <div className="account-detail-icon">
              💳
            </div>

          </div>


          <div className="account-balance-section">

            <span>Available Balance</span>

            <strong>
              ₹
              {Number(
                selectedAccount.balance || 0
              ).toLocaleString("en-IN", {
                minimumFractionDigits: 2,
              })}
            </strong>

          </div>


          <div className="account-detail-grid">

            <div>
              <span>Account ID</span>
              <strong>
                {selectedAccount.id}
              </strong>
            </div>

            <div>
              <span>Customer ID</span>
              <strong>
                {selectedAccount.customerId}
              </strong>
            </div>

            <div>
              <span>Account Type</span>
              <strong>
                {selectedAccount.accountType}
              </strong>
            </div>

            <div>
              <span>Account Number</span>
              <strong>
                {selectedAccount.accountNumber}
              </strong>
            </div>

          </div>

        </div>
      )}

      {/* =========================
          ALL ACCOUNTS
      ========================= */}

      <h3>
        All Accounts
      </h3>

      {accounts.length === 0 ? (

        <p>
          No accounts found.
        </p>

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
                  setAccountId(
                    String(account.id)
                  );
                  onSelectAccount(account.id);
                }}
              >
                Select Account
              </button>


              {isAdmin && (

                <button
                  className="delete-button"
                  onClick={() =>
                    handleDeleteAccount(
                      account.id
                    )
                  }
                >
                  Delete Account
                </button>

              )}

            </div>

          ))}

        </div>

      )}

    </section>

  );
}

export default AccountSection;