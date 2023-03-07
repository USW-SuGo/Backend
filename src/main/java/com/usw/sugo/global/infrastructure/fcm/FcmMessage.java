package com.usw.sugo.global.infrastructure.fcm;

import com.usw.sugo.domain.user.user.User;
import lombok.Getter;

@Getter
public class FcmMessage {

    private final User user;
    private final String title;
    private final String body;

    public FcmMessage(User user, String title, String body) {
        this.user = user;
        this.title = title;
        this.body = body;
    }
}
