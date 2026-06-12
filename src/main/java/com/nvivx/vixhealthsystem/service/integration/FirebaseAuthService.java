package com.nvivx.vixhealthsystem.service.integration;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.UserRecord;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Service
public class FirebaseAuthService {
    @Value("${firebase.web-api-key}")
    private String firebaseWebApiKey;

    public String createUser(
            String email,
            String temporaryPassword
    ) throws Exception {

        try {
            UserRecord.CreateRequest request =
                    new UserRecord.CreateRequest()
                            .setEmail(email)
                            .setPassword(temporaryPassword)
                            .setEmailVerified(false)
                            .setDisabled(false);

            UserRecord userRecord = FirebaseAuth.getInstance().createUser(request);
            return userRecord.getUid();

        } catch (com.google.firebase.auth.FirebaseAuthException e) {
            if ("EMAIL_EXISTS".equals(e.getAuthErrorCode() != null
                    ? e.getAuthErrorCode().name() : "")) {
                // Account already exists from a previous attempt — reuse the existing UID
                return FirebaseAuth.getInstance().getUserByEmail(email).getUid();
            }
            // Check error message as fallback (some SDK versions use message instead of code)
            if (e.getMessage() != null && e.getMessage().contains("EMAIL_EXISTS")) {
                return FirebaseAuth.getInstance().getUserByEmail(email).getUid();
            }
            throw e;
        }
    }

    public String generatePasswordResetLink(String email)
            throws Exception {

        return FirebaseAuth
                .getInstance()
                .generatePasswordResetLink(email);
    }

    public void deleteUser(String firebaseUid) throws Exception {

        FirebaseAuth.getInstance().deleteUser(firebaseUid);
    }

    public String signInWithEmailAndPassword(String email, String password) throws Exception {
        String url = "https://identitytoolkit.googleapis.com/v1/accounts:signInWithPassword?key="
                + firebaseWebApiKey;

        RestTemplate restTemplate = new RestTemplate();

        Map<String, Object> request = new HashMap<>();
        request.put("email", email);
        request.put("password", password);
        request.put("returnSecureToken", true);

        ResponseEntity<Map> response =
                restTemplate.postForEntity(url, request, Map.class);

        return (String) response.getBody().get("localId");
    }
}