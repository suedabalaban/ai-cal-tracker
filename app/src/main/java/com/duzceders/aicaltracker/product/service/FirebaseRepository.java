package com.duzceders.aicaltracker.product.service;

import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;

import com.duzceders.aicaltracker.product.models.Meal;
import com.duzceders.aicaltracker.product.models.User;
import com.duzceders.aicaltracker.product.service.manager.FirebaseServiceManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.UUID;
import java.util.function.Consumer;

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
    private String getCurrentUserId() {
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
}
