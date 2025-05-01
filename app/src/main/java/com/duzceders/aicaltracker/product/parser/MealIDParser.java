package com.duzceders.aicaltracker.product.parser;

public class MealIDParser {
    public static String extractMealIdWithoutRegex(String url) {

        int startIndex = url.indexOf("/meals/") + "/meals/".length();

        int endIndex = url.lastIndexOf(".");

        if (startIndex > 0 && endIndex > startIndex) {
            return url.substring(startIndex, endIndex);
        }
        return "";
    }
}
