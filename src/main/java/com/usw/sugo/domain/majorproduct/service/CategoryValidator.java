package com.usw.sugo.domain.majorproduct.service;

import com.usw.sugo.global.exception.CustomException;
import com.usw.sugo.global.exception.ErrorCode;
import org.springframework.stereotype.Component;

@Component
public class CategoryValidator {

    public static boolean validateCategory(String category) {
        if (category.equals("서적") || category.equals("생활용품") || category.equals("전자제품") || category.equals("기타")) {
            return true;
        }

        throw new CustomException(ErrorCode.CATEGORY_NOT_FOUND);
    }
}
