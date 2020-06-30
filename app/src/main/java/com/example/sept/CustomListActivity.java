package com.example.sept;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.example.sept.Adapter.CustomAdapter;
import com.example.sept.Class.CustomContent;
import com.example.sept.Class.CustomContent1;
import com.example.sept.Interface.UploadService;
import com.example.sept.Retrofit.MyRetrofit2;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.example.sept.MainActivity.c_name_final;

public class CustomListActivity extends AppCompatActivity {


    Toolbar up_toolbar;
    ArrayList<CustomContent1> list;
    CustomAdapter customAdapter;
    RecyclerView recyclerView;
    static Switch custom_list_want;
    Button custom_list_want_btn;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.custom_list);

        up_toolbar=findViewById(R.id.up_toolbar);
        setSupportActionBar(up_toolbar);
        getSupportActionBar().setSubtitle(c_name_final+"님 환영합니다!");


        custom_list_want_btn=findViewById(R.id.custom_list_want_btn);
        custom_list_want=findViewById(R.id.custom_list_want);

        recyclerView=findViewById(R.id.custom_list_first);
        list=new ArrayList<>();

        customAdapter=new CustomAdapter(list);


        recyclerView.setAdapter(customAdapter);
//        // 리사이클러뷰에 LinearLayoutManager 객체 지정.
//        recyclerView.setLayoutManager(new LinearLayoutManager(this,RecyclerView.VERTICAL, false));

        recyclerView.setLayoutManager(new GridLayoutManager(this,2));





        //recyclerview 화면 눌렀을 때
        customAdapter.setOnItemClickListener(new CustomAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(CustomAdapter.ViewHolder holder, View view, int position) {
                Log.i("click",String.valueOf(position));

                Log.i("click",String.valueOf(list.get(position).getCus_contents_email()));



                Intent intent=new Intent(getApplicationContext(),ProfileShowActivity.class);
                intent.putExtra("id",list.get(position).getCus_contents_email());
                startActivity(intent);

            }
        });







        Log.i("customlist","start");
        SharedPreferences login_true=getSharedPreferences("login_true", Activity.MODE_PRIVATE);
        // 로그인 이메일 정보를 가지고 옴.
        String loginTrue = login_true.getString("login_email","no");

        SharedPreferences custom_list_want_save=getSharedPreferences("custom_list_want_save",MODE_PRIVATE);
        String result=custom_list_want_save.getString("custom_list_want_save","no");

        if(result.equals("true")){ //true일 때 -> 검색조건이 있는 것을 의미함.
            Log.i("custom list want save","custom list want save");
            custom_list_want.setChecked(true);
            //검색조건이 있는 경우를 의미->기존 검색 조건에 따라 검색하게끔...
            UploadService service = MyRetrofit2.getRetrofit2().create(UploadService.class);
            RequestBody c_id_email= RequestBody.create(
                    MediaType.parse("multipart/form-data"), loginTrue);
            Call<CustomContent> call = service.wannabeChoice(c_id_email);

            call.enqueue(new Callback<CustomContent>() {
                @Override
                public void onResponse(Call<CustomContent> call, Response<CustomContent> response) {


                    for (int i = 0; i <response.body().result.length ; i++) {
                        Log.i("result",response.body().result[i].getCus_contents_email());


                        //region-> 숫자에 따라서 가져오는 값이 다름. -> 여기서 주의!!!!!
                        String[] city=getResources().getStringArray(R.array.city);

                        list.add(new CustomContent1(response.body().result[i].getCus_contents_email(),
                                response.body().result[i].getCus_contents_nickname(),
                                response.body().result[i].getCus_contents_pic(),
                                response.body().result[i].getCus_contents_age_gender(),
                                city[Integer.parseInt(response.body().result[i].getCus_contents_region())]));



                        }


                     customAdapter.notifyDataSetChanged();


                }

                @Override
                public void onFailure(Call<CustomContent> call, Throwable t) {
                    Log.i("CustomContent fail",t.toString());
                }
            });


        }else{ //검색조건이 없는 것을 의미함.


            Log.i("custom list want save","custom list want not save");

            custom_list_want.setChecked(false);
            UploadService service = MyRetrofit2.getRetrofit2().create(UploadService.class);

            RequestBody description= RequestBody.create(
                    MediaType.parse("multipart/form-data"), loginTrue);

            Call<CustomContent> call1 = service.customList(description);

            call1.enqueue(new Callback<CustomContent>() {
                @Override
                public void onResponse(Call<CustomContent> call, Response<CustomContent> response) {

                    for (int i = 0; i <response.body().result.length ; i++) {
                        Log.i("result",response.body().result[i].getCus_contents_email());


                        //region-> 숫자에 따라서 가져오는 값이 다름.
                        String[] city=getResources().getStringArray(R.array.city);

                        list.add(new CustomContent1(response.body().result[i].getCus_contents_email(),
                                response.body().result[i].getCus_contents_nickname(),
                                response.body().result[i].getCus_contents_pic(),
                                response.body().result[i].getCus_contents_age_gender(),
                                city[Integer.parseInt(response.body().result[i].getCus_contents_region())]));



                    }




                    customAdapter.notifyDataSetChanged();

                }

                @Override
                public void onFailure(Call<CustomContent> call, Throwable t) {
                    Log.i("CustomContent fail",t.toString());
                }
            });


        }


    }






    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_custom_list, menu);
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
            Intent intent=new Intent(CustomListActivity.this,MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
            Toast.makeText(CustomListActivity.this, "logout clicked", Toast.LENGTH_LONG).show();
            return true;
        }else if (id == R.id.action_main) { //메인페이지를 눌렀을 때

            Toast.makeText(CustomListActivity.this, "main clicked", Toast.LENGTH_LONG).show();
            Intent intent=new Intent(CustomListActivity.this,MainActivity.class);
            startActivity(intent);
            finish();
            return true;
        }else if (id == R.id.action_mypage) { //마이페이지를 눌렀을 때
            Toast.makeText(CustomListActivity.this, "mypage clicked", Toast.LENGTH_LONG).show();
            Intent intent=new Intent(CustomListActivity.this, MyProfileActivity.class);
            startActivity(intent);
            finish();
            return true;
        }else if (id == R.id.action_conversation) { //마이페이지를 눌렀을 때
            Toast.makeText(CustomListActivity.this, "conversation clicked", Toast.LENGTH_LONG).show();

            //아직 대화방 없음.,..
            return true;
        }

        return super.onOptionsItemSelected(item);
    }





    @Override
    protected void onStart() {
        super.onStart();

       //검색 조건이 이쪽으로 받을 수 있게 해야겠네요. -> db에 저장시켜 놓았기 때문에 그걸로 접근하게
        //그럼 우선 1. 검색조건이 true 로 바뀌어 있는지 먼저 확인
        //2.바뀌어 있다면 검색 조건  db에 맞춰서 검색한 조건이 나오도록 해야함.





    }


    @Override
    protected void onResume() {
        super.onResume();
        Log.i("customlistactivity","resume");




        //검색조건설정 버튼이 눌렀을때
        custom_list_want_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i("custom_list_want_btn","click");
                //검색조건 설정으로 넘어가야 함.
                startActivity(new Intent(CustomListActivity.this,MyWannabeSaveAllActivity.class));
            }
        });



        //검색조건 on/off
        custom_list_want.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(custom_list_want.isChecked()){
                    Log.i("checked",custom_list_want.getTextOn().toString());

                    final SharedPreferences custom_list_want_save=getSharedPreferences("custom_list_want_save",MODE_PRIVATE);
                    SharedPreferences.Editor editor=custom_list_want_save.edit();
                    editor.putString("custom_list_want_save","true");
                    editor.commit();



                    SharedPreferences login_true=getSharedPreferences("login_true", Activity.MODE_PRIVATE);
                    // 로그인 이메일 정보를 가지고 옴.
                    String loginTrue = login_true.getString("login_email","no");


                    //기존에 검색 조건이 있는지 없는지 여부 체크
                    UploadService service = MyRetrofit2.getRetrofit2().create(UploadService.class);
                    RequestBody c_id_email= RequestBody.create(
                            MediaType.parse("multipart/form-data"), loginTrue);
                    Call<JsonObject> call = service.wannabeSaveCheck(c_id_email);

                    call.enqueue(new Callback<JsonObject>() {
                        @Override
                        public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {

                            try {
                                JSONObject jsonObject=new JSONObject(new Gson().toJson(response.body()));
                                jsonObject.getString("result");



                                if(jsonObject.getString("result").equals("success")){
                                    Log.i("기존에 검색조건이 있다!!!",jsonObject.getString("result"));
                                    //검색조건에 맞춰서 값이 나오게끔 해야함.

                                    SharedPreferences login_true=getSharedPreferences("login_true", Activity.MODE_PRIVATE);
                                    // 로그인 이메일 정보를 가지고 옴.
                                    String loginTrue = login_true.getString("login_email","no");


                                    UploadService service = MyRetrofit2.getRetrofit2().create(UploadService.class);
                                    RequestBody c_id_email= RequestBody.create(
                                            MediaType.parse("multipart/form-data"), loginTrue);
                                    Call<CustomContent> call1 = service.wannabeChoice(c_id_email);

                                    call1.enqueue(new Callback<CustomContent>() {
                                        @Override
                                        public void onResponse(Call<CustomContent> call, Response<CustomContent> response) {


                                            for (int i = 0; i <list.size() ; i++) {
                                                list.clear();
                                            }

                                            Log.i("yes","yes");
                                            for (int i = 0; i <response.body().result.length ; i++) {
                                                Log.i("result",response.body().result[i].getCus_contents_email());


                                                //region-> 숫자에 따라서 가져오는 값이 다름. -> 여기서 주의!!!!!
                                                String[] city=getResources().getStringArray(R.array.city);

                                                list.add(new CustomContent1(response.body().result[i].getCus_contents_email(),response.body().result[i].getCus_contents_nickname(),
                                                        response.body().result[i].getCus_contents_pic(),
                                                        response.body().result[i].getCus_contents_age_gender(),
                                                        city[Integer.parseInt(response.body().result[i].getCus_contents_region())]));



                                            }


                                            customAdapter.notifyDataSetChanged();


                                        }

                                        @Override
                                        public void onFailure(Call<CustomContent> call, Throwable t) {
                                            Log.i("CustomContent fail",t.toString());
                                        }
                                    });


                                }else{ //fail로 나오게 됨.
                                    Log.i("기존에 검색조건이 없다!!!",jsonObject.getString("result"));
                                    //검색조건이 없으면 다이얼로그 창을 띄워서... 검색조건 설정을 하게끔 유도한다!

                                    DialogInterface.OnClickListener searchSaveListener = new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            Log.i("searchSaveListener", "검색조건 창으로 이동하기");
                                            startActivity(new Intent(CustomListActivity.this,MyWannabeSaveAllActivity.class));
                                            dialog.dismiss();     //닫기
                                        }
                                    };

                                    DialogInterface.OnClickListener cancelListener = new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            Log.i("personalpic:사진", "취소");
                                            custom_list_want.setChecked(false);
                                            dialog.dismiss();     //닫기
                                        }
                                    };

                                    //다이얼로그 창 띄우기
                                    new AlertDialog.Builder(CustomListActivity.this)
                                            .setTitle("검색 조건")
                                            .setMessage("설정하신 검색 조건이 없습니다.")
                                            .setPositiveButton("검색조건 설정하기", searchSaveListener)
                                            .setNeutralButton("취소", cancelListener)
                                            .show();


                                }


                            } catch (JSONException e) {
                                e.printStackTrace();
                            }


                        }

                        @Override
                        public void onFailure(Call<JsonObject> call, Throwable t) {
                            Log.i("검색조건 네트워크 오류",t.toString());
                        }
                    });




                    //만약 없다면 새로운 창...
                    //검색조건 클릭한 것이므로 -> 검색 조건 dialog 창 띄우기......
                   // startActivity(new Intent(CustomListActivity.this,MyWannabeSaveActivity.class));

                }else{ //검색조건을 활성화 안하는 것으로 선택했다면

                    SharedPreferences custom_list_want_save=getSharedPreferences("custom_list_want_save",MODE_PRIVATE);
                    SharedPreferences.Editor editor=custom_list_want_save.edit();
                    editor.putString("custom_list_want_save","false");
                    editor.commit();




                    UploadService service = MyRetrofit2.getRetrofit2().create(UploadService.class);
                    SharedPreferences login_true=getSharedPreferences("login_true", Activity.MODE_PRIVATE);
                    // 로그인 이메일 정보를 가지고 옴.
                    String loginTrue = login_true.getString("login_email","no");

                    RequestBody description= RequestBody.create(
                            MediaType.parse("multipart/form-data"), loginTrue);

                    Call<CustomContent> call1 = service.customList(description);

                    call1.enqueue(new Callback<CustomContent>() {
                        @Override
                        public void onResponse(Call<CustomContent> call, Response<CustomContent> response) {



                            for (int j = 0; j <list.size() ; j++) {
                                list.clear();
                            }


                            for (int i = 0; i <response.body().result.length ; i++) {
                                Log.i("result",response.body().result[i].getCus_contents_email());


                                //region-> 숫자에 따라서 가져오는 값이 다름.
                                String[] city=getResources().getStringArray(R.array.city);

                                //우선은 주석 처리...



                                list.add(new CustomContent1(response.body().result[i].getCus_contents_email(),
                                        response.body().result[i].getCus_contents_nickname(),
                                        response.body().result[i].getCus_contents_pic(),
                                        response.body().result[i].getCus_contents_age_gender(),
                                        city[Integer.parseInt(response.body().result[i].getCus_contents_region())]));

                            }


                            customAdapter.notifyDataSetChanged();

//
//


                            //파일이 업로드에 성공했다면,...기존에 저장해두었던 파일을 삭제하고 완료로 넘김.
                            //즉  shared도 지우고 그리구 sdcards에 저장되어 있는 사진도 지워줍니다요!!!

//                Log.i("myprofile success",response.body().getUrl());
//
//

//            //경로만 가지고오면된다.
//                Glide.with(MyProfileReActivity.this)
//                        .load(response.body().getMy_pic_re()).into(my_pic_re);



                        }

                        @Override
                        public void onFailure(Call<CustomContent> call, Throwable t) {
                            Log.i("CustomContent fail",t.toString());
                        }
                    });




                }
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.i("customlist","pause");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.i("customlist","restart");

        //custom_list_want 의 true / false에 따라서

        if(custom_list_want.isChecked()){
            Log.i("check",custom_list_want.getTextOn().toString());
            //true 일 때는,,,,,,
            //검색조건이 있는 경우를 의미->기존 검색 조건에 따라 검색하게끔...

            SharedPreferences login_true=getSharedPreferences("login_true", Activity.MODE_PRIVATE);
            // 로그인 이메일 정보를 가지고 옴.
            String loginTrue = login_true.getString("login_email","no");


            UploadService service = MyRetrofit2.getRetrofit2().create(UploadService.class);
            RequestBody c_id_email= RequestBody.create(
                    MediaType.parse("multipart/form-data"), loginTrue);
            Call<CustomContent> call = service.wannabeChoice(c_id_email);

            call.enqueue(new Callback<CustomContent>() {
                @Override
                public void onResponse(Call<CustomContent> call, Response<CustomContent> response) {

                    for (int j = 0; j <list.size() ; j++) {
                        list.clear();
                    }

                    Log.i("yes","yes");
                    for (int i = 0; i <response.body().result.length ; i++) {
                        Log.i("result",response.body().result[i].getCus_contents_email());


                        //region-> 숫자에 따라서 가져오는 값이 다름. -> 여기서 주의!!!!!
                        String[] city=getResources().getStringArray(R.array.city);

                        list.add(new CustomContent1(response.body().result[i].getCus_contents_email(),
                                response.body().result[i].getCus_contents_nickname(),
                                response.body().result[i].getCus_contents_pic(),
                                response.body().result[i].getCus_contents_age_gender(),
                                city[Integer.parseInt(response.body().result[i].getCus_contents_region())]));



                    }


                    customAdapter.notifyDataSetChanged();


                }

                @Override
                public void onFailure(Call<CustomContent> call, Throwable t) {
                    Log.i("CustomContent fail",t.toString());
                }
            });

        }else{
            Log.i("check",custom_list_want.getTextOff().toString());
            //false일 때는 resume에서 처리하도록 해놓음.
        }
    }
}
