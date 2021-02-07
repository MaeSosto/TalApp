package com.example.talapp.Allegati;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.talapp.R;

import java.util.List;

public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ImageViewHolder> {

    private final LayoutInflater layoutInflater;
    private final Context mContext;
    private List<Uri> mUriList;

    public ImageAdapter(Context context) {
        layoutInflater = LayoutInflater.from(context);
        mContext = context;
    }

    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = layoutInflater.inflate(R.layout.list_item_image, parent, false);
        return new ImageViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ImageAdapter.ImageViewHolder holder, int position) {
        Uri uri = mUriList.get(position);
        holder.setData(uri, position);
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

        public ImageViewHolder(@NonNull View itemView) {
            super(itemView);
            //Edit = itemView.findViewById(R.id.buttonEditEsame);
        }

        public void setData(Uri uri, int position) {
            ImageView imageView = itemView.findViewById(R.id.imageView);
            imageView.setImageURI(uri);
            imageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);

            mPosition = position;
        }

    }
}
