package com.MartinaSosto.talapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.MartinaSosto.talapp.Notification.ForegroundService;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import static com.MartinaSosto.talapp.Utils.Util.Utente;
import static com.MartinaSosto.talapp.Utils.Util.db;
import static com.MartinaSosto.talapp.Utils.Util.mAuth;
import static com.MartinaSosto.talapp.Utils.Util.mFirebaseUser;
import static com.MartinaSosto.talapp.Utils.Util.mGoogleSignInClient;
import static com.MartinaSosto.talapp.Utils.Util.mGoogleUser;

public class LauncherActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        db = FirebaseFirestore.getInstance();

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestIdToken(getString(R.string.default_web_client_id)).requestEmail().build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        mAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mAuth.getCurrentUser();

        if(mFirebaseUser != null){
            Log.d("ACCESSO", "Accesso con firebase");
            Utente = mFirebaseUser.getEmail();
            openHome();
        }
        else if(mGoogleUser != null){
            Log.d("ACCESSO", "Accesso con Google");
            Utente = mGoogleUser.getEmail();
            openHome();
        }
        else{
            Log.d("ACCESSO", "Nessun utente loggato");
            setContentView(R.layout.activity_launcher);
            getSupportActionBar().hide();
        }
    }

    private void openHome(){
        Intent nextScreen = new Intent(getApplicationContext(), HomeActivity.class);
        nextScreen.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        nextScreen.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(nextScreen);
        ActivityCompat.finishAffinity(this);
        if(!ForegroundService.isServiceOn()){
            //Faccio partire il ForegroundService
            Intent serviceIntent = new Intent(this, ForegroundService.class);
            ContextCompat.startForegroundService(this, serviceIntent);
        }
        //Non serve che faccio partire il service ogni volta che apro la App, dovrebbe essere già in running
    }
}