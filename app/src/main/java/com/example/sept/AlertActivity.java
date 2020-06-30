package com.example.sept;


import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.sept.Interface.UploadService;
import com.example.sept.Retrofit.MyRetrofit2;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


//alert _ yes or no
public class AlertActivity extends AppCompatActivity {


    Button alert_yes;
    Button alert_cancel;
    TextView yes_text;

    Intent intent;
    String partid;
    String nickname;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.alert_yesorno);
        alert_yes=findViewById(R.id.alert_yes);
        alert_cancel=findViewById(R.id.alert_cancel);
        yes_text=findViewById(R.id.yes_text);


        intent=getIntent();

        if(intent!=null){
            //받아온 값이 있으므로... 상대방 닉네임, 아이디 가지고 와서 저장해놓고
            //nickname -> 상대방거

            partid=intent.getStringExtra("id");

            nickname=intent.getStringExtra("nick");
            yes_text.setText("[ "+nickname+" ]님에게 대화를 신청하시겠습니까?");
        }
    }


    @Override
    protected void onStart() {
        super.onStart();


    }

    @Override
    protected void onResume() {
        super.onResume();

        alert_yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //확인을 누르면 우선은 -> 내 db에 대화 신청 리스트를 만들어준다.... 내가 대화신청한 사람들.....

                SharedPreferences login_true=getSharedPreferences("login_true", Activity.MODE_PRIVATE);
                // 로그인 이메일 정보를 가지고 옴.
                String loginEmail = login_true.getString("login_email", "no");
                //아이디값 넣어주기

                UploadService service = MyRetrofit2.getRetrofit2().create(UploadService.class);

                RequestBody from_id= RequestBody.create(
                        MediaType.parse("multipart/form-data"), loginEmail);
                RequestBody to_id= RequestBody.create(
                        MediaType.parse("multipart/form-data"), partid);


                Call<JsonObject> call1 = service.converseAsk(from_id,to_id);


                call1.enqueue(new Callback<JsonObject>() {
                    @Override
                    public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                         try {
                            JSONObject jsonObject=new JSONObject(new Gson().toJson(response.body()));
                             Log.i("ask_result",jsonObject.getString("insert"));


                             if(jsonObject.getString("insert").equals("ok")){
                                 Toast.makeText(getApplicationContext(),"[ "+nickname+" ]님에게 대화신청을 보냈습니다.",Toast.LENGTH_SHORT).show();
                             }else{
                                 Toast.makeText(getApplicationContext(),"대화 신청 수락을 기다리고 있습니다.",Toast.LENGTH_SHORT).show();
                             }
                         } catch (JSONException e) {
                            e.printStackTrace();
                        }


                    }

                    @Override
                    public void onFailure(Call<JsonObject> call, Throwable t) {

                    }
                });



                //fcm 보내기
            //id -> 상대방 id로 fcm 보내기
                finish();
            }
        });

        alert_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            //그냥 finish()

                finish();
            }
        });

    }


    @Override
    protected void onPause() {
        super.onPause();

    }


}
