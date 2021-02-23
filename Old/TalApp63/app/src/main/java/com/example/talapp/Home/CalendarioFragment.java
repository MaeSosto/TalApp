package com.example.talapp.Home;

import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.SearchView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.applandeo.materialcalendarview.CalendarView;
import com.applandeo.materialcalendarview.EventDay;
import com.example.talapp.R;
import com.example.talapp.Utils.Util;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.example.talapp.HomeActivity.actionBar;
import static com.example.talapp.Notification.ForegroundService.esamiRef;
import static com.example.talapp.Notification.ForegroundService.terapieRef;
import static com.example.talapp.Notification.ForegroundService.trasfusioniRef;
import static com.example.talapp.Utils.Util.DateToLong;
import static com.example.talapp.Utils.Util.DateToString;
import static com.example.talapp.Utils.Util.KEY_DATA;
import static com.example.talapp.Utils.Util.StringToDate;
import static com.example.talapp.Utils.Util.getPrimoMinuto;
import static com.example.talapp.Utils.Util.getUltimoMinutoDelMese;


public class CalendarioFragment extends Fragment {

    private CalendarView calendarView;
    private View root;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_calendario, container, false);
        this.root = root;

        actionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.DeepChampagne)));

        //SearchView
        LinearLayout LLSearchView = root.findViewById(R.id.LLSearchView);
        LLSearchView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(root).navigate(R.id.action_calendarioFragment_to_searchFragment);
            }
        });

        //Calendario
        calendarView = root.findViewById(R.id.calendarView);
        setCalendario(getPrimoMinuto(calendarView.getCurrentPageDate()), getUltimoMinutoDelMese(calendarView.getCurrentPageDate()));

        //SFOGLIO IL CALENDARIO IN AVANTI
        calendarView.setOnForwardPageChangeListener(() -> setCalendario(getPrimoMinuto(calendarView.getCurrentPageDate()), getUltimoMinutoDelMese(calendarView.getCurrentPageDate())));

        //SFOGLIO IL CALENDARIO IN INDIETRO
        calendarView.setOnPreviousPageChangeListener(() -> setCalendario(getPrimoMinuto(calendarView.getCurrentPageDate()), getUltimoMinutoDelMese(calendarView.getCurrentPageDate())));

        //QUANDO CLICCHI SU UN GIORNO
        calendarView.setOnDayClickListener(eventDay -> touchGiorno(eventDay));

        //FAB
        Animation unrotateButton = AnimationUtils.loadAnimation(getContext(), R.anim.unrotate_fab);
        Animation rotateButton = AnimationUtils.loadAnimation(getContext(), R.anim.rotate_fab);
        Animation showButton = AnimationUtils.loadAnimation(getContext(), R.anim.show_fab);
        Animation hideButton = AnimationUtils.loadAnimation(getContext(), R.anim.hide_fab);
        FloatingActionButton FABGrande = root.findViewById(R.id.floatingActionButton);
        ExtendedFloatingActionButton FABTrasfusioni = root.findViewById(R.id.FABTrasfusioni);
        ExtendedFloatingActionButton FABEsami = root.findViewById(R.id.FABEsami);
        ExtendedFloatingActionButton FABTerapie = root.findViewById(R.id.FABTerapie);
        FABGrande.setOnClickListener(v -> {
            if(FABTrasfusioni.getVisibility() == View.GONE && FABEsami.getVisibility() == View.GONE && FABTerapie.getVisibility() == View.GONE){
                FABTrasfusioni.setVisibility(View.VISIBLE);
                FABTrasfusioni.startAnimation(showButton);
                FABEsami.setVisibility(View.VISIBLE);
                FABEsami.startAnimation(showButton);
                FABTerapie.setVisibility(View.VISIBLE);
                FABTerapie.startAnimation(showButton);
                FABGrande.startAnimation(unrotateButton);
            }
            else{
                FABTrasfusioni.setVisibility(View.GONE);
                FABTrasfusioni.startAnimation(hideButton);
                FABEsami.setVisibility(View.GONE);
                FABEsami.startAnimation(hideButton);
                FABTerapie.setVisibility(View.GONE);
                FABTerapie.startAnimation(hideButton);
                FABGrande.startAnimation(rotateButton);
            }
        });

        FABTrasfusioni.setOnClickListener(v -> Navigation.findNavController(root).navigate(R.id.action_calendarioFragment_to_aggiungiTrasfusioneFragment));

        FABEsami.setOnClickListener(v -> Navigation.findNavController(root).navigate(R.id.action_calendarioFragment_to_aggiungiEsamiFragment));

        FABTerapie.setOnClickListener(v -> Navigation.findNavController(root).navigate(R.id.action_calendarioFragment_to_aggiungiTerapieFragment));

        return  root;
    }

    private void touchGiorno(EventDay eventDay) {
        Calendar clickedDayCalendar = eventDay.getCalendar();
        trasfusioniRef.whereGreaterThanOrEqualTo(KEY_DATA, Util.getPrimoMinuto(clickedDayCalendar).getTime()).whereLessThanOrEqualTo(KEY_DATA, Util.getUltimoMinuto(clickedDayCalendar).getTime()).get().addOnCompleteListener(task1 -> esamiRef.whereGreaterThanOrEqualTo(Util.KEY_DATA, Util.getPrimoMinuto(clickedDayCalendar).getTime()).whereLessThanOrEqualTo(Util.KEY_DATA, Util.getUltimoMinuto(clickedDayCalendar).getTime()).get().addOnCompleteListener(task2 -> terapieRef.get().addOnCompleteListener(task3 -> {
            if((task1.getResult().getDocuments().size() > 0) || (task2.getResult().getDocuments().size() > 0) || (GiornoFragment.getTerapie(task3.getResult(), clickedDayCalendar).size() > 0)) {
                Bundle bundle = new Bundle();
                bundle.putLong("LongGiorno", DateToLong(clickedDayCalendar.getTime()));
                Navigation.findNavController(root).navigate(R.id.actionGiorno, bundle);
            }
        })));
    }

    private void setCalendario(Calendar PrimoMinutoDelMese, Calendar UltimoMinutoDelMese){
        Map<String, Util.Evento> giorni = new HashMap<>();

        getTrasfusioni(giorni, PrimoMinutoDelMese, UltimoMinutoDelMese);
    }

    private void getTrasfusioni(Map<String, Util.Evento> giorni, Calendar primoMinutoDelMese, Calendar ultimoMinutoDelMese) {
        trasfusioniRef.whereGreaterThanOrEqualTo(KEY_DATA, primoMinutoDelMese.getTime()).whereLessThanOrEqualTo(KEY_DATA, ultimoMinutoDelMese.getTime()).get().addOnCompleteListener((OnCompleteListener<QuerySnapshot>) task -> {
            for (int i = 0; i < task.getResult().getDocuments().size(); i++) {
                Calendar calendar = Calendar.getInstance();
                calendar.setTime( Util.TimestampToDate((Timestamp) task.getResult().getDocuments().get(i).getData().get(KEY_DATA)));
                if(!giorni.containsKey(DateToString(calendar.getTime()))){
                    giorni.put(DateToString(calendar.getTime()), new Util.Evento(true, false, false));
                    //Log.d("CALENDAR TRASFUSIONE", "INSERISCO "+ calendar.getTime());
                }
                else{
                    Util.Evento e = (Util.Evento) giorni.get(DateToString(calendar.getTime()));
                    e.setTrasfusione(true);
                    giorni.put(DateToString(calendar.getTime()), e);
                }
            }
            getEsami(giorni, primoMinutoDelMese, ultimoMinutoDelMese);
        });
    }

    private void getEsami(Map<String, Util.Evento> giorni, Calendar primoMinutoDelMese, Calendar ultimoMinutoDelMese) {
        esamiRef.whereGreaterThanOrEqualTo(Util.KEY_DATA, primoMinutoDelMese.getTime()).whereLessThanOrEqualTo(Util.KEY_DATA, ultimoMinutoDelMese.getTime()).get().addOnCompleteListener(task -> {
            for (int i = 0; i < task.getResult().getDocuments().size(); i++) {
                Calendar calendar = Calendar.getInstance();
                calendar.setTime( Util.TimestampToDate((Timestamp) task.getResult().getDocuments().get(i).getData().get(Util.KEY_DATA)));
                if(!giorni.containsKey(DateToString(calendar.getTime()))){
                    giorni.put(DateToString(calendar.getTime()),new Util.Evento(false, true, false));
                }
                else{
                    Util.Evento e = giorni.get(DateToString(calendar.getTime()));
                    e.setEsame(true);
                    giorni.put(DateToString(calendar.getTime()), e);
                }
            }

            getTerapie(giorni);
        });
    }

    private void getTerapie(Map<String, Util.Evento> giorni) {
        //La terapia inizia prima del mese attuale e finisce nel mese attuale
        terapieRef.get().addOnCompleteListener(task -> {
            for (int i = 0; i < task.getResult().getDocuments().size(); i++) {
                Calendar inizioTerapia = Calendar.getInstance();
                inizioTerapia.setTime(Util.TimestampToDate((Timestamp) task.getResult().getDocuments().get(i).getData().get(Util.KEY_DATA)));
                Calendar fineTerapia = Calendar.getInstance();
                fineTerapia.setTime(Util.TimestampToDate((Timestamp) task.getResult().getDocuments().get(i).getData().get(Util.KEY_DATA_FINE)));
                Calendar giorno = inizioTerapia;
                while(inizioTerapia.compareTo(fineTerapia) <= 0) {
                    if (!giorni.containsKey(DateToString(giorno.getTime()))) {
                        giorni.put(DateToString(giorno.getTime()), new Util.Evento(false, false, true));
                    } else {
                        Util.Evento e = giorni.get(DateToString(giorno.getTime()));
                        e.setTerapia(true);
                        giorni.put(DateToString(giorno.getTime()), e);
                    }
                    giorno.add(Calendar.DATE, 1);
                }
            }
            drawCalendario(giorni);
        });
    }

    private void drawCalendario(Map<String, Util.Evento> giorni){
        List<EventDay> events = new ArrayList<>();
        for (Map.Entry<String, Util.Evento> entry : giorni.entrySet()) {
            Util.Evento e = entry.getValue();
            //Log.d("MAP", "Giorno: "+ entry.getKey()+ " Evento: "+ entry.getValue().isTrasfusione()+ " "+ entry.getValue().isEsame()+" "+entry.getValue().isTerapia()+"\n");
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(StringToDate(entry.getKey()));
            if( e.isTrasfusione() && e.isEsame() &&   e.isTerapia()) events.add(new EventDay(calendar, R.drawable.dot_trasfusioni_esami_terapie));
            if( e.isTrasfusione() && e.isEsame() &&  !e.isTerapia()) events.add(new EventDay(calendar, R.drawable.dot_trasfusioni_esami));
            if( e.isTrasfusione() && !e.isEsame() &&  e.isTerapia()) events.add(new EventDay(calendar, R.drawable.dot_trasfusioni_terapie));
            if( e.isTrasfusione() && !e.isEsame() && !e.isTerapia()) events.add(new EventDay(calendar, R.drawable.dot_trasfusioni));
            if(!e.isTrasfusione() && e.isEsame() &&   e.isTerapia()) events.add(new EventDay(calendar, R.drawable.dot_esami_terapie));
            if(!e.isTrasfusione() && e.isEsame() &&  !e.isTerapia()) events.add(new EventDay(calendar, R.drawable.dot_esami));
            if(!e.isTrasfusione() && !e.isEsame() &&  e.isTerapia()) events.add(new EventDay(calendar, R.drawable.dot_terapie));
        }
        calendarView.setEvents(events);
    }
}