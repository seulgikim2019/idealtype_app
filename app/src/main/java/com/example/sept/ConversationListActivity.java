package com.example.sept;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
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

import com.example.sept.Adapter.ConversationAdapter;
import com.example.sept.Class.ConversationList;
import com.example.sept.Class.ConversationList1;
import com.example.sept.Interface.UploadService;
import com.example.sept.Retrofit.MyRetrofit2;

import java.util.ArrayList;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.example.sept.MainActivity.c_name_final;

public class ConversationListActivity extends AppCompatActivity {


    Toolbar up_toolbar_converse;
    public static ArrayList<ConversationList1> conversationList1slist;
    public static ConversationAdapter adapter;
    RecyclerView recyclerView;
    String clickRm;
    SharedPreferences login_true;
    String loginTrue;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.conversation_list);

        Log.i("conversation list","create");
        login_true=getSharedPreferences("login_true", Activity.MODE_PRIVATE);
        // 로그인 이메일 정보를 가지고 옴.
        loginTrue = login_true.getString("login_email","no");

        up_toolbar_converse=findViewById(R.id.up_toolbar_conver);
        setSupportActionBar(up_toolbar_converse);
        getSupportActionBar().setSubtitle(c_name_final+"님 대화방");

        recyclerView=findViewById(R.id.convers_list);
        conversationList1slist=new ArrayList<>();

        adapter=new ConversationAdapter(conversationList1slist);
       // recyclerView_1.setAdapter(customAdapter);

        recyclerView.setAdapter(adapter);

//        // 리사이클러뷰에 LinearLayoutManager 객체 지정.
        recyclerView.setLayoutManager(new LinearLayoutManager(this,RecyclerView.VERTICAL, false));


//        //네트워크 요청
//        UploadService service = MyRetrofit2.getRetrofit2().create(UploadService.class);
//
//        RequestBody my_id= RequestBody.create(
//                MediaType.parse("multipart/form-data"), loginTrue);
//
//        Call<ConversationList> call1 = service.conversationRoomList(my_id);
//
//        call1.enqueue(new Callback<ConversationList>() {
//            @Override
//            public void onResponse(Call<ConversationList> call, Response<ConversationList> response) {
//
//
////            Log.i("hey",response.body().result[0].getResult());
//
//            if(response.body().result.length!=0){
//                for (int i = 0; i <response.body().result.length ; i++) {
//                    list.add(new ConversationList1(response.body().result[i].getId(),
//                            response.body().result[i].getNickname(),response.body().result[i].getC_roomNum(),
//                            response.body().result[i].getC_img_uri()));
//                }
//
//
//                adapter.notifyDataSetChanged();
//            }
//
//
//            }
//
//            @Override
//            public void onFailure(Call<ConversationList> call, Throwable t) {
//                Log.i("Converse fail",t.toString());
//            }
//        });

        adapter.setOnItemClickListener(new ConversationAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(ConversationAdapter.ViewHolder holder, View view, int position) {
                Log.i("click",conversationList1slist.get(position).getC_roomNum().toString());
                clickRm=conversationList1slist.get(position).getC_roomNum();
                Intent intent=new Intent(getApplicationContext(),MessageActivity.class);
                intent.putExtra("clickRm",clickRm);
                intent.putExtra("clickPosition",String.valueOf(position));
                intent.putExtra("clickId",conversationList1slist.get(position).getId());
                intent.putExtra("clickImg",conversationList1slist.get(position).getC_img_uri());
                intent.putExtra("clickNick",conversationList1slist.get(position).getNickname());
                startActivity(intent);


            }
        });






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

            Intent intent=new Intent(ConversationListActivity.this,MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
            Toast.makeText(ConversationListActivity.this, "logout clicked", Toast.LENGTH_LONG).show();
            return true;
        }else if (id == R.id.action_main) { //메인페이지를 눌렀을 때

            Toast.makeText(ConversationListActivity.this, "main clicked", Toast.LENGTH_LONG).show();
            Intent intent=new Intent(ConversationListActivity.this,MainActivity.class);
            startActivity(intent);
            finish();
            return true;
        }else if (id == R.id.action_mypage) { //마이페이지를 눌렀을 때
            Toast.makeText(ConversationListActivity.this, "mypage clicked", Toast.LENGTH_LONG).show();
            Intent intent=new Intent(ConversationListActivity.this, MyProfileActivity.class);
            startActivity(intent);
            finish();
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
        //네트워크 요청
        UploadService service = MyRetrofit2.getRetrofit2().create(UploadService.class);

        RequestBody my_id= RequestBody.create(
                MediaType.parse("multipart/form-data"), loginTrue);

        Call<ConversationList> call1 = service.conversationRoomList(my_id);

        call1.enqueue(new Callback<ConversationList>() {
            @Override
            public void onResponse(Call<ConversationList> call, Response<ConversationList> response) {


                if(response.body().result.length!=0){

                    if(conversationList1slist.size()!=0){
                        conversationList1slist.clear();
                    }


                    //이미 생성되었던 방이라면 db에 저장되어 있는 값이 있을 것이다...
                    SQLiteDatabase ReadDB = getApplication().openOrCreateDatabase("conversation", MODE_PRIVATE, null);





                    for (int i = 0; i <response.body().result.length ; i++) {

//테이블이 존재하지 않으면 새로 생성합니다.
                        ReadDB.execSQL("CREATE TABLE IF NOT EXISTS '" + response.body().result[i].getC_roomNum()
                                + "' (c_seq integer primary key autoincrement, nickname VARCHAR(250)," +
                                "thattime date VARCHAR(250)," +
                                " id VARCHAR(250), content VARCHAR(250));");

                        //SELECT문을 사용하여 테이블에 있는 데이터를 가져옵니다..
                        Cursor c = ReadDB.rawQuery("SELECT * FROM '" + response.body().result[i].getC_roomNum() +"'", null);
                        String content_read = "";
                        if (c != null) {


                            if(c.moveToLast()){
                                content_read = c.getString(c.getColumnIndex("content"));
                                if(content_read.contains("IMG")&&content_read.contains(".jpg")){
                                    content_read="사진";
                                }
                            }

//                            if (c.moveToFirst()) {
//                                do {
//
//                                    content_read = c.getString(c.getColumnIndex("content"));
//
//                                } while (c.moveToNext());
//                            }
                        }

                        conversationList1slist.add(new ConversationList1(response.body().result[i].getId(),
                                response.body().result[i].getNickname(),response.body().result[i].getC_roomNum(),
                                response.body().result[i].getC_img_uri(),content_read));
                    }


                    adapter.notifyDataSetChanged();

                    ReadDB.close();
                }


            }

            @Override
            public void onFailure(Call<ConversationList> call, Throwable t) {
                Log.i("Converse fail",t.toString());
            }
        });

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
