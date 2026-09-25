import { useEffect, useState } from "react";

import {
  getTransactions,
  createTransaction,
} from "../api";

function TransactionSection({ accountId }) {

  const [transactions, setTransactions] = useState([]);

  const [type, setType] = useState("");
  const [amount, setAmount] = useState("");

  const [error, setError] = useState("");
  const [message, setMessage] = useState("");


  // =========================
  // LOAD TRANSACTIONS
  // =========================

  const loadTransactions = async () => {

    if (!accountId) {
      return;
    }

    try {

      setError("");

      const data = await getTransactions(accountId);

      setTransactions(data);

    } catch (error) {

      setError(error.message);

      setTransactions([]);

    }
  };


  useEffect(() => {
    loadTransactions();
  }, [accountId]);


  // =========================
  // ADD TRANSACTION
  // =========================

  const handleSubmit = async (event) => {

    event.preventDefault();

    setError("");
    setMessage("");

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

      await createTransaction(accountId, {
        type,
        amount: Number(amount),
      });

      setMessage(
        "Transaction created successfully."
      );

      setType("");
      setAmount("");

      await loadTransactions();

    } catch (error) {

      setError(error.message);

    }
  };


  return (

    <section className="section">

      <h2>Transaction History</h2>


      {!accountId && (

        <p>
          Select an account to view transactions.
        </p>

      )}
      {accountId && (
        <p>
            <strong>Selected Account:</strong> #{accountId}
        </p>
       )}


      {/* =========================
          ADD TRANSACTION
      ========================= */}

      {accountId && (

        <form
          onSubmit={handleSubmit}
          className="form"
        >

          <h3>
            Add Transaction
          </h3>

          <select
            value={type}
            onChange={(e) =>
              setType(e.target.value)
            }
          >

            <option value="">
              Select Transaction Type
            </option>

            <option value="CREDIT">
              CREDIT
            </option>

            <option value="DEBIT">
              DEBIT
            </option>

          </select>


          <input
            type="number"
            placeholder="Amount"
            value={amount}
            onChange={(e) =>
              setAmount(e.target.value)
            }
          />


          <button type="submit">
            Add Transaction
          </button>

        </form>

      )}


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
          TRANSACTION LIST
      ========================= */}

      {accountId &&
        transactions.length === 0 &&
        !error && (

          <p>
            No transactions found.
          </p>

        )}


      {transactions.map((transaction) => (

        <div
          className="card"
          key={transaction.id}
        >

          <h3>
            {transaction.type}
          </h3>

          <p>
            <strong>Transaction ID:</strong>{" "}
            {transaction.id}
          </p>

          <p>
            <strong>Amount:</strong>{" "}
            ₹{transaction.amount}
          </p>

          <p>
            <strong>Date:</strong>{" "}
            {transaction.transactionDate}
          </p>

          <p>
            <strong>Account:</strong>{" "}
            {transaction.accountId}
          </p>

        </div>

      ))}

    </section>

  );
}

export default TransactionSection;