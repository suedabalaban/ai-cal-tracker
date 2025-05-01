package com.duzceders.aicaltracker.features.auth;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.duzceders.aicaltracker.R;
import com.duzceders.aicaltracker.product.models.User;
import com.duzceders.aicaltracker.product.models.enums.ActivityLevel;
import com.duzceders.aicaltracker.product.parser.AgeParser;
import com.duzceders.aicaltracker.product.service.DailyCalorieManager;
import com.duzceders.aicaltracker.product.service.FirebaseRepository;

public class SignUpActivity extends AppCompatActivity {

    private int currentStep = 1; // Track current step
    private String email, password, gender;
    private EditText emailEditText, passwordEditText, nameEditText, surnameEditText,
            birthdateEditText, weightEditText, heightEditText, bodyFatEditText;
    private Button nextButton;
    ActivityLevel level;
    private FirebaseRepository firebaseRepository;
    private User user = new User();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loadStep(currentStep);
        firebaseRepository = new FirebaseRepository();
    }

    private void loadStep(int step) {
        switch (step) {
            case 1:
                setContentView(R.layout.activity_sign_up_step1);
                setupStep1Components();
                break;

            case 2:
                setContentView(R.layout.activity_sign_up_step2);
                setupStep2Components();
                break;

            case 3:
                setContentView(R.layout.activity_sign_up_step3);
                setupStep3Components();
                break;
        }
    }

    private void setupStep1Components() {
        nameEditText = findViewById(R.id.nameEditText);
        surnameEditText = findViewById(R.id.surnameEditText);
        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        nextButton = findViewById(R.id.nextButton);

        nextButton.setOnClickListener(v -> {
            String name = nameEditText.getText().toString().trim();
            String surname = surnameEditText.getText().toString().trim();
            String emailInput = emailEditText.getText().toString().trim();
            String passwordInput = passwordEditText.getText().toString().trim();

            boolean isValid = true;

            // Validate name
            if (name.isEmpty() || !isAlpha(name)) {
                nameEditText.setError(name.isEmpty() ? getString(R.string.name_required) : getString(R.string.invalid_name));
                isValid = false;
            } else {
                nameEditText.setError(null);
            }

            // Validate surname
            if (surname.isEmpty() || !isAlpha(surname)) {
                surnameEditText.setError(surname.isEmpty() ? getString(R.string.surname_required) : getString(R.string.invalid_surname));
                isValid = false;
            } else {
                surnameEditText.setError(null);
            }

            // Validate email
            if (emailInput.isEmpty() || !isValidEmail(emailInput)) {
                emailEditText.setError(emailInput.isEmpty() ? getString(R.string.email_required) : getString(R.string.invalid_email));
                isValid = false;
            } else {
                emailEditText.setError(null);
            }

            // Validate password
            if (passwordInput.isEmpty() || passwordInput.length() < 6) {
                passwordEditText.setError(passwordInput.isEmpty() ? getString(R.string.password_required) : getString(R.string.password_too_short));
                isValid = false;
            } else {
                passwordEditText.setError(null);
            }

            // If all valid, proceed to next step
            if (!isValid) return;

            user.setName(name);
            user.setSurname(surname);
            user.setEmail(emailInput);
            this.email = emailInput;
            this.password = passwordInput;

            currentStep = 2;
            loadStep(currentStep);
        });

        View backButton = findViewById(R.id.backButton);
        if (backButton != null) {
            backButton.setOnClickListener(v -> finish());
        }

        setupSignInPrompt();
    }

    private void setupStep2Components() {
        birthdateEditText = findViewById(R.id.birthdateEditText);
        weightEditText = findViewById(R.id.weightEditText);
        heightEditText = findViewById(R.id.heightEditText);
        bodyFatEditText = findViewById(R.id.bodyFatEditText);
        RadioGroup genderGroup = findViewById(R.id.genderGroup);
        nextButton = findViewById(R.id.nextButton);

        nextButton.setOnClickListener(v -> {
            String birthdateInput = birthdateEditText.getText().toString().trim();
            String weightInput = weightEditText.getText().toString().trim();
            String heightInput = heightEditText.getText().toString().trim();
            String bodyFatInput = bodyFatEditText.getText().toString().trim();

            boolean isValid = true;

            // Validate birthdate (dd-mm-yyyy format)
            if (birthdateInput.isEmpty() || !birthdateInput.matches("\\d{2}-\\d{2}-\\d{4}")) {
                birthdateEditText.setError(birthdateInput.isEmpty() ? getString(R.string.birthdate_required) : getString(R.string.invalid_birthdate));
                isValid = false;
            } else {
                birthdateEditText.setError(null);
            }

            // Validate weight
            if (weightInput.isEmpty() || Integer.parseInt(weightInput) <= 0 || Integer.parseInt(weightInput) > 200) {
                weightEditText.setError(weightInput.isEmpty() ? getString(R.string.weight_required) : getString(R.string.invalid_weight));
                isValid = false;
            } else {
                weightEditText.setError(null);
            }

            // Validate height
            if (heightInput.isEmpty() || Integer.parseInt(heightInput) <= 0 || Integer.parseInt(heightInput) > 230) {
                heightEditText.setError(heightInput.isEmpty() ? getString(R.string.height_required) : getString(R.string.invalid_height));
                isValid = false;
            } else {
                heightEditText.setError(null);
            }

            // Validate body fat percentage
            if (bodyFatInput.isEmpty() || Double.parseDouble(bodyFatInput) < 0 || Double.parseDouble(bodyFatInput) > 100) {
                bodyFatEditText.setError(bodyFatInput.isEmpty() ? getString(R.string.body_fat_required) : getString(R.string.invalid_body_fat));
                isValid = false;
            } else {
                bodyFatEditText.setError(null);
            }

            // Validate gender selection
            int selectedGenderId = genderGroup.getCheckedRadioButtonId();
            if (selectedGenderId == -1) {
                showToast(getString(R.string.gender_required));
                isValid = false;
            }

            // If all valid, proceed to next step
            if (!isValid) return;

            user.setBirthday(birthdateInput);
            user.setWeight_kg(Integer.parseInt(weightInput));
            user.setHeight_cm(Integer.parseInt(heightInput));
            user.setBody_fat_percent(Integer.parseInt(bodyFatInput));

            String gender = selectedGenderId == R.id.radioMale ? "Male" : selectedGenderId == R.id.radioFemale ? "Female" : "Unspecified";
            user.setGender(gender);

            currentStep = 3;
            loadStep(currentStep);
        });

        View backButton = findViewById(R.id.backButton);
        if (backButton != null) {
            backButton.setOnClickListener(v -> {
                currentStep = 1;
                loadStep(currentStep);
            });
        }

        setupSignInPrompt();
    }

    private void setupStep3Components() {
        RadioGroup activityGroup = findViewById(R.id.activityLevelGroup);

        // Confirm button
        Button confirmButton = findViewById(R.id.confirmButton);
        confirmButton.setOnClickListener(v -> {
            int selectedId = activityGroup.getCheckedRadioButtonId();

            if (selectedId == R.id.radioNoActivity) {
                level = ActivityLevel.NO_ACTIVITY;
            } else if (selectedId == R.id.radioLowActivity) {
                level = ActivityLevel.LOW_ACTIVITY;
            } else if (selectedId == R.id.radioActive) {
                level = ActivityLevel.ACTIVE;
            } else if (selectedId == R.id.radioVeryActive) {
                level = ActivityLevel.VERY_ACTIVE;
                ;
            } else {
                showToast(getString(R.string.please_select_activity));
                return;
            }

            user.setActivity_level(level);
            finishSignup();
        });

        // Sign in prompt (no back button on this step)
        setupSignInPrompt();
    }

    private void setupSignInPrompt() {
        TextView signInPrompt = findViewById(R.id.signInPrompt);
        if (signInPrompt != null) {
            signInPrompt.setOnClickListener(v -> {
                Intent intent = new Intent(SignUpActivity.this, EmailPasswordActivity.class);
                startActivity(intent);
                finish(); // Close signup activity
            });
        }
    }

    private void finishSignup() {
        firebaseRepository.signUp(email, password, new FirebaseRepository.OnAuthResultListener() {
            @Override
            public void onSuccess() {
                setupUserDetails();
                firebaseRepository.addUser(user);
                Intent intent = new Intent(SignUpActivity.this, EmailPasswordActivity.class);
                startActivity(intent);
                finish();
            }

            @Override
            public void onFailure(String errorMessage) {
                showToast(getString(R.string.login_failed));
            }
        });
    }

    private void setupUserDetails() {
        DailyCalorieManager dailyCalorieManager = new DailyCalorieManager();
        AgeParser ageParser = new AgeParser();
        int ageString = ageParser.calculateAge(user.getBirthday());
        int bmr = dailyCalorieManager.calculateBMR(user.getGender(), user.getHeight_cm(), user.getWeight_kg(), ageString);
        int dailyCalorieNeeds = dailyCalorieManager.calculateTDEE(bmr, user.getActivity_level());
        user.setBmr(bmr);
        user.setDaily_calorie_needs(dailyCalorieNeeds);
        user.setDaily_calorie_needs_left(dailyCalorieNeeds);

        // Calorie goals
        User.CalorieGoals calorieGoals = new User.CalorieGoals();
        user.setCalorie_goals(calorieGoals);
        calorieGoals.setMaintain(dailyCalorieNeeds);
        calorieGoals.setWeight_gain(dailyCalorieNeeds + 500);
        calorieGoals.setWeight_loss(dailyCalorieNeeds - 500);

        // Daily macros calculations
        User.DailyMacros dailyMacros = new User.DailyMacros();
        user.setDaily_macros(dailyMacros);

        // Makro besinlerin hesaplanması
        dailyMacros.setDaily_carbs_need_g((int) Math.round(dailyCalorieNeeds * 0.45 / 4));
        dailyMacros.setDaily_fats_need_g((int) Math.round(dailyCalorieNeeds * 0.25 / 9));
        dailyMacros.setDaily_proteins_need_g((int) Math.round(dailyCalorieNeeds * 0.30 / 4));

        // Günlük makro kalanları
        dailyMacros.setDaily_carbs_left_g(dailyMacros.getDaily_carbs_need_g());
        dailyMacros.setDaily_fats_left_g(dailyMacros.getDaily_fats_need_g());
        dailyMacros.setDaily_proteins_left_g(dailyMacros.getDaily_proteins_need_g());

        // Su ihtiyacı
        user.setDaily_water_needs_liters((int) Math.round(user.getWeight_kg() * 0.033));
        user.setDaily_water_needs_left_liters((int) Math.round(user.getWeight_kg() * 0.033));
    }


    private boolean isAlpha(String input) {
        return input.matches("^[a-zA-ZçÇğĞıİöÖşŞüÜ\\s]+$");
    }

    private boolean isValidEmail(String input) {
        return input.matches("^\\S+@\\S+\\.\\S+$");
    }
    private void showToast(String message) {
        Toast.makeText(SignUpActivity.this, message, Toast.LENGTH_SHORT).show();
    }

}