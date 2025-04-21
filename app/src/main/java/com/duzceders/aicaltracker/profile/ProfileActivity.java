package com.duzceders.aicaltracker.profile;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.duzceders.aicaltracker.R;
import com.duzceders.aicaltracker.databinding.ActivityProfileBinding;
import com.duzceders.aicaltracker.product.models.User;

public class ProfileActivity extends AppCompatActivity {

    private ActivityProfileBinding binding;
    private ProfileViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        viewModel = new ViewModelProvider(this).get(ProfileViewModel.class);

        observeUserData();
    }

    private void observeUserData() {
        viewModel.getUserData().observe(this, user -> {
            if (user != null) {
                updateUiWithUserData(user);
            } else {
                Toast.makeText(this, "Kullanıcı bulunamadı.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateUiWithUserData(User user) {
        setUserInfos(user);
        setCalories(user);
        setAllMacros(user);
        setWaterInfos(user);
        setCalorieGoals(user);
        setProgressBars(user);

    }

    private void setCalorieGoals(User user) {
        setTvNumber(binding.tvWeightLossCalories, (int) user.getCalorie_goals().getWeight_loss());
        setTvNumber(binding.tvWeightGainCalories, (int) user.getCalorie_goals().getWeight_gain());
        setTvNumber(binding.tvMaintainCalories, (int) user.getCalorie_goals().getMaintain());
    }

    private void setWaterInfos(User user) {
        int totalWater = (int) user.getDaily_water_needs_liters();
        int waterDrank = totalWater - (int) user.getDaily_water_needs_left_liters();
        setFormattedText(binding.tvTotalWater, R.string.liter, totalWater);
        setTvNumber(binding.tvWaterDrinked, waterDrank);
    }

    private void setAllMacros(User user) {
        setMacro(binding.tvTotalCarbs, binding.tvCarbsEaten,
                (int) user.getDaily_macros().getDaily_carbs_need_g(),
                (int) user.getDaily_macros().getDaily_carbs_left_g());

        setMacro(binding.tvTotalProteins, binding.tvProteinsEaten,
                (int) user.getDaily_macros().getDaily_proteins_need_g(),
                (int) user.getDaily_macros().getDaily_proteins_left_g());

        setMacro(binding.tvTotalFats, binding.tvFatsEaten,
                (int) user.getDaily_macros().getDaily_fats_need_g(),
                (int) user.getDaily_macros().getDaily_fats_left_g());
    }

    private void setCalories(User user) {
        int caloriesEaten = (int) (user.getDaily_calorie_needs() - user.getDaily_calorie_needs_left());
        setTvNumber(binding.tvDailyCalorieEaten, caloriesEaten);
        setFormattedText(binding.tvTotalCalorie, R.string.total_kcal_number, (int) user.getDaily_calorie_needs());
    }

    private void setUserInfos(User user) {
        binding.tvUserFullName.setText(user.getName() + " " + user.getSurname());
        setFormattedText(binding.tvHeight, R.string.cm, (int) user.getHeight_cm());
        setFormattedText(binding.tvWeight, R.string.kg, (int) user.getWeight_kg());
        setFormattedText(binding.tvBodyFat, R.string.percent, (int) user.getBody_fat_percent());
    }

    private void setMacro(TextView totalView, TextView eatenView, int total, int left) {
        setFormattedText(totalView, R.string.total_kcal_number, total);
        setTvNumber(eatenView,  total - left);
    }

    private void setFormattedText(TextView textView, int formatResId, int value) {
        textView.setText(getString(formatResId, value));
    }


    private void setFormattedText(TextView textView, String format, Object value) {
        textView.setText(String.format(format, value));
    }

    private void setTvNumber(TextView textView, Object value) {
        textView.setText(String.valueOf(value));
    }


    private int calculateProgress(double left, double target) {
        return (int) (((float) (target - left) / target) * 100);
    }

    private void setProgressBars(User user) {
        binding.progressCalories.setProgress(calculateProgress(user.getDaily_calorie_needs_left(), user.getDaily_calorie_needs()));
        binding.progressProteins.setProgress(calculateProgress(user.getDaily_macros().getDaily_proteins_left_g(), user.getDaily_macros().getDaily_proteins_need_g()));
        binding.progressCarbs.setProgress(calculateProgress(user.getDaily_macros().getDaily_carbs_left_g(), user.getDaily_macros().getDaily_carbs_need_g()));
        binding.progressFats.setProgress(calculateProgress(user.getDaily_macros().getDaily_fats_left_g(), user.getDaily_macros().getDaily_fats_need_g()));
    }
}
