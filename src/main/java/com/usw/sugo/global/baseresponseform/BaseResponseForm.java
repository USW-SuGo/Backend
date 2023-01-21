package com.usw.sugo.global.baseresponseform;

import lombok.Builder;
import lombok.Data;

import java.util.Map;

@Data
@Builder
public class BaseResponseForm {
    Map<Object, Object> code;
    Map<Object, Object> message;
    Map<Object, Object> data;
}
