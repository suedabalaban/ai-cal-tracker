package com.duzceders.aicaltracker.features.drawer;

import android.os.Bundle;
import android.view.MenuItem;

import com.duzceders.aicaltracker.R;
import com.google.android.material.navigation.NavigationView;
import androidx.annotation.NonNull;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.ActionBarDrawerToggle;
import com.duzceders.aicaltracker.databinding.ActivityDrawerBinding;

public class DrawerActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    private ActivityDrawerBinding binding;
    private NavController navController;
    private ActionBarDrawerToggle toggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityDrawerBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.appBarDrawer.toolbar);

        DrawerLayout drawer = binding.drawerLayout;
        NavigationView navigationView = binding.navView;
        
        // Create ActionBarDrawerToggle for proper hamburger icon behavior
        toggle = new ActionBarDrawerToggle(
                this, drawer, binding.appBarDrawer.toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        
        // Include all destinations as top-level destinations
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.calorieTrackerFragment, R.id.profileFragment)
                .setOpenableLayout(drawer)
                .build();
                
        navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_drawer);
        
        // Setup ActionBar with NavController and AppBarConfiguration
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        
        // Setup navigation view with NavController
        navigationView.setNavigationItemSelectedListener(this::onNavigationItemSelected);
    }

    private boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        
        if (id == R.id.nav_home) {
            navController.navigate(R.id.calorieTrackerFragment);
        } else if (id == R.id.nav_profile) {
            navController.navigate(R.id.profileFragment);
        } else if (id == R.id.nav_settings) {
            // Navigate to settings if you have a settings fragment
        } else if (id == R.id.nav_logout) {
            // Handle logout functionality
        }
        
        binding.drawerLayout.closeDrawers();
        return true;
    }


    @Override
    public boolean onSupportNavigateUp() {
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }
}