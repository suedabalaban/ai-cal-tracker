package com.duzceders.aicaltracker.product.models.enums;

import com.duzceders.aicaltracker.R;

public enum MealType {
    BREAKFAST(R.string.breakfast), LAUNCH(R.string.launch), DINNER(R.string.dinner);
    public int mealTypeId;

    MealType(int mealTypeId) {
        this.mealTypeId = mealTypeId;
    }

}
