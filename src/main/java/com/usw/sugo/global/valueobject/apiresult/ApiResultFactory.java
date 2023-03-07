package com.usw.sugo.global.valueobject.apiresult;

import static com.usw.sugo.global.valueobject.apiresult.ApiResult.EXIST;
import static com.usw.sugo.global.valueobject.apiresult.ApiResult.LIKE;
import static com.usw.sugo.global.valueobject.apiresult.ApiResult.SUCCESS;

import java.util.HashMap;
import java.util.Map;
import org.springframework.stereotype.Component;

@Component
public class ApiResultFactory {

    private static final Map<String, Boolean> SUCCESS_FLAG = new HashMap<>() {{
        put(SUCCESS.getResult(), true);
    }};

    private static final Map<String, Boolean> EXIST_FLAG = new HashMap<>() {{
        put(EXIST.getResult(), true);
    }};

    private static final Map<String, Boolean> NOT_EXIST_FLAG = new HashMap<>() {{
        put(EXIST.getResult(), false);
    }};

    private static final Map<String, Boolean> LIKE_FLAG = new HashMap<>() {{
        put(LIKE.getResult(), true);
    }};

    private static final Map<String, Boolean> DIS_LIKE_FLAG = new HashMap<>() {{
        put(LIKE.getResult(), false);
    }};


    public static Map<String, Boolean> getSuccessFlag() {
        return SUCCESS_FLAG;
    }

    public static Map<String, Boolean> getExistFlag() {
        return EXIST_FLAG;
    }

    public static Map<String, Boolean> getNotExistFlag() {
        return NOT_EXIST_FLAG;
    }

    public static Map<String, Boolean> getLikeFlag() {
        return LIKE_FLAG;
    }

    public static Map<String, Boolean> getDisLikeFlag() {
        return DIS_LIKE_FLAG;
    }
}
