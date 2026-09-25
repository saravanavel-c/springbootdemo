import { useEffect, useState } from "react";

import {
  createBeneficiary,
  deleteBeneficiary,
  getBeneficiaries,
} from "../api";

function BeneficiarySection() {
  const [beneficiaries, setBeneficiaries] =
    useState([]);

  const [name, setName] = useState("");
  const [accountNumber, setAccountNumber] =
    useState("");
  const [bankName, setBankName] =
    useState("");

  const [error, setError] = useState("");
  const [message, setMessage] = useState("");

  const loadBeneficiaries = async () => {
    try {
      setError("");

      const data = await getBeneficiaries();

      setBeneficiaries(data);
    } catch (error) {
      setError(error.message);
    }
  };

  useEffect(() => {
    loadBeneficiaries();
  }, []);

  const handleSubmit = async (event) => {
    event.preventDefault();

    setError("");
    setMessage("");

    if (
      !name.trim() ||
      !accountNumber.trim() ||
      !bankName.trim()
    ) {
      setError("All fields are required.");
      return;
    }

    try {
      await createBeneficiary({
        name,
        accountNumber,
        bankName,
      });

      setMessage(
        "Beneficiary added successfully."
      );

      setName("");
      setAccountNumber("");
      setBankName("");

      loadBeneficiaries();
    } catch (error) {
      setError(error.message);
    }
  };

  const handleDelete = async (id) => {
    const confirmed = window.confirm(
      "Are you sure you want to delete this beneficiary?"
    );

    if (!confirmed) {
      return;
    }

    try {
      setError("");

      await deleteBeneficiary(id);

      setMessage(
        "Beneficiary deleted successfully."
      );

      loadBeneficiaries();
    } catch (error) {
      setError(error.message);
    }
  };

  return (
    <section className="section">
      <h2>Beneficiaries</h2>

      <form
        onSubmit={handleSubmit}
        className="form"
      >
        <input
          type="text"
          placeholder="Name"
          value={name}
          onChange={(e) =>
            setName(e.target.value)
          }
        />

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
          placeholder="Bank Name"
          value={bankName}
          onChange={(e) =>
            setBankName(e.target.value)
          }
        />

        <button type="submit">
          Add Beneficiary
        </button>
      </form>

      {message && (
        <p className="success">{message}</p>
      )}

      {error && (
        <p className="error">{error}</p>
      )}

      <div className="list">
        {beneficiaries.length === 0 ? (
          <p>No beneficiaries found.</p>
        ) : (
          beneficiaries.map(
            (beneficiary) => (
              <div
                className="card"
                key={beneficiary.id}
              >
                <h3>
                  {beneficiary.name}
                </h3>

                <p>
                  <strong>Account:</strong>{" "}
                  {beneficiary.accountNumber}
                </p>

                <p>
                  <strong>Bank:</strong>{" "}
                  {beneficiary.bankName}
                </p>

                <button
                  className="delete-button"
                  onClick={() =>
                    handleDelete(
                      beneficiary.id
                    )
                  }
                >
                  Delete
                </button>
              </div>
            )
          )
        )}
      </div>
    </section>
  );
}

export default BeneficiarySection;