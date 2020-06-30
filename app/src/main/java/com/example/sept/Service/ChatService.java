package com.example.sept.Service;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Notification;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.app.NotificationCompat;

import com.bumptech.glide.Glide;
import com.example.sept.Class.ConversationAcceptList;
import com.example.sept.Class.ConversationList;
import com.example.sept.Class.ConversationList1;
import com.example.sept.Interface.UploadService;
import com.example.sept.MessageActivity;
import com.example.sept.Retrofit.MyRetrofit2;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.example.sept.ConversationListActivity.adapter;
import static com.example.sept.ConversationListActivity.conversationList1slist;
import static com.example.sept.MessageActivity.clickRm;

public class ChatService extends Service {
    boolean isRunning=false;

    SQLiteDatabase sampleDB = null;
    ConnectionThread connectionThread;
    ChatServiceThread chatServiceThread;
    SharedPreferences login_true;
    String loginTrue;

    public static ArrayList<Socket> member_socket_list;
    ArrayList<ConversationList1> list;
//임시방편
    static ArrayList<String> say;


    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }




    @Override
    public void onCreate() {
        super.onCreate();
        sampleDB = this.openOrCreateDatabase("conversation", MODE_PRIVATE, null);


        Log.i("servicetest","create");
        Notification notification = new NotificationCompat.Builder(this, "5032")
                .build();

        startForeground(1, notification);
        chatServiceThread=new ChatServiceThread();
        say=new ArrayList<>();

        member_socket_list=new ArrayList<>();

        list=new ArrayList<>();
    }





    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i("chat servicetest","startcommand");



        if (intent!=null){
            String room=intent.getStringExtra("room");

            login_true=getSharedPreferences("login_true", Activity.MODE_PRIVATE);
            // 로그인 이메일 정보를 가지고 옴.
            loginTrue = login_true.getString("login_email","no");
            if(!room.equals("main")){
                Log.i("servicetest",room);
                connectionThread=new ConnectionThread(room);
                connectionThread.start();
            }else{
                Log.i("room","null입니다.");
                Thread.State state=chatServiceThread.getState();

                if(state == Thread.State.NEW){
                    chatServiceThread.start();
                }
            }
        }


        return START_STICKY;

    }



    public class ChatServiceThread extends Thread{

        boolean isRun = true;

        public ChatServiceThread(){

        }

        public void stopForever(){
            synchronized (this) {
                this.isRun = false;
            }
        }

        public void run(){
            //반복적으로 수행할 작업을 한다.
            while(isRun){

                UploadService service = MyRetrofit2.getRetrofit2().create(UploadService.class);


                RequestBody my_id= RequestBody.create(
                        MediaType.parse("multipart/form-data"), loginTrue);
                Call<ConversationAcceptList> call1 = service.conversationRoomList2(my_id);

                call1.enqueue(new Callback<ConversationAcceptList>() {
                    @Override
                    public void onResponse(Call<ConversationAcceptList> call, Response<ConversationAcceptList> response) {


                            if(response.body().result.length!=0){

                                for (int i = 0; i <response.body().result.length ; i++) {

                                    String room=response.body().result[i].getC_roomNum();


                                    Log.i("방이개설되겠군요..",room);
                                    connectionThread=new ConnectionThread(room);

                                    connectionThread.start();

                                }
                            }else{
                                Log.i("conversationAccept","noroom");
                            }



                    }

                    @Override
                    public void onFailure(Call<ConversationAcceptList> call, Throwable t) {
                        Log.i("chatservice fail",t.toString());
                    }
                });



                try{
                    Thread.sleep(10000);
                }catch (Exception e) {
                }
            }
        }
    }



    String user_nickname;
    Socket member_socket;

    //thread만들어주기 -> connection
    boolean isConnect;
    public class ConnectionThread extends Thread {

        // 서버와 연결되어있는 소켓 객체

        // 사용자 닉네임( 내 닉넴과 일치하면 내가보낸 말풍선으로 설정 아니면 반대설정)


        boolean isRun=true;
        String room;

        public ConnectionThread(String room){
            this.room=room;
        }



        public void stopForever(){
            synchronized (this) {
                this.isRun = false;
            }
        }

        @Override
        public void run() {
            try {
                // 접속한다.
                final Socket socket;
                try {


                    socket = new Socket("192.168.0.13", 9999);

                    member_socket = socket;

                    //리스트 넣어놓기
                    member_socket_list.add(member_socket);


                    String nickName = "room/"+room+"\n";




                    user_nickname = nickName;
                    // 스트림을 추출
                    OutputStream os = socket.getOutputStream();
                    DataOutputStream dos = new DataOutputStream(os);
                    // 닉네임을 송신한다.


                    byte[] buffer=user_nickname.getBytes();

                    dos.write(buffer);



                    Log.i("보냅니다요",String.valueOf(buffer));

//                    dos.writeUTF(nickName);

                    isConnect = true;

                    // 메세지 수신을 위한 스레드 가동
                    isRunning=true;
                    MessageThread thread=new MessageThread(socket);
                    thread.start();

                } catch (IOException e) {
                    e.printStackTrace();
                }

            }catch (Exception e) {
                e.printStackTrace();
            }




        }
    }



    class MessageThread extends Thread {
        Socket socket;
        DataInputStream dis;

        public MessageThread(Socket socket) {
            try {
                this.socket = socket;
                InputStream is = socket.getInputStream();
                dis = new DataInputStream(is);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void run() {
            try{
                while (isRunning){
                    // 서버로부터 데이터를 수신받는다.
                    //final String msg=dis.readUTF();
                    // 화면에 출력

                   // int read=dis.read();
                  //  final String msg=dis.read();


                    Log.i("msg를봐야할거같아", "여기요여기");




                 //   StringBuffer sb = new StringBuffer();
                  //  byte[] b = new byte[4096];

                    String msg="";
                    int c=0;
                    while ( '\n' != (char)( c = dis.read()) ) {
                    //    System.out.println((char)c);
                        msg+=String.valueOf((char)c);
                      //  System.out.println("여깁니다요2"+msg);
                    }

//                    for (int n; (n = dis.read(b)) != -1;) {
//                        sb.append(new String(b, 0, n));
//                    }



                    Log.i("msg를봐야할거같아", msg); //msg

//                            // 텍스트뷰의 객체를 생성
//                            final TextView tv=new TextView(getApplicationContext());
//                            tv.setText(msg);
//                            Log.i("text",tv.getText().toString());

                    //핸들러를 통해서... ?
                    Message message=handler.obtainMessage();
                    message.obj= msg; //msg
                    handler.sendMessage(message);

                }

            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }



    @SuppressLint("HandlerLeak")
    final Handler handler = new Handler()
    {
        public void handleMessage(Message msg)
        {
            //UI 변경 작업을 코딩하세요.
            Log.i("message",msg.obj.toString());

                //이제 null이 아니니까...:)
               // say=new ArrayList<>();

                final TextView tv=new TextView(getApplicationContext());
                tv.setPadding(0,0,0,10);

            final ImageView imageView=new ImageView(getApplicationContext());


                    //임시저장 넣어놓기.
              //  say.add(msg.obj.toString());

                String[] message=msg.obj.toString().split(":");

                String nick=message[1];
                String room_id=message[0];
                String id=message[2];

//테이블이 존재하지 않으면 새로 생성합니다.
                sampleDB.execSQL("CREATE TABLE IF NOT EXISTS '" + room_id
                        + "' (c_seq integer primary key autoincrement, nickname VARCHAR(250)," +
                        "thattime date VARCHAR(250)," +
                        " id VARCHAR(250), content VARCHAR(250));");

                String result="";
                for (int i = 4; i <message.length ; i++) {
                    result+=message[i];
                }


                //같으면 바로 보이게 하기...
                if(clickRm.equals(String.valueOf(room_id))){

                    Log.i("room같아",clickRm);

                    Log.i("room같아2",String.valueOf(room_id));


                    if(loginTrue.equals(id)){ //자기자신이니까 -> 오른쪽으로

                        Log.i("result",result);
                        if(result.contains(".jpg")){
                            Log.i("result",".jpg");

                            String where="http://??/talkimage/"+result;


                            Glide.with(getApplicationContext())
                                    .load(where).override(500,500)
                                    .into(imageView);
                            imageView.setTranslationX(270);
                            imageView.setPadding(0,10,0,0);
                            MessageActivity.container.addView(imageView);
//                            imageView.setImageURI(uri);
//                            imageView.setMaxWidth(60);
//                            imageView.setMaxHeight(60);
//                            imageView.setRight(100);
//                            MessageActivity.container.addView(imageView);

                        }else{
                            tv.setText(result);
                            tv.setTextSize(20);
                            tv.setTextColor(Color.BLUE);
                            tv.setGravity(Gravity.RIGHT);
                            MessageActivity.container.addView(tv);
                        }


                    }else{

                        if(result.contains(".jpg")){
                            final TextView tv2=new TextView(getApplicationContext());
                            tv2.setTextSize(15);
                            tv2.setText(nick);
                            tv2.setGravity(Gravity.LEFT);
                            MessageActivity.container.addView(tv2);
                            String where="http://??/talkimage/"+result;



                            Glide.with(getApplicationContext())
                                    .load(where).override(500,500)
                                    .into(imageView);
                            imageView.setTranslationX(-270);
                            MessageActivity.container.addView(imageView);

                        }else{
                            final TextView tv2=new TextView(getApplicationContext());
                            tv.setTextSize(20);
                            tv2.setTextSize(15);
                            tv2.setText(nick);
                            tv.setText(result);
                            tv.setTextColor(Color.GREEN);
                            tv.setGravity(Gravity.LEFT);
                            tv2.setGravity(Gravity.LEFT);
                            MessageActivity.container.addView(tv2);
                            MessageActivity.container.addView(tv);
                        }

                    }


                    MessageActivity.scroll.fullScroll(View.FOCUS_DOWN);
                }else{
                    Log.i("room안같아",clickRm);

                    Log.i("room안같아2",String.valueOf(room_id));

                    //지금 현재 보고있는 창과 메세지가 온 곳의 룸의 번호가 다르면... 그 때는 그 알람을 울려주면 좋아..
                    //우선 위의 거는.. 잘 못 찾겠으니까..

        }

                final String room_id_hey=String.valueOf(room_id);
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd/HH/mm/ss");
                Date date = new Date();
                sampleDB.execSQL("INSERT INTO '" + room_id
                        + "' (nickname, thattime, id, content)  Values ('" + nick + "','"
                        + dateFormat.format(date) + "','"+id+"',"+
                        "'"+result+"');");

               // sampleDB.close();

            UploadService service = MyRetrofit2.getRetrofit2().create(UploadService.class);

            RequestBody my_id= RequestBody.create(
                    MediaType.parse("multipart/form-data"), loginTrue);

            Call<ConversationList> call1 = service.conversationRoomList(my_id);

            call1.enqueue(new retrofit2.Callback<ConversationList>() {
                @Override
                public void onResponse(Call<ConversationList> call, Response<ConversationList> response) {
                    if(conversationList1slist!=null){
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

//                            if(String.valueOf(response.body().result[i].getC_roomNum()).equals(room_id_hey)){
//                                conversationList1slist.add(0,new ConversationList1(response.body().result[i].getId(),
//                                        response.body().result[i].getNickname(),response.body().result[i].getC_roomNum(),
//                                        response.body().result[i].getC_img_uri(),content_read));
//                            }
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


            SQLiteDatabase ReadDB = getApplication().openOrCreateDatabase("conversation", MODE_PRIVATE, null);

                //SELECT문을 사용하여 테이블에 있는 데이터를 가져옵니다..
                Cursor c = ReadDB.rawQuery("SELECT * FROM '" + room_id +"'", null);

                if (c != null) {


                    if (c.moveToFirst()) {
                        int c_seq;
                        String nick_read;
                        String id_read;
                        String content_read;
                        String date_read;
                        do {

                            //테이블에서 두개의 컬럼값을 가져와서
                            c_seq=c.getInt(c.getColumnIndex("c_seq"));
                            nick_read=c.getString(c.getColumnIndex("nickname"));
                            id_read = c.getString(c.getColumnIndex("id"));
                            content_read = c.getString(c.getColumnIndex("content"));
                            date_read= c.getString(c.getColumnIndex("thattime"));

                            Log.i("result",id_read+" : "+content_read+" : "+date_read);


//                            //HashMap에 넣습니다.
//                            HashMap<String,String> persons = new HashMap<String,String>();
//
//                            persons.put(TAG_NAME,Name);
//                            persons.put(TAG_PHONE,Phone);
//
//                            //ArrayList에 추가합니다..
//                            personList.add(persons);
//
//                            list.add(new ConversationList1(id_read,nick_read,String.valueOf(room_id),
//                                    content_read,date_read));


                        } while (c.moveToNext());

                    }
                }

                ReadDB.close();







        }
    };




    @Override
    public void onDestroy() {
        super.onDestroy();
        sampleDB.close();
        Log.i("servicetest","destroy");
    }
}
