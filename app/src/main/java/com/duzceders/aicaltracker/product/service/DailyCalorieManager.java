package com.duzceders.aicaltracker.product.service;

import com.duzceders.aicaltracker.product.models.enums.ActivityLevel;

public class DailyCalorieManager {

    /// Daily Minimum Calorie Needs
    public int calculateBMR(String gender, int height_cm, int weight_kg, int age) {
        double bmr;
        switch (gender) {
            case "Male":
                bmr = (10 * weight_kg) + (6.25 * height_cm) - (5 * age) + 5;
                break;
            case "Female":
                bmr = (10 * weight_kg) + (6.25 * height_cm) - (5 * age) - 161;
                break;
            default:
                bmr = 2000;
                break;
        }
        return (int) Math.round(bmr); // double yerine int döndürüyoruz
    }

    /// Daily Calorie Needs
    public int calculateTDEE(int bmr, ActivityLevel activityLevel) {
        double tdee;
        switch (activityLevel) {
            case NO_ACTIVITY:
                tdee = bmr * 1.2;
                break;
            case LOW_ACTIVITY:
                tdee = bmr * 1.375;
                break;
            case ACTIVE:
                tdee = bmr * 1.55;
                break;
            case VERY_ACTIVE:
                tdee = bmr * 1.725;
                break;
            default:
                tdee = bmr;
                break;
        }
        return (int) Math.round(tdee); // double yerine int döndürüyoruz
    }
}
