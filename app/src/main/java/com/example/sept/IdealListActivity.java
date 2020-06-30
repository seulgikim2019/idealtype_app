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
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
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

public class IdealListActivity extends AppCompatActivity {


    Toolbar up_toolbar;
    ArrayList<CustomContent1> list;
    ArrayList<CustomContent1> list2;
    CustomAdapter customAdapter;
    CustomAdapter customAdapter2;
    RecyclerView recyclerView1;
    RecyclerView recyclerView2;
    static Switch custom_list_want1;
    Button custom_list_want_btn;
    Button custom_list_change_btn;

    TextView text_ideal1;
    TextView text_ideal2;

    ImageView pic_ideal1;
    ImageView pic_ideal2;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ideal_contents_list);

        up_toolbar=findViewById(R.id.up_toolbar);

        setTitle("이상형 추천");
        setSupportActionBar(up_toolbar);
        getSupportActionBar().setSubtitle(c_name_final+"님 환영합니다!");



        text_ideal1=findViewById(R.id.text_ideal1);
        text_ideal2=findViewById(R.id.text_ideal2);


        pic_ideal1=findViewById(R.id.pic_ideal1);
        pic_ideal2=findViewById(R.id.pic_ideal2);

        custom_list_want_btn=findViewById(R.id.custom_list_want_btn);
        custom_list_change_btn=findViewById(R.id.custom_list_change_btn);

        custom_list_want1=findViewById(R.id.custom_list_want);

        recyclerView1=findViewById(R.id.ideal_content_list_first);
        recyclerView2=findViewById(R.id.ideal_content_list_second);

        list=new ArrayList<>();
        list2=new ArrayList<>();

        customAdapter=new CustomAdapter(list);
        customAdapter2=new CustomAdapter(list2);

        recyclerView1.setAdapter(customAdapter);
        recyclerView2.setAdapter(customAdapter2);
//        // 리사이클러뷰에 LinearLayoutManager 객체 지정.
//        recyclerView.setLayoutManager(new LinearLayoutManager(this,RecyclerView.VERTICAL, false));

        recyclerView1.setLayoutManager(new GridLayoutManager(this,1,GridLayoutManager.HORIZONTAL,false));
        recyclerView2.setLayoutManager(new GridLayoutManager(this,1,GridLayoutManager.HORIZONTAL,false));

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

        //recyclerview 화면 눌렀을 때
        customAdapter2.setOnItemClickListener(new CustomAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(CustomAdapter.ViewHolder holder, View view, int position) {
                Log.i("click",String.valueOf(position));

                Log.i("click",String.valueOf(list2.get(position).getCus_contents_email()));

                Intent intent=new Intent(getApplicationContext(),ProfileShowActivity.class);
                intent.putExtra("id",list2.get(position).getCus_contents_email());
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
            custom_list_want1.setChecked(true);
            //검색조건이 있는 경우를 의미->기존 검색 조건에 따라 검색하게끔...



            //1지망 나오게
            UploadService service = MyRetrofit2.getRetrofit2().create(UploadService.class);
            RequestBody c_id_email= RequestBody.create(
                    MediaType.parse("multipart/form-data"), loginTrue);
            Call<CustomContent> call = service.idealWannabeChoice(c_id_email);

            call.enqueue(new Callback<CustomContent>() {
                @Override
                public void onResponse(Call<CustomContent> call, Response<CustomContent> response) {

                    for (int j = 0; j <list.size() ; j++) {
                        list.clear();
                    }

                    //검색조건은 있으나 -> 회원이 없을때!
                    if(response.body().result.length==0){
                        //text_ideal1.setVisibility(View.GONE);
                        text_ideal1.setText("검색조건에 맞는 1지망 회원이 없습니다.");
                        Glide.with(IdealListActivity.this)
                                .load(R.drawable.profile).into(pic_ideal1);
                    }else {
                        text_ideal1.setText("1순위 이상형");

                        for (int i = 0; i < response.body().result.length; i++) {
                            Log.i("result", response.body().result[i].getCus_contents_email());

                            if(i==0){
                                Glide.with(IdealListActivity.this)
                                        .load(response.body().result[i].getResult()).centerCrop().circleCrop().into(pic_ideal1);
                            }

                            //region-> 숫자에 따라서 가져오는 값이 다름. -> 여기서 주의!!!!!
                            String[] city = getResources().getStringArray(R.array.city);

                            list.add(new CustomContent1(response.body().result[i].getCus_contents_email(),
                                    response.body().result[i].getCus_contents_nickname(),
                                    response.body().result[i].getCus_contents_pic(),
                                    response.body().result[i].getCus_contents_age_gender(),
                                    city[Integer.parseInt(response.body().result[i].getCus_contents_region())]));
                        }
                    }
                    customAdapter.notifyDataSetChanged();

                }

                @Override
                public void onFailure(Call<CustomContent> call, Throwable t) {
                    Log.i("CustomContent fail",t.toString());
                }
            });


//            //2지망 나오게
            Call<CustomContent> call2 = service.idealWannabeChoice2(c_id_email);

            call2.enqueue(new Callback<CustomContent>() {
                @Override
                public void onResponse(Call<CustomContent> call, Response<CustomContent> response) {

                    for (int j = 0; j <list2.size() ; j++) {
                        list2.clear();
                    }

                    //검색조건 -> 회원 노
                    if(response.body().result.length==0){
                        text_ideal2.setText("검색조건에 맞는 2지망 회원이 없습니다.");
                        Glide.with(IdealListActivity.this)
                                .load(R.drawable.profile).into(pic_ideal2);
                    }else {

                        text_ideal2.setText("2순위 이상형");

                        for (int i = 0; i < response.body().result.length; i++) {

                            if(i==0){
                                Glide.with(IdealListActivity.this)
                                        .load(response.body().result[i].getResult()).centerCrop().circleCrop().into(pic_ideal2);
                            }

                            //region-> 숫자에 따라서 가져오는 값이 다름. -> 여기서 주의!!!!!
                            String[] city = getResources().getStringArray(R.array.city);

                            list2.add(new CustomContent1(response.body().result[i].getCus_contents_email(),
                                    response.body().result[i].getCus_contents_nickname(),
                                    response.body().result[i].getCus_contents_pic(),
                                    response.body().result[i].getCus_contents_age_gender(),
                                    city[Integer.parseInt(response.body().result[i].getCus_contents_region())]));
                        }
                    }
                    customAdapter2.notifyDataSetChanged();

                }

                @Override
                public void onFailure(Call<CustomContent> call, Throwable t) {
                    Log.i("CustomContent fail",t.toString());
                }
            });

        }else{ //검색조건이 없는 것을 의미함.


            Log.i("custom list want save","custom list want not save");

            custom_list_want1.setChecked(false);
            UploadService service = MyRetrofit2.getRetrofit2().create(UploadService.class);

            RequestBody id= RequestBody.create(
                    MediaType.parse("multipart/form-data"), loginTrue);

            Call<CustomContent> call = service.idealContentsList(id);

            call.enqueue(new Callback<CustomContent>() {
                @Override
                public void onResponse(Call<CustomContent> call, Response<CustomContent> response) {
                    for (int j = 0; j <list.size() ; j++) {
                        list.clear();
                    }

                    //검색조건 없는데 -> 회원이 없다
                    if(response.body().result.length==0){
                        //이상형이 없다는 것을 의미함.
                        text_ideal1.setText("1지망 이상형을 선택해주세요");
                        Glide.with(IdealListActivity.this)
                                .load(R.drawable.profile).into(pic_ideal1);
                    }else {


                        text_ideal1.setText("1순위 이상형");

                        for (int i = 0; i < response.body().result.length; i++) {

                            //얼마나 닮았는지 정도
                            //닮은정도 ->
                            if(i==0){
                                Glide.with(IdealListActivity.this)
                                        .load(response.body().result[i].getResult()).centerCrop().circleCrop().into(pic_ideal1);
                            }
                            //region-> 숫자에 따라서 가져오는 값이 다름.
                            String[] city = getResources().getStringArray(R.array.city);

                            list.add(new CustomContent1(response.body().result[i].getCus_contents_email(),
                                    response.body().result[i].getCus_contents_nickname(),
                                    response.body().result[i].getCus_contents_pic(),
                                    response.body().result[i].getCus_contents_age_gender(),
                                    city[Integer.parseInt(response.body().result[i].getCus_contents_region())]));


                        }

                    }
                    customAdapter.notifyDataSetChanged();

                }

                @Override
                public void onFailure(Call<CustomContent> call, Throwable t) {
                    Log.i("CustomContent fail",t.toString());
                }
            });


            //2지망 나오게
            Call<CustomContent> call1 = service.idealContentsList2(id);

            call1.enqueue(new Callback<CustomContent>() {
                @Override
                public void onResponse(Call<CustomContent> call, Response<CustomContent> response) {


                    for (int j = 0; j <list2.size() ; j++) {
                        list2.clear();
                    }

                    //2지망 이상형이 없음
                    if(response.body().result.length==0){

                        text_ideal2.setText("2지망 이상형을 선택해주세요");
                        Glide.with(IdealListActivity.this)
                                .load(R.drawable.profile).into(pic_ideal2);
                    }else{

                        text_ideal2.setText("2순위 이상형");

                        for (int i = 0; i <response.body().result.length ; i++) {
                            if(i==0){
                                Glide.with(IdealListActivity.this)
                                        .load(response.body().result[i].getResult()).centerCrop().circleCrop().into(pic_ideal2);
                            }

                            //region-> 숫자에 따라서 가져오는 값이 다름. -> 여기서 주의!!!!!
                            String[] city=getResources().getStringArray(R.array.city);

                            list2.add(new CustomContent1(response.body().result[i].getCus_contents_email(),
                                    response.body().result[i].getCus_contents_nickname(),
                                    response.body().result[i].getCus_contents_pic(),
                                    response.body().result[i].getCus_contents_age_gender(),
                                    city[Integer.parseInt(response.body().result[i].getCus_contents_region())]));
                        }

                    }
                    customAdapter2.notifyDataSetChanged();

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
            Intent intent=new Intent(IdealListActivity.this,MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
            Toast.makeText(IdealListActivity.this, "logout clicked", Toast.LENGTH_LONG).show();
            return true;
        }else if (id == R.id.action_main) { //메인페이지를 눌렀을 때

            Toast.makeText(IdealListActivity.this, "main clicked", Toast.LENGTH_LONG).show();
            Intent intent=new Intent(IdealListActivity.this,MainActivity.class);
            startActivity(intent);
            finish();
            return true;
        }else if (id == R.id.action_mypage) { //마이페이지를 눌렀을 때
            Toast.makeText(IdealListActivity.this, "mypage clicked", Toast.LENGTH_LONG).show();
            Intent intent=new Intent(IdealListActivity.this, MyProfileActivity.class);
            startActivity(intent);
            finish();
            return true;
        }else if (id == R.id.action_conversation) { //마이페이지를 눌렀을 때
            Toast.makeText(IdealListActivity.this, "conversation clicked", Toast.LENGTH_LONG).show();

            //아직 대화방 없음.,..
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    protected void onResume() {
        super.onResume();
        Log.i("customlistactivity","resume");




        //검색조건설정 버튼을 눌렀을때
        custom_list_want_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i("custom_list_want_btn","click");
                //검색조건 설정으로 넘어가야 함.
                //  startActivity(new Intent(IdealListActivity.this,MyWannabeSaveAllActivity.class));
                Intent intent=new Intent(getApplicationContext(),MyWannabeSaveAllActivity.class);
                //버튼값 조정을 위해서 intent 특정값을 넣어서 보냄
                intent.putExtra("ideal","ideal");
                startActivity(intent);
            }
        });


        //이상형 변동 버튼을 눌렀을 때,
        custom_list_change_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i("custom_list_want_btn","click");
                //검색조건 설정으로 넘어가야 함.
                //  startActivity(new Intent(IdealListActivity.this,MyWannabeSaveAllActivity.class));
                Intent intent=new Intent(getApplicationContext(),IdealPicActivity.class);
                //버튼값 조정을 위해서 intent 특정값을 넣어서 보냄
                intent.putExtra("ideal","ideal");
                startActivity(intent);
                finish();
            }
        });



        //검색조건 on/off
        custom_list_want1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(custom_list_want1.isChecked()){
                    Log.i("checked",custom_list_want1.getTextOn().toString());

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


                                    //1지망 나오게
                                    UploadService service = MyRetrofit2.getRetrofit2().create(UploadService.class);
                                    RequestBody c_id_email= RequestBody.create(
                                            MediaType.parse("multipart/form-data"), loginTrue);
                                    Call<CustomContent> call2 = service.idealWannabeChoice(c_id_email);

                                    //검색조건이 있고 -> 1지망
                                    call2.enqueue(new Callback<CustomContent>() {
                                        @Override
                                        public void onResponse(Call<CustomContent> call, Response<CustomContent> response) {

                                            for (int j = 0; j <list.size() ; j++) {
                                                list.clear();
                                            }


                                            //검색조건이 있으나 회원 노
                                            if(response.body().result.length==0){
                                                text_ideal1.setText("검색조건에 맞는 1지망 회원이 없습니다.");
                                                Glide.with(IdealListActivity.this)
                                                        .load(R.drawable.profile).into(pic_ideal1);
                                            }else {
                                                text_ideal1.setText("1순위 이상형");
                                                for (int i = 0; i < response.body().result.length; i++) {


                                                    if(i==0){
                                                        Glide.with(IdealListActivity.this)
                                                                .load(response.body().result[i].getResult()).centerCrop().circleCrop().into(pic_ideal1);
                                                    }

                                                    //region-> 숫자에 따라서 가져오는 값이 다름. -> 여기서 주의!!!!!
                                                    String[] city = getResources().getStringArray(R.array.city);

                                                    list.add(new CustomContent1(response.body().result[i].getCus_contents_email(),
                                                            response.body().result[i].getCus_contents_nickname(),
                                                            response.body().result[i].getCus_contents_pic(),
                                                            response.body().result[i].getCus_contents_age_gender(),
                                                            city[Integer.parseInt(response.body().result[i].getCus_contents_region())]));
                                                }

                                            }
                                            customAdapter.notifyDataSetChanged();
                                        }

                                        @Override
                                        public void onFailure(Call<CustomContent> call, Throwable t) {
                                            Log.i("CustomContent fail",t.toString());
                                        }
                                    });

                                    //2지망
                                    Call<CustomContent> call3 = service.idealWannabeChoice2(c_id_email);

                                    call3.enqueue(new Callback<CustomContent>() {
                                        @Override
                                        public void onResponse(Call<CustomContent> call, Response<CustomContent> response) {

                                            for (int j = 0; j <list2.size() ; j++) {
                                                list2.clear();
                                            }

                                            if(response.body().result.length==0){
                                                text_ideal2.setText("검색조건에 맞는 2지망 회원이 없습니다.");

                                                    Glide.with(IdealListActivity.this)
                                                            .load(R.drawable.profile).into(pic_ideal2);

                                            }else {

                                                text_ideal2.setText("2순위 이상형");

                                                for (int i = 0; i < response.body().result.length; i++) {
                                                    Log.i("result", response.body().result[i].getCus_contents_email());

                                                    if(i==0){
                                                        Glide.with(IdealListActivity.this)
                                                                .load(response.body().result[i].getResult()).centerCrop().circleCrop().into(pic_ideal2);
                                                    }
                                                    //region-> 숫자에 따라서 가져오는 값이 다름. -> 여기서 주의!!!!!
                                                    String[] city = getResources().getStringArray(R.array.city);

                                                    list2.add(new CustomContent1(response.body().result[i].getCus_contents_email(),
                                                            response.body().result[i].getCus_contents_nickname(),
                                                            response.body().result[i].getCus_contents_pic(),
                                                            response.body().result[i].getCus_contents_age_gender(),
                                                            city[Integer.parseInt(response.body().result[i].getCus_contents_region())]));
                                                }




                                            }
                                            customAdapter2.notifyDataSetChanged();
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
                                            startActivity(new Intent(IdealListActivity.this,MyWannabeSaveAllActivity.class));
                                            dialog.dismiss();     //닫기
                                        }
                                    };

                                    DialogInterface.OnClickListener cancelListener = new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            Log.i("personalpic:사진", "취소");
                                            custom_list_want1.setChecked(false);
                                            dialog.dismiss();     //닫기
                                        }
                                    };

                                    //다이얼로그 창 띄우기
                                    new AlertDialog.Builder(IdealListActivity.this)
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


                    RequestBody id= RequestBody.create(
                            MediaType.parse("multipart/form-data"), loginTrue);

                    Call<CustomContent> call = service.idealContentsList(id);

                    call.enqueue(new Callback<CustomContent>() {
                        @Override
                        public void onResponse(Call<CustomContent> call, Response<CustomContent> response) {
                            for (int j = 0; j <list.size() ; j++) {
                                list.clear();
                            }
                            if(response.body().result.length==0){
                                //이상형이 없다는 것을 의미함.
                                text_ideal1.setText("1지망 이상형을 선택해주세요");
                                Glide.with(IdealListActivity.this)
                                        .load(R.drawable.profile).into(pic_ideal1);
                            }else {
                                text_ideal1.setText("1순위 이상형");
                                for (int i = 0; i < response.body().result.length; i++) {
                                    Log.i("result", response.body().result[i].getResult());
                                    if(i==0){
                                        Glide.with(IdealListActivity.this)
                                                .load(response.body().result[i].getResult()).centerCrop().circleCrop().into(pic_ideal1);
                                    }
                                    //얼마나 닮았는지 정도
                                    //닮은정도 ->

                                    //region-> 숫자에 따라서 가져오는 값이 다름.
                                    String[] city = getResources().getStringArray(R.array.city);

                                    list.add(new CustomContent1(response.body().result[i].getCus_contents_email(),
                                            response.body().result[i].getCus_contents_nickname(),
                                            response.body().result[i].getCus_contents_pic(),
                                            response.body().result[i].getCus_contents_age_gender(),
                                            city[Integer.parseInt(response.body().result[i].getCus_contents_region())]));


                                }



                            }
                            customAdapter.notifyDataSetChanged();
                        }

                        @Override
                        public void onFailure(Call<CustomContent> call, Throwable t) {
                            Log.i("CustomContent fail",t.toString());
                        }
                    });



                    //2지망 나오게
                    Call<CustomContent> call1 = service.idealContentsList2(id);

                    call1.enqueue(new Callback<CustomContent>() {
                        @Override
                        public void onResponse(Call<CustomContent> call, Response<CustomContent> response) {


                            for (int j = 0; j <list2.size() ; j++) {
                                list2.clear();
                            }


                            if(response.body().result.length==0){
                                text_ideal2.setText("2지망 이상형을 선택해주세요");
                                Glide.with(IdealListActivity.this)
                                        .load(R.drawable.profile).into(pic_ideal2);
                            }else {
                                text_ideal2.setText("2순위 이상형");

                                for (int i = 0; i < response.body().result.length; i++) {
                                    Log.i("result2", response.body().result[i].getCus_contents_email());
                                    if(i==0){
                                        Glide.with(IdealListActivity.this)
                                                .load(response.body().result[i].getResult()).centerCrop().circleCrop().into(pic_ideal2);
                                    }
                                    //region-> 숫자에 따라서 가져오는 값이 다름. -> 여기서 주의!!!!!
                                    String[] city = getResources().getStringArray(R.array.city);

                                    list2.add(new CustomContent1(response.body().result[i].getCus_contents_email(),
                                            response.body().result[i].getCus_contents_nickname(),
                                            response.body().result[i].getCus_contents_pic(),
                                            response.body().result[i].getCus_contents_age_gender(),
                                            city[Integer.parseInt(response.body().result[i].getCus_contents_region())]));
                                }



                            }
                            customAdapter2.notifyDataSetChanged();


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

        if(custom_list_want1.isChecked()){
            Log.i("check",custom_list_want1.getTextOn().toString());
            //true 일 때는,,,,,,
            //검색조건이 있는 경우를 의미->기존 검색 조건에 따라 검색하게끔...

            SharedPreferences login_true=getSharedPreferences("login_true", Activity.MODE_PRIVATE);
            // 로그인 이메일 정보를 가지고 옴.
            String loginTrue = login_true.getString("login_email","no");



            //1지망 나오게
            UploadService service = MyRetrofit2.getRetrofit2().create(UploadService.class);
            RequestBody c_id_email= RequestBody.create(
                    MediaType.parse("multipart/form-data"), loginTrue);
            Call<CustomContent> call2 = service.idealWannabeChoice(c_id_email);

            call2.enqueue(new Callback<CustomContent>() {
                @Override
                public void onResponse(Call<CustomContent> call, Response<CustomContent> response) {

                    for (int j = 0; j <list.size() ; j++) {
                        list.clear();
                    }

                    //검색조건 활성화
                    if(response.body().result.length==0){
                        //result가 0일 때 -> 조건에 맞는 사람이 없다는 뜻임.
                        //text를 없앤다
                        text_ideal1.setText("검색 조건에 맞는 회원이 없습니다");
                        Glide.with(IdealListActivity.this)
                                .load(R.drawable.profile).into(pic_ideal1);
                    }else{
                        text_ideal1.setText("1순위 이상형");
                        for (int i = 0; i <response.body().result.length ; i++) {
                            Log.i("result",response.body().result[i].getCus_contents_email());

                            if(i==0){
                                Glide.with(IdealListActivity.this)
                                        .load(response.body().result[i].getResult()).centerCrop().circleCrop().into(pic_ideal1);
                            }
                            //region-> 숫자에 따라서 가져오는 값이 다름. -> 여기서 주의!!!!!
                            String[] city=getResources().getStringArray(R.array.city);

                            list.add(new CustomContent1(response.body().result[i].getCus_contents_email(),
                                    response.body().result[i].getCus_contents_nickname(),
                                    response.body().result[i].getCus_contents_pic(),
                                    response.body().result[i].getCus_contents_age_gender(),
                                    city[Integer.parseInt(response.body().result[i].getCus_contents_region())]));
                        }



                    }
                    customAdapter.notifyDataSetChanged();



                }

                @Override
                public void onFailure(Call<CustomContent> call, Throwable t) {
                    Log.i("CustomContent fail",t.toString());
                }
            });

            //2지망
            Call<CustomContent> call3 = service.idealWannabeChoice2(c_id_email);

            call3.enqueue(new Callback<CustomContent>() {
                @Override
                public void onResponse(Call<CustomContent> call, Response<CustomContent> response) {

                    for (int j = 0; j <list2.size() ; j++) {
                        list2.clear();
                    }


                    if(response.body().result.length==0){
                        text_ideal2.setText("검색 조건에 맞는 회원이 없습니다");
                        Glide.with(IdealListActivity.this)
                                .load(R.drawable.profile).into(pic_ideal2);
                    }else{
                        text_ideal2.setText("2순위 이상형");
                        for (int i = 0; i <response.body().result.length ; i++) {
                            Log.i("result",response.body().result[i].getCus_contents_email());
                            if(i==0){
                                Glide.with(IdealListActivity.this)
                                        .load(response.body().result[i].getResult()).centerCrop().circleCrop().into(pic_ideal2);
                            }
                            //region-> 숫자에 따라서 가져오는 값이 다름. -> 여기서 주의!!!!!
                            String[] city=getResources().getStringArray(R.array.city);

                            list2.add(new CustomContent1(response.body().result[i].getCus_contents_email(),
                                    response.body().result[i].getCus_contents_nickname(),
                                    response.body().result[i].getCus_contents_pic(),
                                    response.body().result[i].getCus_contents_age_gender(),
                                    city[Integer.parseInt(response.body().result[i].getCus_contents_region())]));
                        }



                    }
                    customAdapter2.notifyDataSetChanged();



                }

                @Override
                public void onFailure(Call<CustomContent> call, Throwable t) {
                    Log.i("CustomContent fail",t.toString());
                }
            });
        }else{
            Log.i("check",custom_list_want1.getTextOff().toString());
            //false일 때는 resume에서 처리하도록 해놓음.
        }
    }
}






