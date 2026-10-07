import { useState } from "react";

import CustomerSection from "./components/CustomerSection";
import AccountSection from "./components/AccountSection";
import TransactionSection from "./components/TransactionSection";
import BeneficiarySection from "./components/BeneficiarySection";
import ConsentSection from "./components/ConsentSection";
import Dashboard from "./components/Dashboard";
import keycloak from "./keycloak";

import "./App.css";

function App() {
  const [selectedAccountId, setSelectedAccountId] =
    useState(null);

  const [activeSection, setActiveSection] =
    useState("dashboard");

  // =========================
  // GET USER ROLE
  // =========================

  const roles =
    keycloak.tokenParsed?.realm_access?.roles || [];

  const isAdmin = roles.includes("ADMIN");
  const isMaker = roles.includes("MAKER");
  const isChecker = roles.includes("CHECKER");
  const isUser = roles.includes("USER");

  const currentRole =
    isAdmin
      ? "ADMIN"
      : isMaker
        ? "MAKER"
        : isChecker
          ? "CHECKER"
          : isUser
            ? "USER"
            : "USER";

  const username =
    keycloak.tokenParsed?.preferred_username ||
    "User";

  // =========================
  // LOGOUT
  // =========================

  const handleLogout = () => {
    keycloak.logout({
      redirectUri: window.location.origin,
    });
  };

  // =========================
  // ACCOUNT SELECTION
  // =========================

  const handleSelectAccount = (id) => {
    setSelectedAccountId(id);
  };

  // =========================
  // CONTENT
  // =========================

  const renderContent = () => {
    switch (activeSection) {

      case "customers":
        return isAdmin ? (
          <CustomerSection role={currentRole} />
        ) : (
          <div className="section">
            <h2>Access Denied</h2>
            <p>You need ADMIN access to view customers.</p>
          </div>
        );

      case "accounts":
        return (
          <AccountSection
            role={currentRole}
            selectedAccountId={selectedAccountId}
            onSelectAccount={handleSelectAccount}
          />
        );

      case "transactions":
        return (
          <TransactionSection
            role={currentRole}
            accountId={selectedAccountId}
            onSelectAccount={handleSelectAccount}
          />
        );

      case "beneficiaries":
        return (
          <BeneficiarySection
            role={currentRole}
          />
        );

      case "consents":
        return <ConsentSection role={currentRole} />;

      case "dashboard":
      default:
        return (
          <Dashboard
            username={username}
            currentRole={currentRole}
            onSelectAccount={handleSelectAccount}
            setActiveSection={setActiveSection}
          />
        );
    }
  };

  return (
    <div className="app">

      {/* =========================
          SIDEBAR
      ========================= */}

      <aside className="sidebar">

        <div className="brand">

          <div className="brand-icon">
            B
          </div>

          <div>
            <h1>Banking</h1>
            <span>Digital Banking</span>
          </div>

        </div>

        <nav className="navigation">

          <p className="nav-label">
            MAIN MENU
          </p>

          <button
            className={
              activeSection === "dashboard"
                ? "nav-item active"
                : "nav-item"
            }
            onClick={() =>
              setActiveSection("dashboard")
            }
          >
            <span>⌂</span>
            Dashboard
          </button>

          {/* ADMIN ONLY */}

          {isAdmin && (
            <button
              className={
                activeSection === "customers"
                  ? "nav-item active"
                  : "nav-item"
              }
              onClick={() =>
                setActiveSection("customers")
              }
            >
              <span>👤</span>
              Customers
            </button>
          )}

          <button
            className={
              activeSection === "accounts"
                ? "nav-item active"
                : "nav-item"
            }
            onClick={() =>
              setActiveSection("accounts")
            }
          >
            <span>💳</span>
            Accounts
          </button>

          <button
            className={
              activeSection === "transactions"
                ? "nav-item active"
                : "nav-item"
            }
            onClick={() =>
              setActiveSection("transactions")
            }
          >
            <span>↕</span>
            Transactions
          </button>

          <button
            className={
              activeSection === "beneficiaries"
                ? "nav-item active"
                : "nav-item"
            }
            onClick={() =>
              setActiveSection("beneficiaries")
            }
          >
            <span>👥</span>
            Beneficiaries
          </button>
          <button
            className={
              activeSection === "consents"
                ? "nav-item active"
                : "nav-item"
            }
            onClick={() => setActiveSection("consents")}
          >
            <span>🔐</span> Consents
          </button>
        </nav>

        <div className="sidebar-bottom">

          <div className="security-badge">

            <span>🔒</span>

            <div>
              <strong>
                {currentRole} Session
              </strong>

              <small>
                Authenticated
              </small>
            </div>

          </div>

          <button
            className="logout-button"
            onClick={handleLogout}
          >
            <span>↪</span>
            Logout
          </button>

        </div>

      </aside>


      {/* =========================
          MAIN AREA
      ========================= */}

      <main className="main-content">

        <header className="topbar">

          <div className="user-area">

            <div className="user-avatar">
              {username
                .charAt(0)
                .toUpperCase()}
            </div>

            <div className="user-details">

              <strong>
                {username}
              </strong>

              <span>
                {currentRole}
              </span>

            </div>

          </div>

        </header>

        <div className="content-area">
          {renderContent()}
        </div>

      </main>

    </div>
  );
}

export default App;