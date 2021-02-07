package com.example.talapp.Esami;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.example.talapp.R;
import com.example.talapp.Utils.Util;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.List;
import java.util.Map;

import static com.example.talapp.Utils.Util.DateToOrario;
import static com.example.talapp.Utils.Util.KEY_ALLEGATI;
import static com.example.talapp.Utils.Util.KEY_ATTIVAZIONE;
import static com.example.talapp.Utils.Util.KEY_DATA;
import static com.example.talapp.Utils.Util.KEY_DIGIUNO;
import static com.example.talapp.Utils.Util.KEY_ESITO;
import static com.example.talapp.Utils.Util.KEY_NOME;
import static com.example.talapp.Utils.Util.KEY_NOTE;
import static com.example.talapp.Utils.Util.KEY_REFERTO;
import static com.example.talapp.Utils.Util.KEY_RICORDA;
import static com.example.talapp.Utils.Util.KEY_TIPO;
import static com.example.talapp.Utils.Util.TimestampToOrario;

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

            ImageView imageViewAllegato = itemView.findViewById(R.id.imageViewAllegato);
            if(esame.containsKey(KEY_ALLEGATI)){

                List<String> list = (List<String>) esame.get(KEY_ALLEGATI);
                if(list.size() > 0){
                    StorageReference mStorageRef = FirebaseStorage.getInstance().getReference("Allegati esami");
                    mStorageRef.child(documentSnapshot.getId() + "/");
                    StorageReference storageReference = mStorageRef.child(list.get(0));
                    storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            Picasso.get().load(uri).into(imageViewAllegato);
                        }
                    });
                }
                else imageViewAllegato.setVisibility(View.GONE);
            }
            else imageViewAllegato.setVisibility(View.GONE);


            TextView TXVgiorno = itemView.findViewById(R.id.TXVGiornoEsame);
            TXVgiorno.setText(esame.get(KEY_NOME)+" del "+ Util.TimestampToStringData((Timestamp) esame.get(KEY_DATA)) + " ore "+ TimestampToOrario((Timestamp) esame.get(KEY_DATA)));

            TextView tipo = itemView.findViewById(R.id.TXVTipoEsame);
            tipo.setText("Tipo: "+ esame.get(KEY_TIPO));

            TextView esito = itemView.findViewById(R.id.TXVEsitoEsame);
            if(esame.containsKey(KEY_ESITO)) esito.setText("Esito: "+ esame.get(KEY_ESITO));
            else esito.setVisibility(View.GONE);

            TextView TXVAltro = itemView.findViewById(R.id.TXVAltro);
            StringBuilder altro = new StringBuilder();
            if(esame.containsKey(KEY_DIGIUNO)) altro.append("Digiuno: SI");
            else altro.append("Digiuno: NO");

            if(esame.containsKey(KEY_ATTIVAZIONE)){
                if(!altro.toString().isEmpty()) altro.append(" | ");
                altro.append("Attivazione 24: SI");
                if(esame.containsKey(KEY_RICORDA)){
                    altro.append(" | Ricorda di: " + esame.get(KEY_RICORDA));
                }
            }
            else{
                if(!altro.toString().isEmpty()) altro.append(" | ");
                altro.append("Attivazione 24: NO");
            }

            TXVAltro.setText(altro.toString());

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
