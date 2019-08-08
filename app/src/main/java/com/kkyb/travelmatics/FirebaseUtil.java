package com.kkyb.travelmatics;

import android.app.Activity;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FirebaseUtil {

  private static final int RC_SIGN_IN = 123;
  private static Activity caller;

  public static FirebaseDatabase firebaseDatabase;
  public static DatabaseReference databaseReference;

  public static FirebaseAuth firebaseAuth;
  public static FirebaseAuth.AuthStateListener authStateListener;

  private static FirebaseUtil firebaseUtil;

  public static ArrayList<TravelDeal> deals;

  private FirebaseUtil() {
  }

  public static void openFbReference(String ref, Activity callerActivity) {
    if (firebaseUtil == null) {
      caller = callerActivity;

      firebaseUtil = new FirebaseUtil();
      firebaseDatabase = FirebaseDatabase.getInstance();

      firebaseAuth = FirebaseAuth.getInstance();
      authStateListener = firebaseAuth -> {
        if (firebaseAuth.getCurrentUser() == null) {
          signIn();
        }

        Toast.makeText(callerActivity, "Welcome back!", Toast.LENGTH_LONG).show();
      };
    }
    deals = new ArrayList<>();
    databaseReference = firebaseDatabase.getReference().child(ref);
  }

  private static void signIn() {
    List<AuthUI.IdpConfig> providers = Arrays.asList(
        new AuthUI.IdpConfig.EmailBuilder().build(),
        new AuthUI.IdpConfig.GoogleBuilder().build());

    caller.startActivityForResult(
        AuthUI.getInstance()
            .createSignInIntentBuilder()
            .setAvailableProviders(providers)
            .build(),
        RC_SIGN_IN);
  }

  public static void attachAuthListener() {
    firebaseAuth.addAuthStateListener(authStateListener);
  }

  public static void detachAuthListener() {
    firebaseAuth.removeAuthStateListener(authStateListener);
  }

}
