package com.usw.sugo.domain.productpost.service;

import com.usw.sugo.global.exception.CustomException;
import com.usw.sugo.global.exception.ExceptionType;
import org.springframework.stereotype.Component;

@Component
public class CategoryValidator {

    public static boolean validateCategory(String category) {
        if (category.equals("서적") || category.equals("생활용품") || category.equals("전자기기") || category.equals("기타")) {
            return true;
        }

        throw new CustomException(ExceptionType.CATEGORY_NOT_FOUND);
    }
}
