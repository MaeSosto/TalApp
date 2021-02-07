package com.example.talapp.Terapie;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.util.Pair;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.talapp.R;
import com.example.talapp.Sveglie.ClockAlarm;
import com.example.talapp.Sveglie.SvegliaDialog;
import com.example.talapp.Sveglie.SveglieListAdapter;
import com.example.talapp.Utils.SnackbarRimuoviEvento;
import com.example.talapp.Utils.Util;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.QuerySnapshot;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import static com.example.talapp.HomeActivity.actionBar;
import static com.example.talapp.Notification.ForegroundService.sveglieRef;
import static com.example.talapp.Notification.ForegroundService.terapieRef;
import static com.example.talapp.Utils.Util.KEY_DATA;
import static com.example.talapp.Utils.Util.KEY_DATA_FINE;
import static com.example.talapp.Utils.Util.KEY_DOSE;
import static com.example.talapp.Utils.Util.KEY_FARMACO;
import static com.example.talapp.Utils.Util.KEY_NOME;
import static com.example.talapp.Utils.Util.KEY_NOTE;
import static com.example.talapp.Utils.Util.KEY_NOTIFICHE;
import static com.example.talapp.Utils.Util.KEY_ORARIO;
import static com.example.talapp.Utils.Util.KEY_SETTIMANA;
import static com.example.talapp.Utils.Util.checkError;
import static com.example.talapp.Utils.Util.setListeners;


public class ModificaTerapieFragment extends Fragment {

    private Map<String, Object> terapia_old;
    private MutableLiveData<List<Map<String, Object>>> mSveglie;
    private static Date inizioTerapia, fineTerapia;
    private View root;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        actionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.GreeenSheen)));

        //Quando fai back
        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                terapia_old = null;
                remove();
            }
        };
        //requireActivity().getOnBackPressedDispatcher().addCallback(this, callback);

        mSveglie = new MutableLiveData<>();
        mSveglie.setValue(new ArrayList<>());
        inizioTerapia = null;
        fineTerapia = null;

        //Faccio partire il service che setta gli allarmi
        getActivity().startService(new Intent(getContext(), ClockAlarm.class));
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //Fermo l'alarm
        getActivity().stopService(new Intent(getContext(), ClockAlarm.class));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root =  inflater.inflate(R.layout.fragment_modifica_terapie, container, false);
        this.root = root;

        String id = getArguments().getString("ID");

        //Costruisco il Material date picker
        MaterialDatePicker.Builder<Pair<Long, Long>> builder = MaterialDatePicker.Builder.dateRangePicker();
        builder.setTitleText("Seleziona il periodo della terapia");
        MaterialDatePicker materialDatePicker = builder.build();

        //Setto il periodo
        TextInputEditText ETDate = root.findViewById(R.id.editTextDataRangeTerapia);
        TextInputLayout TILDataTerapia = root.findViewById(R.id.TILDataTerapia);
        setDatePicker(materialDatePicker, ETDate);
        setListeners(ETDate, TILDataTerapia, "Inserisci un periodo valido");
        ETDate.setOnClickListener(v -> materialDatePicker.show(getFragmentManager(), "DATE_PICKER"));

        //Dose
        TextInputEditText ETDose = root.findViewById(R.id.editTextDoseTerapia);
        TextInputLayout TILDoseFarmaco = root.findViewById(R.id.TILDoseTerapia);
        setListeners(ETDose, TILDoseFarmaco, "Inserisci una dose valida");

        //Note
        TextInputEditText editTextNoteTerapiaModifica = root.findViewById(R.id.editTextNoteTerapiaModifica);

        //Prendo le informazioni della terapia
        getTerapia(id, ETDate, ETDose, editTextNoteTerapiaModifica);

        //Prendo le sveglie associate alla terapia
        getSveglie(id);

        //RecyclerView
        RecyclerView RecyclerViewSveglie = root.findViewById(R.id.RecyclerViewSveglieModifica);
        SveglieListAdapter sveglieListAdapter = new SveglieListAdapter(getContext());
        mSveglie.observe(getViewLifecycleOwner(), maps -> {
            RecyclerViewSveglie.setVisibility(View.VISIBLE);
            RecyclerViewSveglie.setAdapter(sveglieListAdapter);
            RecyclerViewSveglie.setLayoutManager(new LinearLayoutManager(getContext()));
            sveglieListAdapter.setTerapie(maps, mSveglie);
        });

        //Aggiungi sveglia
        Button buttonAggiungiSveglia = root.findViewById(R.id.buttonAggiungiSveglia);
        buttonAggiungiSveglia.setOnClickListener(v -> aggiungiSveglia());

        //Bottone modifica
        root.findViewById(R.id.buttonModificaTerapia).setOnClickListener(v -> {
            if(checkError(ETDate, TILDataTerapia, "Inserisci un periodo valido") && checkError(ETDose, TILDoseFarmaco, "Inserisci una dose valida")){

                terapia_old.put(KEY_DATA_FINE, fineTerapia);
                if (!ETDose.getText().toString().isEmpty()) terapia_old.put(KEY_DOSE, Double.parseDouble(ETDose.getText().toString()));
                else terapia_old.put(KEY_DOSE, FieldValue.delete());
                if (!editTextNoteTerapiaModifica.getText().toString().isEmpty()) terapia_old.put(KEY_NOTE, editTextNoteTerapiaModifica.getText().toString());
                else terapia_old.put(KEY_NOTE, FieldValue.delete());

                aggiornoTerapia(id);
            }
        });

        //Bottone elimina
        root.findViewById(R.id.buttonEliminaTerapia).setOnClickListener(v -> eliminaTerapia(id));

        return root;
    }

    private void getSveglie(String id) {
        //Prendo le sveglie di questa terapia
        sveglieRef.whereEqualTo(KEY_FARMACO, id).get().addOnCompleteListener(task -> {
            List<Map<String, Object>> tmp = new ArrayList<>();
            for(int i = 0; i < task.getResult().getDocuments().size(); i++){
                tmp.add(task.getResult().getDocuments().get(i).getData());
            }
            mSveglie.setValue(tmp);
        });
    }

    private void getTerapia(String id, TextInputEditText ETDate, TextInputEditText ETDose, TextInputEditText editTextNoteTerapiaModifica) {
        //Prendo la terapia da modificare
        terapieRef.document(id).get().addOnCompleteListener(task -> {
            terapia_old = task.getResult().getData();
            TextView TXVNomeTerapia = root.findViewById(R.id.TXVNomeTerapia);
            TXVNomeTerapia.setText((String)terapia_old.get(KEY_NOME));
            ETDate.setText(Util.TimestampToStringData((Timestamp) terapia_old.get(KEY_DATA))+" - "+ Util.TimestampToStringData((Timestamp) terapia_old.get(KEY_DATA_FINE)));
            ETDose.setText(String.valueOf((double) terapia_old.get(KEY_DOSE)));
            editTextNoteTerapiaModifica.setText((String) terapia_old.get(KEY_NOTE));
        });
    }

    private void aggiungiSveglia() {
        MutableLiveData<String> orario = new MutableLiveData<>();
        DialogFragment dialogFragment = new SvegliaDialog(orario);
        dialogFragment.show(getParentFragmentManager(), "timepicker");

        orario.observe(getViewLifecycleOwner(), s -> {
            Map<String, Object> sveglia = new HashMap<>();
            try {
                sveglia.put(KEY_ORARIO, Util.StringToDateOrario(s));
            } catch (ParseException e) {
                e.printStackTrace();
            }

            List<Boolean> settimana = new ArrayList<>(Arrays.asList(new Boolean[7]));
            Collections.fill(settimana, Boolean.FALSE);

            sveglia.put(KEY_NOTIFICHE, false);
            sveglia.put(KEY_SETTIMANA, settimana);

            List<Map<String, Object>> tmp = mSveglie.getValue();
            tmp.add(sveglia);
            mSveglie.setValue(tmp);

            Log.d("SVEGLIE", "SVEGLIA AGGIUNTA");
        });
    }

    private void aggiornoTerapia(String id) {
        terapieRef.document(id).update(terapia_old).addOnCompleteListener(task -> {

            eliminoVecchieSveglie(id);

            Toast.makeText(getContext(), "Terapia aggiornata", Toast.LENGTH_SHORT).show();
            terapia_old = null;
            Navigation.findNavController(root).popBackStack();
        });
    }

    private void eliminoVecchieSveglie(String id) {
        Log.d("ELIMINO VECCHIE SVEGLIE", "");
        //Elimino le vecchie sveglie
        sveglieRef.whereEqualTo(KEY_FARMACO, id).get().addOnCompleteListener(task -> {
            for(int i = 0; i < task.getResult().getDocuments().size(); i++){
                String id1 = task.getResult().getDocuments().get(i).getId();
                sveglieRef.document(id1).delete();
                Log.d("ELIMINO VECCHIE SVEGLIE", "Id:"+ id1);
                ClockAlarm.removeAlarm(id1);
            }

            Log.d("SETTO LE NUOVE SVEGLIE", "");
            //Setto le nuove sveglie
            for(int i = 0; i< mSveglie.getValue().size(); i++){
                mSveglie.getValue().get(i).put(KEY_FARMACO, id);
                Date orario = null;
                try {
                    orario = Util.TimestampToDate((Timestamp) mSveglie.getValue().get(i).get(KEY_ORARIO));
                } catch (Exception e) {
                    orario = (Date) mSveglie.getValue().get(i).get(KEY_ORARIO);
                }
                Date finalOrario = orario;

                aggiungoSveglia(finalOrario, i);

            }
        });
    }

    private void aggiungoSveglia(Date finalOrario, int i) {
        //AGGIUNGO LE SVEGLIE
        Log.d("AGGIUNGO LE SVEGLIE", "");
        sveglieRef.add(mSveglie.getValue().get(i)).addOnCompleteListener(task -> {
            String idSveglia = task.getResult().getId();
            ClockAlarm.removeAlarm(idSveglia);
            if(fineTerapia.compareTo(Util.getPrimoMinuto(Calendar.getInstance()).getTime()) >= 0){
                Log.d("AGGIUNGO LE SVEGLIE", "HO SALVATO L'ALARM");
                ClockAlarm.setAlarm(finalOrario, idSveglia);
            }
            //ClockAlarm.setAlarm(finalOrario, idSveglia);


        });
    }

    private void eliminaTerapia(String id) {
        //Possiamo recupeare la terapia ma non le sveglie associate ad essa
        terapieRef.document(id).delete();

        //Elimino tutte le sveglie
        sveglieRef.whereEqualTo(KEY_FARMACO, id).get().addOnCompleteListener(task -> {
            for(int i = 0; i < task.getResult().getDocuments().size(); i++){
                String id1 = task.getResult().getDocuments().get(i).getId();
                sveglieRef.document(id1).delete();
                ClockAlarm.removeAlarm(id1);
            }
        });

        //Navigation.findNavController(root).popBackStack();
        SnackbarRimuoviEvento SU = new SnackbarRimuoviEvento();
        SU.rimuovi(terapia_old, terapieRef);
        Snackbar snackbar = Snackbar.make(root, "Terapia rimossa", BaseTransientBottomBar.LENGTH_LONG);
        snackbar.setAction("Cancella operazione", SU);
        snackbar.show();
        Navigation.findNavController(root).popBackStack();
    }

    private void setDatePicker(MaterialDatePicker materialDatePicker, TextInputEditText textInputEditText) {
        materialDatePicker.addOnPositiveButtonClickListener(selection -> {
            Pair<Long, Long> sel = (Pair<Long, Long>) selection;
            //Object dateSelector = materialDatePicker.getSelection();
            //Collection<Pair<Long, Long>> collection = dateSelector.
            Calendar inizio = Calendar.getInstance();
            inizio.setTimeInMillis(sel.first);
            Calendar fine = Calendar.getInstance();
            fine.setTimeInMillis(sel.second);
            inizioTerapia = inizio.getTime();
            fineTerapia = fine.getTime();
            textInputEditText.setText(Util.DateToString(inizioTerapia)+" - "+ Util.DateToString(fineTerapia));
            //Log.d("MATERIAL DATE PICKER", inizio.getTime()+"/"+ fine.getTime());
        });
    }
}