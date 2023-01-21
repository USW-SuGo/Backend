package com.usw.sugo.global.baseresponseform;

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

public class BaseResponseForm {
    private String code;
    private String message;
    private Object data;

    public BaseResponseForm build(String code, String message, Object data) {
        this.code = code;
        this.message = message;
        this.data = data;
        return this;
    }
}
