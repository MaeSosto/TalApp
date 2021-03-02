package com.MartinaSosto.talapp.Esami;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.MartinaSosto.talapp.R;
import com.MartinaSosto.talapp.Utils.Util;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.List;
import java.util.Map;

import static com.MartinaSosto.talapp.Utils.Util.KEY_ALLEGATI;
import static com.MartinaSosto.talapp.Utils.Util.KEY_ANALISI;
import static com.MartinaSosto.talapp.Utils.Util.KEY_ATTIVAZIONE;
import static com.MartinaSosto.talapp.Utils.Util.KEY_DATA;
import static com.MartinaSosto.talapp.Utils.Util.KEY_DIGIUNO;
import static com.MartinaSosto.talapp.Utils.Util.KEY_ESITO;
import static com.MartinaSosto.talapp.Utils.Util.KEY_NOME;
import static com.MartinaSosto.talapp.Utils.Util.KEY_NOTE;
import static com.MartinaSosto.talapp.Utils.Util.KEY_RICORDA;
import static com.MartinaSosto.talapp.Utils.Util.KEY_TIPO;
import static com.MartinaSosto.talapp.Utils.Util.TimestampToOrario;
import static com.MartinaSosto.talapp.Utils.Util.Utente;

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
        private final CardView Edit;

        public EsamiViewHolder(@NonNull View itemView) {
            super(itemView);
            Edit = itemView.findViewById(R.id.card);
        }


        public void setData(DocumentSnapshot documentSnapshot, int position) {
            Map<String, Object> esame = documentSnapshot.getData();

            ImageView imageViewAllegato = itemView.findViewById(R.id.imageViewAllegato);
            if(esame.containsKey(KEY_ALLEGATI)){
                Log.d("ALLEGATI", "CONTIENE KEY");
                List<String> list = (List<String>) esame.get(KEY_ALLEGATI);
                Log.d("ALLEGATI", String.valueOf(list.size()));
                if(list.size() > 0){
                    StorageReference mStorageRef = FirebaseStorage.getInstance().getReference(Utente+"/"+ documentSnapshot.getId() + "/");
                    StorageReference storageReference = mStorageRef.child(list.get(0));
                    storageReference.getDownloadUrl().addOnCompleteListener(task -> {
                        Picasso.get().load(task.getResult()).into(imageViewAllegato);
                        imageViewAllegato.setVisibility(View.VISIBLE);
                    });
                }
                else imageViewAllegato.setVisibility(View.GONE);
            }
            else imageViewAllegato.setVisibility(View.GONE);

            TextView TXVTipoEsame = itemView.findViewById(R.id.TXVTipoEsame);
            TXVTipoEsame.setText("Esame "+esame.get(KEY_TIPO));

            TextView tipo = itemView.findViewById(R.id.TXVDataOraEsame);
            tipo.setText("Del "+ Util.TimestampToStringData((Timestamp) esame.get(KEY_DATA)) + " ore "+ TimestampToOrario((Timestamp) esame.get(KEY_DATA)));

            TextView info = itemView.findViewById(R.id.TXVInfoEsame);
            String s = "";
            if(esame.containsKey(KEY_DIGIUNO)) s += "Digiuno";
            if(esame.containsKey(KEY_ATTIVAZIONE)){
                if(!s.isEmpty()) s += " | ";
                s += "Attivazione 24";
                if(esame.containsKey(KEY_RICORDA)){ s += ": " + esame.get(KEY_RICORDA);}
            }
            if(esame.containsKey(KEY_NOTE)){ s +="\nNote: " + esame.get(KEY_NOTE); }
            if(!s.isEmpty()){
                info.setVisibility(View.VISIBLE);
                info.setText(s);
            }

            TextView TXVEsito = itemView.findViewById(R.id.TXVEsito);
            if(esame.containsKey(KEY_ANALISI)) {
                List<Map<String, Object>> list = (List<Map<String, Object>>) esame.get(KEY_ANALISI);
                s = "";
                for (int i = 0; i < list.size(); i++) {
                    //Analisi analisi = documentSnapshot.to
                    s += "• " + list.get(i).get(KEY_NOME);
                    if (list.get(i).get(KEY_ESITO) != null)
                        s += " [" + list.get(i).get(KEY_ESITO) + "]";
                    if (i + 1 < list.size()) s += "\n";
                }
            }
            else{
                s = "• " + esame.get(KEY_NOME);
                if(esame.get(KEY_ESITO) != null)  s += " [" + esame.get(KEY_ESITO) + "]";
            }
                TXVEsito.setVisibility(View.VISIBLE);
                TXVEsito.setText(s);

            mPosition = position;
        }

        public void setListeners() {
           Edit.setOnClickListener(v -> {
               String id = mQuerySnapshot.get(mPosition).getId();
               Bundle bundle = new Bundle();
               bundle.putString("ID", id);
               Navigation.findNavController(v).navigate(R.id.action_global_modificaEsamiFragment, bundle);
           });
        }
    }
}
