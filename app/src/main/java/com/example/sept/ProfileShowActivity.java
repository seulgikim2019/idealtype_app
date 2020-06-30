package com.example.sept;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
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

//마이페이지를 통해서 프로필 수정.
public class ProfileShowActivity extends AppCompatActivity {



    ImageView pic_show;
    TextView nick_show;
    TextView age_show;
    TextView gender_show;
    TextView height_show;
    TextView region_show;
    TextView job_show;
    TextView smoke_show;
    ImageView conver_show;
    Intent intent;
    String id;
    Intent fullpic;


    Intent alert;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_show);

        setTitle("프로필상세보기");

        pic_show=findViewById(R.id.pic_show);
        nick_show=findViewById(R.id.nick_show);
        age_show=findViewById(R.id.age_show);
        gender_show=findViewById(R.id.gender_show);
        height_show=findViewById(R.id.height_show);
        region_show=findViewById(R.id.region_show);
        job_show=findViewById(R.id.job_show);
        smoke_show=findViewById(R.id.smoke_show);
        conver_show=findViewById(R.id.conver_show);


        intent=getIntent();

        if(intent!=null){
            if(intent.getStringExtra("id")!=null){
                id=intent.getStringExtra("id");
            }
        }
        fullpic=new Intent(getApplicationContext(),FullPicActivity.class);
    }

    @Override
    protected void onStart() {
        super.onStart();


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

                    Glide.with(ProfileShowActivity.this)
                       .load(jsonObject.getString("c_img_uri")).into(pic_show);

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


        //대화신청하기 눌렀을 때
        conver_show.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i("conver_show", "click");
                //이거 클릭하면은 진짜 신청할껀지 물어보기.

                startActivity(alert);

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
