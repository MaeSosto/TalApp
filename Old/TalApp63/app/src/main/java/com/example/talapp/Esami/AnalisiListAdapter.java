package com.example.talapp.Esami;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.talapp.R;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.List;
import java.util.Map;

import static com.example.talapp.Esami.ModificaEsamiFragment.updateEsito;
import static com.example.talapp.Utils.Util.KEY_ESITO;
import static com.example.talapp.Utils.Util.KEY_NOME;


public class AnalisiListAdapter extends RecyclerView.Adapter<AnalisiListAdapter.AnalisiViewHolder> {

    private final LayoutInflater layoutInflater;
    private final Context mContext;
    private List<Map<String, Object>> mAnalisi;
    private String operazione;
    public static String KEY_MOSTRA = "mostra";
    public static String KEY_MODIFICA = "modifica";

    public AnalisiListAdapter(Context context, String string) {
        layoutInflater = LayoutInflater.from(context);
        mContext = context;
        this.operazione = string;
    }

    @NonNull
    @Override
    public AnalisiViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = layoutInflater.inflate(R.layout.list_item_analisi, parent, false);
        return new AnalisiViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull AnalisiListAdapter.AnalisiViewHolder holder, int position) {
        if(mAnalisi != null){
            Map<String, Object> Analisi = mAnalisi.get(position);
            holder.setData(Analisi, position);
            holder.setListeners();
        }
    }

    @Override
    public int getItemCount() {
        if(mAnalisi != null)
            return mAnalisi.size();
        else return 0;
    }

    public void setAnalisi(List<Map<String, Object>> querySnapshot){
        mAnalisi = querySnapshot;
        notifyDataSetChanged();
    }

    public class AnalisiViewHolder extends RecyclerView.ViewHolder {

        private int mPosition;
        private final TextInputEditText editTextEsito;

        public AnalisiViewHolder(@NonNull View itemView) {
            super(itemView);
            editTextEsito = itemView.findViewById(R.id.editTextEsito);
        }


        public void setData(Map<String, Object> Analisi, int position) {
            TextView textView = itemView.findViewById(R.id.textViewAnalisi);
            textView.setText("• "+ Analisi.get(KEY_NOME));
            textView.setVisibility(View.VISIBLE);

            if(operazione.compareTo(KEY_MODIFICA) == 0){
                TextInputLayout textInputLayout = itemView.findViewById(R.id.TILEsito);
                textInputLayout.setVisibility(View.VISIBLE);
                TextInputEditText textInputEditText = itemView.findViewById(R.id.editTextEsito);
                if(Analisi.get(KEY_ESITO) != null) {
                    textInputEditText.setText(String.valueOf(Analisi.get(KEY_ESITO)));
                }
            }

            mPosition = position;
        }

        public void setListeners(){
            editTextEsito.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {}

                @Override
                public void afterTextChanged(Editable s) {
                    Double num = Double.valueOf(s.toString());
                    updateEsito(mAnalisi.get(mPosition).get(KEY_NOME), num);
                }
            });
        }
    }
}
