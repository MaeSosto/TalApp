package com.example.talapp.Launcher;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.example.talapp.R;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;

import static android.content.ContentValues.TAG;
import static com.example.talapp.MainActivity.account;
import static com.example.talapp.MainActivity.currentUser;
import static com.example.talapp.MainActivity.mAuth;
import static com.example.talapp.MainActivity.mGoogleSignInClient;
import static com.example.talapp.Utils.Util.isValidEmail;
import static com.example.talapp.Utils.Util.isValidPassword;

public class RegistratiFragment extends Fragment {
    private String Error = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View root =  inflater.inflate(R.layout.fragment_registrati, container, false);

        //Bottone di accesso
        root.findViewById(R.id.buttonAccedi).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText ETEmail = root.findViewById(R.id.editTextTextEmailAddress_forgot);
                EditText ETPassword1 = root.findViewById(R.id.editTextTextPassword1);
                EditText ETPassword2 = root.findViewById(R.id.editTextTextPassword2);
                if(dataValidation(ETEmail.getText().toString(), ETPassword1.getText().toString(), ETPassword2.getText().toString())) {

                    mAuth.createUserWithEmailAndPassword(ETEmail.getText().toString(), ETPassword1.getText().toString()).addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                //Log.d(TAG, "Account registrato");
                                currentUser = mAuth.getCurrentUser();
                                Navigation.findNavController(getActivity(), R.id.registratiFragment).navigate(R.id.action_registratiFragment_to_homeFragment);

                            } else {
                                // If sign in fails, display a message to the user.
                                Log.w(TAG, "createUserWithEmail:failure", task.getException());
                                Toast.makeText(getContext(), "Registrazione fallita",
                                        Toast.LENGTH_SHORT).show();

                            }
                        }
                    });
                }
                else{
                    Log.i("Error", Error);
                    Toast.makeText(getContext(), Error, Toast.LENGTH_LONG).show();
                }
            }
        });
        return root;
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