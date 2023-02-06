package com.usw.sugo.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum ApiResult {

    SUCCESS("Success"),
    EXIST("Exist"),
    LIKE("Like");

    private final String result;
}
