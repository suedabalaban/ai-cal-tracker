package com.duzceders.aicaltracker.product.models.enums;

public enum UserField {
    NAME("name"),
    SURNAME("surname"),
    BIRTHDATE("birthdate"),
    HEIGHT_CM("height_cm"),
    WEIGHT_KG("weight_kg"),
    TARGET_WEIGHT_KG("target_weight_kg"),
    BODY_FAT_PERCENT("body_fat_percent"),
    ACTIVITY_LEVEL("activity_level"),
    BMR("bmr"),
    DAILY_CALORIE_NEEDS("daily_calorie_needs"),
    DAILY_CALORIE_NEEDS_LEFT("daily_calorie_needs_left"),
    DAILY_WATER_NEEDS_LITERS("daily_water_needs_liters"),
    DAILY_WATER_NEEDS_LEFT_LITERS("daily_water_needs_left_liters"),
    CALORIE_GOALS_MAINTAIN("calorie_goals.maintain"),
    CALORIE_GOALS_WEIGHT_GAIN("calorie_goals.weight_gain"),
    CALORIE_GOALS_WEIGHT_LOSS("calorie_goals.weight_loss"),
    DAILY_MACROS_DAILY_CARBS_NEED_G("daily_macros.daily_carbs_need_g"),
    DAILY_MACROS_DAILY_FATS_NEED_G("daily_macros.daily_fats_need_g"),
    DAILY_MACROS_DAILY_PROTEINS_NEED_G("daily_macros.daily_proteins_need_g"),
    DAILY_MACROS_DAILY_CARBS_LEFT_G("daily_macros.daily_carbs_left_g"),
    DAILY_MACROS_DAILY_FATS_LEFT_G("daily_macros.daily_fats_left_g"),
    DAILY_MACROS_DAILY_PROTEINS_LEFT_G("daily_macros.daily_proteins_left_g");


    private final String fieldName;

    UserField(String fieldName) {
        this.fieldName = fieldName;
    }

    public String getFieldName() {
        return fieldName;
    }
}
