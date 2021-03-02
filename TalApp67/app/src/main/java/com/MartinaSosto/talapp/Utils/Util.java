package com.MartinaSosto.talapp.Utils;

import android.content.ContentResolver;
import android.content.Context;
import android.net.Uri;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Patterns;
import android.webkit.MimeTypeMap;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;

import androidx.navigation.NavController;

import com.MartinaSosto.talapp.R;
import com.github.mikephil.charting.components.Description;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class Util {

    //Valori del login
    public static FirebaseAuth mAuth;
    public static GoogleSignInClient mGoogleSignInClient;
    public static FirebaseUser mFirebaseUser;
    public static GoogleSignInAccount mGoogleUser;
    public static NavController navController;
    public static String Utente;

    //Valori generali
    private static SimpleDateFormat SDF = new SimpleDateFormat("dd/MM/yy HH:mm");
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
    public static final String KEY_ANALISI = "Analisi";
    public static final String KEY_DIGIUNO = "DIGIUNO";
    public static final String KEY_ATTIVAZIONE = "ATTIVAZIONE24H";
    public static final String KEY_RICORDA = "RICORDA";
    public static final String KEY_ESITO = "ESITO";
    public static final String KEY_REFERTO = "REFERTO";
    public static final String KEY_DOSE = "DOSE";
    public static final String KEY_FARMACO = "FARMACO";
    public static final String ESAMI_STRUMENTALI = "Strumentale";
    public static final String ESAMI_LABORATORIO = "Di laboratorio";
    public static final String KEY_SVEGLIE = "SVEGLIE";
    public static final String KEY_ORARIO = "ORARIO";
    public static final String KEY_NOTIFICHE = "NOTIFICHE";
    public static final String KEY_SETTIMANA = "SETTIMANA";
    public static final String KEY_ALLEGATI = "ALLEGATI";
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
    public static final String TRASFUSIONI_CHANNEL = "Trasfusioni Channel";
    public static final String ESAMI_CHANNEL = "Esami Channel";
    public static final String ESAMI_PERIODICI_CHANNEL = "Esami periodici Channel";
    public static final String SVEGLIE_CHANNEL = "Sveglie Channel";
    public final static int ID_FOREGROUND = 1;
    public final static int ID_TRASFUSIONI = 2;
    public final static int ID_ESAME = 3;
    public final static int ID_ESAMI_PERIODICI = 4;
    public final static int ID_SVEGLIE = 5;


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

    //Converte la stringa di una data e di un orario in un dato di tipo calendar
    public static Date ConverterStringToCalendar(String giorno, String ora) throws ParseException {
        //Calendar cal = Calendar.getInstance();
        //cal.setTime(date);
        return SDF.parse(giorno + " " + ora);
    }

    //Data una data restituisce il numero del giorno corrispondente: 1 è domenica e 7 è sabato
    public static int getNumeroGiorno(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return cal.get(Calendar.DAY_OF_WEEK);
    }

    //Confronta il numero del giorno attuale con il vettore dei booleani di quando deve suonare la sveglia, se oggi deve suonare allora restituisce true, altrimenti false
    public static boolean getNumeroGiornoDaSettimana(List<Boolean> settimana){
        int oggi = getNumeroGiorno(Calendar.getInstance().getTime());
        //Log.d("NUMEROGIORNOSETTIMANA", String.valueOf(oggi));
        //Log.d("NUMEROGIORNOSETTIMANA", String.valueOf((oggi+5) % 7));
        return settimana.get((oggi+5) % 7);
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
    public static Date setPeriodicita(Date date, String s){
        Calendar tmp = Calendar.getInstance();
        tmp.setTime(date);
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


    //Restituisce true se la password è valida, altrimenti false
    private static boolean isValidPassword (String s){
        List<String> list = new ArrayList<>();
        List<String> list1 = new ArrayList<>();
        List<String> list2 = new ArrayList<>();
        List<String> list3 = new ArrayList<>();
        list.add("!");
        list.add("?");
        list.add("$");
        list.add("?");
        list.add("%");
        list.add("^");
        list.add("&");
        list.add("*");
        list.add("(");
        list.add(")");
        list.add("_");
        list.add("-");
        list.add("+");
        list.add("=");
        list.add("{");
        list.add("[");
        list.add("}");
        list.add("]");
        list.add(":");
        list.add(";");
        list.add("@");
        list.add("#");
        list.add("|");
        list.add("\\");
        list.add("<");
        list.add(",");
        list.add(">");
        list.add(".");
        list.add("?");
        list.add("/");
        list1.add("0");
        list1.add("1");
        list1.add("2");
        list1.add("3");
        list1.add("4");
        list1.add("5");
        list1.add("6");
        list1.add("7");
        list1.add("8");
        list1.add("9");
        list2.add("a");
        list2.add("b");
        list2.add("c");
        list2.add("d");
        list2.add("e");
        list2.add("f");
        list2.add("g");
        list2.add("h");
        list2.add("i");
        list2.add("j");
        list2.add("k");
        list2.add("l");
        list2.add("m");
        list2.add("n");
        list2.add("o");
        list2.add("p");
        list2.add("q");
        list2.add("r");
        list2.add("s");
        list2.add("t");
        list2.add("u");
        list2.add("v");
        list2.add("w");
        list2.add("x");
        list2.add("y");
        list2.add("z");
        list3.add("A");
        list3.add("B");
        list3.add("C");
        list3.add("D");
        list3.add("E");
        list3.add("F");
        list3.add("G");
        list3.add("H");
        list3.add("I");
        list3.add("J");
        list3.add("K");
        list3.add("L");
        list3.add("M");
        list3.add("N");
        list3.add("O");
        list3.add("P");
        list3.add("Q");
        list3.add("R");
        list3.add("S");
        list3.add("T");
        list3.add("U");
        list3.add("V");
        list3.add("W");
        list3.add("X");
        list3.add("Y");
        list3.add("Z");
        boolean caratteriSpeciali = false, numeri = false, lettereMin = false, lettereMa = false;
        for(int i = 0; i< list.size(); i++){caratteriSpeciali = caratteriSpeciali || s.contains(String.valueOf(list.get(i)));}
        for(int i = 0; i< list1.size(); i++){numeri = numeri || s.contains(String.valueOf(list1.get(i)));}
        for(int i = 0; i< list2.size(); i++){lettereMin = lettereMin || s.contains(String.valueOf(list2.get(i)));}
        for(int i = 0; i< list3.size(); i++){lettereMa = lettereMa || s.contains(String.valueOf(list3.get(i)));}
        return caratteriSpeciali && numeri && lettereMin && lettereMa && s.length()>= 8;
    }

    public static boolean isValidEmail(CharSequence target) {
        return (!TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches());
    }
    //Controlla che le due password non siano vuote e coincidano
    public static boolean checkSamePassword(TextInputEditText textInputEditText, TextInputEditText textInputEditText2, TextInputLayout textInputLayout, String string){
        if(textInputEditText.getText().toString().isEmpty() || textInputEditText2.getText().toString().isEmpty() || textInputEditText.getText().toString().compareTo(textInputEditText2.getText().toString()) != 0){
            textInputLayout.setErrorEnabled(true);
            textInputLayout.setError(string);
            return false;
        }
        else {
            textInputLayout.setErrorEnabled(false);
            return true;
        }
    }

    //Controlla che sia una password valida
    public static boolean checkPassword(TextInputEditText textInputEditText, TextInputLayout textInputLayout, String string){
        if(textInputEditText.getText().toString().isEmpty() || !isValidPassword(textInputEditText.getText().toString())){
            textInputLayout.setErrorEnabled(true);
            textInputLayout.setError(string);
            return false;
        }
        else {
            textInputLayout.setErrorEnabled(false);
            return true;
        }
    }

    //Controlla che sia una mail valida
    public static boolean checkEmail(TextInputEditText textInputEditText, TextInputLayout textInputLayout, String string){
        if(textInputEditText.getText().toString().isEmpty() || !isValidEmail(textInputEditText.getText())){
            textInputLayout.setErrorEnabled(true);
            textInputLayout.setError(string);
            return false;
        }
        else {
            textInputLayout.setErrorEnabled(false);
            return true;
        }
    }

    //Fa comparire la scritta di errore dell'ET in input
    public static boolean checkError(TextInputEditText textInputEditText, TextInputLayout textInputLayout, String string){
        if(textInputEditText.getText().toString().isEmpty()){
            textInputLayout.setErrorEnabled(true);
            textInputLayout.setError(string);
            return false;
        }
        else {
            textInputLayout.setErrorEnabled(false);
            return true;
        }
    }

    //Fa comparire la scritta di errore dell'ET in input
    public static boolean checkError(AutoCompleteTextView textInputEditText, TextInputLayout textInputLayout, String string){
        if(textInputEditText.getText().toString().isEmpty()){
            textInputLayout.setErrorEnabled(true);
            textInputLayout.setError(string);
            return false;
        }
        else {
            textInputLayout.setErrorEnabled(false);
            return true;
        }
    }

    //Fa comparire e scomparire il messaggio di errore alla variazione del contenuto dell'ET
    public static void setListenersPassword(TextInputEditText textInputEditText, TextInputLayout textInputLayout, String string) {
        textInputEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                checkPassword(textInputEditText, textInputLayout, string);
            }
        });
    }

    //Rimuove l'errore
    public static void removeError(TextInputEditText textInputEditText, TextInputLayout textInputLayout) {
        textInputEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                textInputLayout.setErrorEnabled(false);
            }
        });
    }

    //Fa comparire e scomparire il messaggio di errore alla variazione del contenuto dell'ET
    public static void setListeners(TextInputEditText textInputEditText, TextInputLayout textInputLayout, String string) {
        textInputEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                checkError(textInputEditText, textInputLayout, string);
            }
        });
    }

    //Fa comparire e scomparire il messaggio di errore alla variazione del contenuto dell'ET
    public static void setListeners(AutoCompleteTextView textInputEditText, TextInputLayout textInputLayout, String string) {
        textInputEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                checkError(textInputEditText, textInputLayout, string);
            }
        });
    }

    //Imposta gli spinner
    public static void setSpinner(String[] stringArray, AutoCompleteTextView autoCompleteTextView, Context context) {
        List<String> list = Arrays.asList(stringArray);
        ArrayAdapter arrayAdapter = new ArrayAdapter<>(context, R.layout.support_simple_spinner_dropdown_item, list);
        autoCompleteTextView.setAdapter(arrayAdapter);
        autoCompleteTextView.setThreshold(1);
    }

    //Prende l'estensione del file
    public static String getFileExtension(Uri uri, Context context){
        ContentResolver contentResolver = context.getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
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
