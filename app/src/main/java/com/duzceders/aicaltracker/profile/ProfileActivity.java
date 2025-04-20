package com.duzceders.aicaltracker.profile;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;

import com.duzceders.aicaltracker.R;
import com.duzceders.aicaltracker.databinding.ActivityProfileBinding;
import com.duzceders.aicaltracker.product.models.User;

public class ProfileActivity extends AppCompatActivity {

    private ActivityProfileBinding binding;

    ProfileViewModel viewModel=new ViewModelProvider(this).get(ProfileViewModel.class);

    @Override
    protected void onCreate(Bundle savedInstanceState) {

    }

    private void updateUiWithUserData(User user) {
    }

}