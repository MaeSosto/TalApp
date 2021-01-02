package com.example.talapp.Utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;

import androidx.annotation.NonNull;
import androidx.navigation.NavController;

import com.example.talapp.Trasfusioni.TrasfusioniViewModel;
import com.github.mikephil.charting.components.Description;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Util {

    //Valori del login
    public static FirebaseAuth mAuth;
    public static GoogleSignInClient mGoogleSignInClient;
    public static FirebaseUser mFirebaseUser;
    public static GoogleSignInAccount mGoogleUser;
    public static NavController navController;
    public static String Utente;

    //Valori generali
    private static SimpleDateFormat SDF = new SimpleDateFormat("dd/MM/yyyy hh:mm");
    private static SimpleDateFormat SDFDate = new SimpleDateFormat("dd/MM/yy");
    public static FirebaseFirestore db;

    //Valori delle trasfusioni
    public static TrasfusioniViewModel trasfusioniViewModel;
    public static final String KEY_UTENTI = "Users";
    public static final String KEY_TRASFUSIONE = "Trasfusione";
    public final static String KEY_TRASFUSIONE_UNITA = "unita";
    public final static String KEY_TRASFUSIONE_DATA = "data";
    public final static String KEY_TRASFUSIONE_HB = "hb";
    public final static String KEY_TRASFUSIONE_NOTE = "note";

    public static boolean isConnectedToInternet(@NonNull Context _context) {
        ConnectivityManager cm = (ConnectivityManager)_context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm == null)
            return false;
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
    }

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

    public static Date LongToDate(Long value) {
        return value == null ? null : new Date(value);
    }
    public static Long DateToLong(Date date) {
        return date == null ? null : date.getTime();
    }
    public static String DateToString(Date date){
        return SDFDate.format(date);
    }
    public static String DateToOrario(Date date){
        return date.getHours()+":"+ date.getMinutes();
    }
    public static String LongToString(Long l){
        return DateToString(LongToDate(l));
    }
    public static Calendar ConverterStringToCalendar(String giorno, String ora) throws ParseException {
        Log.i("Devo convertire", giorno + " alle "+ ora);
        Calendar cal = Calendar.getInstance();
        Date date = null;
        date = SDF.parse(giorno + " " + ora);
        cal.setTime(date);
        Log.i("Devo convertire", String.valueOf(cal));
        return cal;
    }

    //SETTA LA DESCRIZIONE NULLA
    public static Description getDescription(){
        Description description = new Description();
        description.setText("");
        return description;
    }

}
