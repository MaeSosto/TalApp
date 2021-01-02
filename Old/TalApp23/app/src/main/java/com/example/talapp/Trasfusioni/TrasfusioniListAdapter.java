package com.example.talapp.Trasfusioni;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.example.talapp.R;
import com.example.talapp.Utils.Util;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.List;
import java.util.Map;

import static com.example.talapp.Utils.Util.KEY_TRASFUSIONE_DATA;
import static com.example.talapp.Utils.Util.KEY_TRASFUSIONE_HB;
import static com.example.talapp.Utils.Util.KEY_TRASFUSIONE_NOTE;
import static com.example.talapp.Utils.Util.KEY_TRASFUSIONE_UNITA;

public class TrasfusioniListAdapter extends RecyclerView.Adapter<TrasfusioniListAdapter.TrasfusioniViewHolder> {

    private final LayoutInflater layoutInflater;
    private final Context mContext;
    private List<DocumentSnapshot> mQuerySnapshot;

    public TrasfusioniListAdapter(Context context) {
        layoutInflater = LayoutInflater.from(context);
        mContext = context;
    }

    @NonNull
    @Override
    public TrasfusioniViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = layoutInflater.inflate(R.layout.list_item_trasfusioni, parent, false);
        return new TrasfusioniViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull TrasfusioniListAdapter.TrasfusioniViewHolder holder, int position) {
        if(mQuerySnapshot != null){
            DocumentSnapshot documentSnapshot = mQuerySnapshot.get(position);
            Log.i("SingleDataSS", String.valueOf(documentSnapshot));
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

    public void setTrasfusioni(List<DocumentSnapshot> querySnapshot){
        mQuerySnapshot = querySnapshot;
        notifyDataSetChanged();
    }

    public class TrasfusioniViewHolder extends RecyclerView.ViewHolder {

        private int mPosition;
        private  ImageView IMGEdit;

        public TrasfusioniViewHolder(@NonNull View itemView) {
            super(itemView);
            IMGEdit = itemView.findViewById(R.id.IMGEdit);
        }


        public void setData(DocumentSnapshot documentSnapshot, int position) {
            Map<String, Object> trasfusione = documentSnapshot.getData();
            TextView TXVgiorno = itemView.findViewById(R.id.TXVGiornoTrasfusione);
            TXVgiorno.setText("Trasfusione del "+ Util.DateToString(Util.LongToDate((Long) trasfusione.get(KEY_TRASFUSIONE_DATA))) + " ore "+ Util.LongToDate((Long) trasfusione.get(KEY_TRASFUSIONE_DATA)).getHours()+ ":"+ Util.LongToDate((Long) trasfusione.get(KEY_TRASFUSIONE_DATA)).getMinutes());
            TextView unita = itemView.findViewById(R.id.TXVUnitaTrasfusione);
            unita.setText("Unità: " + trasfusione.get(KEY_TRASFUSIONE_UNITA));
            TextView hb = itemView.findViewById(R.id.TXVHbTrasfusione);
            if(trasfusione.containsKey(KEY_TRASFUSIONE_HB)){
                hb.setVisibility(View.VISIBLE);
                hb.setText("Valore Hb: " + trasfusione.get(KEY_TRASFUSIONE_HB));
            }
            else  hb.setVisibility(View.GONE);
            TextView note = itemView.findViewById(R.id.TXVNoteTrasfusione);
            if(trasfusione.containsKey(KEY_TRASFUSIONE_NOTE)){
                note.setVisibility(View.VISIBLE);
                note.setText("Note: " + trasfusione.get(KEY_TRASFUSIONE_NOTE));
            }
            else note.setVisibility(View.GONE);
            mPosition = position;
        }

        public void setListeners() {
           IMGEdit.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View v) {
                   String id = mQuerySnapshot.get(mPosition).getId();
                   Bundle bundle = new Bundle();
                   bundle.putString("TrasfusioneID", id);
                   Navigation.findNavController(v).navigate(R.id.actionModificaTrasfusione, bundle);
               }
           });
        }
    }
}
