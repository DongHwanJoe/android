package com.example.step23mp3player;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemClickListener, Util.RequestListener {

    MediaPlayer mp;
    ImageButton playBtn;
    //재생준비 완료 여부
    boolean isPrepared = false;
    ProgressBar progress;
    TextView time;
    SeekBar seek;
    List<String> songs;
    ArrayAdapter<String> adapter;

    //서비스의 참조값을 저장할 필드
    MusicService service;
    //서비스에 연결됐는지 여부
    boolean isConnected;

    SharedPreferences pref;
    String sessionId;
    String id;

    //서비스 연결객체
    ServiceConnection sConn = new ServiceConnection() {
        //서비스에 연결됐을 때 호출되는 메소드
        @Override
        public void onServiceConnected(ComponentName name, IBinder binder) {
            //MusicService 객체의 참조값을 얻어와서 필드에 저장
            //IBinder 객체를 원래 type으로 casting
            MusicService.LocalBinder IBinder = (MusicService.LocalBinder)binder;
            service = IBinder.getService();
            //연결되었다고 표시
            isConnected = true;
            //핸들러에 메세지 보내기
            handler.removeMessages(0);
            handler.sendEmptyMessageDelayed(0,100);
        }
        //서비스에 연결이 해제 됐을 때 호출되는 메소드
        @Override
        public void onServiceDisconnected(ComponentName name) {
            //연결 해제 됐다고 표시
            isConnected = false;
        }
    };

    Handler handler = new Handler(){
        @Override
        public void handleMessage(@NonNull Message msg) {

            if(service.isPrepared()){
                int maxTime = service.getMp().getDuration();
                progress.setMax(maxTime);
                seek.setMax(maxTime);
                int currentTime = service.getMp().getCurrentPosition();
                progress.setProgress(currentTime);
                seek.setProgress(currentTime);

                String info = String.format("%d min, %d sec",
                        TimeUnit.MILLISECONDS.toMinutes(currentTime),
                        TimeUnit.MILLISECONDS.toSeconds(currentTime)
                                -TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS. toMinutes(currentTime)) );
                time.setText(info);
            }
            //makeManualCancelNoti();
            handler.sendEmptyMessageDelayed(0,100);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        time = findViewById(R.id.time);
        //%d는 숫자, %s 문자자
        String info = String.format("%d min, %d sec", 0, 0);
        time.setText(info);

        progress = findViewById(R.id.progress);
        seek = findViewById(R.id.seek);

        playBtn = findViewById(R.id.playBtn);
        playBtn.setEnabled(false);
        playBtn.setOnClickListener(v -> {
            service.playMusic();
            //service.initMusic("http://192.168.0.29:9000/boot07/resources/upload/mp3piano.mp3");
        });

        ImageButton pauseBtn = findViewById(R.id.pauseBtn);
        pauseBtn.setOnClickListener(v -> {
            service.pauseMusic();
            });

        //알림채널만들기
        createNotificationChannel();

        //listView 관련 작업
        ListView listView = findViewById(R.id.listView);
        songs = new ArrayList<>();
        //listview에 연결할 아답타
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_activated_1, songs);
        listView.setAdapter(adapter);
        //listview에 아이템 클릭 리스너 등록
        listView.setOnItemClickListener(this);

        Util.sendGetRequest(1, "http://192.168.0.29:9000/boot07/api/music/list", null, this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        //MusicService에 연결할 인텐트 객체
        Intent intent = new Intent(this, MusicService.class);
        //startService(intent);
        //액티비티의 bindService() 메소드를 이용헤서 연결한다.
        //만일 서비스가 시작이 되지 않았으면 서비스 개게를 생성해서
        //시작할 준비만 된 서비스에 바인딩이 된다.
        bindService(intent, sConn, Context.BIND_AUTO_CREATE);

        pref = PreferenceManager.getDefaultSharedPreferences(this);
        sessionId = pref.getString("sessionId", "");

        //로그인여부 체크
        new LoginCheckTask().execute(AppConstants.BASE_URL + "/music/logincheck");
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(isConnected){
            //서비스 바인딩 해제
            unbindService(sConn);
            isConnected = false;
        }
    }

    //앱의 사용자가 알림을 직접 관리 할수 있도록 알림 체널을 만들어야한다.

    public void createNotificationChannel(){
        //알림 체널을 지원하는 기기인지 확인해서
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            //알림 체널을 만들기

            //셈플 데이터
            String name = "Music Player";
            String text = "Control";
            //알림체널 객체를 얻어내서
            NotificationChannel channel =
                    new NotificationChannel(AppConstants.CHANNEL_ID, name, NotificationManager.IMPORTANCE_LOW);
            //체널의 설명을 적고
            channel.setDescription(text);
            //알림 메니저 객체를 얻어내서
            NotificationManager notiManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
            //알림 체널을 만든다.
            notiManager.createNotificationChannel(channel);

        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case 0:
                //권한을 부여 했다면
                if(grantResults[0] == PackageManager.PERMISSION_GRANTED){

                }else{//권한을 부여 하지 않았다면
                    Toast.makeText(this, "알림을 띄울 권한이 필요합니다.",
                            Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    //listView의 cell을 클릭하면 호출되는 메소드
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        //position은 클릭한 셀의 인덱스
        String fileName = songs.get(position);
        service.initMusic(AppConstants.MUSIC_URL+fileName);
    }

    @Override
    public void onSuccess(int requestId, Map<String, Object> result) {
        if(requestId == 1){
            String jsonStr = (String)result.get("data");
            try {
                JSONArray arr = new JSONArray(jsonStr);
                for(int i = 0; i < arr.length(); i++){
                    JSONObject obj = arr.getJSONObject(i);
                    String fileName = obj.getString("fileName");
                    songs.add(fileName);
                }
                //ListView에 연결될 adapter에 모델이 바뀌었다고 알려서 ListView에 목록이 출력되도록 한다.
                adapter.notifyDataSetChanged();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onFail(int requestId, Map<String, Object> result) {

    }

    //로그인 여부를 체크하는 작업을 할 비동기 task
    class LoginCheckTask extends AsyncTask<String, Void, Boolean> {

        @Override
        protected Boolean doInBackground(String... strings) {
            //로그인 체크 url
            String requestUrl=strings[0];
            //서버가 http 요청에 대해서 응답하는 문자열을 누적할 객체
            StringBuilder builder=new StringBuilder();
            HttpURLConnection conn=null;
            InputStreamReader isr=null;
            BufferedReader br=null;
            boolean isLogin=false;
            try{
                //URL 객체 생성
                URL url=new URL(requestUrl);
                //HttpURLConnection 객체의 참조값 얻어오기
                conn=(HttpURLConnection)url.openConnection();
                if(conn!=null){//연결이 되었다면
                    conn.setConnectTimeout(20000); //응답을 기다리는 최대 대기 시간
                    conn.setRequestMethod("GET");//Default 설정
                    conn.setUseCaches(false);//케쉬 사용 여부
                    //App 에 저장된 session id 가 있다면 요청할때 쿠키로 같이 보내기
                    if(!sessionId.equals("")) {
                        // JSESSIONID=xxx 형식의 문자열을 쿠키로 보내기
                        conn.setRequestProperty("Cookie", sessionId);
                    }

                    //응답 코드를 읽어온다.
                    int responseCode=conn.getResponseCode();

                    if(responseCode==200){//정상 응답이라면...
                        //서버가 출력하는 문자열을 읽어오기 위한 객체
                        isr=new InputStreamReader(conn.getInputStream());
                        br=new BufferedReader(isr);
                        //반복문 돌면서 읽어오기
                        while(true){
                            //한줄씩 읽어들인다.
                            String line=br.readLine();
                            //더이상 읽어올 문자열이 없으면 반복문 탈출
                            if(line==null)break;
                            //읽어온 문자열 누적 시키기
                            builder.append(line);
                        }
                    }
                }
                //서버가 응답한 쿠키 목록을 읽어온다.
                List<String> cookList=conn.getHeaderFields().get("Set-Cookie");
                //만일 쿠키가 존대 한다면
                if(cookList != null){
                    //반복문 돌면서
                    for(String tmp : cookList){
                        //session id 가 들어 있는 쿠키를 찾아내서
                        if(tmp.contains("JSESSIONID")){
                            //session id 만 추출해서
                            String sessionId=tmp.split(";")[0];
                            //SharedPreferences 을 편집할수 있는 객체를 활용해서
                            SharedPreferences.Editor editor=pref.edit();
                            //sessionId 라는 키값으로 session id 값을 저장한다.
                            editor.putString("sessionId", sessionId);
                            editor.apply();//apply() 는 비동기로 저장하기 때문에 실행의 흐름이 잡혀 있지 않다(지연이 없음)
                            //필드에도 담아둔다.
                            MainActivity.this.sessionId=sessionId;
                        }
                    }
                }
                //출력받은 문자열 전체 얻어내기
                JSONObject obj=new JSONObject(builder.toString());
                /*
                    {"isLogin":false} or {"isLogin":true, "id":"kimgura"}
                    서버에서 위와 같은 형식의 json 문자열을 응답할 예정이다.
                 */
                Log.d("서버가 응답한 문자열", builder.toString());
                //로그인 여부를 읽어와서
                isLogin=obj.getBoolean("isLogin");
                //만일 로그인을 했다면
                if(isLogin){
                    //필드에 로그인된 아이디를 담아둔다.
                    id=obj.getString("id");
                }
            }catch(Exception e){//예외가 발생하면
                Log.e("LoginCheckTask", e.getMessage());
            }finally {
                try{
                    if(isr!=null)isr.close();
                    if(br!=null)br.close();
                    if(conn!=null)conn.disconnect();
                }catch(Exception e){}
            }
            //로그인 여부를 리턴하면 아래의 onPostExecute() 메소드에 전달된다.
            return isLogin;
        }

        @Override
        protected void onPostExecute(Boolean isLogin) {
            super.onPostExecute(isLogin);
            //여기는 UI 스레드 이기 때문에 UI 와 관련된 작업을 할수 있다.
            //TextView 에 로그인 여부를 출력하기
            if(isLogin){
                TextView infoText = findViewById(R.id.infoText);
                infoText.setText(id + " 님 로그인중");
            }else{
                //로그인 액티비티로 이동
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        }
    }
}