package com.MartinaSosto.talapp.Launcher;

import android.content.Intent;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.MartinaSosto.talapp.HomeActivity;
import com.MartinaSosto.talapp.Notification.ForegroundService;
import com.MartinaSosto.talapp.R;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textview.MaterialTextView;

import static android.content.ContentValues.TAG;
import static com.MartinaSosto.talapp.Utils.Util.Utente;
import static com.MartinaSosto.talapp.Utils.Util.mAuth;
import static com.MartinaSosto.talapp.Utils.Util.mFirebaseUser;


public class AccediFragment extends Fragment {


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_accedi, container, false);

        //Link password dimenticata
        SpannableString ss = new SpannableString(getString(R.string.password_dimenticata_label1));
        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(@NonNull View widget) {
                //Log.i("Password Dimenticata", "CLICK");
                Navigation.findNavController(widget).navigate(R.id.action_accediFragment_to_passwordDimenticataFragment);
            }
        };
        ss.setSpan(clickableSpan, 0, 24, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        MaterialTextView textView = root.findViewById(R.id.TXVPasswordDimenticata);
        textView.setText(ss);
        textView.setMovementMethod(LinkMovementMethod.getInstance());

        //Bottone per accedere
        root.findViewById(R.id.buttonAccedi).setOnClickListener(view -> {
            TextInputEditText ETemail = root.findViewById(R.id.editTextTextEmailAddress_forgot);
            TextInputEditText ETPassword = root.findViewById(R.id.editTextTextPassword);
            if (ETemail.getText().toString().isEmpty() || ETPassword.getText().toString().isEmpty()) {
                Toast.makeText(getContext(), "Inserici delle credenziali valide", Toast.LENGTH_SHORT).show();
            } else {
                logIn(ETemail.getText().toString(), ETPassword.getText().toString());
            }
        });

        return root;
    }


    private void logIn(String email, String password){
        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(getActivity(), task -> {
            if (task.isSuccessful()) {
                // Sign in success, update UI with the signed-in user's information
                Log.d(TAG, "signInWithEmail:success");
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
                Log.w(TAG, "signInWithEmail:failure", task.getException());
                Toast.makeText(getContext(), "Autenticazione fallita",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }
}

