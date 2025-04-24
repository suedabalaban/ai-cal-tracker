package com.duzceders.aicaltracker.features.auth;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.duzceders.aicaltracker.R;

public class SignUpActivity extends AppCompatActivity {

    private int currentStep = 1; // Track current step

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loadStep(currentStep);
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
        // Next button
        Button nextButton = findViewById(R.id.nextButton);
        nextButton.setOnClickListener(v -> loadStep(2));

        // Back button (will just finish the activity on step 1)
        View backButton = findViewById(R.id.backButton);
        if (backButton != null) {
            backButton.setOnClickListener(v -> finish());
        }

        // Sign in prompt
        setupSignInPrompt();
    }

    private void setupStep2Components() {
        // Next button
        Button nextButton = findViewById(R.id.nextButton);
        nextButton.setOnClickListener(v -> loadStep(3));

        // Back button (goes back to step 1)
        View backButton = findViewById(R.id.backButton);
        if (backButton != null) {
            backButton.setOnClickListener(v -> {
                currentStep = 1;
                loadStep(currentStep);
            });
        }

        // Sign in prompt
        setupSignInPrompt();
    }

    private void setupStep3Components() {
        // Confirm button
        Button confirmButton = findViewById(R.id.confirmButton);
        confirmButton.setOnClickListener(v -> finishSignup());

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
        // Handle final signup logic, such as saving user data
        finish(); // Close the activity after signup
    }
}