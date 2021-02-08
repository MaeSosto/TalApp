package com.example.talapp.Allegati;

import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.talapp.R;
import com.squareup.picasso.Picasso;

public class MostraAllegato extends AppCompatActivity {
    private Uri uri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mostra_allegato);
        uri = Uri.parse(getIntent().getStringExtra("uri"));
        ImageView imageView = findViewById(R.id.imageViewAllegato);
        Picasso.get().load(uri).into(imageView);
    }
}