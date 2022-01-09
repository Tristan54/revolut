package fr.miage.revolut;

import org.springframework.beans.factory.annotation.Value;

import java.util.Map;

import static io.restassured.RestAssured.given;

public abstract class AccessTokenProvider {

    @Value("${app.auth-server-url}")
    String authServerUrl;
    @Value("${keycloak.resource}")
    String clientId;

    protected String getAccessToken(String username, String password) {
        return given().contentType("application/x-www-form-urlencoded")
                .formParams(Map.of(
                        "username", username,
                        "password", password,
                        "grant_type", "password",
                        "client_id", clientId,
                        "client_secret", "secret"
                ))
                .post(authServerUrl + "/protocol/openid-connect/token")
                .then().assertThat().statusCode(200)
                .extract().path("access_token");
    }
}