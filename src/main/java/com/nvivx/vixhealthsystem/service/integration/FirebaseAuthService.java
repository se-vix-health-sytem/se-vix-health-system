package com.nvivx.vixhealthsystem.service.integration;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.UserRecord;
import org.springframework.stereotype.Service;

@Service
public class FirebaseAuthService {

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
}