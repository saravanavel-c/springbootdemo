import { useEffect, useState } from "react";

import {
    getConsents,
    createConsent,
    approveConsent,
    rejectConsent,
    getCustomers,
} from "../api";

function ConsentSection({ role }) {
    const [consents, setConsents] = useState([]);
    const [customers, setCustomers] = useState([]);

    const [customerId, setCustomerId] = useState("");
    const [purpose, setPurpose] = useState("");

    const [loading, setLoading] = useState(true);
    const [submitting, setSubmitting] = useState(false);
    const [error, setError] = useState("");
    const [success, setSuccess] = useState("");

    const canCreate = role === "USER" || role === "ADMIN";
    const canApprove = role === "CHECKER" || role === "ADMIN";

    // ==========================================
    // LOAD DATA
    // ==========================================

    const loadConsents = async () => {
        try {
            setLoading(true);
            setError("");

            const data = await getConsents();
            setConsents(data || []);
        } catch (err) {
            console.error(err);
            setError(err.message || "Failed to load consents.");
        } finally {
            setLoading(false);
        }
    };

    const loadCustomers = async () => {
        try {
            const data = await getCustomers();
            setCustomers(data || []);
        } catch (err) {
            console.error(err);
        }
    };

    useEffect(() => {
        loadConsents();

        if (canCreate) {
            loadCustomers();
        }
    }, [canCreate]);

    // ==========================================
    // CREATE CONSENT
    // ==========================================

    const handleCreateConsent = async (e) => {
        e.preventDefault();

        if (!customerId || !purpose.trim()) {
            setError("Please select a customer and enter the purpose.");
            return;
        }

        try {
            setSubmitting(true);
            setError("");
            setSuccess("");

            await createConsent({
                customerId: Number(customerId),
                purpose: purpose.trim(),
            });

            setCustomerId("");
            setPurpose("");

            setSuccess("Consent request created successfully.");

            await loadConsents();
        } catch (err) {
            console.error(err);
            setError(err.message || "Failed to create consent.");
        } finally {
            setSubmitting(false);
        }
    };

    // ==========================================
    // APPROVE CONSENT
    // ==========================================

    const handleApprove = async (id) => {
        try {
            setError("");
            setSuccess("");

            await approveConsent(id);

            setSuccess("Consent approved successfully.");

            await loadConsents();
        } catch (err) {
            console.error(err);
            setError(err.message || "Failed to approve consent.");
        }
    };

    // ==========================================
    // REJECT CONSENT
    // ==========================================

    const handleReject = async (id) => {
        try {
            setError("");
            setSuccess("");

            await rejectConsent(id);

            setSuccess("Consent rejected successfully.");

            await loadConsents();
        } catch (err) {
            console.error(err);
            setError(err.message || "Failed to reject consent.");
        }
    };

    // ==========================================
    // STATUS CLASS
    // ==========================================

    const getStatusClass = (status) => {
        switch (status) {
            case "APPROVED":
                return "status-approved";

            case "REJECTED":
                return "status-rejected";

            case "PENDING":
                return "status-pending";

            case "EXPIRED":
                return "status-expired";

            default:
                return "";
        }
    };

    // ==========================================
    // FORMAT DATE
    // ==========================================

    const formatDate = (date) => {
        if (!date) return "-";

        return new Date(date).toLocaleString();
    };

    return (
        <div className="section">

            {/* ==========================================
          HEADER
          ========================================== */}

            <div className="section-header">
                <div>
                    <h2>Consent Management</h2>
                    <p>
                        Create, review and manage banking consent requests.
                    </p>
                </div>
            </div>


            {/* ==========================================
          SUCCESS / ERROR MESSAGES
          ========================================== */}

            {success && (
                <div className="success-message">
                    {success}
                </div>
            )}

            {error && (
                <div className="error-message">
                    {error}
                </div>
            )}


            {/* ==========================================
          CREATE CONSENT
          ========================================== */}

            {canCreate && (
                <div className="card consent-create-card">

                    <div className="card-header">
                        <div>
                            <h3>Create Consent Request</h3>
                            <p>
                                Request permission to access customer account information.
                            </p>
                        </div>
                    </div>

                    <form
                        className="form-grid"
                        onSubmit={handleCreateConsent}
                    >

                        <div className="form-group">

                            <label>
                                Customer
                            </label>

                            <select
                                value={customerId}
                                onChange={(e) => setCustomerId(e.target.value)}
                            >

                                <option value="">
                                    Select customer
                                </option>

                                {customers.map((customer) => (
                                    <option
                                        key={customer.id}
                                        value={customer.id}
                                    >
                                        {customer.name} - ID {customer.id}
                                    </option>
                                ))}

                            </select>

                        </div>


                        <div className="form-group">

                            <label>
                                Purpose
                            </label>

                            <input
                                type="text"
                                placeholder="Example: Account Information Access"
                                value={purpose}
                                onChange={(e) => setPurpose(e.target.value)}
                            />

                        </div>


                        <div className="form-actions">

                            <button
                                type="submit"
                                className="primary-button"
                                disabled={submitting}
                            >
                                {submitting
                                    ? "Creating..."
                                    : "Create Consent"}
                            </button>

                        </div>

                    </form>

                </div>
            )}


            {/* ==========================================
          CONSENT LIST
          ========================================== */}

            <div className="card">

                <div className="card-header">

                    <div>
                        <h3>Consent Requests</h3>
                        <p>
                            View the current status of consent requests.
                        </p>
                    </div>

                    <button
                        className="secondary-button"
                        onClick={loadConsents}
                    >
                        Refresh
                    </button>

                </div>


                {loading ? (
                    <div className="loading">
                        Loading consents...
                    </div>
                ) : consents.length === 0 ? (

                    <div className="empty-state">
                        <h3>No Consent Requests</h3>
                        <p>
                            There are currently no consent requests.
                        </p>
                    </div>

                ) : (

                    <div className="consent-list">

                        {consents.map((consent) => (

                            <div
                                className="consent-item"
                                key={consent.id}
                            >

                                <div className="consent-main">

                                    <div className="consent-reference">
                                        <strong>
                                            {consent.consentReference}
                                        </strong>

                                        <span
                                            className={`status-badge ${getStatusClass(
                                                consent.status
                                            )}`}
                                        >
                                            {consent.status}
                                        </span>
                                    </div>


                                    <div className="consent-details">

                                        <div>
                                            <span className="detail-label">
                                                Customer
                                            </span>

                                            <strong>
                                                {consent.customerName}
                                            </strong>
                                        </div>


                                        <div>
                                            <span className="detail-label">
                                                Purpose
                                            </span>

                                            <strong>
                                                {consent.purpose}
                                            </strong>
                                        </div>


                                        <div>
                                            <span className="detail-label">
                                                Requested
                                            </span>

                                            <span>
                                                {formatDate(consent.requestedAt)}
                                            </span>
                                        </div>


                                        <div>
                                            <span className="detail-label">
                                                Expires
                                            </span>

                                            <span>
                                                {formatDate(consent.expiresAt)}
                                            </span>
                                        </div>

                                    </div>


                                    {consent.approvedAt && (
                                        <div className="consent-approved">

                                            Approved at:{" "}
                                            {formatDate(consent.approvedAt)}

                                        </div>
                                    )}

                                </div>


                                {/* ==========================================
                    CHECKER / ADMIN ACTIONS
                    ========================================== */}

                                {canApprove &&
                                    consent.status === "PENDING" && (

                                        <div className="consent-actions">

                                            <button
                                                className="success-button"
                                                onClick={() =>
                                                    handleApprove(consent.id)
                                                }
                                            >
                                                Approve
                                            </button>

                                            <button
                                                className="danger-button"
                                                onClick={() =>
                                                    handleReject(consent.id)
                                                }
                                            >
                                                Reject
                                            </button>

                                        </div>

                                    )}

                            </div>

                        ))}

                    </div>

                )}

            </div>

        </div>
    );
}

export default ConsentSection;