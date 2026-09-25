import { useEffect, useState } from "react";
import { createCustomer, getCustomers, deleteCustomer } from "../api";

function CustomerSection() {
  const [customers, setCustomers] = useState([]);

  const [name, setName] = useState("");
  const [email, setEmail] = useState("");
  const [phone, setPhone] = useState("");

  const [error, setError] = useState("");
  const [message, setMessage] = useState("");

  const loadCustomers = async () => {
    try {
      setError("");

      const data = await getCustomers();

      setCustomers(data);
    } catch (error) {
      setError(error.message);
    }
  };

  useEffect(() => {
    loadCustomers();
  }, []);

  const handleSubmit = async (event) => {
    event.preventDefault();

    setError("");
    setMessage("");

    if (!name.trim() || !email.trim() || !phone.trim()) {
      setError("All fields are required.");
      return;
    }

    try {
      await createCustomer({
        name,
        email,
        phone,
      });

      setMessage("Customer created successfully.");

      setName("");
      setEmail("");
      setPhone("");

      loadCustomers();
    } catch (error) {
      setError(error.message);
    }
  };
  const handleDelete = async (id) => {

  const confirmed = window.confirm(
    "Are you sure you want to delete this customer?"
  );

  if (!confirmed) {
    return;
  }

  try {

    setError("");
    setMessage("");

    await deleteCustomer(id);

    setMessage(
      "Customer deleted successfully."
    );

    await loadCustomers();

  } catch (error) {

    setError(error.message);

  }
};

  return (
    <section className="section">
      <h2>Customers</h2>

      <form onSubmit={handleSubmit} className="form">
        <input
          type="text"
          placeholder="Name"
          value={name}
          onChange={(e) => setName(e.target.value)}
        />

        <input
          type="email"
          placeholder="Email"
          value={email}
          onChange={(e) => setEmail(e.target.value)}
        />

        <input
          type="text"
          placeholder="Phone"
          value={phone}
          onChange={(e) => setPhone(e.target.value)}
        />

        <button type="submit">
          Create Customer
        </button>
      </form>

      {message && (
        <p className="success">{message}</p>
      )}

      {error && (
        <p className="error">{error}</p>
      )}

      <div className="list">
        {customers.length === 0 ? (
          <p>No customers found.</p>
        ) : (
          customers.map((customer) => (
            <div className="card" key={customer.id}>
              <h3>{customer.name}</h3>

              <p>
                <strong>ID:</strong> {customer.id}
              </p>

              <p>
                <strong>Email:</strong> {customer.email}
              </p>

              <p>
                <strong>Phone:</strong> {customer.phone}
              </p>
              <button
                className="delete-button"
                onClick={() => handleDelete(customer.id)}
                >
                Delete
              </button>
            </div>
          ))
        )}
      </div>
    </section>
  );
}

export default CustomerSection;