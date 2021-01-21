package com.example.talapp.Esami;

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

import static com.example.talapp.Utils.Util.DateToOrario;
import static com.example.talapp.Utils.Util.KEY_ATTIVAZIONE;
import static com.example.talapp.Utils.Util.KEY_DATA;
import static com.example.talapp.Utils.Util.KEY_DIGIUNO;
import static com.example.talapp.Utils.Util.KEY_ESITO;
import static com.example.talapp.Utils.Util.KEY_NOME;
import static com.example.talapp.Utils.Util.KEY_NOTE;
import static com.example.talapp.Utils.Util.KEY_REFERTO;
import static com.example.talapp.Utils.Util.KEY_RICORDA;
import static com.example.talapp.Utils.Util.KEY_TIPO;

public class EsamiListAdapter extends RecyclerView.Adapter<EsamiListAdapter.EsamiViewHolder> {

    private final LayoutInflater layoutInflater;
    private final Context mContext;
    private List<DocumentSnapshot> mQuerySnapshot;

    public EsamiListAdapter(Context context) {
        layoutInflater = LayoutInflater.from(context);
        mContext = context;
    }

    @NonNull
    @Override
    public EsamiViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = layoutInflater.inflate(R.layout.list_item_esami, parent, false);
        return new EsamiViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull EsamiListAdapter.EsamiViewHolder holder, int position) {
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

    public void setEsami(List<DocumentSnapshot> querySnapshot){
        mQuerySnapshot = querySnapshot;
        notifyDataSetChanged();
    }

    public class EsamiViewHolder extends RecyclerView.ViewHolder {

        private int mPosition;
        private Button Edit;

        public EsamiViewHolder(@NonNull View itemView) {
            super(itemView);
            Edit = itemView.findViewById(R.id.buttonEditEsame);
        }


        public void setData(DocumentSnapshot documentSnapshot, int position) {
            Map<String, Object> esame = documentSnapshot.getData();

            TextView TXVgiorno = itemView.findViewById(R.id.TXVGiornoEsame);
            Timestamp tmp = (Timestamp) esame.get(KEY_DATA);
            TXVgiorno.setText(esame.get(KEY_NOME)+" del "+ Util.DateToString(tmp.toDate()) + " ore "+ DateToOrario(tmp.toDate()));

            TextView tipo = itemView.findViewById(R.id.TXVTipoEsame);
            tipo.setText("Tipo: "+ esame.get(KEY_TIPO));

            TextView esito = itemView.findViewById(R.id.TXVEsitoEsame);
            if(esame.containsKey(KEY_ESITO)){
                esito.setText("Esito: "+ esame.get(KEY_ESITO));
            }
            else{
                esito.setVisibility(View.GONE);
            }

            TextView referto = itemView.findViewById(R.id.textViewReferto);
            if(esame.containsKey(KEY_REFERTO)){
                referto.setText("Referto: SI");
            }
            else{
                referto.setText("Referto: NO");
            }

            TextView digiuno = itemView.findViewById(R.id.textViewDigiuno);
            if(esame.containsKey(KEY_DIGIUNO)){
                digiuno.setText("Digiuno: SI");
            }
            else{
                digiuno.setText("Digiuno: NO");
            }

            TextView attivazione = itemView.findViewById(R.id.textViewDigiuno);
            TextView ricorda = itemView.findViewById(R.id.TXVRicordaEsame);
            if(esame.containsKey(KEY_ATTIVAZIONE)){
                attivazione.setText("Attivazione 24: SI");
                if(esame.containsKey(KEY_RICORDA)){
                    ricorda.setVisibility(View.VISIBLE);
                    ricorda.setText("Ricorda di: " + esame.get(KEY_RICORDA));
                }
            }
            else{
                digiuno.setText("Attivazione 24: NO");
                ricorda.setVisibility(View.GONE);
            }

            TextView note = itemView.findViewById(R.id.TXVNoteEsame);
            if(esame.containsKey(KEY_NOTE)){
                note.setVisibility(View.VISIBLE);
                note.setText("Note: " + esame.get(KEY_NOTE));
            }
            else note.setVisibility(View.GONE);

            mPosition = position;
        }

        public void setListeners() {
           Edit.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View v) {
                   String id = mQuerySnapshot.get(mPosition).getId();
                   Bundle bundle = new Bundle();
                   bundle.putString("ID", id);
                   Navigation.findNavController(v).navigate(R.id.action_global_modificaEsamiFragment, bundle);
               }
           });
        }
    }
}
