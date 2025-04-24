package com.duzceders.aicaltracker.features.profile;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.duzceders.aicaltracker.product.models.User;
import com.duzceders.aicaltracker.product.service.FirebaseRepository;

public class ProfileViewModel extends ViewModel {
    private FirebaseRepository repository = new FirebaseRepository();

    public LiveData<User> getUserData() {
        return repository.getUserByUID();
    }
}
