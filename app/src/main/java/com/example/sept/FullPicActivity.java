package com.example.sept;


import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;


//이미지 전체 화면 페이지
public class FullPicActivity extends AppCompatActivity {


    ImageView img_full;

    Intent intent;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.img_full);
        img_full=findViewById(R.id.img_full);

        intent=getIntent();

        if (intent!=null){
            if(intent.getStringExtra("url")!=null){

                Glide.with(FullPicActivity.this)
                        .load(intent.getStringExtra("url")).into(img_full);
            }
        }


    }


    @Override
    protected void onStart() {
        super.onStart();


    }

    @Override
    protected void onResume() {
        super.onResume();


    }


    @Override
    protected void onPause() {
        super.onPause();

    }


}
