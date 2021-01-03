package com.example.talapp.Home;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.applandeo.materialcalendarview.CalendarView;
import com.applandeo.materialcalendarview.EventDay;
import com.example.talapp.R;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import static android.content.ContentValues.TAG;
import static com.example.talapp.HomeActivity.actionBar;
import static com.example.talapp.HomeActivity.esamiRef;
import static com.example.talapp.HomeActivity.trasfusioniRef;
import static com.example.talapp.Utils.Util.DateToLong;
import static com.example.talapp.Utils.Util.KEY_ESAME_DATA;
import static com.example.talapp.Utils.Util.KEY_TRASFUSIONE_DATA;
import static com.example.talapp.Utils.Util.esamiViewModel;
import static com.example.talapp.Utils.Util.isConnectedToInternet;
import static com.example.talapp.Utils.Util.trasfusioniViewModel;


public class CalendarioFragment extends Fragment {

    private boolean FAB = false;
    private CalendarView calendarView;

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

        openCloseFAB(root);
        root.findViewById(R.id.floatingActionButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openCloseFAB(root);
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
        Calendar Cmese = calendarView.getCurrentPageDate();
        int ultimo = Cmese.getActualMaximum(Calendar.DAY_OF_MONTH);
        Calendar Fmese = calendarView.getCurrentPageDate();
        Fmese.set(Calendar.DAY_OF_MONTH, ultimo);

        trasfusioniViewModel.setCalendarioTrasfusioni(Cmese, Fmese, calendarView);
        //esamiViewModel.setCalendarioEsami(Cmese, Fmese, calendarView);
    }

    private void openCloseFAB(View root){
        if(FAB){
            FAB = false;
            root.findViewById(R.id.FABTrasfusioni).setVisibility(View.VISIBLE);
            root.findViewById(R.id.FABEsami).setVisibility(View.VISIBLE);
            root.findViewById(R.id.FABTerapie).setVisibility(View.VISIBLE);
        }
        else{
            FAB = true;
            root.findViewById(R.id.FABTrasfusioni).setVisibility(View.GONE);
            root.findViewById(R.id.FABEsami).setVisibility(View.GONE);
            root.findViewById(R.id.FABTerapie).setVisibility(View.GONE);
        }
    }
}