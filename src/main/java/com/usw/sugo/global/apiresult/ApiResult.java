package com.usw.sugo.global.apiresult;

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
