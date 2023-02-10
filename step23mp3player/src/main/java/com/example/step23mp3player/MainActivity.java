package com.example.step23mp3player;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity implements MediaPlayer.OnPreparedListener {

    MediaPlayer mp;
    ImageButton playBtn;
    //재생준비 완료 여부
    boolean isPrepared = false;
    ProgressBar progress;
    TextView time;

    Handler handler = new Handler(){
        @Override
        public void handleMessage(@NonNull Message msg) {
            int currentTime = mp.getCurrentPosition();
            progress.setProgress(currentTime);

            String info = String.format("%d min, %d sec", TimeUnit.MILLISECONDS.toMinutes(currentTime),
                    TimeUnit.MILLISECONDS.toSeconds(currentTime));
            time.setText(info);
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
        playBtn = findViewById(R.id.playBtn);
        playBtn.setEnabled(false);
        playBtn.setOnClickListener(v -> {
            if(!isPrepared){
                return;
            }
            mp.start();
        });

        ImageButton pauseBtn = findViewById(R.id.pauseBtn);
        pauseBtn.setOnClickListener(v -> {
            mp.pause();
        });


    }

    @Override
    protected void onStart() {
        super.onStart();
        //음악을 재생할 준비
        try {
            mp = new MediaPlayer();
            mp.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mp.setDataSource("http://192.168.0.29:9000/boot07/resources/upload/mp3piano.mp3");
            mp.setOnPreparedListener(this);
            //로딩하기
            mp.prepareAsync();

        }catch(Exception e){
            Log.e("MainActivity", e.getMessage());
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        mp.stop();
        mp.release();
    }

    //재생할 준비가 끝나면 호출되는 메소드
    @Override
    public void onPrepared(MediaPlayer mp) {
        Toast.makeText(this, "로딩완료", Toast.LENGTH_SHORT).show();
        isPrepared = true;
        playBtn.setEnabled(true);
        progress.setMax(mp.getDuration());
        Log.e("전체 시간", "duration"+mp.getDuration());

        handler.sendEmptyMessageDelayed(0, 100);
    }
}