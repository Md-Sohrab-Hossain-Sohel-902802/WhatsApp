package com.example.whatsapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

public class ImageViewerActivity extends AppCompatActivity {

    private ImageView imageView;
    private  String imageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_viewer);

    imageUri=getIntent().getStringExtra("image");




        imageView=findViewById(R.id.imageviewid);
        Picasso.get().load(imageUri).into(imageView);









    }
}