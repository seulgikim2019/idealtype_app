package com.example.sept;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

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
import android.widget.CheckBox;
import android.widget.EditText;

import com.example.sept.Class.RequestHttpConnection;


//로그인 페이지 (자동로그인을 통해 로그인 / 회원가입)
public class LoginActivity extends AppCompatActivity {


    EditText login_email;
    EditText login_psword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.i("login.j","create");
        setContentView(R.layout.login_email);
        setTitle("로그인");

        login_email=findViewById(R.id.loginEmail);
        login_psword=findViewById(R.id.loginPassword);


    }



    //회원가입 후 id 받기
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Log.i("login.j","resultt");

        if(requestCode==1){
            if(resultCode==RESULT_OK){

                String email = data.getStringExtra("login_email");

                Log.i("login.j","result: "+email);
                SharedPreferences login_pause=getSharedPreferences("login_pause",Activity.MODE_PRIVATE);
                SharedPreferences.Editor editor=login_pause.edit();
                editor.putString("login_email",email);
                editor.commit();
                //회원가입한 이메일 값 가지고 있기.
                login_email.setText(email);
            }
        }

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

            return result;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            Log.i("login.j:db",s);

            if(s.equals("loginfalse")){ //id와 비번이 다른 경우를 의미
                AlertDialog.Builder alert = new AlertDialog.Builder(LoginActivity.this);
                alert.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();     //닫기
//                                            startActivity(loginIntent);
                    }
                });

                alert.setMessage("아이디와 비밀번호가 일치하지 않습니다.");
                alert.show();


            }else{

                if(s.equals("t")){ //자동로그인
                    SharedPreferences auto_login=getSharedPreferences("auto_login",Activity.MODE_PRIVATE);
                    SharedPreferences.Editor editor=auto_login.edit();
                    editor.putString("login_email",login_email.getText().toString());
                    editor.commit();
                }

                SharedPreferences login_true=getSharedPreferences("login_true",Activity.MODE_PRIVATE);
                SharedPreferences.Editor editor1=login_true.edit();
                editor1.putString("login_email",login_email.getText().toString());
                editor1.commit();

                Intent intent=new Intent(LoginActivity.this,MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
                finish();
            }

        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.i("login.j","start");
    }


    @Override
    protected void onResume() {
        super.onResume();



        //shared에 저장된 아이디가 있는 경우

        SharedPreferences login_pause=getSharedPreferences("login_pause",Activity.MODE_PRIVATE);
        if(login_pause!=null){
            String id=login_pause.getString("login_email","");
            login_email.setText(id);
        }


        Log.i("login.j","resume");

        Button.OnClickListener onClickListener = new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (view.getId()) {
                    case R.id.btnLogin:
                    //로그인 버튼 클릭시 -> id, password 받기


                        CheckBox auto_login=findViewById(R.id.autoLogin);

                        String auto_check="t";
                        if (auto_login.isChecked()) {
                            auto_check="t";
                        } else {
                            auto_check="f";
                        }
                        Log.i("login.j:로그인 클릭","아이디 "+login_email.getText().toString()+
                                " /비번: "+login_psword.getText().toString());

                        Log.i("login.j:로그인 클릭","자동로그인"+auto_check);


                        // 로그인 확인 차 보내기

                        String url = "http://??/login_check.php";

                        // AsyncTask를 통해 HttpURLConnection 수행.
                        ContentValues values=new ContentValues();
                        //이메일 값을 보냄.
                        values.put("id_email",login_email.getText().toString());
                        values.put("psword",login_psword.getText().toString());
                        values.put("auto_check",auto_check);
                        NetworkTask networkTask = new NetworkTask(url, values);
                        networkTask.execute();


                        break ;
                    case R.id.btnEnroll:
                         //회원가입 버튼 클릭시
                        //회원가입 창으로 이동하면서 본 로그인 페이지는 없애기
                        Log.i("login.j:회원가입","회원가입클릭");
                        Intent intent=new Intent(LoginActivity.this, EmailEnrollActivity.class);
                        startActivityForResult(intent, 1);
                        break ;
                }
            }
        } ;
        Button btnLogin = (Button) findViewById(R.id.btnLogin) ;
        btnLogin.setOnClickListener(onClickListener) ;

        Button btnEnroll = (Button) findViewById(R.id.btnEnroll) ;
        btnEnroll.setOnClickListener(onClickListener) ;
    }

    @Override
    protected void onPause() {
        super.onPause();

        Log.i("login.j","pause");
        SharedPreferences login_pause=getSharedPreferences("login_pause",Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor=login_pause.edit();
        editor.putString("login_email",login_email.getText().toString());
        editor.commit();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
