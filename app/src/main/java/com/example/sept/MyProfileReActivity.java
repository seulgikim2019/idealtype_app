package com.example.sept;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import com.bumptech.glide.Glide;
import com.example.sept.Class.NaverRepo;
import com.example.sept.Class.PersonalProfileRe;
import com.example.sept.Interface.NaverApiInterface;
import com.example.sept.Interface.UploadService;
import com.example.sept.Retrofit.MyRetrofit2;
import com.example.sept.Retrofit.MyRetrofit22;
import com.example.sept.Retrofit.MyRetrofit2_1;
import com.soundcloud.android.crop.Crop;

import java.io.File;
import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

import static com.example.sept.MainActivity.c_name_final;

//마이페이지를 통해서 프로필 수정.
public class MyProfileReActivity extends AppCompatActivity {


    ImageView my_pic_re;
    Button my_upload_re;
    EditText c_nickname_re;
    Spinner c_region_re;
    Spinner c_job_re;
    RadioGroup c_smoke_re;
    EditText c_height_re;

    Uri imageCaptureUri;
    File filePath=null;



    ProgressDialog progressDialog;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.my_profile_re);

        setTitle("프로필편집");

        my_pic_re=findViewById(R.id.my_pic_re);
        my_upload_re=findViewById(R.id.my_upload_re);
        c_nickname_re=findViewById(R.id.c_nickname_re);
        c_region_re=findViewById(R.id.c_region_re);
        c_job_re=findViewById(R.id.c_job_re);
        c_smoke_re=findViewById(R.id.c_smoke_re);
        c_height_re=findViewById(R.id.c_height_re);
        progressDialog=new ProgressDialog(MyProfileReActivity.this);
    }

    @Override
    protected void onStart() {
        super.onStart();

        SharedPreferences login_true=getSharedPreferences("login_true", Activity.MODE_PRIVATE);
        // 로그인 이메일 정보를 가지고 옴.
        String loginEmail = login_true.getString("login_email", "no");
        //아이디값 넣어주기

        UploadService service = MyRetrofit2.getRetrofit2().create(UploadService.class);

        RequestBody description= RequestBody.create(
                MediaType.parse("multipart/form-data"), loginEmail);


        Call<PersonalProfileRe> call1 = service.profileRe(description);


        call1.enqueue(new Callback<PersonalProfileRe>() {
            @Override
            public void onResponse(Call<PersonalProfileRe> call, Response<PersonalProfileRe> response) {
                Log.i("myprofile success",response.toString());
                //파일이 업로드에 성공했다면,...기존에 저장해두었던 파일을 삭제하고 완료로 넘김.
                //즉  shared도 지우고 그리구 sdcards에 저장되어 있는 사진도 지워줍니다요!!!

//                Log.i("myprofile success",response.body().getUrl());
//
//

//            //경로만 가지고오면된다.
                Glide.with(MyProfileReActivity.this)
                        .load(response.body().getMy_pic_re()).into(my_pic_re);

                Log.i("getMypic",response.body().getMy_pic_re());

                c_nickname_re.setText(response.body().getC_nickname_re());
                c_region_re.setSelection(response.body().getC_region_re());
                c_job_re.setSelection(response.body().getC_job_re());
                RadioButton c_smoke_check_re=findViewById(R.id.c_smoke_ok_re);
                if(response.body().getC_smoke_re().equals("무")){
                    c_smoke_check_re=findViewById(R.id.c_smoke_no_re);
                }
                c_smoke_check_re.setChecked(true);

                c_height_re.setText(String.valueOf(response.body().getC_height_re()));

            }

            @Override
            public void onFailure(Call<PersonalProfileRe> call, Throwable t) {
                Log.i("myprofile fail",t.toString());
            }
        });


    }

    @Override
    protected void onResume() {
        super.onResume();

        //사진 등록하기 클릭
        my_upload_re.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i("click","img re_upload");
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
                new AlertDialog.Builder(MyProfileReActivity.this)
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


        //앨범.
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        if(requestCode == 0 && resultCode == RESULT_OK) {  //카메라 사진촬영 선택->정상적으로 사진이 찍혔을 때


            if(filePath != null) {

                //crop할 수 있도록 넘어가기
                cropImage(imageCaptureUri);


            }
        }else if(requestCode == Crop.REQUEST_CROP && resultCode == RESULT_OK) { //crop

            Log.i("여기까지는 잘 오는 거죠?","네@_@");



            String clientId= "x4HE1I9HGsC3KNfAz58w";
            String clientSecret = "GRqNXh0k8u";

            Retrofit client= MyRetrofit22.getRetrofit2();
            NaverApiInterface service=client.create(NaverApiInterface.class);
            final RequestBody requestBody=RequestBody.create(MediaType.parse("image/jpeg"),filePath);
            MultipartBody.Part body1=MultipartBody.Part.createFormData("image", filePath.getName(), requestBody);

            Call<NaverRepo> call1=service.naverRepo2(clientId,clientSecret,body1);

            call1.enqueue(new Callback<NaverRepo>() {
                @Override
                public void onResponse(Call<NaverRepo> call, Response<NaverRepo> response) {
                    Log.i("naver",response.toString());

                    if(response.isSuccessful()) {
                        Log.i("face", response.toString());

                        //얼굴이 제대로 인식 되었을 때가 중요한 것.


                        if (response.body().info.faceCount == 0) {
                            //얼굴 인식이 안된다는 것이므로 다른 사진을 요청하자!
                            AlertDialog.Builder alert = new AlertDialog.Builder(MyProfileReActivity.this);
                            //다이얼로그 창 띄어주기
                            alert.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();     //닫기
                                }
                            });

                            alert.setMessage("얼굴 인식이 잘 되지 않습니다.");
                            alert.show();
                        } else if (response.body().info.faceCount > 1) {
                            //사람 얼굴이 2명이상이라는 뜻이므로 한명의 사진만을 요청하자!
                            AlertDialog.Builder alert = new AlertDialog.Builder(MyProfileReActivity.this);
                            //다이얼로그 창 띄어주기
                            alert.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();     //닫기
                                }
                            });

                            alert.setMessage("여러명의 얼굴이 포함된 사진은 등록이 불가합니다.");
                            alert.show();
                        } else if (response.body().info.faceCount == 1) {

                            //얼굴 한명 인식 성공! -> db에 올리기.....
                            //우선은 같은 아이디가 있으므로... sum 0으로 기존의 1을 바꾸고 신입을 1로 설정
                            //하지만 최종 선택 때, 수정 확인을 누르지 않으면.... 다시 이 부분을 수정해주어야 함!
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

                                    Call<ResponseBody> call2=uploadService.matchingChangeFace(id,path);

                                    progressDialog.setMessage("얼굴을 인식 중이니 잠시 기다려주세요.");
                                    progressDialog.show();
                                    call2.enqueue(new Callback<ResponseBody>() {
                                        @Override
                                        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                                            Log.i("success",response.toString());
                                            progressDialog.cancel();
                                            Intent intent = new Intent(MyProfileReActivity.this, MyProfileReActivity.class);
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




//
//
//            BitmapFactory.Options imgOptions = new BitmapFactory.Options();
//            imgOptions.inSampleSize = 1;
//            Bitmap bitmap = BitmapFactory.decodeFile(filePath.getAbsolutePath(), imgOptions);
//            my_pic_re.setImageBitmap(bitmap);
//


        }else if(requestCode == 1 && resultCode == RESULT_OK) {
            Log.i("이미지","앨범");
            Log.i("이미지",String.valueOf(data.getData()));
            Uri uri = data.getData();
            cropImage(uri);

        }

    }


    @Override
    protected void onPause() {
        super.onPause();
        Log.i("pasue_myprofilere","pause");
        SharedPreferences login_true=getSharedPreferences("login_true", Activity.MODE_PRIVATE);
        // 로그인 이메일 정보를 가지고 옴.
        String loginEmail = login_true.getString("login_email", "no");

        UploadService service = MyRetrofit2.getRetrofit2().create(UploadService.class);

        RequestBody description= RequestBody.create(
                MediaType.parse("multipart/form-data"), loginEmail);
        RequestBody description1= RequestBody.create(
                MediaType.parse("multipart/form-data"), c_nickname_re.getText().toString());

        c_name_final=c_nickname_re.getText().toString();

        RequestBody description2= RequestBody.create(
                MediaType.parse("multipart/form-data"), String.valueOf(c_region_re.getSelectedItemPosition()));
        RequestBody description3= RequestBody.create(
                MediaType.parse("multipart/form-data"), String.valueOf(c_job_re.getSelectedItemPosition()));

        RadioButton c_smoke_re_choice=findViewById(c_smoke_re.getCheckedRadioButtonId());


        Log.i("pause",c_nickname_re.getText().toString()+"/"+
                String.valueOf(c_region_re.getSelectedItemPosition())+"/"+String.valueOf(c_job_re.getSelectedItemPosition())
        +"/"+c_smoke_re.getCheckedRadioButtonId()+"/"+c_smoke_re_choice.getText().toString());

        RequestBody description4= RequestBody.create(
                MediaType.parse("multipart/form-data"), c_smoke_re_choice.getText().toString());

        RequestBody description5= RequestBody.create(
                MediaType.parse("multipart/form-data"), c_height_re.getText().toString());

        Call<PersonalProfileRe> call1 = service.profileReText(description,description1,description2,
                description3,description4,description5);


        call1.enqueue(new Callback<PersonalProfileRe>() {
            @Override
            public void onResponse(Call<PersonalProfileRe> call, Response<PersonalProfileRe> response) {
                Log.i("myprofile success",response.toString());
                //db에만 잘 들어가면 됩니다. 이부분은..


            }

            @Override
            public void onFailure(Call<PersonalProfileRe> call, Throwable t) {
                Log.i("myprofile fail",t.toString());
            }
        });



    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.i("stop_myprofilere","stop");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
