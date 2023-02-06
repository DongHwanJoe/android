package com.example.step18login;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.step18login.databinding.ActivityMainBinding;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    ActivityMainBinding binding;
    //session id 값을 영구 저장할 SharedPreferences
    SharedPreferences pref;
    //session id 값을 임시 저장할 필드
    String sessionId;
    //로그인 된 id 값을 저장할 필드
    String id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());

        setContentView(binding.getRoot());

        binding.login.setOnClickListener(v -> {
            //로그인 액티비티로 이동
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
        });

        binding.logout.setOnClickListener(v -> {
            //new LogoutTask().execute(AppConstants.BASE_URL+"/logout");

            Intent intent = new Intent(this, LogoutActivity.class);
            startActivity(intent);
        });

        binding.gallery.setOnClickListener(v -> {
            Intent intent = new Intent(this, GalleryActivity.class);
            startActivity(intent);
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        //SharedPreferences 객체의 참조값 얻어와서 필드에 저장하기
        pref = PreferenceManager.getDefaultSharedPreferences(this);
        //저장된 session id 가 있는지 읽어와 보기(없다면 기본값은 빈 문자열)
        sessionId = pref.getString("sessionId", "");

        //로그인 여부 요청해보기
        new LoginCheckTask().execute(AppConstants.BASE_URL+"/logincheck");
    }

    //로그인 여부를 체크하는 작업을 할 비동기 task
    class LoginCheckTask extends AsyncTask<String, Void, Boolean> {

        @Override
        protected Boolean doInBackground(String... strings) {
            //로그인 체크 url
            String requestUrl = strings[0];
            //서버가 http 요청에 대해서 응답하는 문자열을 누적할 객체
            StringBuilder builder = new StringBuilder();
            HttpURLConnection conn = null;
            InputStreamReader isr = null;
            BufferedReader br = null;
            boolean isLogin = false;
            try{
                //URL 객체 생성
                URL url = new URL(requestUrl);
                //HttpURLConnection 객체의 참조값 얻어오기
                conn = (HttpURLConnection)url.openConnection();
                if(conn != null){//연결이 되었다면
                    conn.setConnectTimeout(20000); //응답을 기다리는 최대 대기 시간
                    conn.setRequestMethod("GET");//Default 설정
                    conn.setUseCaches(false);//케쉬 사용 여부
                    //App에 저장된 session id 가 있다면 요청할 때 쿠키로 같이 보내기
                    if(!sessionId.equals("")) {
                        // JSESSIONID=xxx 형식의 문자열을 쿠키로 보내기
                        conn.setRequestProperty("Cookie", sessionId);
                    }

                    //응답 코드를 읽어온다.
                    int responseCode = conn.getResponseCode();

                    if(responseCode == 200){//정상 응답이라면...
                        //서버가 출력하는 문자열을 읽어오기 위한 객체
                        isr = new InputStreamReader(conn.getInputStream());
                        br = new BufferedReader(isr);
                        //반복문 돌면서 읽어오기
                        while(true){
                            //한줄씩 읽어들인다.
                            String line = br.readLine();
                            //더이상 읽어올 문자열이 없으면 반복문 탈출
                            if(line == null)break;
                            //읽어온 문자열 누적 시키기
                            builder.append(line);
                        }
                    }
                }
                //서버가 응답한 쿠키 목록을 읽어온다.
                List<String> cookList = conn.getHeaderFields().get("Set-Cookie");
                //만일 쿠키가 존재 한다면
                if(cookList != null){
                    Log.d("session id = ", sessionId);
                    for(String tmp : cookList){
                        //session id 가 들어있는 쿠키를 찾기
                        if(tmp.contains("JSESSIONID")){
                            //session id 만 추출
                            String sessionId = tmp.split(";")[0];
                            //SharedPreferences를 편집할 수 있는 객체를 활용해서
                            SharedPreferences.Editor editor = pref.edit();
                            //sessionId 라는 키 값으로 저장한다
                            editor.putString("sessionId", sessionId);
                            editor.apply(); //apply()는 비동기로 저장하기 때문에 실행의 흐름이 잡혀있지 않다(지연이 없음)
                            //필드에 담기
                            MainActivity.this.sessionId = sessionId;
                        }
                    }
                }
                //출력받은 문자열 전체 얻어내기
                JSONObject obj = new JSONObject(builder.toString());
                /*
                    {"isLogin" : false} or {"isLogin" : true, "id" : "kimgura"}
                    서버에서 위와 같은 형식의 json 문자열을 응답할 예정이다.
                 */
                Log.d("서버가 응답한 문자열", builder.toString());
                //로그인 여부 읽어오기
                isLogin = obj.getBoolean("isLogin");
                if(isLogin){
                    //로그인을 했다면 필드에 로그인 된 아이디를 담아둔다.
                    id = obj.getString("id");
                }
            }catch(Exception e){//예외가 발생하면
                Log.e("LoginCheckTask", e.getMessage());
            }finally {
                try{
                    if(isr != null)isr.close();
                    if(br != null)br.close();
                    if(conn != null)conn.disconnect();
                }catch(Exception e){}
            }
            //로그인 여부를 리턴하면 아래의 onPostExecute() 메소드에 전달된다.
            return isLogin;
        }

        @Override
        protected void onPostExecute(Boolean isLogin) {
            super.onPostExecute(isLogin);
            //UI스레드 이므로 UI와 관련된 작업을 할 수 있다.
            //TextView에 로그인 여부를 출력한다.
            if(isLogin){
                binding.userInfo.setText(id+" 님 로그인중...");
            }else{
                binding.userInfo.setText("로그인이 필요 합니다.");
            }
        }
    }

    class LogoutTask extends AsyncTask<String, Void, Boolean> {

        @Override
        protected Boolean doInBackground(String... strings) {
            String requestUrl = strings[0];

            StringBuilder builder = new StringBuilder();
            HttpURLConnection conn = null;
            InputStreamReader isr = null;
            BufferedReader br = null;
            boolean isLogout = false;

            try{
                URL url = new URL(requestUrl);
                conn = (HttpURLConnection)url.openConnection();
                if(conn != null){
                    conn.setConnectTimeout(20000);
                    conn.setRequestMethod("POST");
                    conn.setUseCaches(false);

                    int responseCode = conn.getResponseCode();

                    if(responseCode == 200){
                        isr = new InputStreamReader(conn.getInputStream());
                        br = new BufferedReader(isr);
                        while(true){
                            String line = br.readLine();
                            if(line == null)break;
                            builder.append(line);
                        }
                    }
                }

                JSONObject obj = new JSONObject(builder.toString());

                if(!sessionId.equals("")) {
                    isLogout = obj.getBoolean("isSuccess");
                }

                List<String> cookList = conn.getHeaderFields().get("Set-Cookie");
                if(cookList != null){
                    for(String tmp : cookList){
                        if(tmp.contains("JSESSIONID")){
                            SharedPreferences.Editor editor = pref.edit();
                            editor.clear();
                            editor.apply();
                            sessionId = "";
                        }
                    }
                }
            }catch(Exception e){
                Log.e("LogoutTask", e.getMessage());
            }finally {
                try{
                    if(isr != null)isr.close();
                    if(br != null)br.close();
                    if(conn != null)conn.disconnect();
                }catch(Exception e){}
            }

            return isLogout;
        }

        @Override
        protected void onPostExecute(Boolean isLogout) {
            super.onPostExecute(isLogout);

            if(isLogout){
                new AlertDialog.Builder(MainActivity.this)
                        .setTitle("알림")
                        .setMessage("로그아웃 되었습니다.")
                        .setNeutralButton("확인", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                sessionId = "";
                                id = "";
                                binding.userInfo.setText("로그인이 필요 합니다.");
                            }
                        })
                        .create()
                        .show();
            }else{
                new AlertDialog.Builder(MainActivity.this)
                        .setTitle("알림")
                        .setMessage("이미 로그아웃 상태입니다.")
                        .setNeutralButton("확인", null)
                        .create()
                        .show();
            }
        }
    }
}