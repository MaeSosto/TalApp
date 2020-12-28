package com.example.talapp.Utils;

import android.text.TextUtils;
import android.util.Patterns;

import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Util {
    public String KEY_TRASFUSIONE_DATA = "data";
    public String KEY_TRASFUSIONE_UNITA = "unità";
    public String KEY_TRASFUSIONE_HB = "Hb";

    public static boolean isValidPassword(final String password) {
        Pattern pattern;
        Matcher matcher;
        final String PASSWORD_PATTERN = "^(?=.*[0-9])(?=.*[A-Z])(?=.*[@#$%^&+=!])(?=\\S+$).{4,}$";
        pattern = Pattern.compile(PASSWORD_PATTERN);
        matcher = pattern.matcher(password);
        return matcher.matches();
    }

    public static boolean isValidEmail(CharSequence target) {
        return (!TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches());
    }
}
