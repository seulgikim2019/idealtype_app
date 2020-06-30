package com.example.sept;

import android.app.Activity;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.sept.Class.RequestHttpConnection;

//개인정보를 입력받을 것임. 초기 입력창임.
public class PersonalProfileActivity extends AppCompatActivity {



    Button personal_next;


    EditText c_nickname;
    EditText c_age;
    RadioGroup c_gender;
    Spinner c_region;
    Spinner c_job;
    EditText c_height;
    RadioGroup c_smoke;
//    Calendar calendar;
//    DatePickerDialog.OnDateSetListener date;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.personal_profile);
        setTitle("프로필입력");


        c_nickname=findViewById(R.id.c_nickname);
        c_age=findViewById(R.id.c_age);
        c_gender=findViewById(R.id.c_gender);
        c_region=findViewById(R.id.c_region);
        c_job=findViewById(R.id.c_job);
        c_height=findViewById(R.id.c_height);
        c_smoke=findViewById(R.id.c_smoke);


        personal_next=findViewById(R.id.personal_next);



        String url = "http://??/personal_profile_check.php";

        // AsyncTask를 통해 HttpURLConnection 수행.
        ContentValues values=new ContentValues();
        // 개인정보 보냄

        //자동로그인 -> auto_login/login_true둘다 있음
        //일반로그인 -> login_true에 있음.
        SharedPreferences login_true=getSharedPreferences("login_true", Activity.MODE_PRIVATE);
            values.put("id_email",login_true.getString("login_email","").toString());
            NetworkTask networkTask = new NetworkTask(url, values);
            networkTask.execute();

        if(savedInstanceState!=null){
            String name=savedInstanceState.getString("nickname");
            c_nickname.setText(name);
            String age=savedInstanceState.getString("age");
            c_age.setText(age);
            int gender=savedInstanceState.getInt("gender");
            RadioButton c_gender_check=findViewById(gender);
            c_gender_check.setChecked(true);
            int region=savedInstanceState.getInt("region");
            c_region.setSelection(region);
            int job=savedInstanceState.getInt("job");
            c_job.setSelection(job);
            String height=savedInstanceState.getString("height");
            c_height.setText(height);
            int smoke=savedInstanceState.getInt("smoke");
            RadioButton c_smoke_check=findViewById(smoke);
            c_smoke_check.setChecked(true);
        }

    }

    @Override
    protected void onStart() {
        super.onStart();


    }
    public class NetworkTask extends AsyncTask<Void, Void, String> {

        private String url;
        private ContentValues values;

        public NetworkTask(String url, ContentValues values) {
            this.url = url;
            this.values = values;
        }

        @Override
        protected String doInBackground(Void... params) {

            String result; // 요청 결과를 저장할 변수.
            RequestHttpConnection requestHttpURLConnection = new RequestHttpConnection();
            result = requestHttpURLConnection.request(url, values); // 해당 URL로 부터 결과물을 얻어온다.
            Log.i("결과",result);
            return result;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if(s.equals("personalprofileno")){
                //여기에 머무른다.
            }else if(s.equals("personalimgok")){
                //메인으로 보낸다.
                Intent intent=new Intent(PersonalProfileActivity.this,MainActivity.class);
                startActivity(intent);
                finish();
            }else if(s.equals("personalprofileok")){ //다른 값이 들어왔을 때
                //이 때, db내용 지우기
                String url = "http://??/personal_profile_detail.php";

                // AsyncTask를 통해 HttpURLConnection 수행.
                ContentValues values=new ContentValues();
                // 개인정보 보냄
                SharedPreferences login_true=getSharedPreferences("login_true", Activity.MODE_PRIVATE);
                values.put("c_id_email",login_true.getString("login_email",""));

                NetworkTask networkTask = new NetworkTask(url, values);
                networkTask.execute();
//                Intent intent=new Intent(PersonalProfileActivity.this,PersonalPicActivity.class);
//                startActivity(intent);
            }else{
                //들어온 값들을 저장.
                Log.i("무슨값이 들어왔을까?",s);

                Intent intent=new Intent(PersonalProfileActivity.this, PersonalPicActivity.class);

                String profile[]=s.split("/");

                   // Log.i("응???",String.valueOf(profile.length));


                    if(profile.length==7){

                c_nickname.setText(profile[0]);
                c_age.setText(profile[1]);

                RadioButton c_gender_check=findViewById(R.id.c_gender_female);
                if(profile[2].equals("남자")){
                    c_gender_check=findViewById(R.id.c_gender_male);
                }
                c_gender_check.setChecked(true);

                c_region.setSelection(Integer.parseInt(profile[3]));
                c_job.setSelection(Integer.parseInt(profile[4]));
                c_height.setText(profile[5]);
                RadioButton c_smoke_check=findViewById(R.id.c_smoke_ok);
                    if(profile[2].equals("무")){
                        c_smoke_check=findViewById(R.id.c_smoke_no);
                }
                c_smoke_check.setChecked(true);

                    startActivity(intent);

                    }




            }

        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i("personalprofile","resume");

        personal_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i("personalprofile","next");



                String result="true";
                String reason="";
                if(c_nickname.getText().toString().equals("")){
                    result="false";
                    reason+="[닉네임]";
                    Log.i("personalprofile","nickname적으세요");
                }else{
                    if(c_nickname.getText().length()>10){
                        result="false";
                        reason+="[닉네임]";
                        Toast.makeText(getApplicationContext(),"닉네임을 10자 내외로 작성해주세요.",Toast.LENGTH_SHORT).show();
                    }
                    Log.i("personalprofile",c_nickname.getText().toString());
                }


                if(c_age.getText().toString().equals("")){
                    result="false";
                    reason+="[나이]";
                    Log.i("personalprofile","age적으세요");
                }else{

                    //나이는 적었는데 만약 나이가 20살 미만인 경우,

                    String age=c_age.getText().toString();


                    if(Integer.parseInt(age)<20){
                        c_age.setText("");
                        result="false";
                        reason+="[나이]";
                        AlertDialog.Builder alert = new AlertDialog.Builder(PersonalProfileActivity.this);
                        //다이얼로그 창 띄어주기
                        alert.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();     //닫기
                            }
                        });

                        alert.setMessage("20살 미만은 어플 가입에 제한됩니다.");
                        alert.show();

                    }

                    Log.i("personalprofile",c_age.getText().toString());
                }


                RadioButton c_gender_check=findViewById(c_gender.getCheckedRadioButtonId());

                if(c_gender_check==null){

                    result="false";
                    reason+="[성별]";
                    Log.i("personalprofile","성별선택");

                }else{

                    Log.i("personalprofile",c_gender_check.getText().toString());
                }


                    Log.i("personalprofile",c_region.getSelectedItem().toString());




                Log.i("personalprofile",c_job.getSelectedItem().toString());


                if(c_height.getText().toString().equals("")){
                    result="false";
                    reason+="[키]";
                    Log.i("personalprofile","c_height적으세요");
                }else{


                    Log.i("personalprofile",c_height.getText().toString());
                }

                RadioButton c_smoke_check=findViewById(c_smoke.getCheckedRadioButtonId());

                if(c_smoke_check==null){

                    result="false";
                    reason+="[흡연여부]";
                    Log.i("personalprofile","흡연여부선택");

                }else{

                    Log.i("personalprofile",c_smoke_check.getText().toString());
                }



                if(result.equals("false")){
                    AlertDialog.Builder alert = new AlertDialog.Builder(PersonalProfileActivity.this);
                    //다이얼로그 창 띄어주기
                    alert.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();     //닫기
                        }
                    });

                    alert.setMessage(reason+" 입력이 완료되어야 다음단계로 넘어갈 수 있습니다.");
                    alert.show();


                    //이 때, db내용 지우기
                    String url = "http://??/personal_profile.php";

                    // AsyncTask를 통해 HttpURLConnection 수행.
                    ContentValues values=new ContentValues();
                    // 개인정보 보냄
                    SharedPreferences login_true=getSharedPreferences("login_true", Activity.MODE_PRIVATE);
                    values.put("c_id_email",login_true.getString("login_email",""));
                    values.put("deleteProfile","true");
                    NetworkTask networkTask = new NetworkTask(url, values);
                    networkTask.execute();

                }else{
                    //사진 등록으로 넘어가기

                    Intent intent=new Intent(PersonalProfileActivity.this, PersonalPicActivity.class);

                    String url = "http://??/personal_profile.php";

                    // AsyncTask를 통해 HttpURLConnection 수행.
                    ContentValues values=new ContentValues();
                    // 개인정보 보냄

                    //자동로그인 -> auto_login/login_true둘다 있음
                    //일반로그인 -> login_true에 있음.
                    SharedPreferences login_true=getSharedPreferences("login_true", Activity.MODE_PRIVATE);

                    String id="";
                  if(login_true!=null){
                        id=login_true.getString("login_email","");
                      values.put("c_id_email",id);
                      values.put("c_nickname",c_nickname.getText().toString());
                      values.put("c_age",Integer.parseInt(c_age.getText().toString()));
                      values.put("c_gender",c_gender_check.getText().toString());

                      values.put("c_region_item",c_region.getSelectedItemPosition());
                      values.put("c_region",c_region.getSelectedItem().toString());

                      values.put("c_job_item",c_job.getSelectedItemPosition());
                      values.put("c_job",c_job.getSelectedItem().toString());

                      values.put("c_height",Integer.parseInt(c_height.getText().toString()));
                      values.put("c_smoke",c_smoke_check.getText().toString());

                      NetworkTask networkTask = new NetworkTask(url, values);
                      networkTask.execute();
                      startActivity(intent);


                  }else{
                      Toast.makeText(getApplicationContext(),"관리자에게 문의하세요.",Toast.LENGTH_SHORT).show();
                  }


                }
            }
        });

    }


    @Override
    protected void onPause() {
        super.onPause();
        Log.i("personalprofile","개인정보pause");

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);


        c_nickname=findViewById(R.id.c_nickname);
        c_age=findViewById(R.id.c_age);
        c_gender=findViewById(R.id.c_gender);
        c_region=findViewById(R.id.c_region);
        c_job=findViewById(R.id.c_job);
        c_height=findViewById(R.id.c_height);
        c_smoke=findViewById(R.id.c_smoke);


        String nickname=c_nickname.getText().toString();
        String age=c_age.getText().toString();
        int gender=c_gender.getCheckedRadioButtonId();
        int region=c_region.getSelectedItemPosition();
        int job=c_job.getSelectedItemPosition();
        String height=c_height.getText().toString();
        int smoke=c_smoke.getCheckedRadioButtonId();

        outState.putString("nickname",nickname);
        outState.putString("age",age);
        outState.putInt("gender",gender);
        outState.putInt("region",region);
        outState.putInt("job",job);
        outState.putString("height",height);
        outState.putInt("smoke",smoke);
    }
}
