package com.nvivx.vixhealthsystem.service.integration;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.UserRecord;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

/**
 * Thin wrapper around the Firebase Admin SDK and the Identity Toolkit REST API.
 *
 * Handles the three account-management operations the hospital system needs:
 * creating staff accounts on hire, generating password-reset links, deleting
 * accounts on termination, and verifying credentials on login.
 *
 * The Admin SDK is used for privileged server-side operations (create/delete/reset).
 * Sign-in uses the public REST endpoint because the Admin SDK does not expose
 * password verification — it is intended for server-side management, not auth.
 *
 * @see com.nvivx.vixhealthsystem.service.core.EmployeeService
 * @see com.nvivx.vixhealthsystem.config.FirebaseConfig
 */
@Service
public class FirebaseAuthService {

    // =========================================================
    // FIELDS
    // =========================================================

    /** REST key for the Identity Toolkit sign-in endpoint (not the Admin SDK). */
    @Value("${firebase.web-api-key}")
    private String firebaseWebApiKey;

    // =========================================================
    // WRITE OPERATIONS
    // =========================================================

    /**
     * Creates a new Firebase Authentication account and returns its UID.
     *
     * If the email already exists (e.g., from a previous failed transaction) the
     * existing account's UID is returned instead of throwing, so the caller can
     * write the UID back to the database without restarting the whole flow.
     *
     * @param email              Email address for the new account; must be unique.
     * @param temporaryPassword  Initial password that the employee should change on first login.
     * @return                   Firebase UID of the created (or pre-existing) account.
     * @throws Exception When Firebase is unreachable or returns an unexpected error.
     */
    public String createUser(String email, String temporaryPassword) throws Exception {
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

    /**
     * Generates a Firebase password-reset link for the given email address.
     *
     * The link is valid for a fixed Firebase-determined window (typically 1 hour).
     * In production this would be emailed to the user; in the demo it is logged to console.
     *
     * @param email  The account's email address.
     * @return       One-time password-reset URL.
     * @throws Exception When Firebase is unreachable or the email is not registered.
     */
    public String generatePasswordResetLink(String email) throws Exception {
        return FirebaseAuth.getInstance().generatePasswordResetLink(email);
    }

    /**
     * Permanently deletes a Firebase Authentication account.
     *
     * Called during employee termination ({@link com.nvivx.vixhealthsystem.service.core.EmployeeService#deleteEmployee}).
     * If the UID is unknown or already deleted, Firebase throws and the caller handles it.
     *
     * @param firebaseUid  The UID of the account to delete.
     * @throws Exception When Firebase is unreachable or the UID does not exist.
     */
    public void deleteUser(String firebaseUid) throws Exception {
        FirebaseAuth.getInstance().deleteUser(firebaseUid);
    }

    // =========================================================
    // READ OPERATIONS
    // =========================================================

    /**
     * Verifies an email/password pair against Firebase and returns the user's Firebase UID.
     *
     * Uses the Identity Toolkit REST endpoint rather than the Admin SDK because the Admin
     * SDK does not provide password verification — that is intentional by design.
     * The returned UID is used to look up the matching {@link com.nvivx.vixhealthsystem.model.person.employee.Employee}
     * row in the database.
     *
     * @param email     The account's email address.
     * @param password  The password to verify.
     * @return          Firebase UID ({@code localId}) of the authenticated user.
     * @throws Exception When credentials are wrong or Firebase is unreachable.
     */
    public String signInWithEmailAndPassword(String email, String password) throws Exception {
        String url = "https://identitytoolkit.googleapis.com/v1/accounts:signInWithPassword?key="
                + firebaseWebApiKey;

        RestTemplate restTemplate = new RestTemplate();

        Map<String, Object> request = new HashMap<>();
        request.put("email", email);
        request.put("password", password);
        request.put("returnSecureToken", true);

        ResponseEntity<Map> response = restTemplate.postForEntity(url, request, Map.class);

        return (String) response.getBody().get("localId");
    }
}