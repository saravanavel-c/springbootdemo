const API_URL = import.meta.env.VITE_API_URL;

async function request(endpoint, options = {}) {
  const response = await fetch(`${API_URL}${endpoint}`, {
    headers: {
      "Content-Type": "application/json",
      ...options.headers,
    },
    ...options,
  });

  let data = null;

  try {
    data = await response.json();
  } catch {
    data = null;
  }

  if (!response.ok) {
    const message =
      data?.message ||
      data?.error ||
      `Request failed with status ${response.status}`;

    throw new Error(message);
  }

  return data;
}

export const getCustomers = () =>
  request("/api/customers");

export const createCustomer = (customer) =>
  request("/api/customers", {
    method: "POST",
    body: JSON.stringify(customer),
  });

export const deleteCustomer = (id) =>
  request(`/api/customers/${id}`, {
    method: "DELETE",
  });

export const getAccounts = () =>
  request("/api/accounts");
export const createAccount = (account) =>
  request("/api/accounts", {
    method: "POST",
    body: JSON.stringify(account),
  });

export const updateAccount = (id, account) =>
  request(`/api/accounts/${id}`, {
    method: "PUT",
    body: JSON.stringify(account),
  });

export const getAccountById = (id) =>
  request(`/api/accounts/${id}`);

export const deleteAccount = (id) =>
  request(`/api/accounts/${id}`, {
    method: "DELETE",
  });

export const getTransactions = (accountId) =>
  request(`/api/accounts/${accountId}/transactions`);

export const createTransaction = (accountId, transaction) =>
  request(`/api/accounts/${accountId}/transactions`, {
    method: "POST",
    body: JSON.stringify(transaction),
  });

export const getBeneficiaries = () =>
  request("/api/beneficiaries");

export const createBeneficiary = (beneficiary) =>
  request("/api/beneficiaries", {
    method: "POST",
    body: JSON.stringify(beneficiary),
  });

export const deleteBeneficiary = (id) =>
  request(`/api/beneficiaries/${id}`, {
    method: "DELETE",
  });