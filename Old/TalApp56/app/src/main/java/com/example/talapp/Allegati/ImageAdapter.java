package com.example.talapp.Allegati;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.talapp.R;
import com.squareup.picasso.Picasso;

import java.util.List;

public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ImageViewHolder> {


    private final LayoutInflater layoutInflater;
    private final Context mContext;
    private List<Uri> mUriList;
    private final String item;
    public final static String UPLOAD = "UPLOAD";
    public final static String DOWNLOAD = "DOWNLOAD";

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

        public ImageViewHolder(@NonNull View itemView) {
            super(itemView);
            //Edit = itemView.findViewById(R.id.buttonEditEsame);
            cardView = itemView.findViewById(R.id.cardViewDownload);
        }

        public void setData(Uri uri, int position) {
            ImageView imageView = itemView.findViewById(R.id.imageView);
            Picasso.get().load(uri).into(imageView);

            mPosition = position;
        }

        public void setListeners() {
            cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mContext, MostraAllegato.class);
                    //Bundle bundle = new Bundle();
                    //bundle.putString();
                    intent.putExtra("uri", mUriList.get(mPosition).toString());
                    intent.putExtra("pos", mPosition);

                    mContext.startActivity(intent);
                }
            });
        }
    }
}
