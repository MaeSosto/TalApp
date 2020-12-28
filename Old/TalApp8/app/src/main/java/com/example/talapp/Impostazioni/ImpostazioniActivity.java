package com.example.talapp.Impostazioni;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.example.talapp.MainActivity;
import com.example.talapp.R;

import static com.example.talapp.MainActivity.account;
import static com.example.talapp.MainActivity.currentUser;
import static com.example.talapp.MainActivity.mAuth;
import static com.example.talapp.MainActivity.mGoogleSignInClient;

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

                Intent nextScreen = new Intent(getApplicationContext(), MainActivity.class);
                nextScreen.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(nextScreen);
                //ActivityCompat.finishAffinity(this);
            }
        });

    }
}