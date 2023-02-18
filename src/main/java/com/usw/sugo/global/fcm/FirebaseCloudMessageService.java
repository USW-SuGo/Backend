package com.usw.sugo.global.fcm;

import static com.usw.sugo.global.exception.ExceptionType.INTERNAL_PUSH_SERVER_EXCEPTION;

import com.google.auth.oauth2.GoogleCredentials;
import com.usw.sugo.global.exception.CustomException;
import java.io.IOException;
import java.util.List;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

@Service
public class FirebaseCloudMessageService {

    private String getAccessToken() {
        String firebaseConfigPath = "sugo-firebase-adminsdk.json";
        GoogleCredentials googleCredentials;
        try {
            googleCredentials =
                GoogleCredentials.fromStream(
                        new ClassPathResource(firebaseConfigPath).getInputStream())
                    .createScoped(List.of("https://www.googleapis/auth/cloud-platform"));
            googleCredentials.refreshIfExpired();
        } catch (IOException e) {
            throw new CustomException(INTERNAL_PUSH_SERVER_EXCEPTION);
        }
        return googleCredentials.getAccessToken().getTokenValue();
    }
}
