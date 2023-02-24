package com.usw.sugo.domain.user.user;

import java.math.BigDecimal;
import java.math.RoundingMode;
import org.junit.jupiter.api.Test;

class UserTest {


    @Test
    public void updateMannerGrade() {
        BigDecimal grade = BigDecimal.valueOf(4.5);

        BigDecimal currentGrade = BigDecimal.valueOf(0.0);

        Long countMannerEvaluation = 2L;

        currentGrade = currentGrade.add(grade)
            .divide(
                BigDecimal.valueOf(countMannerEvaluation), RoundingMode.FLOOR
            );

        System.out.println("grade = " + currentGrade);
    }

}