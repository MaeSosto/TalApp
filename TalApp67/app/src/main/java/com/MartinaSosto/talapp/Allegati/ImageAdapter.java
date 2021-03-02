package com.MartinaSosto.talapp.Allegati;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.MartinaSosto.talapp.Esami.ModificaEsamiFragment;
import com.MartinaSosto.talapp.R;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.List;

import static com.MartinaSosto.talapp.Notification.ForegroundService.esamiRef;
import static com.MartinaSosto.talapp.Utils.Util.KEY_ALLEGATI;
import static com.MartinaSosto.talapp.Utils.Util.Utente;

public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ImageViewHolder> {


    private final LayoutInflater layoutInflater;
    private final Context mContext;
    private List<Uri> mUriList;
    private final String item;
    public final static String UPLOAD = "UPLOAD";
    public final static String DOWNLOAD = "DOWNLOAD";
    public boolean elimina = true;

    public ImageAdapter(Context context, String tipo) {
        item = tipo;
        layoutInflater = LayoutInflater.from(context);
        mContext = context;
    }

    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = null;
        if(item.equals(UPLOAD)) itemView = layoutInflater.inflate(R.layout.list_item_image_upload, parent, false);
        if(item.equals(DOWNLOAD)) itemView = layoutInflater.inflate(R.layout.list_item_image_download, parent, false);
        assert itemView != null;
        return new ImageViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ImageAdapter.ImageViewHolder holder, int position) {
        Uri uri = mUriList.get(position);
        holder.setData(uri, position);
        if(item.equals(DOWNLOAD)) holder.setListeners();
    }

    @Override
    public int getItemCount() {
        return mUriList.size();
    }

    public void setImage(List<Uri> uriList){
        mUriList = uriList;
        notifyDataSetChanged();
    }

    public class ImageViewHolder extends RecyclerView.ViewHolder {

        private int mPosition;
        private final CardView cardView;// = itemView.findViewById(R.id.cardViewDownload);
        private final Button buttonEliminaAllegato;
        public Uri uri;
        public String idUri;

        public ImageViewHolder(@NonNull View itemView) {
            super(itemView);
            buttonEliminaAllegato = itemView.findViewById(R.id.buttonEliminaAllegato);
            cardView = itemView.findViewById(R.id.cardViewDownload);
        }

        public void setData(Uri uri, int position) {
            this.uri = uri;
            ImageView imageView = itemView.findViewById(R.id.imageView);
            Picasso.get().load(uri).into(imageView);

            mPosition = position;
        }

        public void setListeners() {
            cardView.setOnClickListener(v -> {
                Intent intent = new Intent(mContext, MostraAllegato.class);
                intent.putExtra("uri", mUriList.get(mPosition).toString());
                intent.putExtra("pos", mPosition);

                mContext.startActivity(intent);
            });


            buttonEliminaAllegato.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ModificaEsamiFragment.removeFromListDownload(uri);
                    Snackbar snackbar = Snackbar
                            .make(v, "Immagine rimossa", BaseTransientBottomBar.LENGTH_LONG)
                            .setAction("Cancella operazione", v1 -> elimina = false)
                            .addCallback(new BaseTransientBottomBar.BaseCallback<Snackbar>() {
                                @Override
                                public void onDismissed(Snackbar transientBottomBar, int event) {
                                    super.onDismissed(transientBottomBar, event);
                                    Log.d("SNACKBAR", "DISMISS");
                                    if(elimina) {
                                        idUri = ModificaEsamiFragment.getListAllegati().get(mPosition);
                                        StorageReference mStorageRef = FirebaseStorage.getInstance().getReference(Utente);
                                        StorageReference fileReference = mStorageRef.child("/" + ModificaEsamiFragment.getIdEsame() + "/" + idUri);
                                        Log.d("ELIMINO ALLEGATO", fileReference.toString());
                                        fileReference.delete().addOnCompleteListener(task -> esamiRef.document(ModificaEsamiFragment.getIdEsame()).get().addOnCompleteListener(task1 -> {
                                            Log.d("LISTA ALLEGATI", String.valueOf(ModificaEsamiFragment.getListAllegati()));
                                            List<String> list = ModificaEsamiFragment.getListAllegati();
                                            list.remove(mPosition);
                                            ModificaEsamiFragment.setListAllegati(list);
                                            Log.d("LISTA ALLEGATI", String.valueOf(ModificaEsamiFragment.getListAllegati()));
                                            esamiRef.document(ModificaEsamiFragment.getIdEsame()).update(KEY_ALLEGATI, list).addOnCompleteListener(task11 -> {

                                            });
                                        }));
                                    }
                                    else{
                                        ModificaEsamiFragment.addInListDownload(uri);
                                    }
                                }
                            });
                    snackbar.show();
                }
            });
        }
    }
}
