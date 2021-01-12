package com.example.talapp.Impostazioni;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import com.example.talapp.LauncherActivity;
import com.example.talapp.Notification.ForegroundService;
import com.example.talapp.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import static com.example.talapp.HomeActivity.trasfusioniRef;
import static com.example.talapp.Notification.ForegroundService.setServiceOn;
import static com.example.talapp.Utils.Util.KEY_TRASFUSIONE_DATA;
import static com.example.talapp.Utils.Util.KEY_TRASFUSIONE_NOTIFICA;
import static com.example.talapp.Utils.Util.Utente;
import static com.example.talapp.Utils.Util.mAuth;
import static com.example.talapp.Utils.Util.mFirebaseUser;
import static com.example.talapp.Utils.Util.mGoogleSignInClient;

public class ImpostazioniActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_impostazioni);

        LinearLayout LLSwitchTrasfusione = findViewById(R.id.LLSwitchTrasfusione);
        Switch switchTrasfusione = findViewById(R.id.switchTrasfusione);
        /*switchTrasfusione.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    trasfusioniRef.whereGreaterThan(KEY_TRASFUSIONE_DATA, Calendar.getInstance().getTime())
                            .orderBy(KEY_TRASFUSIONE_DATA, Query.Direction.DESCENDING)
                            .limit(1)
                            .get()
                            .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                @Override
                                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                    if(queryDocumentSnapshots.getDocuments().size() > 0){
                                        switchTrasfusione.setChecked(true);
                                        LLSwitchTrasfusione.setVisibility(View.VISIBLE);
                                        Map<String, Object> trasfusione = new HashMap<>();
                                        if(queryDocumentSnapshots.getDocuments().get(0).contains(KEY_TRASFUSIONE_NOTIFICA)){
                                            trasfusione = queryDocumentSnapshots.getDocuments().get(0).getData();
                                            if(trasfusione.containsKey(KEY_TRASFUSIONE_NOTIFICA)){
                                                trasfusione.put(KEY_TRASFUSIONE_NOTIFICA, true);
                                                trasfusioniRef.document(queryDocumentSnapshots.getDocuments().get(0).getId()).update(trasfusione);
                                                      //  .ad
                                            }
                                            else {
                                                trasfusione.put(KEY_TRASFUSIONE_NOTIFICA, true);
                                                trasfusioniRef.add(trasfusione);
                                            }
                                        }
                                    }
                                    else{
                                        switchTrasfusione.setChecked(false);
                                        LLSwitchTrasfusione.setVisibility(View.GONE);
                                    }
                                }
                            });
                }
                else {

                }
            }
        });*/

        TextView User = findViewById(R.id.TXVUser);
        User.setText(getString(R.string.impostazioni_label2) + Utente);

        findViewById(R.id.buttonDisconnetti).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               //currentUser = null;
               mGoogleSignInClient.signOut();
               mAuth.signOut();
               stopService();

                Intent nextScreen = new Intent(getApplicationContext(), LauncherActivity.class);
                nextScreen.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(nextScreen);
                //ActivityCompat.finishAffinity(this);
            }
        });

    }

    public void stopService() {
        Intent serviceIntent = new Intent(this, ForegroundService.class);
        stopService(serviceIntent);
        setServiceOn(false);
    }
}