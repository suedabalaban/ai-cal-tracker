package com.duzceders.aicaltracker.home;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.duzceders.aicaltracker.R;
import com.duzceders.aicaltracker.product.models.User;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.firebase.FirebaseApp;

public class CalorieTracker extends AppCompatActivity {


    private TextView totalCaloriesValue;
    private TextView totalProteinValue;
    private TextView totalCarbsValue;
    private TextView totalFatsValue;
    private TextView welcomeText;

    private CircularProgressIndicator circularProgressCalories;
    private CircularProgressIndicator circularProgressProtein;
    private CircularProgressIndicator circularProgressCarbs;
    private CircularProgressIndicator circularProgressFats;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseApp.initializeApp(this);
        setContentView(R.layout.activity_calorie_tracker);

        CalorieTrackerViewModel viewModel = new ViewModelProvider(this).get(CalorieTrackerViewModel.class);

        initializeViews();

        viewModel.getUserData().observe(this, user -> {
            if (user != null) {
                updateUiWithUserData(user);
            } else {
                Toast.makeText(this, "Kullanıcı bulunamadı.", Toast.LENGTH_SHORT).show();
            }
        });


        setClickListeners();

    }

    private void updateUiWithUserData(User user) {
        String welcomeMessage = getResources().getString(R.string.welcome, user.getName() + " " + user.getSurname());
        welcomeText.setText(welcomeMessage);

        totalCaloriesValue.setText(String.valueOf(user.getDaily_calorie_needs_left()));
        totalProteinValue.setText(String.valueOf(user.getDaily_macros().getDaily_proteins_left_g()));
        totalCarbsValue.setText(String.valueOf(user.getDaily_macros().getDaily_carbs_left_g()));
        totalFatsValue.setText(String.valueOf(user.getDaily_macros().getDaily_fats_left_g()));

        int progressCalories = calculateProgress(user.getDaily_calorie_needs_left(), user.getDaily_calorie_needs());
        int progressProtein = calculateProgress(user.getDaily_macros().getDaily_proteins_left_g(), user.getDaily_macros().getDaily_proteins_need_g());
        int progressCarbs = calculateProgress(user.getDaily_macros().getDaily_carbs_left_g(), user.getDaily_macros().getDaily_carbs_need_g());
        int progressFats = calculateProgress(user.getDaily_macros().getDaily_fats_left_g(), user.getDaily_macros().getDaily_fats_need_g());


        circularProgressCalories.setProgress(progressCalories);
        circularProgressProtein.setProgress(progressProtein);
        circularProgressCarbs.setProgress(progressCarbs);
        circularProgressFats.setProgress(progressFats);
    }

    private void initializeViews() {

        totalCaloriesValue = findViewById(R.id.totalCaloriesValue);
        totalProteinValue = findViewById(R.id.totalProteinValue);
        totalCarbsValue = findViewById(R.id.totalCarbsValue);
        totalFatsValue = findViewById(R.id.totalFatsValue);
        welcomeText = findViewById(R.id.welcomeText);

        circularProgressCalories = findViewById(R.id.circularProgressCalories);
        circularProgressProtein = findViewById(R.id.circularProgressProtein);
        circularProgressCarbs = findViewById(R.id.circularProgressCarbs);
        circularProgressFats = findViewById(R.id.circularProgressFats);
    }


    private int calculateProgress(double left, double target) {
        return (int) (((float) (target - left) / target) * 100);
    }

    private void setClickListeners() {
        findViewById(R.id.fabAddFood).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });
    }
}
