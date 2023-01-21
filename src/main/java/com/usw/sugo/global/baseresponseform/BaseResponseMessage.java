package com.usw.sugo.global.baseresponseform;

import lombok.AllArgsConstructor;
import lombok.Getter;

/*
{
  "code": "200",
  "message": "SUCCESS",
  "data": {
    "resultCode": "SUCCESS",
    "resultMessage": "115"
  }
}
 */
@Getter
@AllArgsConstructor
public enum BaseResponseMessage {

    SUCCESS("SUCCESS"),

    ;

    private final String message;
}
