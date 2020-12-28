package com.example.talapp.Launcher;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.example.talapp.R;
import com.example.talapp.Utils.Util;

import static com.example.talapp.MainActivity.mAuth;


public class PasswordDimenticataFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View root =  inflater.inflate(R.layout.fragment_password_dimenticata, container, false);

        //Bottone di invio mail
        root.findViewById(R.id.buttonInviaMail).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText ETEmail = root.findViewById(R.id.editTextTextEmailAddress_forgot);
                String email = ETEmail.getText().toString();
                if(Util.isValidEmail(email)){
                    mAuth.sendPasswordResetEmail(email);
                    Toast.makeText(getContext(), "Abbiamo inviato una mail a "+ email, Toast.LENGTH_LONG).show();
                }
                else{
                    Toast.makeText(getContext(), "Email non valida", Toast.LENGTH_LONG).show();
                }
            }
        });
        return root;
    }
}