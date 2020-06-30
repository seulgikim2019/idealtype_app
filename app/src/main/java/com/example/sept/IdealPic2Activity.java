package com.example.sept;


import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.sept.Adapter.IdealAdapter;
import com.example.sept.Class.IdealContent;
import com.example.sept.Class.IdealContent1;
import com.example.sept.Interface.UploadService;
import com.example.sept.Retrofit.MyRetrofit2;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.graphics.Color.RED;
import static android.graphics.Color.WHITE;
import static android.graphics.Color.YELLOW;
import static com.example.sept.IdealPicActivity.b_holder;
import static com.example.sept.MainActivity.c_name_final;

public class IdealPic2Activity extends AppCompatActivity {


    Toolbar up_toolbar;
    ArrayList<IdealContent1> list;
    IdealAdapter idealAdapter;
    RecyclerView recyclerView;
    Switch ideal_list_want;
    ImageView ideal_pic_1;
    ImageView ideal_pic_2;
    TextView ideal_name_1;
    TextView ideal_name_2;

    //1지망 젠더
    String genderTrue_first;

    Button ideal_next_3;
    Button ideal_before_1;
    LinearLayout ideal_second;
    static String genderWhat;
    String checkGender="";
    public static IdealAdapter.ViewHolder b_holder_2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.ideal_list);
        setTitle("이상형 2지망 선택");
        up_toolbar=findViewById(R.id.up_toolbar);
        setSupportActionBar(up_toolbar);
        getSupportActionBar().setSubtitle(c_name_final+"님 환영합니다!");

        ideal_list_want=findViewById(R.id.ideal_list_want);
        ideal_pic_1=findViewById(R.id.ideal_pic_1);
        ideal_pic_2=findViewById(R.id.ideal_pic_2);
        ideal_name_1=findViewById(R.id.ideal_name_1);
        ideal_name_2=findViewById(R.id.ideal_name_2);
        ideal_second=findViewById(R.id.ideal_second);

        ideal_second.setVisibility(View.VISIBLE);


        ideal_before_1=findViewById(R.id.ideal_before_1);
        ideal_before_1.setText("1지망이상형");
        ideal_next_3=findViewById(R.id.ideal_next_3);

        recyclerView=findViewById(R.id.ideal_list_first);
        list=new ArrayList<>();

        idealAdapter=new IdealAdapter(list);




        //1지망 이상형
        SharedPreferences ideal_pic_save_first=getSharedPreferences("ideal_pic_save_first",MODE_PRIVATE);
        final String ideal_pic_save_first_result_final=ideal_pic_save_first.getString("ideal_pic_save_first","no");


        //2지망 이상형
        SharedPreferences ideal_pic_save_second=getSharedPreferences("ideal_pic_save_second",MODE_PRIVATE);
        final String ideal_pic_save_second_result_final=ideal_pic_save_second.getString("ideal_pic_save_second","no");



        //1지망때 -> 성별 구분
        SharedPreferences ideal_pic_save_gender=getSharedPreferences("ideal_pic_save_gender",MODE_PRIVATE);
        genderTrue_first = ideal_pic_save_gender.getString("ideal_pic_save_gender","남자");

        //1지망에 선택한 이미지 선택 못 하게 하기
        b_holder.ideal_pic_show.setClickable(false);
        b_holder.ideal_name_show.setClickable(false);


        //2지망 -> 성별
        SharedPreferences ideal_pic_save_gender_second=getSharedPreferences("ideal_pic_save_gender_second",MODE_PRIVATE);
        String genderTrue = ideal_pic_save_gender_second.getString("ideal_pic_save_gender_second","남자");

        genderWhat=genderTrue;
        checkGender=genderWhat;

        //2지망 성별
        if(genderWhat.equals("여자")){
            Log.i("genderWhat",genderWhat);
            ideal_list_want.setText("여자");
            ideal_list_want.setChecked(true);

            String url= null;
            try {
                url =URLDecoder.decode(ideal_pic_save_second_result_final+".jpg","UTF-8");

            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            Glide.with(getApplicationContext())
                    .load("http://??/men/"+url)
                    .error(R.drawable.profile)
                    .circleCrop().into(ideal_pic_2);

            if(genderTrue_first.equals("남자")){
                String url1= null;
                try {
                    url1 =URLDecoder.decode(ideal_pic_save_first_result_final+".jpg","UTF-8");

                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                Glide.with(getApplicationContext())
                        .load("http://??/men/"+url1)
                        .error(R.drawable.profile)
                        .circleCrop().into(ideal_pic_1);

            }else{
                String url2= null;
                try {
                    url2 =URLDecoder.decode(ideal_pic_save_first_result_final+".jpg","UTF-8");

                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                Glide.with(getApplicationContext())
                        .load("http://??/women/"+url2)
                        .error(R.drawable.profile)
                        .circleCrop().into(ideal_pic_1);

            }
        }else{ //2지망이 남자일때
            String url= null;
            try {
                url = URLDecoder.decode(ideal_pic_save_second_result_final+".jpg","UTF-8");

            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            Glide.with(getApplicationContext())
                    .load("http://??/women/"+url)
                    .error(R.drawable.profile)
                    .circleCrop().into(ideal_pic_2);

            if(genderTrue_first.equals("여자")){
                String url1= null;
                try {
                    url1 =URLDecoder.decode(ideal_pic_save_first_result_final+".jpg","UTF-8");

                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                Glide.with(getApplicationContext())
                        .load("http://??/women/"+url1)
                        .error(R.drawable.profile)
                        .circleCrop().into(ideal_pic_1);

            }else{
                String url2= null;
                try {
                    url2 =URLDecoder.decode(ideal_pic_save_first_result_final+".jpg","UTF-8");

                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                Glide.with(getApplicationContext())
                        .load("http://??/men/"+url2)
                        .error(R.drawable.profile)
                        .circleCrop().into(ideal_pic_1);
            }
        }



        if(ideal_pic_save_second_result_final.equals("no")){

            ideal_name_2.setText("이름");
        }else{
            ideal_name_2.setText(ideal_pic_save_second_result_final);
        }


        Log.i("genderWhat",genderWhat);



        recyclerView.setAdapter(idealAdapter);
        recyclerView.setLayoutManager(new GridLayoutManager(this,3));

        SharedPreferences login_true=getSharedPreferences("login_true", Activity.MODE_PRIVATE);
        // 로그인 이메일 정보를 가지고 옴.
        String loginTrue = login_true.getString("login_email","no");

        UploadService service = MyRetrofit2.getRetrofit2().create(UploadService.class);
        RequestBody id= RequestBody.create(
        MediaType.parse("multipart/form-data"), loginTrue);
        RequestBody gender= RequestBody.create(
                MediaType.parse("multipart/form-data"), genderTrue);

        //아이디 정보 -> 1,2지망 애들 체크 따로.. -> if/else로 구분하기
        final String[] firstpicpath = new String[1];
        Call<IdealContent> call=service.idealList(id,gender);


            call.enqueue(new Callback<IdealContent>() {
                @Override
                public void onResponse(Call<IdealContent> call, Response<IdealContent> response) {


                    for (int i = 0; i <response.body().result.length ; i++) {
                        Log.i("result",response.body().result[i].getIdeal_contents_name());


                        if(ideal_pic_save_first_result_final.equals(response.body().result[i].getIdeal_contents_name())){

                            Glide.with(getApplicationContext()).load(response.body().result[i].getIdeal_contents_pic()).
                                    circleCrop().into(ideal_pic_1);

                            firstpicpath[0] =response.body().result[i].getIdeal_contents_pic();
                            ideal_name_1.setText(ideal_pic_save_first_result_final);


                            list.add(new IdealContent1("1000",
                                    response.body().result[i].getIdeal_contents_name(),
                                    response.body().result[i].getIdeal_contents_pic()));

                        }else if(ideal_pic_save_second_result_final.equals(response.body().result[i].getIdeal_contents_name())){

                            Glide.with(getApplicationContext()).load(response.body().result[i].getIdeal_contents_pic()).
                                    circleCrop().into(ideal_pic_2);
                            ideal_name_2.setText(ideal_pic_save_second_result_final);


                            list.add(new IdealContent1("10000",
                                    response.body().result[i].getIdeal_contents_name(),
                                    response.body().result[i].getIdeal_contents_pic()));
                        }else if(!ideal_pic_save_first_result_final.equals(response.body().result[i].getIdeal_contents_name())){

//                            if(firstpicpath[0]!=null){
//                                Glide.with(getApplicationContext()).load(firstpicpath[0]).
//                                        circleCrop().error(R.drawable.profile).into(ideal_pic_1);
//
//                            }
                            ideal_name_1.setText(ideal_pic_save_first_result_final);


                            list.add(new IdealContent1(response.body().result[i].getIdeal_num(),
                                    response.body().result[i].getIdeal_contents_name(),
                                    response.body().result[i].getIdeal_contents_pic()));
                        }
                    }


                    idealAdapter.notifyDataSetChanged();


                }

                @Override
                public void onFailure(Call<IdealContent> call, Throwable t) {
                    Log.i("CustomContent fail",t.toString());
                }
            });

        //recyclerview 화면 눌렀을 때
        idealAdapter.setOnItemClickListener(new IdealAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(IdealAdapter.ViewHolder holder, View view, int position) {


                String clickName=String.valueOf(list.get(position).getIdeal_contents_name());
                Log.i("click",clickName);


                if(!clickName.equals(ideal_pic_save_first_result_final)){

                    SharedPreferences ideal_pic_save_second=getSharedPreferences("ideal_pic_save_second",MODE_PRIVATE);
                    String ideal_pic_save_second_result=ideal_pic_save_second.getString("ideal_pic_save_second","no");


                    //저장된 값이 없을때
                    if(ideal_pic_save_second_result.equals("no")){
                        SharedPreferences.Editor editor=ideal_pic_save_second.edit();
                        editor.putString("ideal_pic_save_second",clickName);
                        editor.commit();
                        b_holder_2=holder;
                        checkGender=ideal_list_want.getText().toString();
                        //1지망써주기
                        Glide.with(getApplicationContext()).load(list.get(position).getIdeal_contents_pic()).
                                circleCrop().into(ideal_pic_2);
                        ideal_name_2.setText(clickName);

                        holder.ideal_pic_show.setBackgroundColor(Color.YELLOW);
                    }else if(ideal_pic_save_second_result.equals(clickName)){ //1차로 저장된 값이랑 같을때
                        //이때는 두번 선택한 것이므로 -> 선택을 취소한다는 의미로 받아들임.
                        holder.ideal_pic_show.setBackgroundColor(WHITE);
                        b_holder_2=holder;
                        SharedPreferences.Editor editor=ideal_pic_save_second.edit();
                        editor.putString("ideal_pic_save_second","no");
                        editor.commit();

                        Glide.with(getApplicationContext()).load(R.drawable.profile).
                                circleCrop().into(ideal_pic_2);
                        ideal_name_2.setText("이름");

                        Log.i("same",ideal_pic_save_second_result);
                    }else{ //그외
                        if(b_holder_2!=null){
                            b_holder_2.ideal_pic_show.setBackgroundColor(WHITE);
                        }
                        b_holder_2=holder;

                        checkGender=ideal_list_want.getText().toString();
                        holder.ideal_pic_show.setBackgroundColor(Color.YELLOW);
                        Log.i("else",ideal_pic_save_second_result);
                        SharedPreferences.Editor editor=ideal_pic_save_second.edit();
                        editor.putString("ideal_pic_save_second",clickName);
                        editor.commit();


                        Glide.with(getApplicationContext()).load(list.get(position).getIdeal_contents_pic()).
                                circleCrop().into(ideal_pic_2);
                        ideal_name_2.setText(clickName);

                        ideal_pic_save_second_result=ideal_pic_save_second.getString("ideal_pic_save_second","no");

                        Log.i("else change",ideal_pic_save_second_result);
                    }
                }





            }
        });


    }

    @Override
    protected void onStart() {
        super.onStart();


    }

    @Override
    protected void onResume() {
        super.onResume();


        //1지망으로 가는 버튼
        ideal_before_1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("click","1지망가자!");
                ideal_before_1.setText("돌아가기");
                Intent intent=new Intent(getApplicationContext(),IdealPicActivity.class);
                startActivity(intent);
                finish();
            }
        });

        //완료
        ideal_next_3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { //2지망은 선택이 없으면.. 그냥 1지망만 검색합니다로....
                Log.i("click","완료다!");


                SharedPreferences ideal_pic_save_first=getSharedPreferences("ideal_pic_save_first",MODE_PRIVATE);
                final String ideal_pic_save_first_result_final=ideal_pic_save_first.getString("ideal_pic_save_first","no");

                SharedPreferences ideal_pic_save_second=getSharedPreferences("ideal_pic_save_second",MODE_PRIVATE);
                String ideal_pic_save_second_result=ideal_pic_save_second.getString("ideal_pic_save_second","no");

                if(ideal_pic_save_second_result.equals("no")){
                    Toast.makeText(getApplicationContext(),"1지망이상형만 선택하셨습니다.",Toast.LENGTH_LONG).show();

                    SharedPreferences login_true=getSharedPreferences("login_true", Activity.MODE_PRIVATE);
                    // 로그인 이메일 정보를 가지고 옴.
                    String loginTrue = login_true.getString("login_email","no");


                    //기존에 검색 조건이 있는지 없는지 여부 체크
                    UploadService service = MyRetrofit2.getRetrofit2().create(UploadService.class);
                    RequestBody w_pic_id= RequestBody.create(
                            MediaType.parse("multipart/form-data"), loginTrue);
                    RequestBody w_pic_1= RequestBody.create(
                            MediaType.parse("multipart/form-data"), ideal_pic_save_first_result_final);
                    RequestBody w_pic_2= RequestBody.create(
                            MediaType.parse("multipart/form-data"), "NULL");

                    Call<JsonObject> call = service.idealPicList(w_pic_id,w_pic_1,w_pic_2);


                    call.enqueue(new Callback<JsonObject>() {
                        @Override
                        public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                            try {
                                JSONObject jsonObject=new JSONObject(new Gson().toJson(response.body()));
                                jsonObject.get("w_pic_1").toString();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            //성공하면은 -> 이상형 페이지로 이동하게 할 것임....
                            Intent intent=new Intent(getApplicationContext(),IdealListActivity.class);
                            startActivity(intent);
                            finish();

                        }

                        @Override
                        public void onFailure(Call<JsonObject> call, Throwable t) {
                            Log.i("fail",t.toString());
                        }
                    });


                }else{
                    Toast.makeText(getApplicationContext(),"1지망,2지망 모두 선택완료하였습니다.",Toast.LENGTH_LONG).show();
                    SharedPreferences login_true=getSharedPreferences("login_true", Activity.MODE_PRIVATE);
                    // 로그인 이메일 정보를 가지고 옴.
                    String loginTrue = login_true.getString("login_email","no");


                    //기존에 검색 조건이 있는지 없는지 여부 체크
                    UploadService service = MyRetrofit2.getRetrofit2().create(UploadService.class);
                    RequestBody w_pic_id= RequestBody.create(
                            MediaType.parse("multipart/form-data"), loginTrue);
                    RequestBody w_pic_1= RequestBody.create(
                            MediaType.parse("multipart/form-data"), ideal_pic_save_first_result_final);
                    RequestBody w_pic_2= RequestBody.create(
                            MediaType.parse("multipart/form-data"), ideal_pic_save_second_result);

                    Call<JsonObject> call = service.idealPicList(w_pic_id,w_pic_1,w_pic_2);


                    call.enqueue(new Callback<JsonObject>() {
                        @Override
                        public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {

                            //성공하면은 -> 이상형 페이지로 이동하게 할 것임....
                            Intent intent=new Intent(getApplicationContext(),IdealListActivity.class);
                            startActivity(intent);
                            finish();

                        }

                        @Override
                        public void onFailure(Call<JsonObject> call, Throwable t) {
                            Log.i("fail",t.toString());
                        }
                    });
                }

            }
        });




        //검색조건설정 버튼이 눌렀을때
        ideal_list_want.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(ideal_list_want.isChecked()){
                    Log.i("checked",ideal_list_want.getTextOn().toString());

                    ideal_list_want.setText("여자");

                    if(checkGender.equals(ideal_list_want.getText())){
                        if(b_holder_2!=null){
                            b_holder_2.ideal_pic_show.setBackgroundColor(Color.YELLOW);
                        }
                    }else{
                        //b_holder가 null 이 아닐 때 -> white
                        if(b_holder_2!=null){
                            b_holder_2.ideal_pic_show.setBackgroundColor(WHITE);
                        }
                    }

                    if(genderTrue_first.equals("여자")){
                        //그대로 보여주면 됨
                        b_holder.ideal_pic_show.setBackgroundColor(RED);
                        b_holder.ideal_pic_show.setClickable(false);
                        b_holder.ideal_name_show.setClickable(false);
                    }else{ //아니면.. 바꿔주기
                        b_holder.ideal_pic_show.setBackgroundColor(WHITE);
                        b_holder.ideal_pic_show.setClickable(false);
                        b_holder.ideal_name_show.setClickable(false);
                    }

                    final SharedPreferences ideal_pic_save_gender_second=getSharedPreferences("ideal_pic_save_gender_second",MODE_PRIVATE);
                    SharedPreferences.Editor editor=ideal_pic_save_gender_second.edit();
                    editor.putString("ideal_pic_save_gender_second","여자");
                    editor.commit();

                    SharedPreferences login_true=getSharedPreferences("login_true", Activity.MODE_PRIVATE);
                    // 로그인 이메일 정보를 가지고 옴.
                    String loginTrue = login_true.getString("login_email","no");


                    //기존에 검색 조건이 있는지 없는지 여부 체크
                    UploadService service = MyRetrofit2.getRetrofit2().create(UploadService.class);
                    RequestBody id= RequestBody.create(
                            MediaType.parse("multipart/form-data"), loginTrue);
                    RequestBody gender= RequestBody.create(
                            MediaType.parse("multipart/form-data"), "여자");

                    //1지망 이상형
                    SharedPreferences ideal_pic_save_first=getSharedPreferences("ideal_pic_save_first",MODE_PRIVATE);
                    final String ideal_pic_save_first_result_final=ideal_pic_save_first.getString("ideal_pic_save_first","no");


                    //2지망 이상형
                    SharedPreferences ideal_pic_save_second=getSharedPreferences("ideal_pic_save_second",MODE_PRIVATE);
                    final String ideal_pic_save_second_result_final=ideal_pic_save_second.getString("ideal_pic_save_second","no");


                                    Call<IdealContent> call = service.idealList(id,gender);

                                    call.enqueue(new Callback<IdealContent>() {
                                        @Override
                                        public void onResponse(Call<IdealContent> call, Response<IdealContent> response) {


                                            for (int i = 0; i <list.size() ; i++) {
                                                list.clear();
                                            }

                                            Log.i("yes","yes");
                                            for (int i = 0; i <response.body().result.length ; i++) {
                                                Log.i("result",response.body().result[i].getIdeal_num());



                                                if(ideal_pic_save_first_result_final.equals(response.body().result[i].getIdeal_contents_name())){

                                                    Glide.with(getApplicationContext()).load(response.body().result[i].getIdeal_contents_pic()).
                                                            circleCrop().into(ideal_pic_1);
                                                    ideal_name_1.setText(ideal_pic_save_first_result_final);


                                                    list.add(new IdealContent1("1000",
                                                            response.body().result[i].getIdeal_contents_name(),
                                                            response.body().result[i].getIdeal_contents_pic()));

                                                }else if(ideal_pic_save_second_result_final.equals(response.body().result[i].getIdeal_contents_name())){
                                                    Glide.with(getApplicationContext()).load(response.body().result[i].getIdeal_contents_pic()).
                                                            circleCrop().into(ideal_pic_2);
                                                    ideal_name_2.setText(ideal_pic_save_second_result_final);


                                                    list.add(new IdealContent1("10000",
                                                            response.body().result[i].getIdeal_contents_name(),
                                                            response.body().result[i].getIdeal_contents_pic()));
                                                }else if(!ideal_pic_save_first_result_final.equals(response.body().result[i].getIdeal_contents_name())){

//                                                    Glide.with(getApplicationContext()).load(b_holder.ideal_pic_show.getDrawable()).
//                                                            circleCrop().into(ideal_pic_1);
                                                    ideal_name_1.setText(ideal_pic_save_first_result_final);


                                                    list.add(new IdealContent1(response.body().result[i].getIdeal_num(),
                                                            response.body().result[i].getIdeal_contents_name(),
                                                            response.body().result[i].getIdeal_contents_pic()));
                                                }
                                            }


                                            idealAdapter.notifyDataSetChanged();


                                        }

                                        @Override
                                        public void onFailure(Call<IdealContent> call, Throwable t) {
                                            Log.i("CustomContent fail",t.toString());
                                        }
                                    });


                    //만약 없다면 새로운 창...
                    //검색조건 클릭한 것이므로 -> 검색 조건 dialog 창 띄우기......
                    // startActivity(new Intent(CustomListActivity.this,MyWannabeSaveActivity.class));

                }else{ //검색조건을 활성화 안하는 것으로 선택했다면

                    ideal_list_want.setText("남자");


                    if(checkGender.equals(ideal_list_want.getText())){
                        if(b_holder_2!=null){
                            b_holder_2.ideal_pic_show.setBackgroundColor(YELLOW);
                        }
                    }else{
                        //b_holder가 null 이 아닐 때 -> white
                        if(b_holder_2!=null){
                            b_holder_2.ideal_pic_show.setBackgroundColor(WHITE);
                        }
                    }


                    if(genderTrue_first.equals("남자")){
                        //그대로 보여주면 됨
                        b_holder.ideal_pic_show.setBackgroundColor(RED);
                        b_holder.ideal_pic_show.setClickable(false);
                        b_holder.ideal_name_show.setClickable(false);
                    }else{ //아니면.. 바꿔주기
                        b_holder.ideal_pic_show.setBackgroundColor(WHITE);
                        b_holder.ideal_pic_show.setClickable(false);
                        b_holder.ideal_name_show.setClickable(false);
                    }


                    final SharedPreferences ideal_pic_save_gender_second=getSharedPreferences("ideal_pic_save_gender_second",MODE_PRIVATE);
                    SharedPreferences.Editor editor=ideal_pic_save_gender_second.edit();
                    editor.putString("ideal_pic_save_gender_second","남자");
                    editor.commit();

                    SharedPreferences login_true=getSharedPreferences("login_true", Activity.MODE_PRIVATE);
                    // 로그인 이메일 정보를 가지고 옴.
                    String loginTrue = login_true.getString("login_email","no");


                    //1지망 이상형
                    SharedPreferences ideal_pic_save_first=getSharedPreferences("ideal_pic_save_first",MODE_PRIVATE);
                    final String ideal_pic_save_first_result_final=ideal_pic_save_first.getString("ideal_pic_save_first","no");


                    //2지망 이상형
                    SharedPreferences ideal_pic_save_second=getSharedPreferences("ideal_pic_save_second",MODE_PRIVATE);
                    final String ideal_pic_save_second_result_final=ideal_pic_save_second.getString("ideal_pic_save_second","no");

                    //기존에 검색 조건이 있는지 없는지 여부 체크
                    UploadService service = MyRetrofit2.getRetrofit2().create(UploadService.class);
                    RequestBody id= RequestBody.create(
                            MediaType.parse("multipart/form-data"), loginTrue);
                    RequestBody gender= RequestBody.create(
                            MediaType.parse("multipart/form-data"), "남자");


                    Call<IdealContent> call = service.idealList(id,gender);

                    call.enqueue(new Callback<IdealContent>() {
                        @Override
                        public void onResponse(Call<IdealContent> call, Response<IdealContent> response) {


                            for (int i = 0; i <list.size() ; i++) {
                                list.clear();
                            }

                            Log.i("yes","yes");
                            for (int i = 0; i <response.body().result.length ; i++) {
                                Log.i("result",response.body().result[i].getIdeal_num());

                                if(ideal_pic_save_first_result_final.equals(response.body().result[i].getIdeal_contents_name())){

                                    Glide.with(getApplicationContext()).load(response.body().result[i].getIdeal_contents_pic()).
                                            circleCrop().into(ideal_pic_1);
                                    ideal_name_1.setText(ideal_pic_save_first_result_final);


                                    Log.i("comehere2",response.body().result[i].getIdeal_contents_name());
                                    list.add(new IdealContent1("1000",
                                            response.body().result[i].getIdeal_contents_name(),
                                            response.body().result[i].getIdeal_contents_pic()));

                                }else if(ideal_pic_save_second_result_final.equals(response.body().result[i].getIdeal_contents_name())){
                                    Glide.with(getApplicationContext()).load(response.body().result[i].getIdeal_contents_pic()).
                                            circleCrop().into(ideal_pic_2);
                                    ideal_name_2.setText(ideal_pic_save_second_result_final);


                                    Log.i("comehere3",response.body().result[i].getIdeal_contents_name());

                                    list.add(new IdealContent1("10000",
                                            response.body().result[i].getIdeal_contents_name(),
                                            response.body().result[i].getIdeal_contents_pic()));
                                }else if(!ideal_pic_save_first_result_final.equals(response.body().result[i].getIdeal_contents_name())){


                                    Log.i("comehere4",response.body().result[i].getIdeal_contents_name());

                                    list.add(new IdealContent1(response.body().result[i].getIdeal_num(),
                                            response.body().result[i].getIdeal_contents_name(),
                                            response.body().result[i].getIdeal_contents_pic()));
                                }
                            }


                            idealAdapter.notifyDataSetChanged();


                        }

                        @Override
                        public void onFailure(Call<IdealContent> call, Throwable t) {
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
        Log.i("idealpic","pause");

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);


    }
}
