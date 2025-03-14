package com.mdavydau.spribe.utils;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class UnitUtils {
    public static Integer addCostSystemMarkup(Integer cost) {
        return new BigDecimal(cost * 1.15)
                .setScale(0, RoundingMode.HALF_UP)
                .intValue();
    }
}
