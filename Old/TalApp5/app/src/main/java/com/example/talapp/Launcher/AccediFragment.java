package com.example.talapp.Launcher;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.talapp.Home.HomeActivity;
import com.example.talapp.R;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseUser;

import static android.content.ContentValues.TAG;
import static com.example.talapp.Launcher.LauncherActivity.mAuth;
import static com.example.talapp.Launcher.LauncherActivity.mGoogleSignInClient;
import static com.example.talapp.Launcher.LauncherActivity.currentUser;
import static com.example.talapp.Launcher.LauncherActivity.account;


public class AccediFragment extends Fragment {
    private static final int RC_SIGN_IN = 007;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_accedi, container, false);

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

        root.findViewById(R.id.sign_in_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v.getId() == R.id.sign_in_button) {
                    Intent signInIntent = mGoogleSignInClient.getSignInIntent();
                    startActivityForResult(signInIntent, RC_SIGN_IN);
                }
            }
        });

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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            account = GoogleSignIn.getLastSignedInAccount(getContext());
            openHome();
        }
    }

    private void logIn(String email, String password){
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithEmail:success");
                            currentUser = mAuth.getCurrentUser();
                            openHome();

                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                            Toast.makeText(getContext(), "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();

                        }

                    }
                });
    }

    private void openHome(){
        Intent nextScreen = new Intent(getContext(), HomeActivity.class);
        nextScreen.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(nextScreen);
        ActivityCompat.finishAffinity(getActivity());
    }
}

