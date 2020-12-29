package com.example.talapp.Utils;

import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Util {
    private static SimpleDateFormat SDF = new SimpleDateFormat("dd/MM/yyyy");

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

    public static int ConverterStringToDay(String s){
        Date date = null;
        try {
            date = SDF.parse(s);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date.getDay();
    }

    public static int ConverterStringToMonth(String s){
        Date date = null;
        try {
            date = SDF.parse(s);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date.getMonth();
    }

    public static int ConverterStringToYear(String s){
        Date date = null;
        try {
            date = SDF.parse(s);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date.getYear();
    }

    public static int ConverterStringToHour(String s){
        String[] parts = s.split(":");
        String part1 = parts[0];
        int ora = Integer.parseInt(part1);
        return ora;
    }

    public static int ConverterStringToMinute(String s){
        String[] parts = s.split(":");
        String part1 = parts[1];
        int ora = Integer.parseInt(part1);
        return ora;
    }

    public static Calendar ConverterStringToCalendar(String giorno, String ora) throws ParseException {
        Log.i("Devo convertire", giorno + " alle "+ ora);
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy hh:mm");
        cal.setTime(sdf.parse(giorno+" "+ora));
        Log.i("Devo convertire", String.valueOf(cal));
        return cal;
    }

}
