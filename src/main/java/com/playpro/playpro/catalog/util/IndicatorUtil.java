package com.playpro.playpro.catalog.util;

public final class IndicatorUtil {

    public static final String YES = "Y";
    public static final String NO = "N";

    private IndicatorUtil() {
    }

    public static boolean isYes(String indicator) {
        return YES.equalsIgnoreCase(indicator);
    }

    public static String toIndicator(Boolean value) {
        return Boolean.TRUE.equals(value) ? YES : NO;
    }

    public static Boolean fromIndicator(String indicator) {
        if (indicator == null) {
            return null;
        }
        return isYes(indicator);
    }
}
