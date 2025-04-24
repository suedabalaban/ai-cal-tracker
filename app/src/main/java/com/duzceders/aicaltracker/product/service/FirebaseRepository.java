package com.duzceders.aicaltracker.product.service;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.duzceders.aicaltracker.product.models.Meal;
import com.duzceders.aicaltracker.product.models.User;
import com.duzceders.aicaltracker.product.models.enums.UserField;
import com.duzceders.aicaltracker.product.service.manager.FirebaseServiceManager;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.UUID;

public class FirebaseRepository {

    public static final String TAG = "FirebaseRepository";
    private final FirebaseFirestore db;
    private final FirebaseAuth auth;

    public FirebaseRepository() {
        FirebaseServiceManager manager = FirebaseServiceManager.getInstance();
        db = manager.getFirebaseFirestore();
        auth = manager.getFirebaseAuth();
    }

    /// Gets current user's uid if its null returns null
    public String getCurrentUserId() {
        FirebaseUser user = auth.getCurrentUser();
        return user != null ? user.getUid() : null;
    }

    /// Add user to firestore with user information.
    public void addUser(User user) {
        String userId = getCurrentUserId();
        if (userId == null) {
            Log.e(TAG, "User not authenticated");
            return;
        }

        db.collection("users").document(userId).set(user)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "DocumentSnapshot successfully written!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error writing document", e);
                    }
                });
    }

    public void addMeal(Meal meal) {
        String userId = getCurrentUserId();
        if (userId == null) {
            Log.e(TAG, "User not authenticated");
            return;
        }
        String mealId = UUID.randomUUID().toString();
        db.collection("users")
                .document(userId)
                .collection("meals")
                .document(mealId)
                .set(meal)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "DocumentSnapshot successfully written!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error writing document", e);
                    }
                });
    }

    /// This method retrieves the current user's information from Firestore in viewmodels.
    public LiveData<User> getUserByUID() {
        MutableLiveData<User> userLiveData = new MutableLiveData<>();
        String userId = getCurrentUserId();
        if (userId == null) {
            Log.e(TAG, "User not authenticated");
            userLiveData.setValue(null);
            return userLiveData;
        }
        db.collection("users")
                .document(userId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        User user = documentSnapshot.toObject(User.class);
                        userLiveData.setValue(user);
                    } else {
                        userLiveData.setValue(null);
                    }
                })
                .addOnFailureListener(e -> {
                    userLiveData.setValue(null);
                });
        return userLiveData;
    }

    //authentication methods

    public void signIn(String email, String password, Context context, OnAuthResultListener listener) {
        FirebaseAuth.getInstance()
                .signInWithEmailAndPassword(email, password)
                .addOnSuccessListener(authResult -> {
                    listener.onSuccess();
                })
                .addOnFailureListener(e -> {
                    listener.onFailure(e.getMessage());
                });
    }


    public void signUp(String email, String password, OnAuthResultListener listener) {
        auth.createUserWithEmailAndPassword(email, password)
                .addOnSuccessListener(authResult -> {
                    listener.onSuccess();
                })
                .addOnFailureListener(e -> {
                    listener.onFailure(e.getMessage());
                });
    }
    public interface OnAuthResultListener {
        void onSuccess();
        void onFailure(String errorMessage);
    }
    public void signOut() {
        auth.signOut();  // FirebaseServiceManager'dan gelen auth objesi üzerinden
    }


    /// This method updates the current user's information in Firestore with UserField enum and value.
    public void updateUser(UserField field, Object value) {
        String userId = getCurrentUserId();
        if (userId == null) {
            Log.e(TAG, "User not authenticated");
            return;
        }
        if (field == null || value == null) {
            Log.e(TAG, "Field or value is null");
            return;
        }

        db.collection("users").document(userId).update(field.getFieldName(), value)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, field + " updated successfully");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, field + " updated successfully", e);
                    }
                });
    }
}
