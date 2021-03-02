package com.MartinaSosto.talapp.Launcher;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.MartinaSosto.talapp.R;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import static com.MartinaSosto.talapp.Utils.Util.checkEmail;
import static com.MartinaSosto.talapp.Utils.Util.mAuth;
import static com.MartinaSosto.talapp.Utils.Util.removeError;


public class PasswordDimenticataFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View root =  inflater.inflate(R.layout.fragment_password_dimenticata, container, false);

        TextInputEditText ETEmail = root.findViewById(R.id.editTextTextEmailAddress_forgot);
        TextInputLayout TILEmail = root.findViewById(R.id.TILEmail);
        removeError(ETEmail, TILEmail);

        //Bottone invia mail
        root.findViewById(R.id.buttonInviaMail).setOnClickListener(v -> {
            if(checkEmail(ETEmail, TILEmail, "Inserisci una mail valida")){
                mAuth.sendPasswordResetEmail(ETEmail.getText().toString()).addOnCompleteListener(task -> {
                    if(task.isSuccessful()){
                        Toast.makeText(getContext(), "Abbiamo inviato una mail a "+ ETEmail.getText().toString(), Toast.LENGTH_LONG).show();
                    }
                    else{
                        Toast.makeText(getContext(), "Non siamo riusciti a inviare una mail", Toast.LENGTH_LONG).show();
                    }
                });
            }
        });
        return root;
    }
}