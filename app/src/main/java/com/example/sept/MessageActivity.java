package com.example.sept;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;


import com.bumptech.glide.Glide;
import com.example.sept.Interface.UploadService;
import com.example.sept.Retrofit.MyRetrofit2;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;


import gun0912.tedbottompicker.TedBottomPicker;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.example.sept.Service.ChatService.member_socket_list;

public class MessageActivity extends AppCompatActivity {
    // 서버 접속 여부를 판별하기 위한 변수

    boolean isConnect;
    EditText edit1;
    Button btn1;
    Button button_pic;

    public static LinearLayout container = null;
    public static ScrollView scroll = null;
    ProgressDialog pro;
    // 어플 종료시 스레드 중지를 위해...
    boolean isRunning = false;

    // 사용자 닉네임( 내 닉넴과 일치하면 내가보낸 말풍선으로 설정 아니면 반대설정)
    String user_nickname;

    SharedPreferences login_true;
    String loginTrue;

    public static String clickRm = "";
    public static String clickPosition;
    String clickId;
    String clickImg;
    String clickNick;
    //임시로 대화내용 담을곳
    Context context;

    TedBottomPicker tedBottomPicker;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.i("message activity", "create");
        setContentView(R.layout.message);
        edit1 = findViewById(R.id.editText);
        button_pic = findViewById(R.id.button_pic);
        btn1 = findViewById(R.id.button);
        container = findViewById(R.id.container);
        scroll = findViewById(R.id.scroll);
        login_true = getSharedPreferences("login_true", Activity.MODE_PRIVATE);

        loginTrue = login_true.getString("login_email", "no");
        tedPermission();

        context = getApplicationContext();

        Intent intent = getIntent();

        if (intent != null) {
            clickRm = intent.getExtras().getString("clickRm");
            clickPosition = intent.getExtras().getString("clickPosition");
            clickId = intent.getExtras().getString("clickId");
            clickImg = intent.getExtras().getString("clickImg");
            clickNick = intent.getExtras().getString("clickNick");

        }


        //이미 생성되었던 방이라면 db에 저장되어 있는 값이 있을 것이다...
        SQLiteDatabase ReadDB = getApplication().openOrCreateDatabase("conversation", MODE_PRIVATE, null);
//테이블이 존재하지 않으면 새로 생성합니다.
        ReadDB.execSQL("CREATE TABLE IF NOT EXISTS '" + clickRm
                + "' (c_seq integer primary key autoincrement, nickname VARCHAR(250)," +
                "thattime date(250),id VARCHAR(250), content VARCHAR(250));");
        //SELECT문을 사용하여 테이블에 있는 데이터를 가져옵니다..
        Cursor c = ReadDB.rawQuery("SELECT * FROM '" + clickRm + "'", null);

        if (c != null) {


            if (c.moveToFirst()) {
                do {

                    //테이블에서 두개의 컬럼값을 가져와서
                    int c_seq = c.getInt(c.getColumnIndex("c_seq"));
                    String nick_read = c.getString(c.getColumnIndex("nickname"));
                    String id_read = c.getString(c.getColumnIndex("id"));
                    String content_read = c.getString(c.getColumnIndex("content"));
                    String date_read = c.getString(c.getColumnIndex("thattime"));

                    Log.i("result", id_read + " : " + content_read + " : " + date_read);


                    final TextView tv = new TextView(getApplicationContext());
                    tv.setPadding(0,0,0,10);

                    final ImageView imageView=new ImageView(getApplicationContext());

                    if (loginTrue.equals(id_read)) { //자기자신이니까 -> 오른쪽으로
                        if(content_read.contains("IMG")&&content_read.contains(".jpg")){
                            String where="http://??/talkimage/"+content_read;


                            Glide.with(getApplicationContext())
                                    .load(where).override(500,500)
                                    .into(imageView);
                            imageView.setTranslationX(270);
                            imageView.setPadding(0,10,0,0);
                            MessageActivity.container.addView(imageView);
                        }else{
                            tv.setText(content_read);
                            tv.setTextSize(20);
                            tv.setTextColor(Color.BLUE);
                            tv.setGravity(Gravity.RIGHT);
                            container.addView(tv);
                        }

                    } else {
                        if(content_read.contains("IMG")&&content_read.contains(".jpg")){
                            final TextView tv2=new TextView(getApplicationContext());
                            tv2.setTextSize(15);
                            tv2.setText(nick_read);
                            tv2.setGravity(Gravity.LEFT);
                            MessageActivity.container.addView(tv2);
                            String where="http://??/talkimage/"+content_read;



                            Glide.with(getApplicationContext())
                                    .load(where).override(500,500)
                                    .into(imageView);
                            imageView.setTranslationX(-270);
                            MessageActivity.container.addView(imageView);
                        }else{
                            final TextView tv2 = new TextView(getApplicationContext());
                            tv.setTextSize(20);
                            tv2.setTextSize(15);
                            tv2.setText(nick_read);
                            tv.setText(content_read);
                            tv.setTextColor(Color.GREEN);
                            tv.setGravity(Gravity.LEFT);
                            tv2.setGravity(Gravity.LEFT);
                            container.addView(tv2);
                            container.addView(tv);
                        }

                    }


                    scroll.fullScroll(View.FOCUS_DOWN);

                } while (c.moveToNext());
            }
        }

        ReadDB.close();




    }





    private void tedPermission() {

        PermissionListener permissionListener = new PermissionListener() {
            @Override
            public void onPermissionGranted() {
                // 권한 요청 성공
                //   Toast.makeText(PersonalPicActivity.this,"승인 허가 완료",Toast.LENGTH_LONG).show();
            }

            @Override
            public void onPermissionDenied(ArrayList<String> deniedPermissions) {
                // 권한 요청 실패
                Toast.makeText(getApplicationContext(), "아직 승인받지 않았습니다.", Toast.LENGTH_LONG).show();
            }
        };

//        ActivityCompat.requestPermissions(this,
//                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},1);

        TedPermission.with(getApplicationContext())
                .setPermissionListener(permissionListener)
                .setRationaleMessage(getResources().getString(R.string.permission_2))
                .setDeniedMessage(getResources().getString(R.string.permission_1))
                .setPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA)
                .check();

    }


    // 버튼과 연결된 메소드
    public void btnMethod(View v) {
        // 접속 후
        // 입력한 문자열을 가져온다.
        String msg = edit1.getText().toString();
        // 송신 스레드 가동


      // Log.i("어떤소켓일까?",String.valueOf(member_socket_list.get(Integer.parseInt(clickPosition))));
        SendToServerThread thread = new SendToServerThread(member_socket_list.get(Integer.parseInt(clickPosition)), msg);
        thread.start();

    }




    // 버튼과 연결된 메소드 1 -> 이미지 보내기
    public void btnMethod1(View v) {

        tedBottomPicker.show(getSupportFragmentManager());

    }




    // 이미지 데이터를 전송하는 메소드
    class SendPicture extends Thread{
        // 소켓이 존재하는 경우
        FileInputStream input_stream;
        DataOutputStream dataOutputStream;
        Socket socket;
       // Uri uri;
        File file;


        private boolean stop; // stop 플래그



        public SendPicture(Socket socket, File file) throws IOException {
            try {
                this.socket = socket;
                this.file = file;
                OutputStream out=socket.getOutputStream();
                dataOutputStream=new DataOutputStream(out);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }


        @Override
        public void run() {
            try {


                   // String imagePath = uri.getPath();
                   // File file=new File(imagePath);

                    input_stream = new FileInputStream(file.getPath());
              //      byte[] buffer = new byte[4096];

//      //바이트단위로 임시저장하는 버퍼를 생성합니다.
//                    int len;                               //전송할 데이터의 길이를 측정하는 변수입니다.
//                    int data=0;                            //전송횟수, 용량을 측정하는 변수입니다.
//
//                    while((len = input_stream.read(buffer))>0){     //FileInputStream을 통해 파일에서 입력받은 데이터를 버퍼에 임시저장하고 그 길이를 측정합니다.
//                        data++;                        //데이터의 양을 측정합니다.
//                    }
//
//                    int datas = data;                      //아래 for문을 통해 data가 0이되기때문에 임시저장한다.
//
//                    input_stream.close();

                    int data=0;
                    int totalSize = input_stream.available();
//                    dataOutputStream.writeUTF(clickNick + ":" + loginTrue +
//                            ":IMG:"+totalSize+":"+file.getPath());
                                  //데이터 전송횟수를 서버에 전송하고,
                                //파일의 이름을 서버에 전송합니다.
                    String a=clickNick + ":" + loginTrue +
                            ":IMG:"+totalSize+":"+file.getPath()+"\n";
                    byte[] buffera=a.getBytes();

                    dataOutputStream.write(buffera);

//                    int totalRead = 0;
//
//                    while ( (totalRead=input_stream.read(buffer)) > 0) {
//
//                        dataOutputStream.write(buffer, 0, totalRead);
//
//
//                        Log.i("haha",String.valueOf(buffer.length));
//                    }
//
//
//                    dataOutputStream.flush();




//                    len = 0;
//
//
//                    for(;data>0;data--){                   //데이터를 읽어올 횟수만큼 FileInputStream에서 파일의 내용을 읽어옵니다.
//                        len = input_stream.read(buffer);        //FileInputStream을 통해 파일에서 입력받은 데이터를 버퍼에 임시저장하고 그 길이를 측정합니다.
//                        dataOutputStream.write(buffer,0,len);       //서버에게 파일의 정보(1kbyte만큼보내고, 그 길이를 보냅니다.
//                    }








                    } catch(IOException e) {
                    }
                }




            }



    // 서버에 데이터를 전달하는 스레드
    class SendToServerThread extends Thread {
        Socket socket;
        String msg;
        OutputStream os;
        DataOutputStream dos;
        boolean stop;
        public SendToServerThread(Socket socket, String msg) {
            try {
                this.socket = socket;
                this.msg = msg;
                os = socket.getOutputStream();
                dos = new DataOutputStream(os);
                this.stop=false;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void run() {
            try {
                // 서버로 데이터를 보낸다.
//                dos.writeUTF(clickNick + ":" + loginTrue +
//                        ":TEXT:"+ msg);


                    String a=clickNick + ":" + loginTrue +
                            ":TEXT:"+ msg+"\n";
                    byte[] buffera=a.getBytes();

                    dos.write(buffera);
                    Log.i("응??",String.valueOf(buffera));

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            edit1.setText("");
                        }
                    });





            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }




    @Override
    protected void onStart() {
        super.onStart();
        tedBottomPicker = new TedBottomPicker.Builder(MessageActivity.this)
                .setImageProvider(new TedBottomPicker.ImageProvider() {
                    @Override
                    public void onProvideImage(ImageView imageView, Uri imageUri) {

                        Glide.with(getApplicationContext()).load(imageUri).into(imageView);
                        Log.d("Log", "Uri Log : " + imageUri.toString());

                    }
                })
                .setOnMultiImageSelectedListener(new TedBottomPicker.OnMultiImageSelectedListener() {
                    @Override
                    public void onImagesSelected(final ArrayList<Uri> uriList) {

                        if(uriList.size()!=0){
                            for (int i = 0; i <uriList.size() ; i++) {


                                final File file= new File(uriList.get(i).getPath());

                                try {
                                    Log.i("urilist?"+i,file.getAbsolutePath());

                                    String result="/sdcard/DCIM/Camera/"+file.getName();

                                    Log.i("urilist result?"+i,result);

                                    UploadService service = MyRetrofit2.getRetrofit2().create(UploadService.class);


                                    RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), file);
                                    MultipartBody.Part body1=MultipartBody.Part.createFormData("image", result, requestFile);


                                    SharedPreferences login_true=getSharedPreferences("login_true", Activity.MODE_PRIVATE);
                                    // 로그인 이메일 정보를 가지고 옴.
                                    String loginEmail = login_true.getString("login_email", "no");

                                    RequestBody description = RequestBody.create(
                                            MediaType.parse("multipart/form-data"), loginEmail);


                                    Call<ResponseBody> call1 = service.uploadChat(description, body1);

                                    call1.enqueue(new Callback<ResponseBody>() {
                                        @Override
                                        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                                            Log.i("success",response.toString());


                                            SendPicture sendPicture= null;
                                            try {
                                                sendPicture = new SendPicture(member_socket_list.get(Integer.parseInt(clickPosition)),file);
                                            } catch (IOException e) {
                                                e.printStackTrace();
                                            }
                                            sendPicture.start();
                                        }



                                        @Override
                                        public void onFailure(Call<ResponseBody> call, Throwable t) {
                                            Log.i("fail",t.toString());
                                        }
                                    });






                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }




                    }
                }).setPreviewMaxCount(50)
                .setCompleteButtonText("이미지선택완료")
                .setEmptySelectionText("이미지")
                .showCameraTile(false)
                .create();


    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        //지금은 편의상 이렇게 해 둔 것이지만 실제로는 destroy되어도 계속 메세지는 주고 받아져야 하며
        //대화방에서 나간다면.. 그 때는 service가 멈춰야 하는 것이 맞다.
        //  stopService(new Intent(getApplicationContext(),TestService.class));

    }


}