package com.example.step23mp3player;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import java.util.concurrent.TimeUnit;

/*
    MusicService를 이용해서 음악을 재생하는 방법
    - initMusic()메소드를 호출하면서 음원의 위치를 넣어주고,
    - 음원 로딩이 완료되면 자동으로 play 된다.
 */

public class MusicService extends Service implements MediaPlayer.OnPreparedListener {

    MediaPlayer mp = new MediaPlayer();
    boolean isPrepared;

    public void initMusic(String url) {
        isPrepared = false;
        if (mp == null) {
            mp = new MediaPlayer();
            mp.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mp.setOnPreparedListener(this);
        }

        //만일 현재 재생중이면
        if (mp.isPlaying()) {
            mp.stop(); //재생을 중지하고
        }
        mp.reset();//초기화
        try {
            //로딩할 음원의 위치를 넣어주고
            mp.setDataSource(url);
        } catch (Exception e) {
            Log.e("initMusic()", e.getMessage());
        }
        //비동기로 로딩을 시킨다.
        mp.prepareAsync();
    }

    public void playMusic() {
        mp.start();
    }

    public void pauseMusic() {
        mp.pause();
    }

    public void stopMusic() {
        mp.stop();
    }

    //재생이 준비되었는지 여부를 리턴하는 메소드
    public boolean isPrepared() {
        return isPrepared;
    }

    //MediaPlayer 객체의 참조값 리턴하는 메소드
    public MediaPlayer getMp() {
        return mp;
    }

    //서비스가 최초 활성화 될 때 한 번 호출되는 메소드
    @Override
    public void onCreate() {
        super.onCreate();
    }

    //최초 활성화 혹은 이미 활성화 된 이후 이 서비스를
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        switch (intent.getAction()) {
            case AppConstants.ACTION_PLAY:
                Log.d("onStartCommand()", "play");
                playMusic();
                break;

            case AppConstants.ACTION_PAUSE:
                Log.d("onStartCommand()", "pause");
                pauseMusic();
                break;

            case AppConstants.ACTION_STOP:
                Log.d("onStartCommand()", "stop");
                stopMusic();
                break;
        }
        return START_NOT_STICKY;
    }

    public class LocalBinder extends Binder {
        public MusicService getService() {
            return MusicService.this;
        }
    }

    //필드에 바인더 객체의 참조값 넣어두기
    final IBinder binder = new LocalBinder();

    //액티비티에서 바인딩연결이 되면 호출되는 메소드
    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        isPrepared = true;
        //준비가 되면 자동으로 음악을 재생한다.
        playMusic();
        handler.removeMessages(0);
        handler.sendEmptyMessageDelayed(0, 100);
    }

    @Override
    public void onDestroy() {
        if(mp != null){
            mp.stop();
            mp.release();
            mp = null;
        }
        handler.removeMessages(0);
        super.onDestroy();
    }

    Handler handler = new Handler(){
        @Override
        public void handleMessage(@NonNull Message msg) {

            makeManualCancelNoti();
            handler.sendEmptyMessageDelayed(0, 100);
        }
    };

    //수동으로 취소하는 알림을 띄우는 메소드
    public void makeManualCancelNoti() {
        //현재 재생시간을 문자열로 얻어내기
        int currentTime = mp.getCurrentPosition();
        String info = String.format("%d min, %d sec",
                TimeUnit.MILLISECONDS.toMinutes(currentTime),
                TimeUnit.MILLISECONDS.toSeconds(currentTime)
                        - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(currentTime)));

        //인텐트 전달자 객체
        //PendingIntent pendingIntent = PendingIntent.getActivity(this, NOTI_ID, intent, PendingIntent.FLAG_MUTABLE);

        Intent iPlay = new Intent(this, MusicService.class);
        iPlay.setAction(AppConstants.ACTION_PLAY);
        PendingIntent pIntentPlay = PendingIntent.getService(this, 1, iPlay, PendingIntent.FLAG_MUTABLE);

        Intent iPause = new Intent(this, MusicService.class);
        iPlay.setAction(AppConstants.ACTION_PAUSE);
        PendingIntent pIntentPause = PendingIntent.getService(this, 1, iPlay, PendingIntent.FLAG_MUTABLE);

        Intent iStop = new Intent(this, MusicService.class);
        iPlay.setAction(AppConstants.ACTION_STOP);
        PendingIntent pIntentStop = PendingIntent.getService(this, 1, iPlay, PendingIntent.FLAG_MUTABLE);

        //띄울 알림을 구성하기
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, AppConstants.CHANNEL_ID)
                .setSmallIcon(android.R.drawable.star_on) //알림의 아이콘
                .setContentTitle("쇼팽 녹턴") //알림의 제목
                .setContentText(info)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT) //알림의 우선순위
                .addAction(new NotificationCompat.Action(android.R.drawable.ic_media_play, "Play", pIntentPlay))
                .addAction(new NotificationCompat.Action(android.R.drawable.ic_media_play, "Pause", pIntentPause))
                .addAction(new NotificationCompat.Action(android.R.drawable.ic_media_play, "Stop", pIntentStop))
                .setProgress(mp.getDuration(), mp.getCurrentPosition(), false)
                //.setContentIntent(pendingIntent)  //인텐트 전달자 객체
                .setAutoCancel(false); //자동 취소 되는 알림인지 여부

        //알림 만들기
        Notification noti = builder.build();

        //알림 권한이 없다면
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            //메소드를 여기서 종료
            return;
        }
        //알림 메니저를 이용해서 알림을 띄운다.
        NotificationManagerCompat.from(this).notify(AppConstants.NOTI_ID, noti);
    }
}