package com.example.sept;


import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import com.example.sept.Class.NaverRepo;
import com.example.sept.Interface.NaverApiInterface;
import com.example.sept.Interface.UploadService;
import com.example.sept.Retrofit.MyRetrofit2;
import com.example.sept.Retrofit.MyRetrofit22;
import com.example.sept.Retrofit.MyRetrofit2_1;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;
import com.soundcloud.android.crop.Crop;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

//개인정보->개인사진을 입력받을 것임. 초기 입력창임.
public class PersonalPicActivity extends AppCompatActivity {


    Uri imageCaptureUri;
    File filePath=null;
    Button personal_before;
    Button personal_next;
    Button pic_upload;
    ImageView personal_pic;

    ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.personal_pic);

        setTitle("프로필사진");


        personal_before=findViewById(R.id.personal_before);
        personal_next=findViewById(R.id.personal_next);
        pic_upload=findViewById(R.id.pic_upload);
        personal_pic=findViewById(R.id.personal_pic);
        tedPermission();
        progressDialog = new ProgressDialog(PersonalPicActivity.this);


        //이전페이지를 갔다가 다시 돌아올때, shared에 저장된 file경로를 가지고 이미지를 보여줌.
        SharedPreferences personal_pic_save=getSharedPreferences("personal_pic_save", Activity.MODE_PRIVATE);
        if(personal_pic_save!=null){
            String personal_pic_save1=personal_pic_save.getString("personal_pic_save","@drawable/profile");

            if(personal_pic_save1.equals("@drawable/profile")){
                personal_pic.setImageResource(R.drawable.profile);
            }else{
                filePath= new File(personal_pic_save1);

                if(!filePath.exists()){
                    personal_pic.setImageResource(R.drawable.profile);
                }else{
                    BitmapFactory.Options imgOptions = new BitmapFactory.Options();
                    imgOptions.inSampleSize = 1;
                    Bitmap bitmap = BitmapFactory.decodeFile(filePath.getAbsolutePath(), imgOptions);
                    personal_pic.setImageBitmap(bitmap);

                }
                 }
        }


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
                Toast.makeText(PersonalPicActivity.this,"아직 승인받지 않았습니다.",Toast.LENGTH_LONG).show();
            }
        };

//        ActivityCompat.requestPermissions(this,
//                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},1);

        TedPermission.with(this)
                .setPermissionListener(permissionListener)
                .setRationaleMessage(getResources().getString(R.string.permission_2))
                .setDeniedMessage(getResources().getString(R.string.permission_1))
                .setPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.CAMERA)
                .check();

    }


    @Override
    protected void onStart() {
        super.onStart();


    }



    @Override
    protected void onResume() {
        super.onResume();
        Log.i("personalpic","resume");



        personal_before.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i("personalpic:before","이전페이지이동");
                //사진을 올려놨다면 shared에 저장시키자.
                //그리고 완료 버튼 눌렀을 때 이 shared는 삭제하고, saveinstance로 저장시켜 놓기.

                if(filePath!=null){ //등록된 사진이 있다는 것을 의미하므로 이 때는 shared에 저장시켜 놓기.
                    Log.i("이전버튼:사진이 있다?",String.valueOf(filePath));
                    SharedPreferences personal_pic_save=getSharedPreferences("personal_pic_save", Activity.MODE_PRIVATE);
                    SharedPreferences.Editor editor=personal_pic_save.edit();
                    editor.putString("personal_pic_save",String.valueOf(filePath));
                    editor.commit();
                }
                finish();
            }
        });

        personal_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i("personalpic:next", "완료");
                //이때 사진이 정확하게 업로드 되어있는지의 체크가 요구됨.


                if (filePath != null) {

                    //네이버 오픈 api 아이디와 비밀번호
                    String clientId= "naver api id";
                    String clientSecret = "naver api password";

                    Retrofit client= MyRetrofit22.getRetrofit2();
                    NaverApiInterface service=client.create(NaverApiInterface.class);
                    final RequestBody requestBody=RequestBody.create(MediaType.parse("image/jpeg"),filePath);
                    MultipartBody.Part body1=MultipartBody.Part.createFormData("image", filePath.getName(), requestBody);

                    Call<NaverRepo> call1=service.naverRepo2(clientId,clientSecret,body1);

                    call1.enqueue(new Callback<NaverRepo>() {
                        @Override
                        public void onResponse(Call<NaverRepo> call, Response<NaverRepo> response) {
                            Log.i("naver",response.toString());

                            if(response.isSuccessful()){
                                Log.i("face",response.toString());

                                //얼굴이 제대로 인식 되었을 때가 중요한 것.


                                if(response.body().info.faceCount==0){
                                    //얼굴 인식이 안된다는 것이므로 다른 사진을 요청하자!
                                    AlertDialog.Builder alert = new AlertDialog.Builder(PersonalPicActivity.this);
                                    //다이얼로그 창 띄어주기
                                    alert.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();     //닫기
                                        }
                                    });

                                    alert.setMessage("얼굴 인식이 잘 되지 않습니다.");
                                    alert.show();
                                }else if(response.body().info.faceCount>1){
                                    //사람 얼굴이 2명이상이라는 뜻이므로 한명의 사진만을 요청하자!
                                    AlertDialog.Builder alert = new AlertDialog.Builder(PersonalPicActivity.this);
                                    //다이얼로그 창 띄어주기
                                    alert.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();     //닫기
                                        }
                                    });

                                    alert.setMessage("여러명의 얼굴이 포함된 사진은 등록이 불가합니다.");
                                    alert.show();
                                }else if(response.body().info.faceCount==1){
                                    //인식이 된 것임!! 앗싸!!! 그럼 서버에 파일을 업로드 시키자!
                                    //이미지를 업로드시키는 것.


                                    Toast.makeText(getApplicationContext(),"얼굴이 제대로 인식되었습니다.",Toast.LENGTH_SHORT).show();

                                    UploadService service = MyRetrofit2.getRetrofit2().create(UploadService.class);


                    RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), filePath);
                    MultipartBody.Part body1=MultipartBody.Part.createFormData("image", filePath.getName(), requestFile);


                    Log.i("part",filePath.getName());

                    Log.i("part1",requestFile.toString());

                    Log.i("part2",body1.toString());


                          final SharedPreferences login_true=getSharedPreferences("login_true", Activity.MODE_PRIVATE);
                                    // 로그인 이메일 정보를 가지고 옴.
                           String loginEmail = login_true.getString("login_email", "no");

                    RequestBody description = RequestBody.create(
                            MediaType.parse("multipart/form-data"), loginEmail);


                    Log.i("part3",description.toString());

                    Call<ResponseBody> call1 = service.uploadFile(description, body1);


                    Log.i("???",call.request().url().toString()); //todo 디버깅용

                    call1.enqueue(new Callback<ResponseBody>() {
                        @Override
                        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                            Log.i("success",response.toString());
                            //파일이 업로드에 성공했다면,...기존에 저장해두었던 파일을 삭제하고 완료로 넘김.
                            //즉  shared도 지우고 그리구 sdcards에 저장되어 있는 사진도 지워줍니다요!!!


                            SharedPreferences personal_pic_save=getSharedPreferences("personal_pic_save", Activity.MODE_PRIVATE);
                             String personal_pic_save1=personal_pic_save.getString("personal_pic_save","");
                               File b_filePath= new File(personal_pic_save1);
                               //파일먼저지우기
                               boolean deleted=b_filePath.delete();
                               //shared지우기
                               SharedPreferences.Editor editor=personal_pic_save.edit();
                                editor.clear();
                                editor.commit();


                                //그리고 나서 메인페이지로 넘어갑니다요!
                                Log.i("메인으로 그다음에 가야지","응!");



                                //여기서 추가 로직!
                                //이 사진을 가지고 이상형들의 사진과 매칭도를 db에 저장해서 넣어놓기!

                                //경로 추출 가능... tmp_name으로 만들기 때문에
                                 String filePathGuess="/var/www/html/uploads/"+filePath.getName();

                            SharedPreferences login_true=getSharedPreferences("login_true", Activity.MODE_PRIVATE);
                            // 로그인 이메일 정보를 가지고 옴.
                            String loginEmail = login_true.getString("login_email", "no");

                                 UploadService uploadService= MyRetrofit2_1.getRetrofit2().create(UploadService.class);
                                 RequestBody id=RequestBody.create(MediaType.parse("multipart/form-data"),loginEmail);
                                 RequestBody path=RequestBody.create(MediaType.parse("multipart/form-data"),filePathGuess);

                                 Call<ResponseBody> call2=uploadService.matchingFace(id,path);

                                progressDialog.setMessage("얼굴을 인식 중이니 잠시 기다려주세요.");
                                progressDialog.show();
                                 call2.enqueue(new Callback<ResponseBody>() {
                                     @Override
                                     public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                                         Log.i("success",response.toString());
                                         progressDialog.cancel();
                                         Intent intent=new Intent(PersonalPicActivity.this,MainActivity.class);
                                         startActivity(intent);
                                         finish();
                                     }

                                     @Override
                                     public void onFailure(Call<ResponseBody> call, Throwable t) {
                                         Log.i("fail","fail");
                                     }
                                 });







                        }

                        @Override
                        public void onFailure(Call<ResponseBody> call, Throwable t) {
                            Log.i("fail",t.toString());
                        }
                    });

                                }

                            }else{
                                Log.i("face","false");
                            }

                        }
                        @Override
                        public void onFailure(Call<NaverRepo> call, Throwable t) {
                            Log.i("naver",t.toString());
                        }
                    });

                } else {
                    //사진이 없는 것이므로 다음페이지 못 넘어감.
                    AlertDialog.Builder alert = new AlertDialog.Builder(PersonalPicActivity.this);
                    //다이얼로그 창 띄어주기
                    alert.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();     //닫기
                        }
                    });

                    alert.setMessage("사진이 업로드 되어야 다음단계로 넘어갈 수 있습니다.");
                    alert.show();
                }
            }
        });

        pic_upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i("personalpic:upload","사진 올리자!");

                //다이얼로그 창 띄어주기


                DialogInterface.OnClickListener cameraListener=new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Log.i("personalpic:사진","사진촬영");
                        doTakePicture();
                        dialog.dismiss();     //닫기
                    }
                };

                DialogInterface.OnClickListener albumListener=new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Log.i("personalpic:사진","앨범선택");
                        doTakeAlbum();
                        dialog.dismiss();     //닫기
                    }
                };

                DialogInterface.OnClickListener cancelListener=new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Log.i("personalpic:사진","취소");
                        dialog.dismiss();     //닫기
                    }
                };

                //찍어서 올릴건지, 사진 앨범 중 올릴건지 check
                new AlertDialog.Builder(PersonalPicActivity.this)
                        .setTitle("사진 선택")
                        .setPositiveButton("사진촬영",cameraListener)
                        .setNeutralButton("취소",cancelListener)
                        .setNegativeButton("앨범선택",albumListener)
                        .show();




            }
        });

    }


    //카메라
    public void doTakePicture(){ //카메라 촬영 후 이미지 가져와야 하므로



        try {

            String dirPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/myApp";
            File dir = new File(dirPath);
            if(!dir.exists()) {

                dir.mkdir();
            }

            filePath = File.createTempFile("IMG", ".jpg", dir);
            if(!filePath.exists()) {
                filePath.createNewFile();
            }



            imageCaptureUri = FileProvider.getUriForFile(this, "com.example.sept", filePath);
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, imageCaptureUri);
            startActivityForResult(intent, 0);

        }catch (Exception e) {
            e.printStackTrace();
        }

    }


    //앨범
    public void doTakeAlbum(){



        try {
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*");
            startActivityForResult(intent.createChooser(intent, "사진을 선택하세요."), 1);


        }catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        if(requestCode == 0 && resultCode == RESULT_OK) {  //카메라 사진촬영 선택->정상적으로 사진이 찍혔을 때


            if(filePath != null) {

                //crop할 수 있도록 넘어가기
                cropImage(imageCaptureUri);


            }
        }else if(requestCode == Crop.REQUEST_CROP && resultCode == RESULT_OK) { //crop


            BitmapFactory.Options imgOptions = new BitmapFactory.Options();
            imgOptions.inSampleSize = 1;
            Bitmap bitmap = BitmapFactory.decodeFile(filePath.getAbsolutePath(), imgOptions);
            personal_pic.setImageBitmap(bitmap);


            SharedPreferences personal_pic_save=getSharedPreferences("personal_pic_save", Activity.MODE_PRIVATE);
            if(personal_pic_save!=null){
                String personal_pic_save1=personal_pic_save.getString("personal_pic_save","");

                File b_filePath= new File(personal_pic_save1);


                if(filePath!=null){
                    if(!String.valueOf(filePath).equals(String.valueOf(b_filePath))){
                        Log.i("이전에있었던사진경로",String.valueOf(b_filePath));
                        boolean deleted=b_filePath.delete();
                        Log.i("이전에있었던사진경로삭제결과",String.valueOf(deleted));
                    }
                }

            }



            if(filePath!=null){ //등록된 사진이 있다는 것을 의미하므로 이 때는 shared에 저장시켜 놓기.
                Log.i("지금 사진 경로 저장",String.valueOf(filePath));
                SharedPreferences.Editor editor=personal_pic_save.edit();
                editor.putString("personal_pic_save",String.valueOf(filePath));
                editor.commit();
            }



        }else if(requestCode == 1 && resultCode == RESULT_OK) {
            Log.i("이미지","앨범");
            Log.i("이미지",String.valueOf(data.getData()));
            Uri uri = data.getData();
            cropImage(uri);

        }

    }



    public void cropImage(Uri imageCaptureUri){
        Uri imageCaptureUri_crop;
        if(filePath != null) { //카메라로 온 경우

            imageCaptureUri_crop =
                    FileProvider.getUriForFile(this, "com.example.sept", filePath);
//
            Crop.of(imageCaptureUri, imageCaptureUri_crop).asSquare().start(this);


        }else{ //앨범에서 온 경우

            String dirPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/myApp";
            File dir = new File(dirPath);
            //File dir = new File("/sdcard/myApp");
            if(!dir.exists()) {

                dir.mkdir();
            }

            try {
                filePath = File.createTempFile("IMG", ".jpg", dir);
                if(!filePath.exists()) {
                    filePath.createNewFile();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }


            imageCaptureUri_crop =
                    FileProvider.getUriForFile(this, "com.example.sept", filePath);
            Crop.of(imageCaptureUri, imageCaptureUri_crop).asSquare().start(this);


        }

    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.i("personalpic","pause");

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);


    }
}
