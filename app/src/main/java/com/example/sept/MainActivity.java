package com.example.sept;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.sept.Class.BackPressCloseHandler;
import com.example.sept.Class.RequestHttpConnection;
import com.example.sept.Service.ChatService;
import com.example.sept.Service.TestService;

import java.util.ArrayList;

//메인 화면 -> 로그인 여부 체크, 프로필 설정 체크
public class MainActivity extends AppCompatActivity {

    private BackPressCloseHandler backPressCloseHandler;

   // TextView login_true_id;
   // Button logout;
    Toolbar up_toolbar;
    ImageView card1;
    ImageView card2;
    ImageView card3;
    ImageView card4;
    static String c_name_final;
    Context context;




    static ArrayList<LinearLayout> container_list;
    static ArrayList<String> container_list_id;
    static ArrayList<String> container_list_img;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        context=getApplicationContext();

       // login_true_id=findViewById(R.id.login_true_id);
     //   logout=findViewById(R.id.logout);
        backPressCloseHandler = new BackPressCloseHandler(this);
        up_toolbar=findViewById(R.id.up_toolbar);
        setSupportActionBar(up_toolbar);
        card1=findViewById(R.id.card1);
        card2=findViewById(R.id.card2);
        card3=findViewById(R.id.card3);
        card4=findViewById(R.id.card4);

        container_list=new ArrayList<>();
        container_list_id=new ArrayList<>();
        container_list_img=new ArrayList<>();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            context.startForegroundService(new Intent(context, TestService.class));
            Intent intent1=new Intent(MainActivity.this, ChatService.class);
            intent1.putExtra("room","main");
            context.startForegroundService(intent1);
        } else {

            context.startService(new Intent(context, TestService.class));
            Intent intent1=new Intent(MainActivity.this,ChatService.class);
            intent1.putExtra("room","main");
            context.startService(intent1);
        }


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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
                Intent intent=new Intent(MainActivity.this,MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            Toast.makeText(MainActivity.this, "logout clicked", Toast.LENGTH_LONG).show();
            return true;
        }else if (id == R.id.action_main) { //메인페이지를 눌렀을 때
            Toast.makeText(MainActivity.this, "main clicked", Toast.LENGTH_LONG).show();
            return true;
        }else if (id == R.id.action_mypage) { //마이페이지를 눌렀을 때
            Toast.makeText(MainActivity.this, "mypage clicked", Toast.LENGTH_LONG).show();
            Intent intent=new Intent(MainActivity.this, MyProfileActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        backPressCloseHandler.onBackPressed();
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

            if(s.equals("personalprofileok")){ //프로필만 등록된 경우이므로
                Intent intent=new Intent(MainActivity.this, PersonalProfileActivity.class);
               // Intent intent=new Intent(MainActivity.this,PersonalPicActivity.class);
                startActivity(intent);

            }else if(s.equals("personalprofileno")){ //개인정보가 없는 경우
                Intent intent=new Intent(MainActivity.this, PersonalProfileActivity.class);
                startActivity(intent);

            }else if(s.contains("personalimgok")){ //이미지까지 모두 저장이 된 경우
                //걍 메인에 있도록 하기
                String c_name[]=s.split("/");
                c_name_final=c_name[1];
                Log.i("여기요",c_name[1]);
                getSupportActionBar().setSubtitle(c_name[1]+"님 환영합니다!");

            }


        }
    }


    @Override
    protected void onStart() {
        super.onStart();
        //자동로그인 여부를 체크한다.
        SharedPreferences auto_login = getSharedPreferences("auto_login", Activity.MODE_PRIVATE);
        SharedPreferences login_true=getSharedPreferences("login_true", Activity.MODE_PRIVATE);
        // 로그인 이메일 정보를 가지고 옴.
        String loginEmail = auto_login.getString("login_email", "no");
        String loginTrue = login_true.getString("login_email","no");
        if(loginEmail.equals("no")){


            if(loginTrue.equals("no")){
                Intent intent=new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }else{

                //자동저장은 아니나, 로그인이 이루어지면 이리로 넘어온다.
                Log.i("here","here");

                //그럼 여기서 개인 프로필이 작성이 안되어 있는 경우, 개인 프로필 작성으로 바로 넘어가게끔 하기.
                //서버와 연동해서 이 부분을 체크해야함.
                String url = "http://??/personal_profile_check.php";

                ContentValues values=new ContentValues();
                //이메일 값을 보냄.
                values.put("id_email",loginTrue);
                NetworkTask networkTask = new NetworkTask(url, values);
                networkTask.execute();

            }




        }else{


            //우선은 login_true에 값 넣어주기
            SharedPreferences.Editor editor=login_true.edit();
            editor.putString("login_email",loginEmail);
            editor.commit();

            //그럼 여기서 개인 프로필이 작성이 안되어 있는 경우, 개인 프로필 작성으로 바로 넘어가게끔 하기.
            //서버와 연동해서 이 부분을 체크해야함.

            String url = "http://??/personal_profile_check.php";

            ContentValues values=new ContentValues();
            values.put("id_email",loginEmail);
            NetworkTask networkTask = new NetworkTask(url, values);
            networkTask.execute();

//            Intent intent=new Intent(MainActivity.this,PersonalProfileActivity.class);
//            startActivity(intent);


        }

    }


    @Override
    protected void onResume() {
        super.onResume();

        card1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i("click","main->customlist");
                Intent intent=new Intent(MainActivity.this,CustomListActivity.class);
                startActivity(intent);
            }
        });



        card2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i("click","main->이상형list");
                Intent intent=new Intent(MainActivity.this,IdealListActivity.class);
                startActivity(intent);
            }
        });

        card3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {  //대화방

                Log.i("click","main->room");
                Intent intent=new Intent(MainActivity.this, ConversationListActivity.class);
                startActivity(intent);

            }
        });



        card4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Log.i("click","main->mypage");
                Intent intent=new Intent(MainActivity.this, MyProfileActivity.class);
                startActivity(intent);
            }
        });


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i("destroy","destroy");
        SharedPreferences login_true=getSharedPreferences("login_true", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor=login_true.edit();
        editor.remove("login_email");
        editor.clear();
        editor.commit();

        stopService(new Intent(getApplicationContext(),TestService.class));

    }
}