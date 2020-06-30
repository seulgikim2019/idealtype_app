package com.example.sept.Service;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.sept.Class.CustomContent;
import com.example.sept.ConversationListActivity;
import com.example.sept.ConverseComeListActivity;
import com.example.sept.Interface.UploadService;
import com.example.sept.R;
import com.example.sept.Retrofit.MyRetrofit2;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TestService extends Service {

    ServiceThread thread;
    String cus_contents_nick="";
    private boolean isStop;

    @Override
    public IBinder onBind(Intent intent) {
        //데이터 전달과 관련된 부분....

        return null;
    }


    static NotificationManager notificationManager;
    static NotificationChannel notificationChannel;
    @Override
    public void onCreate() {
        super.onCreate();
        Log.i("service","service");
        myServiceHandler handler = new myServiceHandler();
        thread = new ServiceThread(handler);
        createNotificationChannel();


    }

    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            notificationChannel = new NotificationChannel("5032", "5032",
                    NotificationManager.IMPORTANCE_DEFAULT);
            notificationChannel.setDescription("channel description");
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.RED);
            notificationChannel.enableVibration(true);
            notificationChannel.setVibrationPattern(new long[]{100, 200, 100, 200});
            notificationChannel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
            notificationManager.createNotificationChannel(notificationChannel);

            Notification notification = new NotificationCompat.Builder(this, "5032")
                    .build();

            startForeground(1, notification);

        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i("서비스실행때마다","start");

        myServiceHandler handler = new myServiceHandler();
        thread = new ServiceThread(handler);
        thread.start();

        return super.onStartCommand(intent, flags, startId);
    }

    class myServiceHandler extends Handler {
        @Override
        public void handleMessage(android.os.Message msg) {


            //로그아웃 부분 잡기위해서.. 생각을 해봐야함..
            Intent intent;


            PendingIntent pendingIntent;

            NotificationCompat.Builder builder=null;

            switch (msg.what){
                case 0: //메세지 요청이 들어왔을 때
                    intent= new Intent(TestService.this, ConverseComeListActivity.class);
//
//                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
//                            | Intent.FLAG_ACTIVITY_CLEAR_TOP
//                            | Intent.FLAG_ACTIVITY_SINGLE_TOP);


                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|
                            Intent.FLAG_ACTIVITY_SINGLE_TOP|
                            Intent.FLAG_ACTIVITY_CLEAR_TOP);


                    pendingIntent = PendingIntent.getActivity(TestService.this, 0, intent,PendingIntent.FLAG_ONE_SHOT);


                    builder = new NotificationCompat.Builder(TestService.this, "5032")
                        .setSmallIcon(R.drawable.love)
                        .setContentTitle("[이상형을 부탁해]")
                        .setContentText(
                                "["+cus_contents_nick+"]님의 대화요청을 수락해주세요")
                        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                        // Set the intent that will fire when the user taps the notification
                        .setContentIntent(pendingIntent)
                        .setAutoCancel(true);
                    break;

                case 1:  //메세지 요청이 거절당했을 때
                    intent= new Intent(TestService.this, ConverseComeListActivity.class);

                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                            | Intent.FLAG_ACTIVITY_CLEAR_TOP
                            | Intent.FLAG_ACTIVITY_SINGLE_TOP);

                    pendingIntent = PendingIntent.getActivity(TestService.this, 0, intent,PendingIntent.FLAG_ONE_SHOT);

                    builder = new NotificationCompat.Builder(TestService.this, "5032")
                            .setSmallIcon(R.drawable.love)
                            .setContentTitle("[이상형을 부탁해]")
                            .setContentText("["+cus_contents_nick+"]님에게 대화요청을 거절당했습니다")
                            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                            // Set the intent that will fire when the user taps the notification
                            .setContentIntent(pendingIntent)
                            .setAutoCancel(true);
                    break;

                case 2:  //메세지 요청 수락되었을때



                    intent= new Intent(TestService.this, ConversationListActivity.class);

                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                            | Intent.FLAG_ACTIVITY_CLEAR_TOP
                            | Intent.FLAG_ACTIVITY_SINGLE_TOP);

                    pendingIntent = PendingIntent.getActivity(TestService.this, 0, intent,PendingIntent.FLAG_ONE_SHOT);

                    builder = new NotificationCompat.Builder(TestService.this, "5032")
                            .setSmallIcon(R.drawable.love)
                            .setContentTitle("[이상형을 부탁해]")
                            .setContentText("["+cus_contents_nick+"]님과의 대화방이 만들어졌습니다.")
                            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                            // Set the intent that will fire when the user taps the notification
                            .setContentIntent(pendingIntent)
                            .setAutoCancel(true);
                    break;
            }



            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(TestService.this);

// notificationId is a unique int for each notification that you must define
            notificationManager.notify(0, builder.build());
            notificationManager.cancel(1);
        }
    };



    @Override
    public void onDestroy() {
        super.onDestroy();
        thread.stopForever();
        thread = null;
        Log.i("service","bye");
    }

    public class ServiceThread extends Thread{
        Handler handler;
        boolean isRun = true;

        public ServiceThread(Handler handler){
            this.handler = handler;
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
                SharedPreferences login_true=getSharedPreferences("login_true", Activity.MODE_PRIVATE);
                // 로그인 이메일 정보를 가지고 옴.
                String loginTrue = login_true.getString("login_email","no");
                RequestBody my_id= RequestBody.create(
                        MediaType.parse("multipart/form-data"), loginTrue);
                Call<CustomContent> call1 = service.notificationService(my_id);

                call1.enqueue(new Callback<CustomContent>() {
                    @Override
                    public void onResponse(Call<CustomContent> call, Response<CustomContent> response) {
                        //Log.i("service background",response.body().result[0].getCus_contents_email());


                        if(response.body().result.length==0){
                            Log.i("service background","0");

                        }else{
                            Log.i("service background",response.body().result[0].getCus_contents_email());


                            for (int i = 0; i <response.body().result.length ; i++) {

                                if(response.body().result[i].getResult().equals("come")){//메세지요청이왔을때
                                    cus_contents_nick=response.body().result[i].getCus_contents_nickname();
                                    handler.sendEmptyMessage(0);//쓰레드에 있는 핸들러에게 메세지를 보냄
                                }else if(response.body().result[i].getResult().equals("no")){//메세지거절당했을때
                                    cus_contents_nick=response.body().result[i].getCus_contents_nickname();
                                    handler.sendEmptyMessage(1);//쓰레드에 있는 핸들러에게 메세지를 보냄
                                }else if(response.body().result[i].getResult().equals("room")){//메세지요청이 수락되었을 때
                                    cus_contents_nick=response.body().result[i].getCus_contents_nickname();
                                    handler.sendEmptyMessage(2);//쓰레드에 있는 핸들러에게 메세지를 보냄
                                }

                            }




                        }




                    }

                    @Override
                    public void onFailure(Call<CustomContent> call, Throwable t) {
                        Log.i("CustomContent fail",t.toString());
                    }
                });



                try{
                    Thread.sleep(10000); //10초씩 쉰다.

                    notificationManager.cancel(0);
                    Thread.sleep(50000); //50초씩 쉰다.
                }catch (Exception e) {
                }
            }
            }
        }







}
