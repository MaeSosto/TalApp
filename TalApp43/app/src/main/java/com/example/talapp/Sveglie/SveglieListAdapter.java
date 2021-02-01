package com.example.talapp.Sveglie;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.talapp.R;
import com.example.talapp.Utils.Util;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.Date;
import java.util.List;
import java.util.Map;

import static com.example.talapp.Utils.Util.KEY_NOTIFICHE;
import static com.example.talapp.Utils.Util.KEY_ORARIO;
import static com.example.talapp.Utils.Util.KEY_SETTIMANA;

public class SveglieListAdapter extends RecyclerView.Adapter<SveglieListAdapter.SveglieViewHolder> {

    private final LayoutInflater layoutInflater;
    private final Context mContext;
    private List<Map<String, Object>> mSveglie;

    public SveglieListAdapter(Context context) {
        layoutInflater = LayoutInflater.from(context);
        mContext = context;
    }

    @NonNull
    @Override
    public SveglieViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = layoutInflater.inflate(R.layout.list_item_sveglie, parent, false);
        return new SveglieViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull SveglieListAdapter.SveglieViewHolder holder, int position) {
        if(mSveglie != null){
            Map<String, Object> sveglia = mSveglie.get(position);
            holder.setData(sveglia, position);
            holder.setListeners();
        }
    }

    @Override
    public int getItemCount() {
        if(mSveglie != null)
            return mSveglie.size();
        else return 0;
    }

    public void setTerapie(List<Map<String, Object>> list){
        mSveglie = list;
        notifyDataSetChanged();
    }

    public class SveglieViewHolder extends RecyclerView.ViewHolder {

        private int mPosition;
        private final TextView textViewOrarioSveglia;
        private final Switch switchNotificheSveglia;
        private final Chip checkBoxLun;
        private final Chip checkBoxMar;
        private final Chip checkBoxMer;
        private final Chip checkBoxGio;
        private final Chip checkBoxVen;
        private final Chip checkBoxSab;
        private final Chip checkBoxDom;

        public SveglieViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewOrarioSveglia = itemView.findViewById(R.id.textViewOrarioSveglia);
            switchNotificheSveglia = itemView.findViewById(R.id.switchNotificheSveglia);
            checkBoxLun = itemView.findViewById(R.id.checkBoxLun);
            checkBoxMar = itemView.findViewById(R.id.checkBoxMar);
            checkBoxMer = itemView.findViewById(R.id.checkBoxMer);
            checkBoxGio = itemView.findViewById(R.id.checkBoxGio);
            checkBoxVen = itemView.findViewById(R.id.checkBoxVen);
            checkBoxSab = itemView.findViewById(R.id.checkBoxSab);
            checkBoxDom = itemView.findViewById(R.id.checkBoxDom);
        }


        public void setData(Map<String, Object> sveglia, int position) {
            //Map<String, Object> sveglia = documentSnapshot.getData();
            Date data = (Date) sveglia.get(KEY_ORARIO);
            textViewOrarioSveglia.setText(data.getHours()+":"+ data.getMinutes());

            Boolean[] settimana = (Boolean[]) sveglia.get(KEY_SETTIMANA);
            if(settimana[0]) checkBoxLun.isChecked();
            if(settimana[1]) checkBoxMar.isChecked();
            if(settimana[2]) checkBoxMer.isChecked();
            if(settimana[3]) checkBoxGio.isChecked();
            if(settimana[4]) checkBoxVen.isChecked();
            if(settimana[5]) checkBoxSab.isChecked();
            if(settimana[6]) checkBoxDom.isChecked();



            switchNotificheSveglia.setChecked((boolean) sveglia.get(KEY_NOTIFICHE));

            mPosition = position;
        }

        public void setListeners() {



            //Edit.setOnClickListener(new View.OnClickListener() {
            //    @Override
            //    public void onClick(View v) {
            //        //String id = mQuerySnapshot.get(mPosition).getId();
            //        //Bundle bundle = new Bundle();
            //        //bundle.putString("ID", id);
            //        //Navigation.findNavController(v).navigate(R.id.action_global_modificaTerapiaFragment, bundle);
            //    }
            //});
        }
    }
}
