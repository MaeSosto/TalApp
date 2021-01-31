package com.example.talapp.Home;

import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.applandeo.materialcalendarview.CalendarView;
import com.applandeo.materialcalendarview.EventDay;
import com.applandeo.materialcalendarview.listeners.OnCalendarPageChangeListener;
import com.example.talapp.R;
import com.example.talapp.Utils.Util;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.content.ContentValues.TAG;
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
import static com.example.talapp.Utils.Util.isConnectedToInternet;


public class CalendarioFragment extends Fragment {

    MutableLiveData <Boolean> FAB;
    private CalendarView calendarView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_calendario, container, false);

        actionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.DeepChampagne)));

        FAB = new MutableLiveData<>();
        FAB.setValue(false);

        //Calendario
        calendarView = root.findViewById(R.id.calendarView);
        setCalendario(getPrimoMinuto(calendarView.getCurrentPageDate()), getUltimoMinutoDelMese(calendarView.getCurrentPageDate()));

        //SFOGLIO IL CALENDARIO IN AVANTI
        calendarView.setOnForwardPageChangeListener(() -> setCalendario(getPrimoMinuto(calendarView.getCurrentPageDate()), getUltimoMinutoDelMese(calendarView.getCurrentPageDate())));

        //SFOGLIO IL CALENDARIO IN INDIETRO
        calendarView.setOnPreviousPageChangeListener(() -> setCalendario(getPrimoMinuto(calendarView.getCurrentPageDate()), getUltimoMinutoDelMese(calendarView.getCurrentPageDate())));

        //QUANDO CLICCHI SU UN GIORNO
        calendarView.setOnDayClickListener(eventDay -> {
            Calendar clickedDayCalendar = eventDay.getCalendar();
            Bundle bundle = new Bundle();
            bundle.putLong("LongGiorno", DateToLong(clickedDayCalendar.getTime()));
            Navigation.findNavController(root).navigate(R.id.actionGiorno, bundle);
        });

        FAB.observe(getViewLifecycleOwner(), aBoolean -> {
            if(aBoolean){
                root.findViewById(R.id.FABTrasfusioni).setVisibility(View.VISIBLE);
                root.findViewById(R.id.FABEsami).setVisibility(View.VISIBLE);
                root.findViewById(R.id.FABTerapie).setVisibility(View.VISIBLE);
            }
            else{
                root.findViewById(R.id.FABTrasfusioni).setVisibility(View.GONE);
                root.findViewById(R.id.FABEsami).setVisibility(View.GONE);
                root.findViewById(R.id.FABTerapie).setVisibility(View.GONE);
            }
        });

        root.findViewById(R.id.floatingActionButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FAB.setValue(!FAB.getValue());
            }
        });

        root.findViewById(R.id.FABTrasfusioni).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(root).navigate(R.id.action_calendarioFragment_to_aggiungiTrasfusioneFragment);
            }
        });

        root.findViewById(R.id.FABEsami).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(root).navigate(R.id.action_calendarioFragment_to_aggiungiEsamiFragment);
            }
        });

        root.findViewById(R.id.FABTerapie).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(root).navigate(R.id.action_calendarioFragment_to_aggiungiTerapieFragment);
            }
        });

        return  root;
    }

    private void setCalendario(Calendar PrimoMinutoDelMese, Calendar UltimoMinutoDelMese){
        Map<String, Util.Evento> giorni = new HashMap<>();
        trasfusioniRef.whereGreaterThanOrEqualTo(KEY_DATA, PrimoMinutoDelMese.getTime()).whereLessThanOrEqualTo(KEY_DATA, UltimoMinutoDelMese.getTime()).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                for (int i = 0; i < value.getDocuments().size(); i++) {
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTime( Util.TimestampToDate((Timestamp) value.getDocuments().get(i).getData().get(Util.KEY_DATA)));
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

                esamiRef.whereGreaterThanOrEqualTo(Util.KEY_DATA, PrimoMinutoDelMese.getTime()).whereLessThanOrEqualTo(Util.KEY_DATA, UltimoMinutoDelMese.getTime()).addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        for (int i = 0; i < value.getDocuments().size(); i++) {
                            Calendar calendar = Calendar.getInstance();
                            calendar.setTime( Util.TimestampToDate((Timestamp) value.getDocuments().get(i).getData().get(Util.KEY_DATA)));
                            if(!giorni.containsKey(DateToString(calendar.getTime()))){
                                giorni.put(DateToString(calendar.getTime()),new Util.Evento(false, true, false));
                            }
                            else{
                                Util.Evento e = giorni.get(DateToString(calendar.getTime()));
                                e.setEsame(true);
                                giorni.put(DateToString(calendar.getTime()), e);
                            }
                        }

                        //La terapia inizia prima del mese attuale e finisce nel mese attuale
                        terapieRef.addSnapshotListener(new EventListener<QuerySnapshot>() {
                            @Override
                            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                                for (int i = 0; i < value.getDocuments().size(); i++) {
                                    Calendar inizioTerapia = Calendar.getInstance();
                                    inizioTerapia.setTime(Util.TimestampToDate((Timestamp) value.getDocuments().get(i).getData().get(Util.KEY_DATA)));
                                    Calendar fineTerapia = Calendar.getInstance();
                                    fineTerapia.setTime(Util.TimestampToDate((Timestamp) value.getDocuments().get(i).getData().get(Util.KEY_DATA_FINE)));
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
                            }
                        });
                    }
                });
            }
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