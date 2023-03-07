package com.usw.sugo.global.infrastructure.fcm.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import java.io.IOException;
import java.io.InputStream;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

@Configuration
public class FcmConfig {

    @Value("${fcm.key}")
    private String secretKey;

    @Value("${fcm.project-id}")
    private String projectId;

    @Bean
    public FirebaseApp firebaseApp() {
        final ClassPathResource resource = new ClassPathResource(secretKey);
        try (InputStream stream = resource.getInputStream()) {
            final FirebaseOptions firebaseOptions = FirebaseOptions.builder()
                .setCredentials(GoogleCredentials.fromStream(stream))
                .setProjectId(projectId)
                .build();
            return FirebaseApp.initializeApp(firebaseOptions);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}