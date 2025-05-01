package com.duzceders.aicaltracker.features.settings;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;

import com.duzceders.aicaltracker.R;
import com.duzceders.aicaltracker.databinding.FragmentSettingsBinding;
import com.duzceders.aicaltracker.features.calorie_tracker.CalorieTrackerFragment;
import com.duzceders.aicaltracker.features.drawer.DrawerActivity;
import com.duzceders.aicaltracker.product.utils.LanguageHelper;

public class SettingsFragment extends Fragment {

    private FragmentSettingsBinding binding;

    public SettingsFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentSettingsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        boolean isDarkMode = isDarkModeEnabled();

        setThemeSwitch(isDarkMode);

        setAppTheme();

        setLanguageIcon();

        binding.languageToggleButton.setOnClickListener(v -> showLanguageSelectionDialog());

    }

    private void setAppTheme() {
        binding.themeSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            AppCompatDelegate.setDefaultNightMode(isChecked ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO);
            requireActivity().recreate();
        });
    }

    private void setThemeSwitch(boolean isDark) {
        binding.themeSwitch.setChecked(isDark);
    }

    private boolean isDarkModeEnabled() {
        int currentNightMode = requireContext().getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
        return currentNightMode == Configuration.UI_MODE_NIGHT_YES;

    }
    private void setLanguageIcon() {
        String currentLanguage = LanguageHelper.getLanguage(requireContext());
        if ("tr".equals(currentLanguage)) {
            binding.languageFlag.setImageResource(R.drawable.flag_tr);
        } else {
            binding.languageFlag.setImageResource(R.drawable.flag_en);
        }
    }

    private void showLanguageSelectionDialog() {
        String[] languages = {
                getString(R.string.language_en),
                getString(R.string.language_tr)
        };

        new AlertDialog.Builder(requireContext())
                .setTitle(getString(R.string.language_title))
                .setItems(languages, (dialog, which) -> {
                    String selectedLanguage = (which == 0) ? "en" : "tr";
                    changeLanguage(selectedLanguage);
                })
                .create()
                .show();
    }

    private void changeLanguage(String languageCode) {
        if (languageCode.equals(LanguageHelper.getLanguage(requireContext()))) {
            return; // Aynı dilse bir şey yapma
        }

        LanguageHelper.setLanguage(requireContext(), languageCode);

        // Ana aktiviteyi yeniden başlatarak dili uygula
        Intent intent = new Intent(requireContext(), DrawerActivity.class );
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }
}
