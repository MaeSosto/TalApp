package com.example.talapp.Sveglie;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.recyclerview.widget.RecyclerView;

import com.example.talapp.R;
import com.example.talapp.Utils.Util;
import com.google.android.material.chip.Chip;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.firebase.Timestamp;

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
    private MutableLiveData<List<Map<String, Object>>> MLDSveglie;

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

    public void setTerapie(List<Map<String, Object>> list, MutableLiveData<List<Map<String, Object>>> MLDSveglie){
        mSveglie = list;
        this.MLDSveglie = MLDSveglie;
        notifyDataSetChanged();
    }

    public class SveglieViewHolder extends RecyclerView.ViewHolder implements CompoundButton.OnCheckedChangeListener {

        private int mPosition;
        private final TextView textViewOrarioSveglia;
        private final SwitchMaterial switchNotificheSveglia;
        private final Chip chipLun;
        private final Chip chipMar;
        private final Chip chipMer;
        private final Chip chipGio;
        private final Chip chipVen;
        private final Chip chipSab;
        private final Chip chipDom;
        private final Button buttonEditSveglia;

        public SveglieViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewOrarioSveglia = itemView.findViewById(R.id.textViewOrarioSveglia);
            switchNotificheSveglia = itemView.findViewById(R.id.switchNotificheSveglia);
            chipLun = itemView.findViewById(R.id.chipLun);
            chipMar = itemView.findViewById(R.id.chipMar);
            chipMer = itemView.findViewById(R.id.chipMer);
            chipGio = itemView.findViewById(R.id.chipGio);
            chipVen = itemView.findViewById(R.id.chipVen);
            chipSab = itemView.findViewById(R.id.chipSab);
            chipDom = itemView.findViewById(R.id.chipDom);
            buttonEditSveglia = itemView.findViewById(R.id.buttonEditSveglia);
        }


        public void setData(Map<String, Object> sveglia, int position) {

            Log.d("SVEGLIA", String.valueOf(sveglia));

            //Date data = (Date) sveglia.get(KEY_ORARIO);
            try {
                textViewOrarioSveglia.setText(Util.TimestampToOrario((Timestamp) sveglia.get(KEY_ORARIO)));
            } catch (Exception e) {
                textViewOrarioSveglia.setText(Util.DateToOrario((Date) sveglia.get(KEY_ORARIO)));
            }

            List<Boolean> settimana = (List<Boolean>) sveglia.get(KEY_SETTIMANA);
            //Log.d("SVEGLIA", String.valueOf(settimana));
            //Log.d("SVEGLIA", String.valueOf(settimana.get(0)));

            chipLun.setChecked(settimana.get(0));
            chipMar.setChecked(settimana.get(1));
            chipMer.setChecked(settimana.get(2));
            chipGio.setChecked(settimana.get(3));
            chipVen.setChecked(settimana.get(4));
            chipSab.setChecked(settimana.get(5));
            chipDom.setChecked(settimana.get(6));

            switchNotificheSveglia.setChecked((boolean) sveglia.get(KEY_NOTIFICHE));

            mPosition = position;
        }

        public void setListeners() {
            buttonEditSveglia.setOnClickListener(v -> {
              //AggiungiTerapieFragment.removeSveglia(mPosition);
                List<Map<String, Object>> tmp = MLDSveglie.getValue();
                tmp.remove(mPosition);
                MLDSveglie.setValue(tmp);
            });

            chipLun.setOnCheckedChangeListener(this);
            chipMar.setOnCheckedChangeListener(this);
            chipMer.setOnCheckedChangeListener(this);
            chipGio.setOnCheckedChangeListener(this);
            chipVen.setOnCheckedChangeListener(this);
            chipSab.setOnCheckedChangeListener(this);
            chipDom.setOnCheckedChangeListener(this);
            switchNotificheSveglia.setOnCheckedChangeListener(this);
        }

        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

            Map<String, Object> MAPtmp = mSveglie.get(mPosition);
            List<Boolean> settimana = (List<Boolean>) MAPtmp.get(KEY_SETTIMANA);
            Boolean bool = (Boolean) MAPtmp.get(KEY_NOTIFICHE);

                    switch (buttonView.getId()){
                case R.id.chipLun:
                    settimana.set(0, isChecked);
                    break;
                case R.id.chipMar:
                    settimana.set(1, isChecked);
                    break;
                case R.id.chipMer:
                    settimana.set(2, isChecked);
                    break;
                case R.id.chipGio:
                    settimana.set(3, isChecked);
                    break;
                case R.id.chipVen:
                    settimana.set(4, isChecked);
                    break;
                case R.id.chipSab:
                    settimana.set(5, isChecked);
                    break;
                case R.id.chipDom:
                    settimana.set(6, isChecked);
                    break;
                case R.id.switchNotificheSveglia:
                    bool = isChecked;
                    break;

            }
            MAPtmp.put(KEY_SETTIMANA,settimana);
            MAPtmp.put(KEY_NOTIFICHE, bool);
            //AggiungiTerapieFragment.changeItem(tmp, mPosition);

            List<Map<String, Object>> list = MLDSveglie.getValue();
            list.set(mPosition, MAPtmp);
            MLDSveglie.setValue(list);
        }
    }
}
