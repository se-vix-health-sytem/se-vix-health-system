package com.nvivx.vixhealthsystem.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;

import java.io.InputStream;

/**
 * @brief Sets up the Firebase Admin SDK from a service-account JSON file.
 *
 * Reads {@code firebase.service-account} from {@code application.properties}.
 * If {@code firebase.enabled=false}, skips initialisation entirely : useful for
 * running tests without a live Firebase project.
 *
 * @see SecurityConfig
 * @see com.nvivx.vixhealthsystem.service.integration.FirebaseAuthService
 */
@Configuration
public class FirebaseConfig {

    // =========================================================
    // INJECTED PROPERTIES
    // =========================================================

    /** Classpath or filesystem resource pointing to the Firebase service-account JSON. */
    @Value("${firebase.service-account}")
    private Resource serviceAccount;

    /**
     * When {@code false} the entire Firebase initialisation is skipped.
     * Defaults to {@code true}; set to {@code false} in test profiles.
     */
    @Value("${firebase.enabled:true}")
    private boolean enabled;

    // =========================================================
    // LIFECYCLE
    // =========================================================

    /**
     * Initialises the Firebase Admin SDK using the service-account JSON.
     *
     * Is a no-op when:
     * - {@code firebase.enabled=false}, or
     * - Firebase is already initialised (avoids duplicate-app errors on hot-reload).
     *
     * @throws Exception When the credential file cannot be read or parsed.
     */
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