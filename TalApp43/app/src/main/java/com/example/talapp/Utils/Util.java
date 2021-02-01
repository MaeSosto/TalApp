package com.example.talapp.Utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;

import androidx.annotation.NonNull;
import androidx.navigation.NavController;

import com.github.mikephil.charting.components.Description;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;

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
    private static SimpleDateFormat SDF = new SimpleDateFormat("dd/MM/yyyy HH:mm");
    private static SimpleDateFormat SDFHour = new SimpleDateFormat("HH:mm");
    private static SimpleDateFormat SDFDate = new SimpleDateFormat("dd/MM/yy");
    public static FirebaseFirestore db;
    public static FirebaseFirestoreSettings FFS;
    public static final String KEY_UTENTI = "Users";

    //Valori colonne
    public final static String KEY_DATA = "DATA";
    public final static String KEY_DATA_FINE = "DATA FINE";
    public static final String KEY_NOTE = "NOTE";
    public static final String KEY_TRASFUSIONE = "TRASFUSIONE";
    public final static String KEY_UNITA = "UNITA";
    public final static String KEY_HB = "HB";
    public static final String KEY_ESAME = "ESAME";
    public static final String KEY_TERAPIE = "TERAPIE";
    public static final String KEY_NOME = "NOME";
    public static final String KEY_PERIODICITA = "PERIODICITA";
    public static final String KEY_TIPO = "TIPO";
    public static final String KEY_DIGIUNO = "DIGIUNO";
    public static final String KEY_ATTIVAZIONE = "ATTIVAZIONE24H";
    public static final String KEY_RICORDA = "RICORDA";
    public static final String KEY_ESITO = "ESITO";
    public static final String KEY_REFERTO = "REFERTO";
    public static final String KEY_DOSE = "DOSE";
    public static final String ESAMI_STRUMENTALI = "Strumentale";
    public static final String ESAMI_LABORATORIO = "Di laboratorio";
    public static final String KEY_SVEGLIE = "SVEGLIE";
    public static final String KEY_ORARIO = "ORARIO";
    public static final String KEY_NOTIFICHE = "NOTIFICHE";
    public static final String KEY_SETTIMANA = "SETTIMANA";
    public final static String IMPOSTAZIONI_TRASFUSIONI = "TRASFUSIONE";
    public final static String IMPOSTAZIONI_TRASFUSIONI_ORARIO = "TRASFUSIONE ORARIO";
    public final static String IMPOSTAZIONI_ESAMI = "ESAMI";
    public final static String IMPOSTAZIONI_ESAMI_ORARIO = "ESAMI ORARIO";
    public final static String IMPOSTAZIONI_ESAMI_PERIODICI = "ESAMI NON PROGRAMMATI";
    public final static String IMPOSTAZIONI_ESAMI_PERIODICI_ORARIO = "ESAMI STRUMENTALI NON PROGRAMMATI ORARIO";
    public final static String IMPOSTAZIONI_ESAMI_DIGIUNO = "ESAMI DIGIUNO";
    public final static String IMPOSTAZIONI_ESAMI_ATTIVAZIONE_ANTICIPATA = "ESAMI ATTIVAZIONE ANTICIPATA";

    //Action per le notifiche
    public static final String FOREGROUND_SERVICE_CHANNEL = "Foreground Service Channel";
    public static final String NOTIFICATION_CHANNEL = "Notification Channel";
    public final static int ID_TRASFUSIONI = 2;
    public final static int ID_ESAME = 3;
    public final static int ID_ESAMI_PERIODICI = 4;

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

    //Converte da Timestamp a Date
    public static Date TimestampToDate(Timestamp tmp){
        return tmp.toDate();
    }

    //Converte da Timestamp alla stringa della data corrispondente
    public static String TimestampToStringData(Timestamp tmp){
        return DateToString(TimestampToDate(tmp));
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
    public static Date StringToDate(String string){
        Date date = null;
        try {
            date = SDFDate.parse(string);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }
    //Converte una Date in una String che mostra solo l'orario
    public static String DateToOrario(Date date){
        return date.getHours()+":"+ date.getMinutes();
    }

    //Converte un Timestamp in una String che mostra solo l'orario
    public static String TimestampToOrario(Timestamp timestamp){
        Date date = TimestampToDate(timestamp);
        return date.getHours()+":"+ date.getMinutes();
    }

    public static Calendar ConverterStringToCalendar(String giorno, String ora) throws ParseException {
        Calendar cal = Calendar.getInstance();
        Date date = null;
        date = SDF.parse(giorno + " " + ora);
        cal.setTime(date);
        return cal;
    }

    //Converte dalla String ora a Date, il giorno/mese/anno sono fasulli
    public static Date StringToDateOrario(String ora) throws ParseException {
        //Log.d("ConverterStringToDate", ora);
        return SDFHour.parse(ora);
    }

    //Restituisce il calendar dell' ultimo minuto della giornata di domani
    public static Calendar setDomani(){
        //Calcolo a che ora finisce domani
        Calendar FineGiorno = Calendar.getInstance();
        FineGiorno.add(Calendar.DATE, 1);
        FineGiorno.set(Calendar.HOUR_OF_DAY, 24);
        FineGiorno.set(Calendar.MINUTE, 59);
        FineGiorno.set(Calendar.SECOND, 59);
        FineGiorno.set(Calendar.MILLISECOND, 59);
        return FineGiorno;
    }

    //Restituisce il primo minuto del giorno passato in input
    public static Calendar getPrimoMinuto(Calendar giorno){
        Calendar inizio= Calendar.getInstance();
        inizio.setTime(giorno.getTime());
        inizio.set(Calendar.HOUR_OF_DAY, 0);
        inizio.set(Calendar.MINUTE, 0);
        inizio.set(Calendar.SECOND, 0);
        inizio.set(Calendar.MILLISECOND, 0);
        return inizio;
    }

    //Restituisce l'ultimo minuto del giorno passato in input
    public static Calendar getUltimoMinuto(Calendar giorno){
        Calendar fine= Calendar.getInstance();
        fine.setTime(giorno.getTime());
        fine.set(Calendar.HOUR_OF_DAY, 24);
        fine.set(Calendar.MINUTE, 59);
        fine.set(Calendar.SECOND, 59);
        fine.set(Calendar.MILLISECOND, 59);
        return fine;
    }

    //Restituisce l'ultimo minuto del mese
    public static Calendar getUltimoMinutoDelMese(Calendar pagina){
        int ultimo = getPrimoMinuto(pagina).getActualMaximum(Calendar.DAY_OF_MONTH);
        Calendar UltimoMinutoDelMese = pagina;
        UltimoMinutoDelMese.set(Calendar.DAY_OF_MONTH, ultimo);
        UltimoMinutoDelMese.set(Calendar.HOUR_OF_DAY, 24);
        UltimoMinutoDelMese.set(Calendar.MINUTE, 59);
        UltimoMinutoDelMese.set(Calendar.SECOND, 59);
        UltimoMinutoDelMese.set(Calendar.MILLISECOND, 59);
        return UltimoMinutoDelMese;
    }

    //Restituisce la data in base alla periodicità di un esame
    public static Date setPeriodicita(Calendar date, String s){
        Calendar tmp = Calendar.getInstance();
        tmp.setTime(date.getTime());
        switch (s){
            case "Settimanale":
                tmp.add(Calendar.DATE, 7);
                break;
            case "Mensile":
                tmp.add(Calendar.MONTH, 1);
                break;
            case "Trimestrale":
                tmp.add(Calendar.MONTH, 3);
                break;
            case "Semestrale":
                tmp.add(Calendar.MONTH, 6);
                break;
            case "Annuale":
                tmp.add(Calendar.YEAR, 1);
                break;
        }
        return tmp.getTime();
    }

    //SETTA LA DESCRIZIONE NULLA
    public static Description getDescription(){
        Description description = new Description();
        description.setText("");
        return description;
    }

    public static class Evento{
        boolean trasfusione = false;
        boolean esame = false;
        boolean terapia = false;

        public Evento(boolean trasfusione, boolean esame, boolean terapia) {
            this.trasfusione = this.trasfusione | trasfusione;
            this.esame = this.esame | esame;
            this.terapia = this.terapia | terapia;
        }

        public boolean isTrasfusione() {
            return trasfusione;
        }

        public void setTrasfusione(boolean trasfusione) {
            this.trasfusione = trasfusione;
        }

        public boolean isEsame() {
            return esame;
        }

        public void setEsame(boolean esame) {
            this.esame = esame;
        }

        public boolean isTerapia() {
            return terapia;
        }

        public void setTerapia(boolean terapia) {
            this.terapia = terapia;
        }
    }

}
