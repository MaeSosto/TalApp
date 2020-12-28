package com.example.talapp.Impostazioni;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.example.talapp.Home.HomeActivity;
import com.example.talapp.Launcher.LauncherActivity;
import com.example.talapp.R;
import com.google.firebase.auth.FirebaseAuth;

import static com.example.talapp.Launcher.LauncherActivity.account;
import static com.example.talapp.Launcher.LauncherActivity.currentUser;
import static com.example.talapp.Launcher.LauncherActivity.mAuth;
import static com.example.talapp.Launcher.LauncherActivity.mGoogleSignInClient;

public class ImpostazioniActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_impostazioni);

        TextView User = findViewById(R.id.TXVUser);
        if(currentUser != null) {
            User.setText("Al momento sei loggato con Email " + currentUser.getEmail());
        }
        else{
            User.setText("Al momento sei loggato con Google " + account.getEmail());
        }

        findViewById(R.id.buttonDisconnetti).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               //currentUser = null;
               mGoogleSignInClient.signOut();
               mAuth.signOut();

                Intent nextScreen = new Intent(getApplicationContext(), LauncherActivity.class);
                nextScreen.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(nextScreen);
                //ActivityCompat.finishAffinity(this);
            }
        });

    }
}