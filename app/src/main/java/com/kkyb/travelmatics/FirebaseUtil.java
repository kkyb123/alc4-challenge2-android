package com.kkyb.travelmatics;

import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.firebase.ui.auth.AuthUI;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FirebaseUtil {

  private static final int RC_SIGN_IN = 123;
  private static ListActivity caller;

  public static FirebaseDatabase firebaseDatabase;
  public static DatabaseReference databaseReference;

  public static FirebaseAuth firebaseAuth;
  public static FirebaseAuth.AuthStateListener authStateListener;

  private static FirebaseUtil firebaseUtil;

  public static ArrayList<TravelDeal> deals;

  public static boolean isAdmin;

  private FirebaseUtil() {
  }

  public static void openFbReference(String ref, ListActivity callerActivity) {
    if (firebaseUtil == null) {
      caller = callerActivity;

      firebaseUtil = new FirebaseUtil();
      firebaseDatabase = FirebaseDatabase.getInstance();

      firebaseAuth = FirebaseAuth.getInstance();
      authStateListener = firebaseAuth -> {
        if (firebaseAuth.getCurrentUser() == null) {
          signIn();
        } else {
          checkAdmin(firebaseAuth.getUid());
        }

        Toast.makeText(callerActivity, "Welcome back!", Toast.LENGTH_LONG).show();
      };
    }
    deals = new ArrayList<>();
    databaseReference = firebaseDatabase.getReference().child(ref);
  }

  private static void checkAdmin(String uid) {
    isAdmin = false;
    DatabaseReference ref = firebaseDatabase.getReference().child("administrators").child(uid);
    ChildEventListener listener = new ChildEventListener() {
      @Override
      public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
        FirebaseUtil.isAdmin = true;
        caller.showMenu();
      }

      @Override
      public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

      }

      @Override
      public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

      }

      @Override
      public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

      }

      @Override
      public void onCancelled(@NonNull DatabaseError databaseError) {

      }
    };
    ref.addChildEventListener(listener);
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
