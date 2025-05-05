package com.duzceders.aicaltracker.features.profile;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.duzceders.aicaltracker.product.models.User;
import com.duzceders.aicaltracker.product.models.enums.UserField;
import com.duzceders.aicaltracker.product.service.FirebaseRepository;

public class ProfileViewModel extends ViewModel {
    private FirebaseRepository repository = new FirebaseRepository();

    public LiveData<User> getUserData() {
        return repository.getUserByUID();
    }
    
    public void updateHeight(int heightCm) {
        repository.updateUser(UserField.HEIGHT_CM, heightCm);
    }
    
    public void updateWeight(int weightKg) {
        repository.updateUser(UserField.WEIGHT_KG, weightKg);
    }
    
    public void updateBodyFat(int bodyFatPercent) {
        repository.updateUser(UserField.BODY_FAT_PERCENT, bodyFatPercent);
    }
    
    @Override
    protected void onCleared() {
        repository.removeUserListener();
        super.onCleared();
    }
}
