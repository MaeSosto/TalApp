package com.example.talapp.Terapie;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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
import com.example.talapp.Utils.DatePickerFragment;
import com.example.talapp.Utils.SnackbarRimuoviEvento;
import com.example.talapp.Utils.Util;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
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


public class ModificaTerapieFragment extends Fragment {

    private Map<String, Object> terapia_old;
    private MutableLiveData<List<Map<String, Object>>> mSveglie;

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

        String id = getArguments().getString("ID");

        TextView TXVNomeTerapia = root.findViewById(R.id.TXVNomeTerapia);
        EditText editTextDataFineTerapia = root.findViewById(R.id.editTextDataFineTerapia);
        //Setto la data di fine
        editTextDataFineTerapia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment dialogFragment = new DatePickerFragment(editTextDataFineTerapia);
                dialogFragment.show(getParentFragmentManager(), "datePicker");
            }
        });

        EditText editTextDoseFarmaco = root.findViewById(R.id.editTextDoseFarmaco);
        EditText editTextNoteTerapiaModifica = root.findViewById(R.id.editTextNoteTerapiaModifica);

        //Prendo la terapia da modificare
        terapieRef.document(id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                terapia_old = task.getResult().getData();
                TXVNomeTerapia.setText((String)terapia_old.get(KEY_NOME));
                editTextDataFineTerapia.setText(Util.TimestampToStringData((Timestamp) terapia_old.get(KEY_DATA_FINE)));
                editTextDoseFarmaco.setText(String.valueOf((double) terapia_old.get(KEY_DOSE)));
                editTextNoteTerapiaModifica.setText((String) terapia_old.get(KEY_NOTE));
            }
        });

        //RecyclerView
        RecyclerView RecyclerViewSveglie = root.findViewById(R.id.RecyclerViewSveglieModifica);
        SveglieListAdapter sveglieListAdapter = new SveglieListAdapter(getContext());
        mSveglie.observe(getViewLifecycleOwner(), new Observer<List<Map<String, Object>>>() {
            @Override
            public void onChanged(List<Map<String, Object>> maps) {
                RecyclerViewSveglie.setVisibility(View.VISIBLE);
                RecyclerViewSveglie.setAdapter(sveglieListAdapter);
                RecyclerViewSveglie.setLayoutManager(new LinearLayoutManager(getContext()));
                sveglieListAdapter.setTerapie(maps, mSveglie);
            }
        });

        //Prendo le sveglie di questa terapia
        sveglieRef.whereEqualTo(KEY_FARMACO, id).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                List<Map<String, Object>> tmp = new ArrayList<>();
                for(int i = 0; i < task.getResult().getDocuments().size(); i++){
                    tmp.add(task.getResult().getDocuments().get(i).getData());
                }
                mSveglie.setValue(tmp);
            }
        });

        //FAB Aggiungi sveglia
        FloatingActionButton FABAggiungiSveglia = root.findViewById(R.id.buttonAggiungiSveglia);
        FABAggiungiSveglia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MutableLiveData<String> orario = new MutableLiveData<>();
                DialogFragment dialogFragment = new SvegliaDialog(orario);
                dialogFragment.show(getParentFragmentManager(), "timepicker");

                orario.observe(getViewLifecycleOwner(), new Observer<String>() {
                    @Override
                    public void onChanged(String s) {
                        Map<String, Object> sveglia = new HashMap<>();
                        try {
                            sveglia.put(KEY_ORARIO, Util.StringToDateOrario(s));
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }

                        List<Boolean> settimana = new ArrayList<Boolean>(Arrays.asList(new Boolean[7]));
                        Collections.fill(settimana, Boolean.FALSE);

                        sveglia.put(KEY_NOTIFICHE, false);
                        sveglia.put(KEY_SETTIMANA, settimana);

                        List<Map<String, Object>> tmp = mSveglie.getValue();
                        tmp.add(sveglia);
                        mSveglie.setValue(tmp);

                        Log.d("SVEGLIE", "SVEGLIA AGGIUNTA");
                    }
                });
            }
        });

        //Bottone modifica
        root.findViewById(R.id.buttonModificaTerapia).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(editTextDataFineTerapia.getText().toString().isEmpty() || (Util.StringToDate(editTextDataFineTerapia.getText().toString()).compareTo(Util.TimestampToDate((Timestamp) terapia_old.get(KEY_DATA))) < 0)){
                    Toast.makeText(getContext(), "Inserisci una data di fine terapia valida", Toast.LENGTH_SHORT).show();
                }
                else if(editTextDoseFarmaco.getText().toString().isEmpty()){
                    Toast.makeText(getContext(), "Inserisci una dose valida", Toast.LENGTH_SHORT).show();
                }
                else {
                    terapia_old.put(KEY_DATA_FINE, Util.StringToDate(editTextDataFineTerapia.getText().toString()));
                    if (!editTextDoseFarmaco.getText().toString().isEmpty()) {
                        terapia_old.put(KEY_DOSE, Double.parseDouble(editTextDoseFarmaco.getText().toString()));
                    } else {
                        terapia_old.put(KEY_DOSE, FieldValue.delete());
                    }
                    if (!editTextNoteTerapiaModifica.getText().toString().isEmpty()) {
                        terapia_old.put(KEY_NOTE, editTextNoteTerapiaModifica.getText().toString());
                    } else {
                        terapia_old.put(KEY_NOTE, FieldValue.delete());
                    }

                    terapieRef.document(id).update(terapia_old).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            Log.d("ELIMINO VECCHIE SVEGLIE", "");
                            //Elimino le vecchie sveglie
                            sveglieRef.whereEqualTo(KEY_FARMACO, id).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    for(int i = 0; i < task.getResult().getDocuments().size(); i++){
                                        String id = task.getResult().getDocuments().get(i).getId();
                                        sveglieRef.document(id).delete();
                                        Log.d("ELIMINO VECCHIE SVEGLIE", "Id:"+ id);
                                        ClockAlarm.removeAlarm(id);
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
                                        //AGGIUNGO LE SVEGLIE
                                        Log.d("AGGIUNGO LE SVEGLIE", "");
                                        sveglieRef.add(mSveglie.getValue().get(i)).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                                            @Override
                                            public void onComplete(@NonNull Task<DocumentReference> task) {
                                                String idSveglia = task.getResult().getId();
                                                ClockAlarm.removeAlarm(idSveglia);
                                                if(Util.StringToDate(editTextDataFineTerapia.getText().toString()).compareTo(Util.getPrimoMinuto(Calendar.getInstance()).getTime()) >= 0){
                                                    Log.d("AGGIUNGO LE SVEGLIE", "HO SALVATO L'ALARM");
                                                    ClockAlarm.setAlarm(finalOrario, idSveglia);
                                                }
                                                //ClockAlarm.setAlarm(finalOrario, idSveglia);
                                            }
                                        });
                                    }
                                }
                            });

                            Toast.makeText(getContext(), "Terapia aggiornata", Toast.LENGTH_SHORT).show();
                            terapia_old = null;
                            Navigation.findNavController(root).popBackStack();
                        }
                    });
                }
            }
        });

        //Bottone elimina
        root.findViewById(R.id.buttonEliminaTerapia).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Possiamo recupeare la terapia ma non le sveglie associate ad essa
                terapieRef.document(id).delete();

                //Elimino tutte le sveglie
                sveglieRef.whereEqualTo(KEY_FARMACO, id).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        for(int i = 0; i < task.getResult().getDocuments().size(); i++){
                            String id = task.getResult().getDocuments().get(i).getId();
                            sveglieRef.document(id).delete();
                            ClockAlarm.removeAlarm(id);
                        }
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
        });

        return root;
    }
}