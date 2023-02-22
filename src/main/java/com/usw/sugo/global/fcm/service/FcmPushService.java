package com.usw.sugo.global.fcm.service;

import static com.usw.sugo.global.exception.ExceptionType.INTERNAL_PUSH_SERVER_EXCEPTION;

import com.google.firebase.FirebaseApp;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.MulticastMessage;
import com.google.firebase.messaging.Notification;
import com.usw.sugo.domain.user.user.User;
import com.usw.sugo.global.exception.CustomException;
import com.usw.sugo.global.fcm.FcmMessage;
import java.util.Collections;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FcmPushService {

    private final FirebaseApp firebaseApp;

    @Async
    public void sendPushNotification(FcmMessage fcmMessage) {
        try {
            extractUserTokenByPushAlarmAllowed(fcmMessage.getUser());
        } catch (NullPointerException exception) {
            return;
        }
        final MulticastMessage multicastMessage = MulticastMessage.builder()
            .setNotification(new Notification(fcmMessage.getTitle(), fcmMessage.getBody()))
            .addAllTokens(
                Collections.singletonList(extractUserTokenByPushAlarmAllowed(fcmMessage.getUser())))
            .build();
        try {
            FirebaseMessaging.getInstance(firebaseApp).sendMulticast(multicastMessage);
        } catch (FirebaseMessagingException firebaseMessagingException) {
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