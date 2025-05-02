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
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.WriteBatch;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

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

        db.collection("users").document(userId).set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d(TAG, "DocumentSnapshot successfully written!");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.w(TAG, "Error writing document", e);
            }
        });
    }

    public void addMeal(Meal meal, String mealId) {
        String userId = getCurrentUserId();
        if (userId == null) {
            Log.e(TAG, "User not authenticated");
            return;
        }

        db.collection("users").document(userId).collection("meals").document(mealId).set(meal).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d(TAG, "DocumentSnapshot successfully written!");
            }
        }).addOnFailureListener(new OnFailureListener() {
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
        db.collection("users").document(userId).get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                User user = documentSnapshot.toObject(User.class);
                userLiveData.setValue(user);
            } else {
                userLiveData.setValue(null);
            }
        }).addOnFailureListener(e -> {
            userLiveData.setValue(null);
        });
        return userLiveData;
    }

    public void getUserInformation(UserCallback callback) {
        String userId = getCurrentUserId();
        if (userId == null) {
            Log.e(TAG, "User not authenticated");
            callback.onFailure(new Exception("User not authenticated"));
            return;
        }

        db.collection("users").document(userId).get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                User user = documentSnapshot.toObject(User.class);
                callback.onUserReceived(user);
            } else {
                Log.e(TAG, "User not found");
                callback.onFailure(new Exception("User not found"));
            }
        }).addOnFailureListener(e -> {
            Log.e(TAG, "Error getting user information", e);
            callback.onFailure(e);
        });
    }

    public void getUserMeals(MealCallback callback) {
        String userId = getCurrentUserId();
        if (userId == null) {
            Log.e(TAG, "User not authenticated");
            callback.onFailure(new Exception("User not authenticated"));
            return;
        }
        db.collection("users").document(userId).collection("meals").get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.isEmpty()) {
                Log.e(TAG, "No meals found for user");
                callback.onFailure(new Exception("No meals found for user"));
                return;
            } else {
                List<Meal> meals = documentSnapshot.toObjects(Meal.class);
                callback.onMealsReceived(meals);
            }

        }).addOnFailureListener(e -> {
            Log.e(TAG, "Error getting user's meals", e);
            callback.onFailure(e);
        });

    }

    public void runDailyNeedsTask() {
        String userId = getCurrentUserId();
        if (userId == null) {
            Log.e(TAG, "User not authenticated");
            return;
        }
        DocumentReference userRef = db.collection("users").document(userId);
        userRef.get().addOnSuccessListener(documentSnapshot -> {
            if (!documentSnapshot.exists()) {
                Log.e(TAG, "User not found");
                return;
            }
            Timestamp lastLogin = documentSnapshot.getTimestamp("last_login");
            if (lastLogin != null && isDayChanged(lastLogin.toDate())) {
                getUserInfoAndUpdateDailyNeeds();
            }

            userRef.update("last_login", Timestamp.now()).addOnSuccessListener(aVoid -> {
                Log.d(TAG, "Last login updated successfully");
            }).addOnFailureListener(e -> {
                Log.e(TAG, "Error updating last login", e);
            });


        }).addOnFailureListener(e -> {
            Log.e(TAG, "Error getting last login", e);
        });
    }

    private void getUserInfoAndUpdateDailyNeeds() {
        String userId = getCurrentUserId();
        if (userId == null) {
            Log.e(TAG, "User not authenticated");
            return;
        }

        DocumentReference userRef = db.collection("users").document(userId);
        WriteBatch batch = db.batch();

        getUserInformation(new UserCallback() {
            @Override
            public void onUserReceived(User user) {

                if (user == null) {
                    Log.e(TAG, "User info is null, skipping batch update.");
                    return;
                }
                batch.update(userRef, UserField.DAILY_CALORIE_NEEDS_LEFT.getFieldName(), user.getDaily_calorie_needs());
                batch.update(userRef, UserField.DAILY_WATER_NEEDS_LEFT_LITERS.getFieldName(), user.getDaily_water_needs_liters());
                batch.update(userRef, UserField.DAILY_MACROS_DAILY_CARBS_LEFT_G.getFieldName(), user.getDaily_macros().getDaily_carbs_need_g());
                batch.update(userRef, UserField.DAILY_MACROS_DAILY_FATS_LEFT_G.getFieldName(), user.getDaily_macros().getDaily_fats_need_g());
                batch.update(userRef, UserField.DAILY_MACROS_DAILY_PROTEINS_LEFT_G.getFieldName(), user.getDaily_macros().getDaily_proteins_need_g());

                batch.commit().addOnSuccessListener(aVoid -> {
                    Log.d(TAG, "All user fields updated successfully in batch");
                }).addOnFailureListener(e -> {
                    Log.e(TAG, "Error updating user fields in batch", e);
                });
            }

            @Override
            public void onFailure(Exception e) {
                Log.e(TAG, "Error: " + e.getMessage());
            }
        });
    }


    private boolean isDayChanged(Date lastLoginDate) {
        Calendar lastLoginCal = Calendar.getInstance();
        lastLoginCal.setTime(lastLoginDate);

        Calendar nowCal = Calendar.getInstance();

        return lastLoginCal.get(Calendar.YEAR) != nowCal.get(Calendar.YEAR) || lastLoginCal.get(Calendar.DAY_OF_YEAR) != nowCal.get(Calendar.DAY_OF_YEAR);
    }

    //authentication methods

    public void signIn(String email, String password, Context context, OnAuthResultListener listener) {
        FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password).addOnSuccessListener(authResult -> {
            listener.onSuccess();
        }).addOnFailureListener(e -> {
            listener.onFailure(e.getMessage());
        });
    }


    public void signUp(String email, String password, OnAuthResultListener listener) {
        auth.createUserWithEmailAndPassword(email, password).addOnSuccessListener(authResult -> {
            listener.onSuccess();
        }).addOnFailureListener(e -> {
            listener.onFailure(e.getMessage());
        });
    }

    public interface OnAuthResultListener {
        void onSuccess();

        void onFailure(String errorMessage);
    }

    public interface UserCallback {
        void onUserReceived(User user);

        void onFailure(Exception e);
    }

    public interface MealCallback {
        void onMealsReceived(List<Meal> meals);

        void onFailure(Exception e);
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

        db.collection("users").document(userId).update(field.getFieldName(), value).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d(TAG, field + " updated successfully");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.w(TAG, field + " updated successfully", e);
            }
        });
    }
}
