package com.smartconsultor.keycloak;

import org.keycloak.authentication.AuthenticationFlowContext;
import org.keycloak.authentication.Authenticator;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.models.UserModel;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

public class TurnstileAuthenticator implements Authenticator {
    private static final String TURNSTILE_SECRET_KEY = System.getenv("TURNSTILE_SECRET_KEY");
    private static final String TURNSTILE_VERIFY_URL = "https://challenges.cloudflare.com/turnstile/v0/siteverify";

    @Override
    public void authenticate(AuthenticationFlowContext context) {
        String turnstileToken = context.getHttpRequest().getDecodedFormParameters().getFirst("cf-turnstile-response");
        if (turnstileToken == null || turnstileToken.isEmpty()) {
            context.form().setError("Turnstile token is missing");
            context.attempted();
            return;
        }

        String clientIp = context.getConnection().getRemoteAddr();

        try (CloseableHttpClient client = HttpClients.createDefault()) {
            HttpPost post = new HttpPost(TURNSTILE_VERIFY_URL);
            post.setHeader("Content-Type", "application/x-www-form-urlencoded");
            String body = "secret=" + TURNSTILE_SECRET_KEY + "&response=" + turnstileToken + "&remoteip=" + clientIp;
            post.setEntity(new StringEntity(body));

            String response = EntityUtils.toString(client.execute(post).getEntity());
            JSONObject json = new JSONObject(response);

            if (json.getBoolean("success")) {
                context.success(); // Tiếp tục luồng xác thực Keycloak
            } else {
                context.form().setError("Invalid Turnstile token: " + json.getJSONArray("error-codes").toString());
                context.attempted();
            }
        } catch (Exception e) {
            context.form().setError("Error verifying Turnstile token: " + e.getMessage());
            context.attempted();
        }
    }

    @Override
    public void action(AuthenticationFlowContext context) {
        authenticate(context);
    }

    @Override
    public boolean requiresUser() {
        return false;
    }

    @Override
    public boolean configuredFor(KeycloakSession session, RealmModel realm, UserModel user) {
        return true;
    }

    @Override
    public void setRequiredActions(KeycloakSession session, RealmModel realm, UserModel user) {
    }

    @Override
    public void close() {
    }
}