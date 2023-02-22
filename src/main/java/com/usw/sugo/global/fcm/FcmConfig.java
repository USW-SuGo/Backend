package com.usw.sugo.global.fcm;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import javax.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FcmConfig {

    @Value("${fcm.key}")
    private String secretKey;

    @Value("${fcm.project-id}")
    private String projectId;

    @PostConstruct
    public FirebaseApp firebaseApp() throws IOException {
        final FirebaseOptions firebaseOptions = FirebaseOptions.builder()
            .setCredentials(
                GoogleCredentials.fromStream(new ByteArrayInputStream(secretKey.getBytes())))
            .setProjectId(projectId)
            .build();

        return FirebaseApp.initializeApp(firebaseOptions);
    }
}