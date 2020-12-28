package com.example.talapp.Launcher;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.IntentCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.talapp.Home.HomeActivity;
import com.example.talapp.MainActivity;
import com.example.talapp.R;
import com.google.android.material.button.MaterialButton;


public class AccediFragment extends Fragment {


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_accedi, container, false);

        SpannableString ss = new SpannableString(getString(R.string.password_dimenticata_label1));
        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(@NonNull View widget) {
                Log.i("Password Dimenticata", "CLICK");
                Navigation.findNavController(widget).navigate(R.id.action_accediFragment_to_passwordDimenticataFragment);
            }
        };
        ss.setSpan(clickableSpan, 0, 24, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        TextView textView = root.findViewById(R.id.TXVPasswordDimenticata);
        textView.setText(ss);
        textView.setMovementMethod(LinkMovementMethod.getInstance());

        root.findViewById(R.id.buttonRegistrati).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent nextScreen = new Intent(getContext(), HomeActivity.class);
                nextScreen.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(nextScreen);
                ActivityCompat.finishAffinity(getActivity());
            }
        });

        return root;
    }
}