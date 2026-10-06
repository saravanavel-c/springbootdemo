import { useEffect, useState } from "react";
import {
    getAccounts,
    getTransactions,
} from "../api";

function Dashboard({
    username,
    currentRole,
    setActiveSection,
    onSelectAccount,
}) {
    const [accounts, setAccounts] = useState([]);
    const [transactions, setTransactions] = useState([]);

    const [loading, setLoading] = useState(true);
    const [error, setError] = useState("");

    // =========================
    // LOAD DASHBOARD DATA
    // =========================

    useEffect(() => {
        loadDashboard();
    }, []);

    const loadDashboard = async () => {
        try {
            setLoading(true);
            setError("");

            const accountData = await getAccounts();

            setAccounts(accountData || []);

            // Get transactions for every account
            const transactionResults = await Promise.all(
                (accountData || []).map(async (account) => {
                    try {
                        const data = await getTransactions(account.id);

                        return (data || []).map((transaction) => ({
                            ...transaction,
                            accountId: account.id,
                            accountNumber: account.accountNumber,
                        }));
                    } catch (err) {
                        console.error(
                            `Failed to load transactions for account ${account.id}`,
                            err
                        );

                        return [];
                    }
                })
            );

            const allTransactions =
                transactionResults.flat();

            // Sort newest first
            allTransactions.sort((a, b) => {
                const dateA = new Date(
                    a.createdAt || a.transactionDate || 0
                );

                const dateB = new Date(
                    b.createdAt || b.transactionDate || 0
                );

                return dateB - dateA;
            });

            setTransactions(allTransactions);
        } catch (err) {
            console.error("Dashboard loading failed:", err);
            setError(err.message || "Failed to load dashboard");
        } finally {
            setLoading(false);
        }
    };

    // =========================
    // CALCULATIONS
    // =========================

    const totalBalance = accounts.reduce(
        (total, account) =>
            total + Number(account.balance || 0),
        0
    );

    const formatCurrency = (amount) => {
        return new Intl.NumberFormat("en-IN", {
            style: "currency",
            currency: "INR",
            maximumFractionDigits: 2,
        }).format(amount);
    };

    const maskAccountNumber = (accountNumber) => {
        if (!accountNumber) {
            return "••••";
        }

        const value = String(accountNumber);

        if (value.length <= 4) {
            return value;
        }

        return `•••• ${value.slice(-4)}`;
    };

    const formatDate = (date) => {
        if (!date) {
            return "";
        }

        const parsedDate = new Date(date);

        if (Number.isNaN(parsedDate.getTime())) {
            return "";
        }

        return parsedDate.toLocaleDateString("en-IN", {
            day: "2-digit",
            month: "short",
            year: "numeric",
        });
    };

    const getTransactionAmount = (transaction) => {
        return Number(
            transaction.amount ||
            transaction.transactionAmount ||
            0
        );
    };

    const getTransactionType = (transaction) => {
        return (
            transaction.type ||
            transaction.transactionType ||
            "TRANSACTION"
        ).toUpperCase();
    };

    // =========================
    // LOADING
    // =========================

    if (loading) {
        return (
            <div className="dashboard-content">

                <div className="welcome-section">
                    <p className="eyebrow">
                        BANKING DASHBOARD
                    </p>

                    <h2>
                        Welcome back, {username}
                    </h2>

                    <p>
                        Loading your banking overview...
                    </p>
                </div>

                <div className="dashboard-loading">
                    Loading dashboard...
                </div>

            </div>
        );
    }

    // =========================
    // ERROR
    // =========================

    if (error) {
        return (
            <div className="dashboard-content">

                <div className="welcome-section">
                    <p className="eyebrow">
                        BANKING DASHBOARD
                    </p>

                    <h2>
                        Welcome back, {username}
                    </h2>
                </div>

                <div className="dashboard-error">
                    <strong>
                        Unable to load dashboard
                    </strong>

                    <p>{error}</p>

                    <button onClick={loadDashboard}>
                        Try Again
                    </button>
                </div>

            </div>
        );
    }

    // =========================
    // DASHBOARD
    // =========================

    return (
        <div className="dashboard-content">

            {/* =========================
          WELCOME
      ========================= */}

            <div className="welcome-section">

                <p className="eyebrow">
                    BANKING DASHBOARD
                </p>

                <h2>
                    Welcome back, {username}
                </h2>

                <p>
                    Here's your banking overview.
                </p>

            </div>


            {/* =========================
          SUMMARY CARDS
      ========================= */}

            <div className="dashboard-summary">

                <div className="summary-card balance-card">

                    <div className="summary-icon">
                        ₹
                    </div>

                    <div>
                        <p>Total Balance</p>

                        <h3>
                            {formatCurrency(totalBalance)}
                        </h3>
                    </div>

                </div>


                <div className="summary-card">

                    <div className="summary-icon">
                        💳
                    </div>

                    <div>
                        <p>Total Accounts</p>

                        <h3>
                            {accounts.length}
                        </h3>
                    </div>

                </div>


                <div className="summary-card">

                    <div className="summary-icon">
                        ↕
                    </div>

                    <div>
                        <p>Transactions</p>

                        <h3>
                            {transactions.length}
                        </h3>
                    </div>

                </div>

            </div>


            {/* =========================
          MAIN DASHBOARD GRID
      ========================= */}

            <div className="dashboard-main-grid">

                {/* =========================
            ACCOUNTS
        ========================= */}

                <div className="dashboard-panel">

                    <div className="panel-header">

                        <div>
                            <h3>Your Accounts</h3>

                            <p>
                                Your connected bank accounts
                            </p>
                        </div>

                        <button
                            className="text-button"
                            onClick={() =>
                                setActiveSection("accounts")
                            }
                        >
                            View All →
                        </button>

                    </div>


                    {accounts.length === 0 ? (

                        <div className="empty-state">
                            <span>💳</span>

                            <p>
                                No accounts available.
                            </p>
                        </div>

                    ) : (

                        <div className="dashboard-account-list">

                            {accounts.map((account) => (

                                <div className="dashboard-account-card"
                                    key={account.id}
                                    onClick={() => {
                                        onSelectAccount(account.id);
                                        setActiveSection("accounts");
                                    }}
                                >

                                    <div className="account-card-top">

                                        <div>
                                            <span className="account-type">
                                                {account.accountType}
                                            </span>

                                            <h4>
                                                {maskAccountNumber(
                                                    account.accountNumber
                                                )}
                                            </h4>
                                        </div>

                                        <span className="account-icon">
                                            💳
                                        </span>

                                    </div>


                                    <div className="account-card-bottom">

                                        <div>
                                            <span>Available Balance</span>

                                            <strong>
                                                {formatCurrency(
                                                    Number(account.balance || 0)
                                                )}
                                            </strong>
                                        </div>

                                        <button
                                            className="account-view-button"
                                            onClick={() => {
                                                onSelectAccount(account.id);
                                                setActiveSection("accounts");
                                            }}
                                        >
                                            View Account →
                                        </button>

                                    </div>

                                </div>

                            ))}

                        </div>

                    )}

                </div>


                {/* =========================
            RECENT TRANSACTIONS
        ========================= */}

                <div className="dashboard-panel">

                    <div className="panel-header">

                        <div>
                            <h3>Recent Transactions</h3>

                            <p>
                                Your latest account activity
                            </p>
                        </div>

                        <button
                            className="text-button"
                            onClick={() =>
                                setActiveSection("transactions")
                            }
                        >
                            View All →
                        </button>

                    </div>


                    {transactions.length === 0 ? (

                        <div className="empty-state">
                            <span>↕</span>

                            <p>
                                No transactions yet.
                            </p>
                        </div>

                    ) : (

                        <div className="transaction-list">

                            {transactions
                                .slice(0, 5)
                                .map((transaction, index) => {

                                    const type =
                                        getTransactionType(transaction);

                                    const amount =
                                        getTransactionAmount(transaction);

                                    const isCredit =
                                        type === "CREDIT";

                                    return (
                                        <div
                                            className="dashboard-transaction"
                                            key={
                                                transaction.id ||
                                                `${transaction.accountId}-${index}`
                                            }
                                        >

                                            <div
                                                className={
                                                    isCredit
                                                        ? "transaction-icon credit"
                                                        : "transaction-icon debit"
                                                }
                                            >
                                                {isCredit ? "↓" : "↑"}
                                            </div>


                                            <div className="transaction-details">

                                                <strong>
                                                    {type}
                                                </strong>

                                                <span>
                                                    {maskAccountNumber(
                                                        transaction.accountNumber
                                                    )}
                                                </span>

                                                <small>
                                                    {formatDate(
                                                        transaction.createdAt ||
                                                        transaction.transactionDate
                                                    )}
                                                </small>

                                            </div>


                                            <strong
                                                className={
                                                    isCredit
                                                        ? "transaction-credit"
                                                        : "transaction-debit"
                                                }
                                            >
                                                {isCredit ? "+" : "-"}
                                                {formatCurrency(amount)}
                                            </strong>

                                        </div>
                                    );
                                })}

                        </div>

                    )}

                </div>

            </div>


            {/* =========================
          BOTTOM SECTION
      ========================= */}

            <div className="dashboard-bottom-grid">

                {/* QUICK ACTIONS */}

                <div className="dashboard-panel">

                    <div className="panel-header">

                        <div>
                            <h3>Quick Actions</h3>

                            <p>
                                Frequently used banking services
                            </p>
                        </div>

                    </div>


                    <div className="dashboard-actions">

                        <button
                            onClick={() =>
                                setActiveSection("accounts")
                            }
                        >
                            <span>💳</span>
                            <strong>Accounts</strong>
                            <small>
                                View your accounts
                            </small>
                        </button>


                        <button
                            onClick={() =>
                                setActiveSection("transactions")
                            }
                        >
                            <span>↕</span>
                            <strong>Transactions</strong>
                            <small>
                                View account activity
                            </small>
                        </button>


                        <button
                            onClick={() =>
                                setActiveSection("beneficiaries")
                            }
                        >
                            <span>👥</span>
                            <strong>Beneficiaries</strong>
                            <small>
                                Manage beneficiaries
                            </small>
                        </button>

                    </div>

                </div>


                {/* SECURITY / ROLE */}

                <div className="dashboard-security">

                    <div className="security-large-icon">
                        🔒
                    </div>

                    <div>

                        <span className="security-label">
                            SECURE SESSION
                        </span>

                        <h3>
                            {currentRole} Access
                        </h3>

                        <p>
                            You're securely authenticated
                            through Keycloak. Your available
                            banking operations are controlled
                            by your assigned role.
                        </p>

                    </div>

                </div>

            </div>

        </div>
    );
}

export default Dashboard;