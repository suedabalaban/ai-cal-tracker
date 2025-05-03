package com.duzceders.aicaltracker.features.food_detail;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.duzceders.aicaltracker.databinding.ActivityFoodDetailBinding;
import com.duzceders.aicaltracker.product.models.Meal;

import java.util.Objects;

public class FoodDetailActivity extends AppCompatActivity {
    private ActivityFoodDetailBinding binding;
    private Meal meal;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        binding = ActivityFoodDetailBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        ViewCompat.setOnApplyWindowInsetsListener(binding.getRoot(), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        setSupportActionBar(binding.toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getMealData();
        setUI();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void getMealData() {
        Intent intent = getIntent();
        meal = (Meal) intent.getSerializableExtra("meal");
    }

    private void setUI() {
        setImage();
        setFoodName();
        setTimeInfo();
        setNutritional();
        setRecommendations();
        setUserNotes();
    }


    private void setUserNotes() {
        String userNote = meal.getUser_note();
        binding.userNotesText.setText(userNote);
    }

    private void setRecommendations() {
        String recommendations = meal.getRecommendations();
        binding.recommendationsText.setText(recommendations);
    }


    private void setNutritional() {
        binding.caloriesValue.setText(String.valueOf(meal.getCalorie_kcal()));
        binding.proteinValue.setText(String.valueOf(meal.getProtein_g()));
        binding.fatValue.setText(String.valueOf(meal.getFat_g()));
        binding.carbsValue.setText(String.valueOf(meal.getCarbs_g()));
    }

    private void setTimeInfo() {
        binding.mealTypeText.setText(meal.getMeal_type().toString());
       // binding.mealTimeText.setText(meal.getMealTimeAsTimestamp());

    }

    private void setFoodName() {
        binding.foodName.setText(meal.getMeal_name());
    }

    private void setImage() {
        Glide.with(this).load(meal.getImage_url()).into(binding.foodImage);
    }
}