package com.duzceders.aicaltracker.features.calorie_tracker.factory;

import android.app.Application;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.duzceders.aicaltracker.features.calorie_tracker.CalorieTrackerViewModel;



public class CalorieTrackerViewModelFactory implements ViewModelProvider.Factory {
    private final Application application;

    public CalorieTrackerViewModelFactory(Application application) {
        this.application = application;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(CalorieTrackerViewModel.class)) {
            return (T) new CalorieTrackerViewModel(application);  // Application context'i geçiyoruz
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}


