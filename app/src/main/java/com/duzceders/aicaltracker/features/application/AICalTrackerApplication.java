package com.duzceders.aicaltracker.features.application;

import android.app.Application;

import com.duzceders.aicaltracker.product.config.CloudinaryConfig;

public class AICalTrackerApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        CloudinaryConfig.initialize(getApplicationContext());
    }
}