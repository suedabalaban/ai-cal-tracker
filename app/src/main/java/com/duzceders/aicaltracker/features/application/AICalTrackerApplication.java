package com.duzceders.aicaltracker.features.application;

import android.app.Application;

import com.duzceders.aicaltracker.product.config.CloudinaryConfig;
import com.duzceders.aicaltracker.product.utils.LanguageHelper;

public class AICalTrackerApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        LanguageHelper.applyLanguage(this);

        CloudinaryConfig.initialize(getApplicationContext());
    }
}