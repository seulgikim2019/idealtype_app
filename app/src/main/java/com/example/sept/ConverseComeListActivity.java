package com.example.sept;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sept.Adapter.ConverseComeAdapter;
import com.example.sept.Class.CustomContent;
import com.example.sept.Class.CustomContent1;
import com.example.sept.Interface.UploadService;
import com.example.sept.Retrofit.MyRetrofit2;

import java.util.ArrayList;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.example.sept.MainActivity.c_name_final;

public class ConverseComeListActivity extends AppCompatActivity {


    Toolbar up_toolbar_converse;
    ArrayList<CustomContent1> list_come;
    ArrayList<CustomContent1> list_go;
    ConverseComeAdapter adapter_come;
    ConverseComeAdapter adapter_go;
    RecyclerView recyclerView_1;
    RecyclerView recyclerView_2;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.converse_come_list);

        Log.i("conversercomelist","create");

        SharedPreferences login_true=getSharedPreferences("login_true", Activity.MODE_PRIVATE);
        // 로그인 이메일 정보를 가지고 옴.
        String loginTrue = login_true.getString("login_email","no");

        up_toolbar_converse=findViewById(R.id.up_toolbar_conver);
        setSupportActionBar(up_toolbar_converse);
        getSupportActionBar().setSubtitle(c_name_final+"님 대화요청방");

        recyclerView_1=findViewById(R.id.convers_come_list);
        list_come=new ArrayList<>();


        recyclerView_2=findViewById(R.id.convers_go_list);
        list_go=new ArrayList<>();

        //2개의 어뎁터만들기
        adapter_come=new ConverseComeAdapter(list_come);
        adapter_go=new ConverseComeAdapter(list_go);

       // recyclerView_1.setAdapter(customAdapter);

        recyclerView_1.setAdapter(adapter_come);
        recyclerView_2.setAdapter(adapter_go);

//        // 리사이클러뷰에 LinearLayoutManager 객체 지정.
        recyclerView_1.setLayoutManager(new LinearLayoutManager(this,RecyclerView.VERTICAL, false));

        recyclerView_2.setLayoutManager(new LinearLayoutManager(this,RecyclerView.VERTICAL, false));


        //네트워크 요청
        UploadService service = MyRetrofit2.getRetrofit2().create(UploadService.class);

        RequestBody my_id= RequestBody.create(
                MediaType.parse("multipart/form-data"), loginTrue);

        Call<CustomContent> call1 = service.conversComeList(my_id);

        call1.enqueue(new Callback<CustomContent>() {
            @Override
            public void onResponse(Call<CustomContent> call, Response<CustomContent> response) {


                //region-> 숫자에 따라서 문자 넣어줘야 함.
                String[] city = getResources().getStringArray(R.array.city);
                for (int i = 0; i < response.body().result.length; i++) {
                    Log.i("converse", response.body().result[i].getResult());


                    String result = response.body().result[i].getResult();
                    //come -> come recyclerview
                    if (result.equals("come")) {
                        list_come.add(new CustomContent1(response.body().result[i].getCus_contents_email(),
                            response.body().result[i].getCus_contents_nickname(),
                            response.body().result[i].getCus_contents_pic(),
                            response.body().result[i].getCus_contents_age_gender(),
                            city[Integer.parseInt(response.body().result[i].getCus_contents_region())]));
                    } else {//go -> go recyclerview
                        list_go.add(new CustomContent1(response.body().result[i].getCus_contents_email(),
                                response.body().result[i].getCus_contents_nickname(),
                                response.body().result[i].getCus_contents_pic(),
                                response.body().result[i].getCus_contents_age_gender(),
                                city[Integer.parseInt(response.body().result[i].getCus_contents_region())]));
                    }


                }

                adapter_come.notifyDataSetChanged();
                adapter_go.notifyDataSetChanged();

            }

            @Override
            public void onFailure(Call<CustomContent> call, Throwable t) {
                Log.i("Converse fail",t.toString());
            }
        });




        //상세보기 버튼 눌었을 때 -> come list에서
        //recyclerview 화면 눌렀을 때
        adapter_come.setOnItemClickListener(new ConverseComeAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(ConverseComeAdapter.ViewHolder holder, View view, int position) {
                Log.i("click",String.valueOf(position));

                Log.i("click",String.valueOf(list_come.get(position).getCus_contents_email()));



                Intent intent=new Intent(getApplicationContext(),ProfileShowAcceptActivity.class);
                intent.putExtra("come_id",list_come.get(position).getCus_contents_email());
                startActivity(intent);
            }

        });


        //상세보기 버튼 눌었을 때 -> go list에서
        //recyclerview 화면 눌렀을 때
        adapter_go.setOnItemClickListener(new ConverseComeAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(ConverseComeAdapter.ViewHolder holder, View view, int position) {
                Log.i("click",String.valueOf(position));

                Log.i("click",String.valueOf(list_go.get(position).getCus_contents_email()));


                Intent intent=new Intent(getApplicationContext(),ProfileShowAcceptActivity.class);
                intent.putExtra("go_id",list_go.get(position).getCus_contents_email());
                startActivity(intent);
            }

        });


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
            Intent intent=new Intent(ConverseComeListActivity.this,MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
            Toast.makeText(ConverseComeListActivity.this, "logout clicked", Toast.LENGTH_LONG).show();
            return true;
        }else if (id == R.id.action_main) { //메인페이지를 눌렀을 때

            Toast.makeText(ConverseComeListActivity.this, "main clicked", Toast.LENGTH_LONG).show();
            Intent intent=new Intent(ConverseComeListActivity.this,MainActivity.class);
            startActivity(intent);
            finish();
            return true;
        }else if (id == R.id.action_mypage) { //마이페이지를 눌렀을 때
            Toast.makeText(ConverseComeListActivity.this, "mypage clicked", Toast.LENGTH_LONG).show();
            Intent intent=new Intent(ConverseComeListActivity.this, MyProfileActivity.class);
            startActivity(intent);
            finish();
            return true;
        }else if (id == R.id.action_conversation) { //대화방
            Toast.makeText(ConverseComeListActivity.this, "conversation clicked", Toast.LENGTH_LONG).show();
            Intent intent=new Intent(ConverseComeListActivity.this, ConversationListActivity.class);
            startActivity(intent);
            finish();
            //아직 대화방 없음.,..
            return true;
        }

        return super.onOptionsItemSelected(item);
    }





    @Override
    protected void onStart() {
        super.onStart();


        Log.i("conversercomelist","start");


    }


    @Override
    protected void onResume() {
        super.onResume();
        Log.i("conversecomelist","resume");


    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.i("conversecomelist","pause");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.i("conversecomelist","restart");
    }
}
