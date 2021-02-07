package com.example.talapp.Launcher;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.example.talapp.HomeActivity;
import com.example.talapp.Notification.ForegroundService;
import com.example.talapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;

import static android.content.ContentValues.TAG;
import static com.example.talapp.Utils.Util.Utente;
import static com.example.talapp.Utils.Util.isValidEmail;
import static com.example.talapp.Utils.Util.isValidPassword;
import static com.example.talapp.Utils.Util.mAuth;
import static com.example.talapp.Utils.Util.mFirebaseUser;

public class RegistratiFragment extends Fragment {
    private String Error = null;
    private static final int RC_SIGN_IN = 007;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root =  inflater.inflate(R.layout.fragment_registrati, container, false);

        EditText ETEmail = root.findViewById(R.id.editTextTextEmailAddress_forgot);
        EditText ETPassword1 = root.findViewById(R.id.editTextTextPassword1);
        EditText ETPassword2 = root.findViewById(R.id.editTextTextPassword2);

        root.findViewById(R.id.buttonAccedi).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(dataValidation(ETEmail.getText().toString(), ETPassword1.getText().toString(), ETPassword2.getText().toString())) {

                    newUser(ETEmail.getText().toString(), ETPassword1.getText().toString());
                }
                else{
                    Log.i("Error", Error);
                    Toast.makeText(getContext(), Error, Toast.LENGTH_LONG).show();
                }
            }
        });

        return root;
    }

    private void newUser(String email, String password){
        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "createUserWithEmail:success");

                            mFirebaseUser = mAuth.getCurrentUser();
                            Utente = mFirebaseUser.getEmail();

                            Intent nextScreen = new Intent(getContext(), HomeActivity.class);
                            nextScreen.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(nextScreen);
                            ActivityCompat.finishAffinity(getActivity());
                            //Faccio partire il ForegroundService
                            Intent serviceIntent = new Intent(getContext(), ForegroundService.class);
                            ContextCompat.startForegroundService(getContext(), serviceIntent);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(getContext(), "Registrazione fallita",
                                    Toast.LENGTH_SHORT).show();

                        }
                    }
                });
    }

    private boolean dataValidation(String email, String pass1, String pass2){
        if(!isValidEmail(email)){
            Error = "Inserisci una email valida";
            return false;
        }
        return passwordValidation(pass1, pass2);
    }

    private boolean passwordValidation(String pass1, String pass2){
        if(pass1.length()<8 &&!isValidPassword(pass1) && pass2.length()<8 &&!isValidPassword(pass2)){
            Error = "La password deve contenere almeno 8 caratteri, un numero e un carattere speciale";
            return false;
        }
        if(!pass1.matches(pass2)){
            Error = "Le password sono diverse";
            return false;
        }
        return true;
    }


}