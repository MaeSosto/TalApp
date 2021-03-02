package com.example.talapp.Trasfusioni;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.example.talapp.R;
import com.example.talapp.Utils.Util;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.List;
import java.util.Map;

import static com.example.talapp.Utils.Util.KEY_DATA;
import static com.example.talapp.Utils.Util.KEY_HB;
import static com.example.talapp.Utils.Util.KEY_NOTE;
import static com.example.talapp.Utils.Util.KEY_UNITA;
import static com.example.talapp.Utils.Util.TimestampToOrario;

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
        private CardView Edit;

        public TrasfusioniViewHolder(@NonNull View itemView) {
            super(itemView);
            Edit = itemView.findViewById(R.id.card);
        }


        public void setData(DocumentSnapshot documentSnapshot, int position) {
            Map<String, Object> trasfusione = documentSnapshot.getData();
            TextView TXVgiorno = itemView.findViewById(R.id.TXVNomeEsame);
            TXVgiorno.setText("Del "+ Util.TimestampToStringData((Timestamp) trasfusione.get(KEY_DATA)) + " ore "+ TimestampToOrario((Timestamp) trasfusione.get(KEY_DATA)));
            TextView info = itemView.findViewById(R.id.TXVInfoTrasfusione);
            String s ="Unità: " + trasfusione.get(KEY_UNITA);
            if(trasfusione.containsKey(KEY_HB)){ s += " | Valore Hb: " + trasfusione.get(KEY_HB); }
            if(trasfusione.containsKey(KEY_NOTE)){ s += "\nNote: " + trasfusione.get(KEY_NOTE); }
            info.setText(s);

            mPosition = position;
        }

        public void setListeners() {
           Edit.setOnClickListener(v -> {
               String id = mQuerySnapshot.get(mPosition).getId();
               Bundle bundle = new Bundle();
               bundle.putString("ID", id);
               Navigation.findNavController(v).navigate(R.id.action_global_modificaTrasfusioneFragment, bundle);
           });
        }
    }
}
