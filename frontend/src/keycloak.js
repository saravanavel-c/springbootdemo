import Keycloak from "keycloak-js";

const keycloak = new Keycloak({
    url: "http://localhost:8081",
    realm: "banking-realm",
    clientId: "banking-api",
});

export default keycloak;