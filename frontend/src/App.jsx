import { useState } from "react";

import CustomerSection from "./components/CustomerSection";
import AccountSection from "./components/AccountSection";
import TransactionSection from "./components/TransactionSection";
import BeneficiarySection from "./components/BeneficiarySection";

import "./App.css";

function App() {
  const [selectedAccountId, setSelectedAccountId] =
    useState(null);

  return (
    <div className="app">

      <header>
        <h1>Banking System</h1>

        <p>
          By Saravanavel C - As a part of Banfico training program
        </p>
      </header>

      <CustomerSection />

      <AccountSection
        onSelectAccount={(id) =>
          setSelectedAccountId(id)
        }
      />

      <TransactionSection
        accountId={selectedAccountId}
      />

      <BeneficiarySection />

    </div>
  );
}

export default App;