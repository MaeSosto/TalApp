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
import static com.example.talapp.HomeActivity.esamiRef;
import static com.example.talapp.HomeActivity.trasfusioniRef;
import static com.example.talapp.Utils.Util.DateToLong;
import static com.example.talapp.Utils.Util.DateToString;
import static com.example.talapp.Utils.Util.KEY_DATA;
import static com.example.talapp.Utils.Util.StringToDate;
import static com.example.talapp.Utils.Util.isConnectedToInternet;


public class CalendarioFragment extends Fragment {

    MutableLiveData <Boolean> FAB;
    private CalendarView calendarView;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FAB = new MutableLiveData<>();
        FAB.setValue(false);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_calendario, container, false);

        actionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.DeepChampagne)));

        //Calendario
        calendarView = root.findViewById(R.id.calendarView);
        updateList();

        //SFOGLIO IL CALENDARIO IN AVANTI
        calendarView.setOnForwardPageChangeListener(this::updateList);

        //SFOGLIO IL CALENDARIO IN INDIETRO
        calendarView.setOnPreviousPageChangeListener(this::updateList);

        //QUANDO CLICCHI SU UN GIORNO
        calendarView.setOnDayClickListener(eventDay -> {
            Calendar clickedDayCalendar = eventDay.getCalendar();
            Bundle bundle = new Bundle();
            bundle.putLong("LongGiorno", DateToLong(clickedDayCalendar.getTime()));
            Navigation.findNavController(root).navigate(R.id.actionGiorno, bundle);
        });

        FAB.observe(getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
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

    private void updateList() {
        //Primo giorno del mese
        Calendar Cmese = calendarView.getCurrentPageDate();
        Cmese.set(Calendar.HOUR_OF_DAY, 0);
        Cmese.set(Calendar.MINUTE, 0);
        Cmese.set(Calendar.SECOND, 0);
        Cmese.set(Calendar.MILLISECOND, 0);
        //Ultimo giorno del mese
        int ultimo = Cmese.getActualMaximum(Calendar.DAY_OF_MONTH);
        Calendar Fmese = calendarView.getCurrentPageDate();
        Fmese.set(Calendar.DAY_OF_MONTH, ultimo);
        Fmese.set(Calendar.HOUR_OF_DAY, 24);
        Fmese.set(Calendar.MINUTE, 59);
        Fmese.set(Calendar.SECOND, 59);
        Fmese.set(Calendar.MILLISECOND, 59);

        setCalendario(Cmese, Fmese);
    }

    private void setCalendario(Calendar cmese, Calendar fmese){
        Map<String, Util.Evento> giorni = new HashMap<>();
        Log.d("MAP", "Entro in setCalendario \n");
        trasfusioniRef.whereLessThanOrEqualTo(KEY_DATA, fmese.getTime())
                .whereGreaterThanOrEqualTo(KEY_DATA, cmese.getTime())
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        if (error != null) {
                            Log.w(TAG, "Listen failed.", error);
                            return;
                        }
                        List<DocumentSnapshot> mTrasfusioni = value.getDocuments();

                        for (int i = 0; i < mTrasfusioni.size(); i++) {
                            Map<String, Object> map = mTrasfusioni.get(i).getData();
                            Calendar calendar = Calendar.getInstance();
                            Timestamp data = (Timestamp) map.get(KEY_DATA);
                            calendar.setTime( data.toDate());
                            //Log.d("MAP", "TRASFUSIONE");
                            if(!giorni.containsKey(DateToString(calendar.getTime()))){
                                giorni.put(DateToString(calendar.getTime()), new Util.Evento(true, false, false));
                                //Log.d("CALENDAR TRASFUSIONE", "INSERISCO "+ calendar.getTime());
                            }
                            else{
                                Util.Evento e = (Util.Evento) giorni.get(DateToString(calendar.getTime()));
                                e.setTrasfusione(true);
                                giorni.put(DateToString(calendar.getTime()), e);
                                //Log.d("CALENDAR TRASFUSIONE", "AGGIORNO "+ calendar.getTime());
                            }
                        }

                        esamiRef.whereLessThanOrEqualTo(Util.KEY_DATA, fmese.getTime())
                                .whereGreaterThanOrEqualTo(Util.KEY_DATA, cmese.getTime())
                                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                                    @Override
                                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                                        if (error != null) {
                                            Log.w(TAG, "Listen failed.", error);
                                            return;
                                        }
                                        List<DocumentSnapshot> documents = value.getDocuments();

                                        for (int i = 0; i < documents.size(); i++) {
                                            Map<String, Object> map = documents.get(i).getData();
                                            Calendar calendar = Calendar.getInstance();
                                            Timestamp data = (Timestamp) map.get(Util.KEY_DATA);
                                            calendar.setTime( data.toDate());
                                            if(!giorni.containsKey(DateToString(calendar.getTime()))){
                                                giorni.put(DateToString(calendar.getTime()),new Util.Evento(false, true, false));
                                            }
                                            else{
                                                Util.Evento e = giorni.get(DateToString(calendar.getTime()));
                                                e.setEsame(true);
                                                giorni.put(DateToString(calendar.getTime()), e);
                                            }
                                        }

                                        drawCalendario(giorni);
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