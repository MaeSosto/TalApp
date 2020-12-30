package com.example.talapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LauncherActivity extends AppCompatActivity {

    public static FirebaseAuth mAuth;
    public static GoogleSignInClient mGoogleSignInClient;
    public static FirebaseUser currentUser;
    public static GoogleSignInAccount account;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        account = GoogleSignIn.getLastSignedInAccount(this);

        if(currentUser != null || account != null){
            Intent nextScreen = new Intent(getApplicationContext(), HomeActivity.class);
            nextScreen.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            nextScreen.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            startActivity(nextScreen);
            ActivityCompat.finishAffinity(this);
        }
        else{
            setContentView(R.layout.activity_launcher);

            //Nasconde la toolbar
            getSupportActionBar().hide();
        }
    }
}