package com.usw.sugo.global.fcm;

import static com.usw.sugo.global.exception.ExceptionType.INTERNAL_PUSH_SERVER_EXCEPTION;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.MulticastMessage;
import com.google.firebase.messaging.Notification;
import com.usw.sugo.domain.user.user.User;
import com.usw.sugo.global.exception.CustomException;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FcmPushService {

    private final FirebaseApp firebaseApp;

    @Value("${fcm.key}")
    private String secretKey;

    @Value("${fcm.project-id}")
    private String projectId;

    private final String CONFIG_PATH = secretKey; // 토큰 발급 URL
    private final String AUTH_URL = "https://www.googleapis.com/auth/cloud-platform"; // 엔드포인트 URL
    private final String SEND_URL =
        "https://fcm.googleapis.com/v1/projects/" + projectId + "/messages:send";

    private String getAccessToken() throws IOException { // 토큰 발급
        GoogleCredentials googleCredentials = GoogleCredentials.fromStream(
            new ClassPathResource(CONFIG_PATH).getInputStream()).createScoped(List.of(AUTH_URL));
        googleCredentials.refreshIfExpired();
        return googleCredentials.getAccessToken().getTokenValue();
    }

    @Async
    public void sendPushNotification(FcmMessage fcmMessage) {
        MulticastMessage multicastMessage = MulticastMessage.builder()
            .setNotification(new Notification(fcmMessage.getTitle(), fcmMessage.getBody()))
            .addAllTokens(
                Collections.singletonList(extractUserTokenByPushAlarmAllowed(fcmMessage.getUser())))
            .build();
        try {
            FirebaseMessaging.getInstance(firebaseApp).sendMulticast(multicastMessage);
        } catch (FirebaseMessagingException e) {
            throw new CustomException(INTERNAL_PUSH_SERVER_EXCEPTION);
        }
    }

    private String extractUserTokenByPushAlarmAllowed(User user) {
        if (user.getPushAlarmStatus()) {
            return user.getFcmToken();
        }
        return null;
    }
}
