package com.example.sept;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.sept.Interface.UploadService;
import com.example.sept.Retrofit.MyRetrofit2;
import com.example.sept.Service.ChatService;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

//나에게 대화를 신청 혹은 내가 대화를 신청한 사람들의 목록 리스트 보기
public class ProfileShowAcceptActivity extends AppCompatActivity {



    ImageView pic_show;
    TextView nick_show;
    TextView age_show;
    TextView gender_show;
    TextView height_show;
    TextView region_show;
    TextView job_show;
    TextView smoke_show;


    LinearLayout convers_ok_no;

    ImageView convers_ok;
    ImageView convers_no;


    LinearLayout convers_cancel;
    ImageView convers_ask_no;


    Intent intent;
    String id;
    Intent fullpic;


    Intent alert;



    String loginTrue;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_show_accept);

        setTitle("프로필 상세보기");

        pic_show=findViewById(R.id.pic_show);
        nick_show=findViewById(R.id.nick_show);
        age_show=findViewById(R.id.age_show);
        gender_show=findViewById(R.id.gender_show);
        height_show=findViewById(R.id.height_show);
        region_show=findViewById(R.id.region_show);
        job_show=findViewById(R.id.job_show);
        smoke_show=findViewById(R.id.smoke_show);

        convers_ok_no=findViewById(R.id.convers_ok_no);
        convers_cancel=findViewById(R.id.convers_cancel);


        convers_ok=findViewById(R.id.conver_ok);
        convers_no=findViewById(R.id.conver_no);

        convers_ask_no=findViewById(R.id.conver_ask_no);

        intent=getIntent();

        if(intent!=null){
            if(intent.getStringExtra("come_id")!=null){
                id=intent.getStringExtra("come_id");
                convers_ok_no.setVisibility(View.VISIBLE);
            }else{
                id=intent.getStringExtra("go_id");
               // convers_cancel.setVisibility(View.VISIBLE);
            }
        }

        fullpic=new Intent(getApplicationContext(),FullPicActivity.class);
    }

    @Override
    protected void onStart() {
        super.onStart();


        SharedPreferences login_true=getSharedPreferences("login_true", Activity.MODE_PRIVATE);
        // 로그인 이메일 정보를 가지고 옴.
        loginTrue = login_true.getString("login_email","no");



        UploadService service = MyRetrofit2.getRetrofit2().create(UploadService.class);

        RequestBody c_id_email= RequestBody.create(
                MediaType.parse("multipart/form-data"), id);


        Call<JsonObject> call1 = service.profileShow(c_id_email);


        call1.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {

                try {
                    JSONObject jsonObject=new JSONObject(new Gson().toJson(response.body()));


                    alert=new Intent(getApplicationContext(),AlertActivity.class);
                    alert.putExtra("id",jsonObject.getString("c_id_email"));
                    alert.putExtra("nick",jsonObject.getString("c_nickname"));


                    String picname=jsonObject.getString("c_img_uri");

                    Glide.with(ProfileShowAcceptActivity.this)
                       .load(picname).into(pic_show);

                    fullpic.putExtra("url",jsonObject.getString("c_img_uri"));

                    nick_show.setText("닉네임: "+jsonObject.getString("c_nickname"));

                    gender_show.setText(jsonObject.getString("c_gender"));

                    age_show.setText(jsonObject.getString("c_age")+"세");

                    height_show.setText(jsonObject.getString("c_height")+"cm");


                    String[] city = getResources().getStringArray(R.array.city);

                    int region=jsonObject.getInt("c_region");
                    region_show.setText(city[region]);


                    String[] job = getResources().getStringArray(R.array.job);
                    int work=jsonObject.getInt("c_job");

                    job_show.setText(job[work]);


                    smoke_show.setText(jsonObject.getString("c_smoke"));

                } catch (JSONException e) {
                    e.printStackTrace();
                }


//                c_nickname_re.setText(response.body().getC_nickname_re());
//                c_region_re.setSelection(response.body().getC_region_re());
//                c_job_re.setSelection(response.body().getC_job_re());
//                RadioButton c_smoke_check_re=findViewById(R.id.c_smoke_ok_re);
//                if(response.body().getC_smoke_re().equals("무")){
//                    c_smoke_check_re=findViewById(R.id.c_smoke_no_re);
//                }
//                c_smoke_check_re.setChecked(true);
//
//                c_height_re.setText(String.valueOf(response.body().getC_height_re()));

            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Log.i("profileshow fail",t.toString());
            }
        });


    }

    @Override
    protected void onResume() {
        super.onResume();

        //사진을 눌렀을 때
        pic_show.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i("pic click", "full screen");
                startActivity(fullpic);
            }
        });


        //대화 수락 눌렀을 때 -> 대화방 만들기
        convers_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i("ok click", "conversation list");

                //update해주러 가야함.......아이디 값 가지고 룰루랄라 가야죠......

                //네트워크 요청
                UploadService service = MyRetrofit2.getRetrofit2().create(UploadService.class);

                RequestBody my_id= RequestBody.create(
                        MediaType.parse("multipart/form-data"), loginTrue);

                RequestBody from_id= RequestBody.create(
                        MediaType.parse("multipart/form-data"), id);

                RequestBody to_id= RequestBody.create(
                        MediaType.parse("multipart/form-data"), "no");

                RequestBody answer= RequestBody.create(
                        MediaType.parse("multipart/form-data"), "ok");

                Call<JsonObject> call1 = service.converseAskAnswer(my_id,from_id,to_id,answer);

                call1.enqueue(new Callback<JsonObject>() {
                    @Override
                    public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                        try {
                            JSONObject jsonObject=new JSONObject(new Gson().toJson(response.body()));

                            //대화방이 만들어진 거 알려줘야 함... -> 서비스에 이 부분 코드 만들기!

                            Log.i("ok click", "conversation make");
                            //그리고 나서 대화방 리스트 db에 넣어가지고 만들어줘야함..... ㅎㅎㅎㅎ
                            UploadService service = MyRetrofit2.getRetrofit2().create(UploadService.class);

                            RequestBody from_id= RequestBody.create(
                                    MediaType.parse("multipart/form-data"), id);

                            RequestBody to_id= RequestBody.create(
                                    MediaType.parse("multipart/form-data"), loginTrue);


                            Call<JsonObject> call1 = service.conversationRoom(from_id,to_id);
                            call1.enqueue(new Callback<JsonObject>() {
                                @Override
                                public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                                    try {
                                        JSONObject jsonObject=new JSONObject(new Gson().toJson(response.body()));


                                        Log.i("conversationRoom11",jsonObject.getString("conversationroom"));
                                        Log.i("conversationRoom12",jsonObject.getString("c_roomNum"));

                                        String conversRoom=jsonObject.getString("conversationroom");


                                        if(conversRoom.equals("already exist")){

                                            Toast.makeText(getApplicationContext(),"대화방이 이미 존재합니다.",Toast.LENGTH_SHORT).show();


                                        }else if(conversRoom.equals("select")){

                                            Toast.makeText(getApplicationContext(),"대화방이 생성되었습니다.",Toast.LENGTH_SHORT).show();

                                            //채팅룸 서비스 start 그래야 수신 가능....
                                            Intent intent=new Intent(ProfileShowAcceptActivity.this, ChatService.class);
                                            intent.putExtra("room",jsonObject.getString("c_roomNum"));
                                            startService(intent);

                                        }

                                        //이방이 사라져야 하고 그러면서 recyclerview에게 값이 바뀌었음을 알려줘야 함..
                                        Intent intent=new Intent(getApplicationContext(),ConverseComeListActivity.class);
                                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                        startActivity(intent);
                                        finish();

                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }

                                @Override
                                public void onFailure(Call<JsonObject> call, Throwable t) {
                                    Log.i("fial->ok 수락 후 어떤 결과값?",t.toString());
                                }
                            });



                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(Call<JsonObject> call, Throwable t) {
                        Log.i("fial->ok 수락 후 어떤 결과값?",t.toString());
                    }
                });





        }
        });

        //대화 거절 눌렀을 때 -> 대화 거절 알려주기
        convers_no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i("no click", "conversation list");
                //update해주러 가야함.......아이디 값 가지고 룰루랄라 가야죠......
                //네트워크 요청
                UploadService service = MyRetrofit2.getRetrofit2().create(UploadService.class);

                RequestBody my_id= RequestBody.create(
                        MediaType.parse("multipart/form-data"), loginTrue);

                RequestBody from_id= RequestBody.create(
                        MediaType.parse("multipart/form-data"), id);

                RequestBody to_id= RequestBody.create(
                        MediaType.parse("multipart/form-data"), "no");

                RequestBody answer= RequestBody.create(
                        MediaType.parse("multipart/form-data"), "no");

                Call<JsonObject> call1 = service.converseAskAnswer(my_id,from_id,to_id,answer);

                call1.enqueue(new Callback<JsonObject>() {
                    @Override
                    public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                        try {
                            JSONObject jsonObject=new JSONObject(new Gson().toJson(response.body()));

                            Log.i("no 수락 후 어떤 결과값?",jsonObject.getString("update"));

                            Intent intent=new Intent(getApplicationContext(),ConverseComeListActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                            finish();


                            //거절된 거 표시하기....... -> 상대방에게 알려주기.. -> service 단에서 알려주기




                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(Call<JsonObject> call, Throwable t) {
                        Log.i("fial->no 수락 후 어떤 결과값?",t.toString());
                    }
                });
            }
        });

//        //자기가 타인에게 대화 요청 취소
//        convers_ask_no.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Log.i("ask no click", "conversation list");
//                //update해주러 가야함.......아이디 값 가지고 룰루랄라 가야죠......
//                UploadService service = MyRetrofit2.getRetrofit2().create(UploadService.class);
//
//
//
//                RequestBody my_id= RequestBody.create(
//                        MediaType.parse("multipart/form-data"), loginTrue);
//
//                RequestBody from_id= RequestBody.create(
//                        MediaType.parse("multipart/form-data"), "no");
//
//                RequestBody to_id= RequestBody.create(
//                        MediaType.parse("multipart/form-data"), id);
//
//                RequestBody answer= RequestBody.create(
//                        MediaType.parse("multipart/form-data"), "cancel");
//
//                Call<JsonObject> call1 = service.converseAskAnswer(my_id,from_id,to_id,answer);
//
//                call1.enqueue(new Callback<JsonObject>() {
//                    @Override
//                    public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
//                        try {
//                            JSONObject jsonObject=new JSONObject(new Gson().toJson(response.body()));
//
//                            Log.i("cancel 후 어떤 결과값?",jsonObject.getString("update"));
//                            Intent intent=new Intent(getApplicationContext(),ConverseComeListActivity.class);
//                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                            startActivity(intent);
//                            finish();
//
//                        } catch (JSONException e) {
//                            e.printStackTrace();
//                        }
//                    }
//
//                    @Override
//                    public void onFailure(Call<JsonObject> call, Throwable t) {
//                        Log.i("f : cancel 어떤 결과값?",t.toString());
//                    }
//                });
//                //굳이 취소한 걸 알려줄 필요는 없다고 생각함...:)
//            }
//        });
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
