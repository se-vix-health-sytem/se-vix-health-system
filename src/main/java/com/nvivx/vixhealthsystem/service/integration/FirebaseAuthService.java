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

        UserRecord.CreateRequest request =
                new UserRecord.CreateRequest()
                        .setEmail(email)
                        .setPassword(temporaryPassword)
                        .setEmailVerified(false)
                        .setDisabled(false);

        UserRecord userRecord =
                FirebaseAuth.getInstance().createUser(request);

        return userRecord.getUid();
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