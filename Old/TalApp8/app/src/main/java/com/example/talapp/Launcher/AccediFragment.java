package com.example.talapp.Launcher;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.example.talapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;

import static com.example.talapp.MainActivity.mAuth;
import static com.example.talapp.MainActivity.currentUser;

public class AccediFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_accedi, container, false);

        //Collagamento a password dimenticata
        SpannableString ss = new SpannableString(getString(R.string.password_dimenticata_label1));
        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(@NonNull View widget) {
                //Log.i("Password Dimenticata", "CLICK");
                Navigation.findNavController(widget).navigate(R.id.action_accediFragment_to_passwordDimenticataFragment);
            }
        };
        ss.setSpan(clickableSpan, 0, 24, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        TextView textView = root.findViewById(R.id.TXVPasswordDimenticata);
        textView.setText(ss);
        textView.setMovementMethod(LinkMovementMethod.getInstance());


        //Bottone di accesso
        root.findViewById(R.id.buttonAccedi).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText ETemail = root.findViewById(R.id.editTextTextEmailAddress_forgot);
                EditText ETPassword = root.findViewById(R.id.editTextTextPassword);
                logIn(ETemail.getText().toString(), ETPassword.getText().toString());
            }
        });

        return root;
    }

    private void logIn(String email, String password){
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            currentUser = mAuth.getCurrentUser();
                            Navigation.findNavController(getActivity(), R.id.accediFragment).navigate(R.id.action_accediFragment_to_homeFragment);
                        } else {
                            Toast.makeText(getContext(), "Accesso fallito", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}

