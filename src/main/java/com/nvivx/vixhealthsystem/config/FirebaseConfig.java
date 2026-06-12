package com.nvivx.vixhealthsystem.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;

import java.io.InputStream;

@Configuration
public class FirebaseConfig {

    @Value("${firebase.service-account}")
    private Resource serviceAccount;

    @Value("${firebase.enabled:true}")
    private boolean enabled;

    @PostConstruct
    public void initializeFirebase() throws Exception {

        if (!enabled) {
            return;
        }

        if (!FirebaseApp.getApps().isEmpty()) {
            return;
        }

        try (InputStream inputStream = serviceAccount.getInputStream()) {
            FirebaseOptions options = FirebaseOptions.builder()
                    .setCredentials(GoogleCredentials.fromStream(inputStream))
                    .build();

            FirebaseApp.initializeApp(options);
        }
    }
}