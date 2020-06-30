package com.example.sept;


import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import com.bumptech.glide.Glide;
import com.crystal.crystalrangeseekbar.interfaces.OnRangeSeekbarChangeListener;
import com.crystal.crystalrangeseekbar.widgets.CrystalRangeSeekbar;
import com.example.sept.Class.MyWannabeSave;
import com.example.sept.Class.NaverRepo;
import com.example.sept.Interface.NaverApiInterface;
import com.example.sept.Interface.UploadService;
import com.example.sept.Retrofit.MyRetrofit2;
import com.example.sept.Retrofit.MyRetrofit22;
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

import static com.example.sept.CustomListActivity.custom_list_want;
import static com.example.sept.IdealListActivity.custom_list_want1;


//원하는 이상형 검색조건
public class MyWannabeSaveAllActivity extends AppCompatActivity {

    Uri imageCaptureUri;
    File filePath=null;
    //사진
    ImageView pic_wannabe;
    Button upload_wannabe;

    //성별
    RadioGroup gender_wannabe;
    String gender_text="all";

    //지역
    Spinner region_wannabe;

    //직업
    Spinner job_wannabe;

    //키
    CrystalRangeSeekbar range_seekbar;
    TextView height_wannabe_min;
    TextView height_wannabe_max;
    Number height_min;
    Number height_max;


    //나이
    CrystalRangeSeekbar age_wannabe;
    TextView age_wannabe_min;
    TextView age_wannabe_max;
    Number age_min;
    Number age_max;


    //흡연
    RadioGroup smoke_wannabe;
    String smoke_text="all";

    Button wannabe_cancel;
    Button wannabe_save;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.my_wannabe_save_all);
        tedPermission();
        range_seekbar=findViewById(R.id.height_wannabe);
        height_wannabe_min=findViewById(R.id.height_wannabe_min);
        height_wannabe_max=findViewById(R.id.height_wannabe_max);

        gender_wannabe=findViewById(R.id.gender_wannabe);

        smoke_wannabe=findViewById(R.id.smoke_wannabe);

        age_wannabe=findViewById(R.id.age_wannabe);
        age_wannabe_min=findViewById(R.id.age_wannabe_min);
        age_wannabe_max=findViewById(R.id.age_wannabe_max);

        pic_wannabe=findViewById(R.id.pic_wannabe);
        upload_wannabe=findViewById(R.id.upload_wannabe);

        region_wannabe=findViewById(R.id.region_wannabe);

        job_wannabe=findViewById(R.id.job_wannabe);

        wannabe_cancel=findViewById(R.id.wannabe_cancel);
        wannabe_save=findViewById(R.id.wannabe_save);


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
                Toast.makeText(MyWannabeSaveAllActivity.this,"아직 승인받지 않았습니다.",Toast.LENGTH_LONG).show();
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
        UploadService service = MyRetrofit2.getRetrofit2().create(UploadService.class);

        SharedPreferences login_true=getSharedPreferences("login_true", Activity.MODE_PRIVATE);
        // 로그인 이메일 정보를 가지고 옴.
        String loginEmail = login_true.getString("login_email", "no");

        RequestBody c_id_email = RequestBody.create(
                MediaType.parse("multipart/form-data"), loginEmail);

        Call<MyWannabeSave> call1 = service.wannabeSaveBefore(c_id_email);

        call1.enqueue(new Callback<MyWannabeSave>() {

            @Override
            public void onResponse(Call<MyWannabeSave> call, Response<MyWannabeSave> response) {
                Log.i("저장되어 있는 게 있으면","넵!");

                if(!response.body().getW_pic().equals("fail")){//저장되어 있는 값이 없으면 그냥 보여줘야 하므로

                    if(response.body().getW_pic().equals("null")){
                        Log.i("pic","null");
                    }else{
                        Glide.with(getApplicationContext()).load(response.body().getW_pic()).into(pic_wannabe);
                    }

                    RadioButton genderButton;
                    switch (response.body().getW_gender()){
                        case "남자":
                            genderButton=findViewById(R.id.gender_male_wannabe);
                            genderButton.setChecked(true);
                            break;
                        case "여자":
                            genderButton=findViewById(R.id.gender_female_wannabe);
                            genderButton.setChecked(true);
                            break;
                        case "all":
                            genderButton=findViewById(R.id.gender_anyway_wannabe);
                            genderButton.setChecked(true);
                            break;
                    }

                    RadioButton smokeButton;
                    switch (response.body().getW_smoke()){
                        case "유":
                            smokeButton=findViewById(R.id.smoke_ok_wannabe);
                            smokeButton.setChecked(true);
                            break;
                        case "무":
                            smokeButton=findViewById(R.id.smoke_no_wannabe);
                            smokeButton.setChecked(true);
                            break;
                        case "all":
                            smokeButton=findViewById(R.id.smoke_anyway_wannabe);
                            smokeButton.setChecked(true);
                            break;
                    }

                    region_wannabe.setSelection(response.body().getW_region()+1);
                    job_wannabe.setSelection(response.body().getW_job()+1);


                    age_wannabe.setMinStartValue(response.body().getW_age_min());
                    age_wannabe.setMaxStartValue(response.body().getW_age_max());
                    age_wannabe.apply();

                    age_min=response.body().getW_age_min();
                    age_max=response.body().getW_age_max();

                    range_seekbar.setMinStartValue(response.body().getW_height_min());
                    range_seekbar.setMaxStartValue(response.body().getW_height_max());
                    range_seekbar.apply();

                    height_min=response.body().getW_height_min();
                    height_max=response.body().getW_height_max();



                    Log.i("저장되어 있는 값이 있으므로",response.body().getW_height_min()+"");
                }else{
                    Log.i("저장되어 있는 값이 없으므로","초기화면을 띄어주면 됩니다.");
                }


            }

            @Override
            public void onFailure(Call<MyWannabeSave> call, Throwable t) {

            }
        });
        //취소 버튼 눌렀을 때
        wannabe_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i("워너비취소","취소하겠다고 합니다.");

                finish();
            }
        });

        gender_wannabe.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                Log.i("gender check","check");
                switch (i){
                    case R.id.gender_male_wannabe:
                        gender_text="남자";
                        break;

                    case R.id.gender_female_wannabe:
                        gender_text="여자";
                        break;

                    case R.id.gender_anyway_wannabe:
                        gender_text="all";
                        break;
                }
            }
        });

        smoke_wannabe.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                Log.i("smoke check","check");
                switch (i){
                    case R.id.smoke_ok_wannabe:
                        smoke_text="유";
                        break;

                    case R.id.smoke_no_wannabe:
                        smoke_text="무";
                        break;

                    case R.id.smoke_anyway_wannabe:
                        smoke_text="all";
                        break;
                }
            }
        });

        //검색 버튼 눌렀을 때
        wannabe_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i("워너비저장","검색조건을 입력했습니다.");

                //값이 있는 것을 저장하면 됨.. 값이 없다면 null 값으로 보내면 됨.

//
                if(filePath!=null){
                    //이상형 사진을 등록할 때
                    Log.i("file exist",filePath.getName());

                    UploadService service = MyRetrofit2.getRetrofit2().create(UploadService.class);


                    RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), filePath);
                    MultipartBody.Part body1=MultipartBody.Part.createFormData("image", filePath.getName(), requestFile);

                    SharedPreferences login_true=getSharedPreferences("login_true", Activity.MODE_PRIVATE);
                    // 로그인 이메일 정보를 가지고 옴.
                    String loginEmail = login_true.getString("login_email", "no");

                    RequestBody c_id_email = RequestBody.create(
                            MediaType.parse("multipart/form-data"), loginEmail);
                    RequestBody w_gender= RequestBody.create(
                            MediaType.parse("multipart/form-data"), gender_text);


                    //spinner 값 넣을 때 조심해야 할 것... 전체가 늘어났기 때문에.. -1로 처리해서 입력할 것.

                    RequestBody w_region= RequestBody.create(
                            MediaType.parse("multipart/form-data"), String.valueOf(region_wannabe.getSelectedItemPosition()-1));

                    RequestBody w_job= RequestBody.create(
                            MediaType.parse("multipart/form-data"), String.valueOf(job_wannabe.getSelectedItemPosition()-1));

                    RequestBody w_age_min= RequestBody.create(
                            MediaType.parse("multipart/form-data"), age_wannabe_min.getText().toString());

                    RequestBody w_age_max= RequestBody.create(
                            MediaType.parse("multipart/form-data"), age_wannabe_max.getText().toString());
                    RequestBody w_smoke= RequestBody.create(
                            MediaType.parse("multipart/form-data"), smoke_text);
                    RequestBody w_height_min= RequestBody.create(
                            MediaType.parse("multipart/form-data"), height_wannabe_min.getText().toString());
                    RequestBody w_height_max= RequestBody.create(
                            MediaType.parse("multipart/form-data"), height_wannabe_max.getText().toString());

                    Call<ResponseBody> call1 = service.wannabeSave(c_id_email,w_gender,w_region,w_job,w_age_min,w_age_max,w_smoke
                            ,w_height_min,w_height_max,body1);

                    call1.enqueue(new Callback<ResponseBody>() {
                        @Override
                        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                            Log.i("success",response.toString());
                            //파일이 업로드에 성공했다면,...기존에 저장해두었던 파일을 삭제하고 완료로 넘김.
                            //즉  shared도 지우고 그리구 sdcards에 저장되어 있는 사진도 지워줍니다요!!!


                            Intent get=getIntent();
                            if(get!=null){
                                custom_list_want1.setChecked(true);
                            }else{
                                 custom_list_want.setChecked(true);

                            }

                        SharedPreferences personal_pic_save=getSharedPreferences("personal_pic_save", Activity.MODE_PRIVATE);
                        String personal_pic_save1=personal_pic_save.getString("personal_pic_save","");
                        File b_filePath= new File(personal_pic_save1);
                        //파일먼저지우기
                        boolean deleted=b_filePath.delete();
                        //shared지우기
                        SharedPreferences.Editor editor=personal_pic_save.edit();
                        editor.clear();
                        editor.commit();

                        finish();


                        }

                        @Override
                        public void onFailure(Call<ResponseBody> call, Throwable t) {
                            Log.i("fail",t.toString());
                        }
                    });
                }else{

                    //이상형 사진을 등록하지 않을 때

                    Log.i("file no","no");


                    UploadService service = MyRetrofit2.getRetrofit2().create(UploadService.class);

                    SharedPreferences login_true=getSharedPreferences("login_true", Activity.MODE_PRIVATE);
                    // 로그인 이메일 정보를 가지고 옴.
                    String loginEmail = login_true.getString("login_email", "no");

                    RequestBody c_id_email = RequestBody.create(
                            MediaType.parse("multipart/form-data"), loginEmail);
                    RequestBody w_gender= RequestBody.create(
                            MediaType.parse("multipart/form-data"), gender_text);
                    RequestBody w_region= RequestBody.create(
                            MediaType.parse("multipart/form-data"), String.valueOf(region_wannabe.getSelectedItemPosition()-1));
                    RequestBody w_job= RequestBody.create(
                            MediaType.parse("multipart/form-data"), String.valueOf(job_wannabe.getSelectedItemPosition()-1));
                    RequestBody w_age_min= RequestBody.create(
                            MediaType.parse("multipart/form-data"), age_wannabe_min.getText().toString());

                    RequestBody w_age_max= RequestBody.create(
                            MediaType.parse("multipart/form-data"), age_wannabe_max.getText().toString());
                    RequestBody w_smoke= RequestBody.create(
                            MediaType.parse("multipart/form-data"), smoke_text);
                    RequestBody w_height_min= RequestBody.create(
                            MediaType.parse("multipart/form-data"), height_wannabe_min.getText().toString());
                    RequestBody w_height_max= RequestBody.create(
                            MediaType.parse("multipart/form-data"), height_wannabe_max.getText().toString());

                    Call<ResponseBody> call1 = service.wannabeSave2(c_id_email,w_gender,w_region,w_job,w_age_min,w_age_max,w_smoke
                            ,w_height_min,w_height_max);

                    call1.enqueue(new Callback<ResponseBody>() {
                        @Override
                        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                            Log.i("success",response.toString());

                            //파일이 업로드에 성공했다면,...기존에 저장해두었던 파일을 삭제하고 완료로 넘김.
                            //즉  shared도 지우고 그리구 sdcards에 저장되어 있는 사진도 지워줍니다요!!!
//
//
                            Intent get=getIntent();
                            if(get!=null){
                                custom_list_want1.setChecked(true);
                            }else{
                                custom_list_want.setChecked(true);

                            }
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
                        finish();


                        }

                        @Override
                        public void onFailure(Call<ResponseBody> call, Throwable t) {
                            Log.i("fail",t.toString());
                        }
                    });

                }
//
//                Log.i("gender",gender_text);
//
//
//
//                Log.i("region",String.valueOf(region_wannabe.getSelectedItemPosition()));
//
//
//                Log.i("job",String.valueOf(job_wannabe.getSelectedItemPosition()));
//
//
//                Log.i("age min",age_wannabe_min.getText().toString());
//                Log.i("age max",age_wannabe_max.getText().toString());
//
//
//                Log.i("smoke",smoke_text);
//
//
//
//                Log.i("height min",height_wannabe_min.getText().toString());
//                Log.i("height max",height_wannabe_max.getText().toString());

            }
        });

        //키
        range_seekbar.setOnRangeSeekbarChangeListener(new OnRangeSeekbarChangeListener() {
            @Override
            public void valueChanged(Number minValue, Number maxValue) {
                if (height_min!=null&height_max!=null){
                    Log.i("age","첫번째");
                    Log.i("age",height_min+"");
                    Log.i("age",height_max+"");
                    minValue=height_min;
                    maxValue=height_max;

                }
                Log.i("change",minValue+"/"+maxValue);
                height_wannabe_min.setText(String.valueOf(minValue));
                height_wannabe_max.setText(String.valueOf(maxValue));
                height_min=null;
                height_max=null;
            }
        });



        //나이
        age_wannabe.setOnRangeSeekbarChangeListener(new OnRangeSeekbarChangeListener() {
            @Override
            public void valueChanged(Number minValue, Number maxValue) {
                Log.i("age","change");
                if (age_min!=null&age_max!=null){
                    Log.i("age","첫번째");
                    minValue=age_min;
                    maxValue=age_max;

                }
                age_wannabe_min.setText(String.valueOf(minValue));
                age_wannabe_max.setText(String.valueOf(maxValue));
                age_min=null;
                age_max=null;
            }
        });

        //이미지 업로드 버튼 클릭
        upload_wannabe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i("업로드 버튼 클릭시", "이미지를 올립니다.");
                //앨범으로할지, 사진으로 할지 선택하게 해야함.....

                DialogInterface.OnClickListener cameraListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Log.i("personalpic:사진", "사진촬영");
                        doTakePicture();
                        dialog.dismiss();     //닫기
                    }
                };

                DialogInterface.OnClickListener albumListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Log.i("personalpic:사진", "앨범선택");
                        doTakeAlbum();
                        dialog.dismiss();     //닫기
                    }
                };

                DialogInterface.OnClickListener cancelListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Log.i("personalpic:사진", "취소");
                        dialog.dismiss();     //닫기
                    }
                };

                //찍어서 올릴건지, 사진 앨범 중 올릴건지 check
                new AlertDialog.Builder(MyWannabeSaveAllActivity.this)
                        .setTitle("사진 선택")
                        .setPositiveButton("사진촬영", cameraListener)
                        .setNeutralButton("취소", cancelListener)
                        .setNegativeButton("앨범선택", albumListener)
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



            //crop이 정상적으로 이루어졌으면 이리로 넘어옴... 이때, 얼굴인식이 되는지 한번 체크체크!!!

            //네이버 오픈 api 아이디와 비밀번호
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

                    if(response.isSuccessful()){
                        Log.i("face",response.toString());

                        //얼굴이 제대로 인식 되었을 때가 중요한 것.


                        if(response.body().info.faceCount==0){
                            //얼굴 인식이 안된다는 것이므로 다른 사진을 요청하자!
                            AlertDialog.Builder alert = new AlertDialog.Builder(MyWannabeSaveAllActivity.this);
                            //다이얼로그 창 띄어주기
                            alert.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();     //닫기
                                }
                            });

                            alert.setMessage("얼굴 인식이 잘 되지 않습니다.");
                            alert.show();
                            if(filePath!=null){
                                    boolean deleted=filePath.delete();
                                    Log.i("이전에있었던사진경로삭제결과",String.valueOf(deleted));
                                    filePath=null;

                            }
                        }else if(response.body().info.faceCount>1){
                            //사람 얼굴이 2명이상이라는 뜻이므로 한명의 사진만을 요청하자!
                            AlertDialog.Builder alert = new AlertDialog.Builder(MyWannabeSaveAllActivity.this);
                            //다이얼로그 창 띄어주기
                            alert.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();     //닫기
                                }
                            });

                            alert.setMessage("여러명의 얼굴이 포함된 사진은 등록이 불가합니다.");
                            alert.show();
                            if(filePath!=null){
                                boolean deleted=filePath.delete();
                                Log.i("이전에있었던사진경로삭제결과",String.valueOf(deleted));
                                filePath=null;

                            }
                        }else if(response.body().info.faceCount==1){
                            //인식이 된 것임!! 앗싸!!! 그럼 서버에 파일을 업로드 시키자!
                            //이미지를 업로드시키는 것.
                            Toast.makeText(getApplicationContext(),"얼굴 인식이 제대로 되었습니다.",Toast.LENGTH_SHORT).show();

                            Glide.with(MyWannabeSaveAllActivity.this)
                                    .load(filePath).into(pic_wannabe);

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
        Log.i("wannabesave","pause");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.i("wannabesave","stop");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.i("wannabesave","restart");

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i("wannabesave","destroy");

    }
}
