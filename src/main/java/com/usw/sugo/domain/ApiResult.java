package com.usw.sugo.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum ApiResult {

    SUCCESS("Success"),
    EXIST("Exist");

    private final String result;
}
