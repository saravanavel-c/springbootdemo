import { useEffect, useState } from "react";

import {
  getAccounts,
  getAccountById,
  getTransactions,
  createTransaction,
} from "../api";


function TransactionSection({
  role,
  accountId,
  onSelectAccount,
}) {

  const [accounts, setAccounts] = useState([]);
  const [selectedAccount, setSelectedAccount] =
    useState(null);

  const [transactions, setTransactions] =
    useState([]);

  const [type, setType] = useState("");
  const [amount, setAmount] = useState("");

  const [error, setError] = useState("");
  const [message, setMessage] = useState("");

  const [loading, setLoading] = useState(false);


  const isMaker = role === "MAKER";


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


  useEffect(() => {

    loadAccounts();

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

      const account =
        await getAccountById(id);

      setSelectedAccount(account);

      const data =
        await getTransactions(id);

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
  // CREATE TRANSACTION
  // =========================

  const handleSubmit = async (event) => {

    event.preventDefault();

    setError("");
    setMessage("");

    if (!accountId) {

      setError(
        "Please select an account."
      );

      return;
    }

    if (!type || !amount) {

      setError(
        "Transaction type and amount are required."
      );

      return;
    }

    if (Number(amount) <= 0) {

      setError(
        "Transaction amount must be greater than 0."
      );

      return;
    }


    try {

      setLoading(true);

      await createTransaction(accountId, {
        type,
        amount: Number(amount),
      });


      setMessage(
        "Transaction created successfully."
      );

      setType("");
      setAmount("");


      // Refresh account balance
      // and transaction history
      await loadSelectedAccount(accountId);


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

    return new Intl.NumberFormat(
      "en-IN",
      {
        style: "currency",
        currency: "INR",
        minimumFractionDigits: 2,
      }
    ).format(Number(value || 0));

  };


  // =========================
  // FORMAT DATE
  // =========================

  const formatDate = (date) => {

    if (!date) {
      return "";
    }

    const parsedDate =
      new Date(date);

    if (
      Number.isNaN(
        parsedDate.getTime()
      )
    ) {
      return date;
    }

    return parsedDate.toLocaleDateString(
      "en-IN",
      {
        day: "2-digit",
        month: "short",
        year: "numeric",
      }
    );

  };


  // =========================
  // TRANSACTION ICON
  // =========================

  const isCredit = (type) =>
    type === "CREDIT";


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
            handleSelectAccount(
              e.target.value
            )
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

                  <span>↕</span>

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

          {isMaker && (

            <div className="create-transaction-panel">

              <div className="transaction-panel-header">

                <div>

                  <h3>
                    Create Transaction
                  </h3>

                  <p>
                    Add a credit or debit
                    transaction.
                  </p>

                </div>

              </div>


              <form
                onSubmit={handleSubmit}
                className="transaction-form"
              >

                <div className="transaction-form-field">

                  <label>
                    Transaction Type
                  </label>

                  <select
                    value={type}
                    onChange={(e) =>
                      setType(
                        e.target.value
                      )
                    }
                  >

                    <option value="">
                      Select transaction type
                    </option>

                    <option value="CREDIT">
                      CREDIT
                    </option>

                    <option value="DEBIT">
                      DEBIT
                    </option>

                  </select>

                </div>


                <div className="transaction-form-field">

                  <label>
                    Amount
                  </label>

                  <input
                    type="number"
                    min="0"
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


                <button
                  type="submit"
                  disabled={loading}
                >

                  {loading
                    ? "Processing..."
                    : "Add Transaction"}

                </button>

              </form>


              {message && (

                <p className="success">
                  {message}
                </p>

              )}

            </div>

          )}


          {/* =========================
              READ ONLY MESSAGE
          ========================= */}

          {!isMaker && (

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
                  create them.
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