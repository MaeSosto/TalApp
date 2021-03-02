package com.MartinaSosto.talapp.Launcher;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.MartinaSosto.talapp.HomeActivity;
import com.MartinaSosto.talapp.Notification.ForegroundService;
import com.MartinaSosto.talapp.R;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.GoogleAuthProvider;

import static android.content.ContentValues.TAG;

import static com.MartinaSosto.talapp.Utils.Util.Utente;
import static com.MartinaSosto.talapp.Utils.Util.mAuth;
import static com.MartinaSosto.talapp.Utils.Util.mFirebaseUser;
import static com.MartinaSosto.talapp.Utils.Util.mGoogleSignInClient;

public class LauncherFragment extends Fragment{

    private int RC_SIGN_IN = 1;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_launcher, container, false);

        //Bottone accedi con Firebase
        root.findViewById(R.id.buttonLogIn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Navigation.findNavController(view).navigate(R.id.action_launcherFragment_to_accediFragment);
            }
        });

        //Bottone registrati con Firebase
        root.findViewById(R.id.buttonSignUp).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(v).navigate(R.id.action_launcherFragment_to_registratiFragment);
            }
        });

        //Bottone accedi con Google
        root.findViewById(R.id.sign_in_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent signInIntent = mGoogleSignInClient.getSignInIntent();
                startActivityForResult(signInIntent, RC_SIGN_IN);

            }
        });

        return root;
    }

   @Override
   public void onActivityResult(int requestCode, int resultCode, Intent data) {
       super.onActivityResult(requestCode, resultCode, data);

       // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
       if (requestCode == RC_SIGN_IN) {
           Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
           try {
               // Google Sign In was successful, authenticate with Firebase
               GoogleSignInAccount account = task.getResult(ApiException.class);
               Log.d(TAG, "firebaseAuthWithGoogle:" + account.getId());
               firebaseAuthWithGoogle(account.getIdToken());
           } catch (ApiException e) {
               // Google Sign In failed, update UI appropriately
               Log.d(TAG, "Google sign in failed", e);
               Toast.makeText(getContext(), "Login fallito", Toast.LENGTH_LONG).show();
           }
       }
   }

   private void firebaseAuthWithGoogle(String idToken) {
       AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
       mAuth.signInWithCredential(credential)
               .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                   @Override
                   public void onComplete(@NonNull Task<AuthResult> task) {
                       if (task.isSuccessful()) {
                           // Sign in success, update UI with the signed-in user's information
                           Log.d(TAG, "signInWithCredential:success");
                           mFirebaseUser = mAuth.getCurrentUser();
                           Utente = mFirebaseUser.getEmail();
                           openHome();
                       } else {
                           // If sign in fails, display a message to the user.
                           Log.w(TAG, "signInWithCredential:failure", task.getException());
                           //Snackbar.make(mBinding., "Authentication Failed.", Snackbar.LENGTH_SHORT).show();
                       }
                   }
               });
   }

    private void openHome(){
        Intent nextScreen = new Intent(getContext(), HomeActivity.class);
        nextScreen.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(nextScreen);
        ActivityCompat.finishAffinity(getActivity());

        //Faccio partire il ForegroundService
        Intent serviceIntent = new Intent(getContext(), ForegroundService.class);
        ContextCompat.startForegroundService(getContext(), serviceIntent);
    }
}

