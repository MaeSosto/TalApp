package com.example.talapp.Launcher;

import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavDeepLinkBuilder;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.talapp.R;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import static com.example.talapp.MainActivity.KEY_GOOGLE;
import static com.example.talapp.MainActivity.UserServiceAccount;
import static com.example.talapp.MainActivity.KEY_FIREBASE;
import static com.example.talapp.MainActivity.currentUser;
import static com.example.talapp.MainActivity.account;
import static com.example.talapp.MainActivity.mGoogleSignInClient;


public class LauncherFragment extends Fragment {

    private static final int RC_SIGN_IN = 007;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_launcher, container, false);

        if(currentUser != null){ //Sono loggato con Email/password
            UserServiceAccount = KEY_FIREBASE;
            Navigation.findNavController(getActivity(), R.id.launcherFragment).navigate(R.id.action_launcherFragment_to_homeFragment);
        }
        else if(account != null){ //Sono loggato con Google
            UserServiceAccount = KEY_GOOGLE;
            Navigation.findNavController(getActivity(), R.id.launcherFragment).navigate(R.id.action_launcherFragment_to_homeFragment);
        }

        //Bottone accedi
        root.findViewById(R.id.buttonLogIn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Navigation.findNavController(view).navigate(R.id.action_launcherFragment_to_accediFragment);
            }
        });

        //Bottone registrati
        root.findViewById(R.id.buttonSignUp).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(v).navigate(R.id.action_launcherFragment_to_registratiFragment);
            }
        });

        //Bottone Google
        root.findViewById(R.id.sign_in_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v.getId() == R.id.sign_in_button) {
                    Intent signInIntent = mGoogleSignInClient.getSignInIntent();
                    startActivityForResult(signInIntent, RC_SIGN_IN);
                }
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
        }
    }

}