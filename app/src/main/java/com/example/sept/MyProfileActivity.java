package com.example.sept;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.example.sept.Class.PersonalPicGet;
import com.example.sept.Interface.UploadService;
import com.example.sept.Retrofit.MyRetrofit2;


import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MyProfileActivity extends AppCompatActivity {


    Toolbar up_toolbar;
    TextView my_id;
    ImageView my_pic;
    ImageView card1;
    ImageView card2;
    ImageView card3;
    Intent fullpic;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.my_profile);
        setTitle("마이페이지");
        up_toolbar=findViewById(R.id.up_toolbar);
        setSupportActionBar(up_toolbar);
        my_id=findViewById(R.id.my_id);
        my_pic=findViewById(R.id.my_pic);

        my_pic.setBackground(new ShapeDrawable(new OvalShape()));

        if(Build.VERSION.SDK_INT>=21){
              my_pic.setClipToOutline(true);
        }

        card1=findViewById(R.id.p_card1);
        card2=findViewById(R.id.p_card2);
        card3=findViewById(R.id.p_card3);

        fullpic=new Intent(MyProfileActivity.this,FullPicActivity.class);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_my, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_logout) { //로그아웃을 눌렀을 때
            Log.i("logout","bye");
            //저장해놓은 아이디와 자동로그인 부분을 모두 삭제.
            SharedPreferences login_true=getSharedPreferences("login_true", Activity.MODE_PRIVATE);
            SharedPreferences auto_login=getSharedPreferences("auto_login", Activity.MODE_PRIVATE);
            SharedPreferences ideal_pic_save_gender=getSharedPreferences("ideal_pic_save_gender",MODE_PRIVATE);
            SharedPreferences ideal_pic_save_first=getSharedPreferences("ideal_pic_save_first",MODE_PRIVATE);
            SharedPreferences ideal_pic_save_second=getSharedPreferences("ideal_pic_save_second",MODE_PRIVATE);
            SharedPreferences ideal_pic_save_gender_second=getSharedPreferences("ideal_pic_save_gender_second",MODE_PRIVATE);
            SharedPreferences.Editor editor=login_true.edit();
            SharedPreferences.Editor editor1=auto_login.edit();
            SharedPreferences.Editor editor2=ideal_pic_save_gender.edit();
            SharedPreferences.Editor editor3=ideal_pic_save_first.edit();
            SharedPreferences.Editor editor4=ideal_pic_save_second.edit();
            SharedPreferences.Editor editor5=ideal_pic_save_gender_second.edit();

            editor.remove("login_email");
            editor1.remove("login_email");
            editor2.remove("ideal_pic_save_gender");
            editor3.remove("ideal_pic_save_first");
            editor4.remove("ideal_pic_save_second");
            editor5.remove("ideal_pic_save_gender_second");
            editor.clear();
            editor1.clear();
            editor2.clear();
            editor3.clear();
            editor4.clear();
            editor5.clear();
            editor.commit();
            editor1.commit();
            editor2.commit();
            editor3.commit();
            editor4.commit();
            editor5.commit();
            Intent intent=new Intent(getApplicationContext(),MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
            Toast.makeText(getApplicationContext(), "logout clicked", Toast.LENGTH_LONG).show();
            return true;
        }else if (id == R.id.action_main) { //메인페이지를 눌렀을 때
            Toast.makeText(getApplicationContext(), "main clicked", Toast.LENGTH_LONG).show();
            Intent intent=new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
            finish();
            return true;
        }else if (id == R.id.action_show) { //회원목록
            Toast.makeText(getApplicationContext(), "mypage clicked", Toast.LENGTH_LONG).show();
            Intent intent=new Intent(getApplicationContext(), CustomListActivity.class);
            startActivity(intent);
            finish();
            return true;
        }else if(id==R.id.action_conversation){
            //대화방눌렀을 때
            Intent intent=new Intent(getApplicationContext(), ConversationListActivity.class);
            startActivity(intent);
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStart() {
        super.onStart();


        SharedPreferences login_true=getSharedPreferences("login_true", Activity.MODE_PRIVATE);
        // 로그인 이메일 정보를 가지고 옴.
        String loginEmail = login_true.getString("login_email", "no");
        //아이디값 넣어주기

        UploadService service = MyRetrofit2.getRetrofit2().create(UploadService.class);

        RequestBody description = RequestBody.create(
                MediaType.parse("multipart/form-data"), loginEmail);

        Log.i("part3",description.toString());

        Call<PersonalPicGet> call1 = service.profilePic(description);


        call1.enqueue(new Callback<PersonalPicGet>() {
            @Override
            public void onResponse(Call<PersonalPicGet> call, Response<PersonalPicGet> response) {
                Log.i("myprofile success",response.toString());
                //파일이 업로드에 성공했다면,...기존에 저장해두었던 파일을 삭제하고 완료로 넘김.
                //즉  shared도 지우고 그리구 sdcards에 저장되어 있는 사진도 지워줍니다요!!!

//                Log.i("myprofile success",response.body().getUrl());
//
//

//            //경로만 가지고오면된다.
               Glide.with(MyProfileActivity.this)
                       .load(response.body().getUrl()).into(my_pic);


                fullpic.putExtra("url",response.body().getUrl());

                SharedPreferences login_true=getSharedPreferences("login_true", Activity.MODE_PRIVATE);
                // 로그인 이메일 정보를 가지고 옴.
                String loginEmail = login_true.getString("login_email", "no");
                //아이디값 넣어주기

                my_id.setText(loginEmail+"/"+response.body().getGender()+"("+response.body().getAge()+"살)");


            }

            @Override
            public void onFailure(Call<PersonalPicGet> call, Throwable t) {
                Log.i("myprofile fail",t.toString());
            }
        });



    }

    @Override
    protected void onResume() {
        super.onResume();

    my_pic.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Log.i("mypic click","full screen");

            startActivity(fullpic);
        }
    });


    //card1 -> 프로필 수정
        card1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i("myprofile","reset");
                Intent intent=new Intent(MyProfileActivity.this, MyProfileReActivity.class);
                startActivity(intent);
            }
        });

        card2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i("myprofile","이상형설정");
                Intent intent=new Intent(MyProfileActivity.this, IdealPicActivity.class);
                startActivity(intent);
            }
        });


        card3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i("myprofile","대화신청목록");
                Intent intent=new Intent(MyProfileActivity.this, ConverseComeListActivity.class);
                startActivity(intent);
            }
        });

    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
