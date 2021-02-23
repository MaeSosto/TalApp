package com.example.talapp.Launcher;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.talapp.HomeActivity;
import com.example.talapp.Notification.ForegroundService;
import com.example.talapp.R;
import com.example.talapp.Utils.Util;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import static android.content.ContentValues.TAG;
import static com.example.talapp.Utils.Util.Utente;
import static com.example.talapp.Utils.Util.checkEmail;
import static com.example.talapp.Utils.Util.checkPassword;
import static com.example.talapp.Utils.Util.checkSamePassword;
import static com.example.talapp.Utils.Util.mAuth;
import static com.example.talapp.Utils.Util.mFirebaseUser;
import static com.example.talapp.Utils.Util.removeError;
import static com.example.talapp.Utils.Util.setListenersPassword;

public class RegistratiFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root =  inflater.inflate(R.layout.fragment_registrati, container, false);

        //Email
        TextInputEditText ETEmail = root.findViewById(R.id.editTextTextEmailAddress_forgot);
        TextInputLayout TILRegistratiEmail = root.findViewById(R.id.TILRegistratiEmail);
        removeError(ETEmail, TILRegistratiEmail);

        //Password
        TextInputEditText ETPassword1 = root.findViewById(R.id.editTextTextPassword1);
        TextInputLayout TILPassword1 = root.findViewById(R.id.TILPassword1);
        removeError(ETPassword1, TILPassword1);
        TextInputEditText ETPassword2 = root.findViewById(R.id.editTextTextPassword2);
        TextInputLayout TILPassword2 = root.findViewById(R.id.TILPassword2);
        removeError(ETPassword2, TILPassword2);

        //Bottone regitrati
        root.findViewById(R.id.buttonAccedi).setOnClickListener(view -> {
            if(checkEmail(ETEmail, TILRegistratiEmail, "Inserisci una mail valida") && checkSamePassword(ETPassword1, ETPassword2, TILPassword2, "Le due password non coincidono") && checkPassword(ETPassword1, TILPassword1, "La password deve contenere almeno 8 caratteri, un numero e un carattere speciale") && checkPassword(ETPassword2, TILPassword2, "La password deve contenere almeno 8 caratteri, un numero e un carattere speciale")) {
                newUser(ETEmail.getText().toString(), ETPassword1.getText().toString());
            }
        });

        return root;
    }

    private void newUser(String email, String password){
        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(getActivity(), task -> {
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
        });
    }
}