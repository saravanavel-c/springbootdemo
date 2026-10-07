import { useEffect, useState } from "react";

import {
  getAccounts,
  getAccountById,
  getTransactions,
  createTransaction,
  getBeneficiaries,
  createTransfer,
} from "../api";

function TransactionSection({
  role,
  accountId,
  onSelectAccount,
}) {
  const [accounts, setAccounts] = useState([]);
  const [beneficiaries, setBeneficiaries] = useState([]);
  const [selectedAccount, setSelectedAccount] = useState(null);
  const [transactions, setTransactions] = useState([]);

  const [type, setType] = useState("");
  const [amount, setAmount] = useState("");

  // Transfer fields
  const [transferMode, setTransferMode] = useState("beneficiary");
  const [beneficiaryId, setBeneficiaryId] = useState("");
  const [toAccountNumber, setToAccountNumber] = useState("");
  const [description, setDescription] = useState("");

  const [error, setError] = useState("");
  const [message, setMessage] = useState("");
  const [loading, setLoading] = useState(false);

  const isMaker = role === "MAKER";
  const canTransfer =
    role === "ADMIN" ||
    role === "MAKER" ||
    role === "USER";

  // =========================
  // LOAD ACCOUNTS
  // =========================

  const loadAccounts = async () => {
    try {
      const data = await getAccounts();
      setAccounts(data || []);
    } catch (error) {
      setError(error.message);
    }
  };

  // =========================
  // LOAD BENEFICIARIES
  // =========================

  const loadBeneficiaries = async () => {
    try {
      const data = await getBeneficiaries();
      setBeneficiaries(data || []);
    } catch (error) {
      console.error("Failed to load beneficiaries:", error);
    }
  };

  useEffect(() => {
    loadAccounts();
    loadBeneficiaries();
  }, []);

  // =========================
  // LOAD SELECTED ACCOUNT
  // =========================

  useEffect(() => {
    if (!accountId) {
      setSelectedAccount(null);
      setTransactions([]);
      return;
    }

    loadSelectedAccount(accountId);
  }, [accountId]);

  const loadSelectedAccount = async (id) => {
    try {
      setLoading(true);
      setError("");

      const account = await getAccountById(id);
      setSelectedAccount(account);

      const data = await getTransactions(id);
      setTransactions(data || []);
    } catch (error) {
      setError(error.message);
      setSelectedAccount(null);
      setTransactions([]);
    } finally {
      setLoading(false);
    }
  };

  // =========================
  // SELECT ACCOUNT
  // =========================

  const handleSelectAccount = (id) => {
    if (!id) {
      setSelectedAccount(null);
      setTransactions([]);
      onSelectAccount(null);
      return;
    }

    onSelectAccount(Number(id));
  };

  // =========================
  // TYPE CHANGE
  // =========================

  const handleTypeChange = (value) => {
    setType(value);

    // Clear transfer-specific fields
    if (value !== "TRANSFER") {
      setBeneficiaryId("");
      setToAccountNumber("");
      setDescription("");
    }

    setError("");
    setMessage("");
  };

  // =========================
  // CREATE TRANSACTION / TRANSFER
  // =========================

  const handleSubmit = async (event) => {
    event.preventDefault();

    setError("");
    setMessage("");

    if (!accountId) {
      setError("Please select an account.");
      return;
    }

    if (!type) {
      setError("Please select a transaction type.");
      return;
    }

    if (!amount) {
      setError("Amount is required.");
      return;
    }

    if (Number(amount) <= 0) {
      setError("Amount must be greater than 0.");
      return;
    }

    // =========================
    // TRANSFER
    // =========================

    if (type === "TRANSFER") {
      if (!canTransfer) {
        setError("Your role does not have permission to make transfers.");
        return;
      }

      if (transferMode === "beneficiary" && !beneficiaryId) {
        setError("Please select a beneficiary.");
        return;
      }

      if (transferMode === "account" && !toAccountNumber.trim()) {
        setError("Please enter the destination account number.");
        return;
      }

      if (
        transferMode === "account" &&
        String(selectedAccount?.accountNumber) ===
        String(toAccountNumber).trim()
      ) {
        setError("You cannot transfer money to the same account.");
        return;
      }

      try {
        setLoading(true);

        const transferData = {
          fromAccountId: Number(accountId),
          beneficiaryId:
            transferMode === "beneficiary"
              ? Number(beneficiaryId)
              : null,
          toAccountNumber:
            transferMode === "account"
              ? toAccountNumber.trim()
              : null,
          amount: Number(amount),
          description: description.trim(),
        };

        const response = await createTransfer(transferData);

        setMessage(
          response?.message ||
          "Transfer completed successfully."
        );

        // Clear form
        setType("");
        setAmount("");
        setBeneficiaryId("");
        setToAccountNumber("");
        setDescription("");
        setTransferMode("beneficiary");

        // Refresh balance and transaction history
        await loadSelectedAccount(accountId);

        // Refresh accounts too
        await loadAccounts();
      } catch (error) {
        setError(error.message);
      } finally {
        setLoading(false);
      }

      return;
    }

    // =========================
    // NORMAL CREDIT / DEBIT
    // =========================

    if (!isMaker) {
      setError(
        "Only MAKER users can create credit or debit transactions."
      );
      return;
    }

    try {
      setLoading(true);

      await createTransaction(accountId, {
        type,
        amount: Number(amount),
      });

      setMessage("Transaction created successfully.");

      setType("");
      setAmount("");

      // Refresh balance and transaction history
      await loadSelectedAccount(accountId);

      // Refresh accounts
      await loadAccounts();
    } catch (error) {
      setError(error.message);
    } finally {
      setLoading(false);
    }
  };

  // =========================
  // FORMAT CURRENCY
  // =========================

  const formatCurrency = (value) => {
    return new Intl.NumberFormat("en-IN", {
      style: "currency",
      currency: "INR",
      minimumFractionDigits: 2,
    }).format(Number(value || 0));
  };

  // =========================
  // FORMAT DATE
  // =========================

  const formatDate = (date) => {
    if (!date) {
      return "";
    }

    const parsedDate = new Date(date);

    if (Number.isNaN(parsedDate.getTime())) {
      return date;
    }

    return parsedDate.toLocaleDateString("en-IN", {
      day: "2-digit",
      month: "short",
      year: "numeric",
    });
  };

  // =========================
  // TRANSACTION ICON
  // =========================

  const isCredit = (transactionType) => {
    return transactionType === "CREDIT";
  };

  // =========================
  // RENDER
  // =========================

  return (
    <section className="section transaction-page">

      {/* =========================
          HEADER
      ========================= */}

      <div className="transaction-page-header">
        <div>
          <p className="eyebrow">
            ACCOUNT ACTIVITY
          </p>

          <h2>
            Transactions
          </h2>

          <p>
            View and manage your account
            transactions.
          </p>
        </div>
      </div>

      {/* =========================
          ACCOUNT SELECTOR
      ========================= */}

      <div className="transaction-account-selector">

        <label>
          Select Account
        </label>

        <select
          value={accountId || ""}
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
              {account.accountType}
              {" •••• "}
              {String(
                account.accountNumber
              ).slice(-4)}
            </option>
          ))}
        </select>

      </div>

      {/* =========================
          ERROR
      ========================= */}

      {error && (
        <div className="transaction-error">
          {error}
        </div>
      )}

      {/* =========================
          SUCCESS
      ========================= */}

      {message && (
        <p className="success">
          {message}
        </p>
      )}

      {/* =========================
          NO ACCOUNT
      ========================= */}

      {!accountId && (
        <div className="transaction-empty">

          <div className="transaction-empty-icon">
            ↕
          </div>

          <h3>
            Select an account
          </h3>

          <p>
            Choose an account above to
            view its transaction history.
          </p>

        </div>
      )}

      {/* =========================
          ACCOUNT CONTENT
      ========================= */}

      {accountId && (
        <>

          {/* =========================
              ACCOUNT SUMMARY
          ========================= */}

          {selectedAccount && (
            <div className="transaction-account-summary">

              <div>
                <span>
                  {selectedAccount.accountType}
                </span>

                <h3>
                  ••••{" "}
                  {String(
                    selectedAccount.accountNumber
                  ).slice(-4)}
                </h3>
              </div>

              <div className="transaction-balance">

                <small>
                  Current Balance
                </small>

                <strong>
                  {formatCurrency(
                    selectedAccount.balance
                  )}
                </strong>

              </div>

            </div>
          )}

          {/* =========================
              TRANSACTION HISTORY
          ========================= */}

          <div className="transaction-history-panel">

            <div className="transaction-panel-header">

              <div>

                <h3>
                  Transaction History
                </h3>

                <p>
                  {transactions.length}{" "}
                  transaction
                  {transactions.length !== 1
                    ? "s"
                    : ""}
                </p>

              </div>

            </div>

            {loading && (
              <div className="transaction-loading">
                Loading...
              </div>
            )}

            {!loading &&
              transactions.length === 0 && (
                <div className="transaction-empty-small">

                  <span>
                    ↕
                  </span>

                  <p>
                    No transactions found
                  </p>

                </div>
              )}

            {!loading &&
              transactions.length > 0 && (
                <div className="transaction-history-list">

                  {transactions.map(
                    (transaction) => {

                      const credit =
                        isCredit(
                          transaction.type
                        );

                      return (
                        <div
                          className="transaction-row"
                          key={transaction.id}
                        >

                          <div
                            className={
                              credit
                                ? "transaction-row-icon credit"
                                : "transaction-row-icon debit"
                            }
                          >
                            {credit
                              ? "↓"
                              : "↑"}
                          </div>

                          <div className="transaction-row-details">

                            <strong>
                              {transaction.type}
                            </strong>

                            <span>
                              Transaction #
                              {transaction.id}
                            </span>

                            <small>
                              {formatDate(
                                transaction.transactionDate
                              )}
                            </small>

                          </div>

                          <strong
                            className={
                              credit
                                ? "transaction-row-amount credit"
                                : "transaction-row-amount debit"
                            }
                          >

                            {credit
                              ? "+"
                              : "-"}

                            {formatCurrency(
                              transaction.amount
                            )}

                          </strong>

                        </div>
                      );
                    }
                  )}

                </div>
              )}

          </div>

          {/* =========================
              CREATE TRANSACTION
          ========================= */}

          {(isMaker || canTransfer) && (
            <div className="create-transaction-panel">

              <div className="transaction-panel-header">

                <div>

                  <h3>
                    Create Transaction
                  </h3>

                  <p>
                    Add a transaction or transfer
                    money to another account.
                  </p>

                </div>

              </div>

              <form
                onSubmit={handleSubmit}
                className="transaction-form"
              >

                {/* TRANSACTION TYPE */}

                <div className="transaction-form-field">

                  <label>
                    Transaction Type
                  </label>

                  <select
                    value={type}
                    onChange={(e) =>
                      handleTypeChange(
                        e.target.value
                      )
                    }
                  >

                    <option value="">
                      Select transaction type
                    </option>

                    {isMaker && (
                      <>
                        <option value="CREDIT">
                          CREDIT
                        </option>

                        <option value="DEBIT">
                          DEBIT
                        </option>
                      </>
                    )}

                    {canTransfer && (
                      <option value="TRANSFER">
                        TRANSFER
                      </option>
                    )}

                  </select>

                </div>

                {/* =========================
                    TRANSFER OPTIONS
                ========================= */}

                {type === "TRANSFER" && (
                  <>

                    <div className="transaction-form-field">

                      <label>
                        Transfer To
                      </label>

                      <select
                        value={transferMode}
                        onChange={(e) => {
                          setTransferMode(
                            e.target.value
                          );

                          setBeneficiaryId("");
                          setToAccountNumber("");
                        }}
                      >

                        <option value="beneficiary">
                          Saved Beneficiary
                        </option>

                        <option value="account">
                          Account Number
                        </option>

                      </select>

                    </div>

                    {/* BENEFICIARY */}

                    {transferMode ===
                      "beneficiary" && (
                        <div className="transaction-form-field">

                          <label>
                            Select Beneficiary
                          </label>

                          <select
                            value={beneficiaryId}
                            onChange={(e) =>
                              setBeneficiaryId(
                                e.target.value
                              )
                            }
                          >

                            <option value="">
                              Select beneficiary
                            </option>

                            {beneficiaries.map(
                              (beneficiary) => (
                                <option
                                  key={
                                    beneficiary.id
                                  }
                                  value={
                                    beneficiary.id
                                  }
                                >
                                  {beneficiary.name}
                                  {" • "}
                                  {beneficiary.accountNumber}
                                </option>
                              )
                            )}

                          </select>

                          {beneficiaries.length ===
                            0 && (
                              <small>
                                No beneficiaries found.
                                You can add one from the
                                Beneficiaries section or
                                transfer using an account
                                number.
                              </small>
                            )}

                        </div>
                      )}

                    {/* DIRECT ACCOUNT */}

                    {transferMode ===
                      "account" && (
                        <div className="transaction-form-field">

                          <label>
                            Destination Account Number
                          </label>

                          <input
                            type="text"
                            placeholder="Enter account number"
                            value={toAccountNumber}
                            onChange={(e) =>
                              setToAccountNumber(
                                e.target.value
                              )
                            }
                          />

                        </div>
                      )}

                    {/* DESCRIPTION */}

                    <div className="transaction-form-field">

                      <label>
                        Description
                      </label>

                      <input
                        type="text"
                        placeholder="Enter transfer description"
                        value={description}
                        onChange={(e) =>
                          setDescription(
                            e.target.value
                          )
                        }
                      />

                    </div>

                  </>
                )}

                {/* AMOUNT */}

                <div className="transaction-form-field">

                  <label>
                    Amount
                  </label>

                  <input
                    type="number"
                    min="0.01"
                    step="0.01"
                    placeholder="Enter amount"
                    value={amount}
                    onChange={(e) =>
                      setAmount(
                        e.target.value
                      )
                    }
                  />

                </div>

                {/* SUBMIT */}

                <button
                  type="submit"
                  disabled={loading}
                >
                  {loading
                    ? "Processing..."
                    : type === "TRANSFER"
                      ? "Transfer Money"
                      : "Add Transaction"}
                </button>

              </form>

            </div>
          )}

          {/* =========================
              READ ONLY MESSAGE
          ========================= */}

          {!isMaker && !canTransfer && (
            <div className="transaction-readonly">

              <span>
                🔒
              </span>

              <div>

                <strong>
                  Read-only access
                </strong>

                <p>
                  Your current role can view
                  transactions but cannot
                  create transactions or
                  transfers.
                </p>

              </div>

            </div>
          )}

        </>
      )}

    </section>
  );
}

export default TransactionSection;