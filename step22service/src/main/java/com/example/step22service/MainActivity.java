package com.example.step22service;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    MusicService service;
    boolean isConnected;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button startBtn = findViewById(R.id.startBtn);
        Button stopBtn = findViewById(R.id.stopBtn);
        Button pauseBtn = findViewById(R.id.pauseBtn);
        startBtn.setOnClickListener(this);
        stopBtn.setOnClickListener(this);
        pauseBtn.setOnClickListener(this);

        //음악 재생 버튼을 눌렀을 때 감시할 리스너 등록
        Button playBtn = findViewById(R.id.playBtn);
        playBtn.setOnClickListener(this);

        //액티비티 종료 버튼을 눌렀을 때 감시할 리스너 등록
        Button endBtn = findViewById(R.id.endBtn);
        endBtn.setOnClickListener(this);
    }

    //서비스에 연결한다.
    @Override
    protected void onStart() {
        super.onStart();
        //MusicService에 연결할 인텐트 객체
        Intent intent = new Intent(this, MusicService.class);
        //액티비티의 bindService() 메소드를 이용해서 연결한다.
        //만일 서비스가 시작되지 않았으면 서비스 객체를 생성해서
        //시작할 준비만 된 서비스에 바인딩이 된다.
        bindService(intent, sConn, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(isConnected){
            unbindService(sConn);
            isConnected = false;
        }
    }

    //서비스 연결 객체를 필드로 선언한다.
    ServiceConnection sConn = new ServiceConnection() {
        //서비스에 연결되었을 때 호출되는 메소드
        @Override
        public void onServiceConnected(ComponentName name, IBinder binder) {
            //IBinder 객체를 원래 type으로 casting
            MusicService.LocalBinder IBinder = (MusicService.LocalBinder)binder;
            //MusicService의 참조값을 필드에 저장
            service = IBinder.getService();
            //연결되었다고 표시
            isConnected = true;
        }
        //서비스와 연결 해제 되었을 때 호출되는 메소드
        @Override
        public void onServiceDisconnected(ComponentName name) {
            //연결 해제 되었다고 표시
            isConnected = false;
        }
    };

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.startBtn:
                //MusicService를 시작시킨다.
                Intent intent = new Intent(this, MusicService.class);
                //액티비티의 메소드를 이용해서 서비스를 시작시킨다.
                startService(intent);
                break;
            case R.id.stopBtn:
                //MusicService를 비활성화 시킨다.
                Intent intent2 = new Intent(this, MusicService.class);
                //액티비티의 메소드를 이용해서 서비스를 종료시킨다.
                stopService(intent2);
                break;
            case R.id.pauseBtn:
                //필드에 저장되어있는 MusicService 객체의 참조값을 이용해서 메소드 호출
                service.pauseMusic();
                break;
            case R.id.playBtn:
                //필드에 저장되어있는 MusicService 객체의 참조값을 이용해서 메소드 호출
                service.playMusic();
                break;
            case R.id.endBtn:
                finish(); //Activity 종료
               break;
        }
    }

    @Override
    protected void onDestroy() {
        Log.e("MusicService", "onDestroy()");
        super.onDestroy();
    }
}