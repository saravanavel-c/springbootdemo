import { StrictMode } from "react";
import { createRoot } from "react-dom/client";
import "./index.css";
import App from "./App.jsx";
import keycloak from "./keycloak";

keycloak
  .init({
    onLoad: "login-required",
    pkceMethod: "S256",
  })
  .then((authenticated) => {
    if (!authenticated) {
      console.log("User is not authenticated");
      return;
    }

    console.log("Keycloak authentication successful");
    console.log(
      "Logged in user:",
      keycloak.tokenParsed?.preferred_username
    );

    createRoot(document.getElementById("root")).render(
      <StrictMode>
        <App />
      </StrictMode>
    );
  })
  .catch((error) => {
    console.error("Keycloak initialization failed:", error);
  });