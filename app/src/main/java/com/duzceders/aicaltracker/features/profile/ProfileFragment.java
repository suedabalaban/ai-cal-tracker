package com.duzceders.aicaltracker.features.profile;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.duzceders.aicaltracker.R;
import com.duzceders.aicaltracker.databinding.FragmentProfileBinding;
import com.duzceders.aicaltracker.product.models.User;

public class ProfileFragment extends Fragment {

    private FragmentProfileBinding binding;
    private ProfileViewModel viewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentProfileBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(this).get(ProfileViewModel.class);
        observeUserData();
    }

    private void observeUserData() {
        viewModel.getUserData().observe(getViewLifecycleOwner(), user -> {
            if (user != null) {
                updateUiWithUserData(user);
            } else {
                Toast.makeText(requireContext(), "Kullanıcı bulunamadı.", Toast.LENGTH_SHORT).show();
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
        setClickableTexts(user);
    }

    private void setClickableTexts(User user) {
        binding.tvHeight.setBackgroundResource(R.drawable.clickable_background);
        binding.tvWeight.setBackgroundResource(R.drawable.clickable_background);
        binding.tvBodyFat.setBackgroundResource(R.drawable.clickable_background);

        binding.tvHeight.setOnClickListener(v -> {
            // Height click action
        });

        binding.tvWeight.setOnClickListener(v -> {
            // Weight click action
        });

        binding.tvBodyFat.setOnClickListener(v -> {
            // Body fat click action
        });
    }

    private void setCalorieGoals(User user) {
        setTvNumber(binding.tvWeightLossCalories, (int) user.getCalorie_goals().getWeight_loss());
        setTvNumber(binding.tvWeightGainCalories, (int) user.getCalorie_goals().getWeight_gain());
        setTvNumber(binding.tvMaintainCalories, (int) user.getCalorie_goals().getMaintain());
    }

    private void setWaterInfos(User user) {
        double totalWater =  user.getDaily_water_needs_liters();
        double waterDrank = totalWater -  user.getDaily_water_needs_left_liters();
        binding.tvTotalWater.setText(getString(R.string.liter, totalWater));
        setTvNumber(binding.tvWaterDrinked, waterDrank);
    }

    private void setAllMacros(User user) {
        setMacro(binding.tvTotalCarbs, binding.tvCarbsEaten, (int) user.getDaily_macros().getDaily_carbs_need_g(), (int) user.getDaily_macros().getDaily_carbs_left_g());
        setMacro(binding.tvTotalProteins, binding.tvProteinsEaten, (int) user.getDaily_macros().getDaily_proteins_need_g(), (int) user.getDaily_macros().getDaily_proteins_left_g());
        setMacro(binding.tvTotalFats, binding.tvFatsEaten, (int) user.getDaily_macros().getDaily_fats_need_g(), (int) user.getDaily_macros().getDaily_fats_left_g());
    }

    private void setCalories(User user) {
        int caloriesEaten = (int) (user.getDaily_calorie_needs() - user.getDaily_calorie_needs_left());
        setTvNumber(binding.tvDailyCalorieEaten, caloriesEaten);
        setFormattedText(binding.tvTotalCalorie, R.string.total_kcal_number, (int) user.getDaily_calorie_needs());
    }

    private void setUserInfos(User user) {
        binding.tvUserFullName.setText(user.getName() + " " + user.getSurname());
        binding.tvHeight.setText(user.getHeight_cm() + " cm");
        binding.tvWeight.setText(user.getWeight_kg() + " kg");
        binding.tvBodyFat.setText(user.getBody_fat_percent() + " %");
    }

    private void setMacro(TextView totalView, TextView eatenView, int total, int left) {
        setFormattedText(totalView, R.string.total_kcal_number, total);
        setTvNumber(eatenView, total - left);
    }

    private void setFormattedText(TextView textView, int formatResId, int value) {
        textView.setText(getString(formatResId, value));
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
        binding.progressWater.setProgress(calculateProgress(user.getDaily_water_needs_left_liters(), user.getDaily_water_needs_liters()));
    }
}
