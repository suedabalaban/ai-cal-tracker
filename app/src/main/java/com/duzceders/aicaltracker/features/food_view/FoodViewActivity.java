package com.duzceders.aicaltracker.features.food_view;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.duzceders.aicaltracker.R;
import com.duzceders.aicaltracker.databinding.ActivityFoodViewBinding;
import com.duzceders.aicaltracker.product.models.FoodInfo;
import com.duzceders.aicaltracker.product.models.Meal;
import com.duzceders.aicaltracker.product.models.User;
import com.duzceders.aicaltracker.product.models.enums.MealType;
import com.duzceders.aicaltracker.product.models.enums.UserField;
import com.duzceders.aicaltracker.product.parser.MealIDParser;
import com.duzceders.aicaltracker.product.service.FirebaseRepository;
import com.duzceders.aicaltracker.product.service.api.GeminiAPIService;
import com.duzceders.aicaltracker.product.utils.LanguageHelper;
import com.google.firebase.Timestamp;

import java.time.LocalTime;

public class FoodViewActivity extends AppCompatActivity {

    private ActivityFoodViewBinding binding;


    private GeminiAPIService geminiAPIService;
    private FirebaseRepository firebaseRepository;
    private FoodViewActivityViewModel viewModel;
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        LanguageHelper.applyLanguage(this);
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider(this).get(FoodViewActivityViewModel.class);
        binding = ActivityFoodViewBinding.inflate(getLayoutInflater());
        firebaseRepository = new FirebaseRepository();
        getUserInfo();
        setContentView(binding.getRoot());
        String imageUrl = getIntent().getStringExtra("imageUrl");
        FoodInfo foodInfo = (FoodInfo) getIntent().getSerializableExtra("foodInfo");

        setListeners(foodInfo);

        if (imageUrl != null) {
            Glide.with(this).load(imageUrl).into(binding.mealImageView);
        } else {
            binding.mealImageView.setImageResource(R.drawable.test_food);
        }

        setFoodInfo(foodInfo);
    }

    private void setFoodInfo(FoodInfo foodInfo) {
        if (foodInfo == null) Log.e("FoodViewActivity", "FoodInfo is null");
        else {
            binding.foodNameTextView.setText(foodInfo.getFoodName());
            binding.totalCaloriesText.setText(getString(R.string.total_calories_format, foodInfo.getCalories()));
            binding.aiAdviceText.setText(foodInfo.getRecommendations());
            binding.proteinTextView.setText(getString(R.string.protein_format, foodInfo.getProtein()));
            binding.carbTextView.setText(getString(R.string.carbs_format, foodInfo.getCarbs()));
            binding.fatTextView.setText(getString(R.string.fat_format, foodInfo.getFat()));
        }
    }

    private void getUserInfo() {
        viewModel.getUserData().observe(this, user -> {
            if (user != null) {
                this.user = user;
            } else {
                showToast("User data is null");
            }
        });
    }

    private void setListeners(FoodInfo foodInfo) {
        binding.saveButton.setOnClickListener(v -> {
            Meal meal = new Meal();
            meal.setMeal_name(binding.foodNameTextView.getText().toString());
            meal.setImage_url(getIntent().getStringExtra("imageUrl"));
            meal.setUser_note(binding.userNoteEditText.getText().toString());
            meal.setProtein_g(foodInfo.getProtein());
            meal.setFat_g(foodInfo.getFat());
            meal.setCarbs_g(foodInfo.getCarbs());
            meal.setCalorie_kcal(foodInfo.getCalories());
            meal.setMeal_time(Timestamp.now());
            meal.setRecommendations(foodInfo.getRecommendations());
            MealType mealType = checkMealType(LocalTime.now());
            meal.setMeal_type(getString(mealType.mealTypeId));

            String mealID = MealIDParser.extractMealIdWithoutRegex(meal.getImage_url());
            meal.setId(mealID);

            firebaseRepository.updateUser(UserField.DAILY_CALORIE_NEEDS_LEFT, (user.getDaily_calorie_needs_left() - foodInfo.getCalories()));
            firebaseRepository.updateUser(UserField.DAILY_MACROS_DAILY_CARBS_LEFT_G, (user.getDaily_macros().getDaily_carbs_left_g() - foodInfo.getCarbs()));
            firebaseRepository.updateUser(UserField.DAILY_MACROS_DAILY_FATS_LEFT_G, (user.getDaily_macros().getDaily_fats_left_g() - foodInfo.getFat()));
            firebaseRepository.updateUser(UserField.DAILY_MACROS_DAILY_PROTEINS_LEFT_G, (user.getDaily_macros().getDaily_proteins_left_g() - foodInfo.getProtein()));

            firebaseRepository.addMeal(meal, mealID);
            finish();
        });
    }

    private MealType checkMealType(LocalTime time) {
        LocalTime noon = LocalTime.NOON; // 12:00
        LocalTime sixPm = LocalTime.of(18, 0); // 18:00

        if (time.isBefore(noon)) {
            return MealType.BREAKFAST;
        } else if (time.isBefore(sixPm)) {
            return MealType.LAUNCH;
        } else {
            return MealType.DINNER;
        }
    }


    private void showToast(String message) {
        Toast.makeText(FoodViewActivity.this, message, Toast.LENGTH_SHORT).show();
    }
}