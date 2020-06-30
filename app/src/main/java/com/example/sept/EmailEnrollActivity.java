package com.example.sept;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.os.AsyncTask;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.sept.Class.RequestHttpConnection;


public class EmailEnrollActivity extends AppCompatActivity {



    EditText et_em, et_pw, et_pw_cf;
    String sEm, sPw, sPw_cf;

    //비밀번호 중복체크 관련 문구
    String tellWhat="";
    //이메일아이디 중복체크 관련 문구
    String idtellWhat="";
    //id중복값을 넣어서 가져가기 위함
    Intent loginIntent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.enroll_email);
        setTitle("회원가입");

        et_em = findViewById(R.id.etEmail);
        et_pw = findViewById(R.id.etPassword);
        et_pw_cf = findViewById(R.id.etPasswordConfirm);

        loginIntent = new Intent(this, LoginActivity.class);


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

            //어떤 결과값을 가지고 왔는지 check
            Log.i("async:emailenroll.j",result);


            if(result.equals("n")||result.equals("y")){ //이메일 중복 체크와 관련된 부분
                if (loginIntent!=null) { //만들어져 있을 때.
                    loginIntent.putExtra("emailcheck", result);

                    Log.i("중복체크값63",String.valueOf(loginIntent.getStringExtra("emailcheck")));
                }
            }else if(result.equals("checkok")||result.equals("checkno")){ //가입 db 저장과 관련된 부분
                Log.i("db와 관련된 부분",result);
            }


            return result;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            //이메일 중복이 제대로 체크되었다면 -> 여기서 문구를 보여줌.
            TextView id_check_tell=findViewById(R.id.idCheckTell);

            Log.i("onpostexecute",s);

            //n,y -> 이메일 아이디 중복확인과 관련해서 체크하는 부분 -> y/n로 구분
            if(s.equals("n")){
                sEm = et_em.getText().toString();
                if(!sEm.equals("")){
                    id_check_tell.setVisibility(View.VISIBLE);
                    idtellWhat="등록 가능한 아이디입니다.";
                }else{
                    id_check_tell.setVisibility(View.GONE);
                    idtellWhat="";
                }
            }else if(s.equals("y")){
                id_check_tell.setVisibility(View.VISIBLE);
                idtellWhat="이미 등록된 아이디입니다.";
            }

            id_check_tell.setText(idtellWhat);

            //회원가입 db저장과 관련 아래 - checkok/checkno로 구분

        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        Log.i("login.j", "resume");

        //email 변화가 있었을 때 -> 다시 체크 -> 중복체크가 무효가 되게끔 설정.
        //email 다 쳤을 때 -

        et_em.addTextChangedListener(new TextWatcher() {


            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                //입력되기 전에....
            }

            @Override

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //입력되는 텍스트에 변화가 있을 때
                Log.i("변하고있는 text",s.toString());
                if (loginIntent!=null) { //만들어져 있을 때. -> 값이 변화했으므로 중복체크를 다시 받게끔 유도해야함.
                    loginIntent.putExtra("emailcheck", "n");
                    Log.i("중복체크값이변했는지보자",String.valueOf(loginIntent.getStringExtra("emailcheck")));
                }



            }

            @Override
            public void afterTextChanged(Editable editable) {
                // 입력한 후에
                if (loginIntent!=null) { //만들어져 있을 때. -> 값이 변화했으므로 중복체크를 다시 받게끔 유도해야함.
                    sEm = et_em.getText().toString();
                    if(!sEm.equals("")){
                        if(String.valueOf(loginIntent.getStringExtra("emailcheck")).equals("n")){
                            TextView id_check_tell=findViewById(R.id.idCheckTell);
                            id_check_tell.setVisibility(View.VISIBLE);
                            idtellWhat="중복확인 필요합니다.";
                            id_check_tell.setText(idtellWhat);
                        }
                    }else{
                        TextView id_check_tell=findViewById(R.id.idCheckTell);
                        id_check_tell.setVisibility(View.GONE);
                        idtellWhat="";
                        id_check_tell.setText(idtellWhat);
                    }



                }
            }



        });



        //비밀번호 확인 부분에 변화.
        et_pw_cf.addTextChangedListener(new TextWatcher() {




            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                //입력되기 전에....
            }

            @Override

            public void onTextChanged(CharSequence s, int start, int before, int count) {
               //text변화를 실시간으로 보여준다.

                sPw = et_pw.getText().toString();

                if(!sPw.equals("")){
                    if(String.valueOf(s).equals(sPw)){
                        Log.i("일치","일치");
                        if (loginIntent!=null) { //만들어져 있을 때. -> 값이 변화했으므로 중복체크를 다시 받게끔 유도해야함.
                            loginIntent.putExtra("pscheck", "y");
                            tellWhat="비밀번호가 일치합니다.";
                            Log.i("비밀번호확인이변했는지보자",String.valueOf(loginIntent.getStringExtra("pscheck")));
                        }
                    }else{
                        Log.i("불일치","불일치");
                        if (loginIntent!=null) { //만들어져 있을 때. -> 값이 변화했으므로 중복체크를 다시 받게끔 유도해야함.
                            loginIntent.putExtra("pscheck", "n");
                            tellWhat="비밀번호가 불일치합니다.";
                            Log.i("비밀번호확인이변했는지보자",String.valueOf(loginIntent.getStringExtra("pscheck")));
                        }
                    }
                }else{
                    if (loginIntent!=null) { //만들어져 있을 때. -> 값이 변화했으므로 중복체크를 다시 받게끔 유도해야함.
                        loginIntent.putExtra("pscheck", "n");
                        tellWhat="";
                        Log.i("비밀번호확인이변했는지보자",String.valueOf(loginIntent.getStringExtra("pscheck")));
                    }
                }

            }


            @Override
            public void afterTextChanged(Editable editable) {
                // 입력한 후에 -> 일치 불일치 여부를 알려주자!
                TextView ps_check_tell=findViewById(R.id.psCheckTell);
                ps_check_tell.setText(tellWhat);
            }



        });

        //비밀번호 부분에 변화.
        et_pw.addTextChangedListener(new TextWatcher() {


            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                //입력되기 전에....
            }

            @Override

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //text변화를 실시간으로 보여준다.

                sPw_cf = et_pw_cf.getText().toString();

                if(!sPw_cf.equals("")){
                    if(String.valueOf(s).equals(sPw_cf)){
                        Log.i("일치","일치");
                        if (loginIntent!=null) { //만들어져 있을 때. -> 값이 변화했으므로 중복체크를 다시 받게끔 유도해야함.
                            loginIntent.putExtra("pscheck", "y");
                            tellWhat="비밀번호가 일치합니다.";
                            Log.i("비밀번호확인이변했는지보자",String.valueOf(loginIntent.getStringExtra("pscheck")));
                        }
                    }else{
                        Log.i("불일치","불일치");
                        if (loginIntent!=null) { //만들어져 있을 때. -> 값이 변화했으므로 중복체크를 다시 받게끔 유도해야함.
                            loginIntent.putExtra("pscheck", "n");
                            tellWhat="비밀번호가 불일치합니다.";
                            Log.i("비밀번호확인이변했는지보자",String.valueOf(loginIntent.getStringExtra("pscheck")));
                        }
                    }
                }else{
                    if (loginIntent!=null) { //만들어져 있을 때. -> 값이 변화했으므로 중복체크를 다시 받게끔 유도해야함.
                        loginIntent.putExtra("pscheck", "n");
                        tellWhat="";
                        Log.i("비밀번호확인이변했는지보자",String.valueOf(loginIntent.getStringExtra("pscheck")));
                    }
                }

            }


            @Override
            public void afterTextChanged(Editable editable) {
                // 입력한 후에
                TextView ps_check_tell=findViewById(R.id.psCheckTell);
                ps_check_tell.setText(tellWhat);
            }



        });

        Button.OnClickListener onClickListener = new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (view.getId()) {
                    case R.id.btnDone:
                        //회원가입 버튼 클릭시 ->
                        //1. 중복확인 여부 check

                        AlertDialog.Builder alert = new AlertDialog.Builder(EmailEnrollActivity.this);
                        if(idtellWhat.equals("등록 가능한 아이디입니다.")){
                            if(String.valueOf(loginIntent.getStringExtra("emailcheck")).equals("n")){
                                sEm = et_em.getText().toString();
                                Log.i("회원가입클릭아이디",String.valueOf(loginIntent.getStringExtra("emailcheck")));
                                Log.i("회원가입클릭아이디보자",sEm);

                                //2. 비밀번호 일치 여부 check

                                if(String.valueOf(loginIntent.getStringExtra("pscheck")).equals("y")){ //비밀번호도 일치할때


                                   //이제 db에 저장해놓는 거 -> 근데 아이디는 승인이 되기 전에는 여러번 중복가입이 가능하므로 비밀번호 업데이트가 요구됨.
                                    sPw_cf = et_pw_cf.getText().toString();
                                     Log.i("회원가입클릭비번보자",sPw_cf);


                                    //이메일로 인증 링크 보내기


                                  //  AlertDialog.Builder alert = new AlertDialog.Builder(EmailEnrollActivity.this);
                                    //다이얼로그 창 띄어주기
                                    alert.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();     //닫기
//                                            startActivity(loginIntent);


                                            //ID값 보내주기
                                            Intent intent = new Intent();
                                            intent.putExtra("login_email",et_em.getText().toString());
                                            setResult(RESULT_OK,intent);
                                            finish();
                                        }
                                    });

                                    alert.setMessage("["+sEm+"]로 발송된 링크를 통해 가입 승인 후 로그인이 가능합니다.");
                                    alert.show();

                                    String url = "http://??/email_enroll.php";
                                    // AsyncTask를 통해 HttpURLConnection 수행.
                                    ContentValues values=new ContentValues();
                                    //이메일 값을 보냄.
                                    values.put("id_email",sEm);
                                    values.put("psword",sPw_cf);
                                    NetworkTask networkTask = new NetworkTask(url, values);
                                    networkTask.execute();


                                }else{
                                    alert.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();     //닫기

                                        }
                                    });

                                    alert.setMessage("비밀번호를 다시 확인해주세요");
                                    alert.show();
                                }

                            }
                        }else if(!idtellWhat.equals("이미 등록된 아이디입니다.")){ //중복확인 체크가 안된 경우.
                            alert.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();     //닫기
                                }
                            });

                            alert.setMessage("아이디 중복확인이 필요합니다.");
                            alert.show();
                        }else{
                            alert.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();     //닫기
                                }
                            });

                            alert.setMessage("이미 등록된 아이디입니다.");
                            alert.show();
                        }



                        //2. 비밀번호 일치 여부 check


                        if(!String.valueOf(loginIntent.getStringExtra("pscheck")).equals("y")){
                            alert.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();     //닫기
                                }
                            });

                            alert.setMessage("비밀번호를 다시 확인해주세요.");
                            alert.show();
                        }

                        break;
                    case R.id.btnCancel:
                        //취소 클릭시
                        //로그인창으로 이동 -> 즉, 메인페이지로 가는 것과 같음.
                        Log.i("enroll.j:취소", "회원가입취소");
                        finish();
                        break;
                    case R.id.btnCheck:
                        //이메일 가입여부 체크  -> db와 연동해서 보내기
                        // URL 설정.
                        sEm = et_em.getText().toString();
                        Log.i("enroll.j:이메일체크", "중복체크");
                        String url = "http://??/email_check.php";

                        // AsyncTask를 통해 HttpURLConnection 수행.
                        ContentValues values=new ContentValues();
                        //이메일 값을 보냄.
                        values.put("id_email",sEm);
                        NetworkTask networkTask = new NetworkTask(url, values);
                        networkTask.execute();
                        break;
                }
            }
        };
        Button btnDone = (Button) findViewById(R.id.btnDone);
        btnDone.setOnClickListener(onClickListener);

        Button btnCancel = (Button) findViewById(R.id.btnCancel);
        btnCancel.setOnClickListener(onClickListener);

        Button btnCheck = (Button) findViewById(R.id.btnCheck);
        btnCheck.setOnClickListener(onClickListener);

    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }
}
