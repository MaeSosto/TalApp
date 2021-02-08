package com.example.talapp.Terapie;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.example.talapp.R;
import com.example.talapp.Utils.Util;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.List;
import java.util.Map;

import static com.example.talapp.Notification.ForegroundService.sveglieRef;
import static com.example.talapp.Utils.Util.KEY_DATA;
import static com.example.talapp.Utils.Util.KEY_DATA_FINE;
import static com.example.talapp.Utils.Util.KEY_DOSE;
import static com.example.talapp.Utils.Util.KEY_FARMACO;
import static com.example.talapp.Utils.Util.KEY_NOME;
import static com.example.talapp.Utils.Util.KEY_NOTE;
import static com.example.talapp.Utils.Util.KEY_ORARIO;
import static com.example.talapp.Utils.Util.KEY_SETTIMANA;

public class TerapieListAdapter extends RecyclerView.Adapter<TerapieListAdapter.TerapieViewHolder> {

    private final LayoutInflater layoutInflater;
    private final Context mContext;
    private List<DocumentSnapshot> mQuerySnapshot;

    public TerapieListAdapter(Context context) {
        layoutInflater = LayoutInflater.from(context);
        mContext = context;
    }

    @NonNull
    @Override
    public TerapieViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = layoutInflater.inflate(R.layout.list_item_terapie, parent, false);
        return new TerapieViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull TerapieListAdapter.TerapieViewHolder holder, int position) {
        if(mQuerySnapshot != null){
            DocumentSnapshot documentSnapshot = mQuerySnapshot.get(position);
            holder.setData(documentSnapshot, position);
            holder.setListeners();
        }
    }

    @Override
    public int getItemCount() {
        if(mQuerySnapshot != null)
            return mQuerySnapshot.size();
        else return 0;
    }

    public void setTerapie(List<DocumentSnapshot> querySnapshot){
        mQuerySnapshot = querySnapshot;
        notifyDataSetChanged();
    }

    public class TerapieViewHolder extends RecyclerView.ViewHolder {

        private int mPosition;
        private Button Edit;

        public TerapieViewHolder(@NonNull View itemView) {
            super(itemView);
            Edit = itemView.findViewById(R.id.buttonEditTerapia);
        }


        public void setData(DocumentSnapshot documentSnapshot, int position) {
            Map<String, Object> terapia = documentSnapshot.getData();

            TextView TXVNomeTerapia = itemView.findViewById(R.id.TXVNomeTerapia);
            TXVNomeTerapia.setText((String) terapia.get(KEY_NOME));
            //Log.d("NOME", String.valueOf(terapia.get(KEY_NOME)));

            TextView TXVDataInizioTerapia = itemView.findViewById(R.id.TXVDataInizioTerapia);
            TXVDataInizioTerapia.setText("Periodo: "+ Util.TimestampToStringData((Timestamp) terapia.get(KEY_DATA)) + " - "+ Util.TimestampToStringData((Timestamp) terapia.get(KEY_DATA_FINE)));

            TextView TXVDoseTerapia = itemView.findViewById(R.id.TXVDoseTerapia);
            TXVDoseTerapia.setText("Dose: "+ terapia.get(KEY_DOSE));

            TextView note = itemView.findViewById(R.id.TXVNoteTerapia);
            if(terapia.containsKey(KEY_NOTE)){
                note.setVisibility(View.VISIBLE);
                note.setText("Note: " + terapia.get(KEY_NOTE));
            }
            else note.setVisibility(View.GONE);

            sveglieRef.whereEqualTo(KEY_FARMACO, documentSnapshot.getId()).get().addOnCompleteListener(task -> {
                TextView TXVElencoSveglie = itemView.findViewById(R.id.TXVElencoSveglie);
                if(task.getResult().getDocuments().size() > 0) {
                    StringBuilder messaggio = new StringBuilder();
                    messaggio.append("Sveglie:").append('\n');
                    for (int i = 0; i < task.getResult().getDocuments().size(); i++) {
                        messaggio.append("• ").append(Util.TimestampToOrario((Timestamp) task.getResult().getDocuments().get(i).getData().get(KEY_ORARIO))).append(" nei giorni: ").append(Util.getGiorniSveglia((List<Boolean>) task.getResult().getDocuments().get(i).getData().get(KEY_SETTIMANA)));
                        if(i+1 != task.getResult().getDocuments().size()) messaggio.append("\n");
                    }
                    TXVElencoSveglie.setText(messaggio);
                    TXVElencoSveglie.setVisibility(View.VISIBLE);
                }
                else TXVElencoSveglie.setVisibility(View.GONE);
            });

            mPosition = position;
        }

        public void setListeners() {
            Edit.setOnClickListener(v -> {
                String id = mQuerySnapshot.get(mPosition).getId();
                Bundle bundle = new Bundle();
                bundle.putString("ID", id);
                Navigation.findNavController(v).navigate(R.id.action_global_modificaTerapiaFragment, bundle);
            });
        }
    }
}
