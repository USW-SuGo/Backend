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
public enum BaseResponseCode {
    SUCCESS("200"),
    CREATED("201"),
    BAD_REQUEST("400")

    ;

    private final String code;
}
