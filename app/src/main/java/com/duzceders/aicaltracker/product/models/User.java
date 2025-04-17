package com.duzceders.aicaltracker.product.models;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class User {
    private String name;
    private String surname;
    private String email;

    private int height_cm;
    private int weight_kg;
    private int target_weight_kg;
    private double body_fat_percent;

    private String activity_level; // ""sedentary" | "light" | "moderate" | "active" | "very_active""

    private double bmr;
    private double daily_calorie_needs;
    private double daily_water_needs_liters;

    private CalorieGoals calorie_goals;
    private DailyMacros daily_macros;

    public User() {}


    /// CalorieGoals: Kilo koruma- kilo alma- kilo verme için gerekli kalori değerlerini tutar.
    @Getter
    @Setter
    public static class CalorieGoals {

        private double maintain;
        private double weight_gain;
        private double weight_loss;

        public CalorieGoals() {}
    }

    /// DailyMacros: Günlük makro besin değerlerini tutar.
    @Getter
    @Setter
    public static class DailyMacros {
        private double carbs_g;
        private double fat_g;
        private double protein_g;

        public DailyMacros() {}
    }
}
