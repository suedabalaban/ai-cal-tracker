package com.duzceders.aicaltracker.features.auth;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.duzceders.aicaltracker.R;
import com.duzceders.aicaltracker.features.drawer.DrawerActivity;
import com.duzceders.aicaltracker.product.service.FirebaseRepository;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class EmailPasswordActivity extends AppCompatActivity {

    private static final String TAG = "EmailPassword";
    private TextInputEditText emailEditText, passwordEditText;
    private TextInputLayout emailTextField, passwordTextField;
    private MaterialButton btnLogin, btnSignUp;

    private String email;
    private String password;
    private FirebaseRepository firebaseRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_email_password);

        initComponents();
        registerEventHandlers();
    }
    //kullanıcı giriş kontrolü ve yönlendirme
    @Override
    protected void onStart() {
        super.onStart();
        if(firebaseRepository.getCurrentUserId() != null){
            navigateToMainActivity();
        }
    }


    private void initComponents() {
        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        emailTextField = findViewById(R.id.emailTextField);
        passwordTextField = findViewById(R.id.passwordTextField);
        btnLogin = findViewById(R.id.loginButton);
        btnSignUp = findViewById(R.id.signUpButton);
        firebaseRepository= new FirebaseRepository();
    }

    private void registerEventHandlers() {
        btnLogin_onClick();
        btnSignUp_onClick();
        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                validateEmail();
                validatePassword();
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        };
        emailEditText.addTextChangedListener(textWatcher);
        passwordEditText.addTextChangedListener(textWatcher);
    }

    private void btnLogin_onClick() {
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = emailEditText.getText().toString();
                String password = passwordEditText.getText().toString();

                firebaseRepository.signIn(email, password, EmailPasswordActivity.this, new FirebaseRepository.OnAuthResultListener() {
                    @Override
                    public void onSuccess() {
                        startActivity(new Intent(EmailPasswordActivity.this, CalorieTracker.class));
                        finish();
                        Toast.makeText(EmailPasswordActivity.this, "Log in successful ", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onFailure(String errorMessage) {
                        Toast.makeText(EmailPasswordActivity.this, "Log in failed: " + errorMessage, Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    private void validatePassword() {
        String content = passwordEditText.getText().toString();
        if (content.length() < 5) {
            passwordTextField.setError("This field must consist of at least 5 characters");
        } else {
            passwordTextField.setError(null); // Clear any previous error
        }
    }

    private void validateEmail() {
        String email = emailEditText.getText().toString().trim();
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailTextField.setError("Please enter a valid email address");
        } else {
            emailTextField.setError(null); // Clear any previous error
        }
    }

    private void btnSignUp_onClick(){
        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(EmailPasswordActivity.this, SignUpActivity.class);
                startActivity(intent);
            }
        });
    }


    public void navigateToMainActivity() {
        Intent intent = new Intent(EmailPasswordActivity.this, DrawerActivity.class);
        startActivity(intent);
        finish();
    }

}