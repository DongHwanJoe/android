package com.example.step09gameview;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GameView extends View {
    //필드
    Bitmap backImg; //배경 이미지
    int width, height; //화면의 폭과 높이(GameView 가 차지하고 있는 화면의 폭과 높이)

    //드래곤의 이미지를 저장할 배열
    Bitmap[] dragonImgs = new Bitmap[4];
    //dragon image index
    int dragonIndex = 0;
    //unit size
    int unitSize;
    //coordinate
    int dragonX, dragonY;
    //backImg Y
    int back1Y, back2Y;
    int count = 0;
    int eCount = 0;

    //Missile 객체를 저장할 List
    List<Missile> missList = new ArrayList<>();
    //미사일의 크기
    int missSize;
    //미사일 이미지를 담을 배열
    Bitmap[] missImgs = new Bitmap[3];
    //미사일 속도
    int speedMissile;

    //적기 이미지를 저장할 배열
    Bitmap[][] enemyImgs = new Bitmap[2][2];
    //Enemy 객체를 저장할 List
    List<Enemy> enemyList = new ArrayList<>();
    //적기의 x 좌표를 저장할 배열
    int[] enemyX = new int[5];
    //랜덤한 숫자를 얻어낼 Random 객체
    Random ran = new Random();
    //적기가 만들어 진 이후 count
    int postCount;
    //점수
    int point;
    //효과음을 재생해주는 객체
    SoundManager soundManager;

    public GameView(Context context) {
        super(context);
    }

    public GameView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    //SoundManager 객체를 전달 받아서 필드에 저장하는 메소드
    public void setSoundManager(SoundManager soundManager) {
        this.soundManager = soundManager;
    }

    //초기화 메소드
    public void init(){
        //원본 배경 이미지 읽어들이기
        Bitmap backImg = BitmapFactory.decodeResource(getResources(), R.drawable.backbg);
        //배경이미지를 view의 크기에 맞게 조절해서 필드에 저장
        this.backImg = Bitmap.createScaledBitmap(backImg, width, height, false);

        //드레곤 이미지를 로딩해서 사이즈를 조절하고 배열에 저장한다.
        Bitmap dragonImg1 = BitmapFactory.decodeResource(getResources(), R.drawable.unit1);
        Bitmap dragonImg2 = BitmapFactory.decodeResource(getResources(), R.drawable.unit2);
        Bitmap dragonImg3 = BitmapFactory.decodeResource(getResources(), R.drawable.unit3);
        dragonImg1 = Bitmap.createScaledBitmap(dragonImg1, unitSize, unitSize, false);
        dragonImg2 = Bitmap.createScaledBitmap(dragonImg2, unitSize, unitSize, false);
        dragonImg3 = Bitmap.createScaledBitmap(dragonImg3, unitSize, unitSize, false);
        dragonImgs[0] = dragonImg1;
        dragonImgs[1] = dragonImg2;
        dragonImgs[2] = dragonImg3;
        dragonImgs[3] = dragonImg2;

        //미사일 이미지 로딩
        Bitmap missImg1 = BitmapFactory.decodeResource(getResources(), R.drawable.mi1);
        Bitmap missImg2 = BitmapFactory.decodeResource(getResources(), R.drawable.mi2);
        Bitmap missImg3 = BitmapFactory.decodeResource(getResources(), R.drawable.mi3);
        //미사일 이미지 크기 조절
        missImg1 = Bitmap.createScaledBitmap(missImg1, missSize, missSize, false);
        missImg2 = Bitmap.createScaledBitmap(missImg2, missSize, missSize, false);
        missImg3 = Bitmap.createScaledBitmap(missImg3, missSize, missSize, false);
        //미사일 이미지를 배열에 넣어두기
        missImgs[0] = missImg1;
        missImgs[1] = missImg2;
        missImgs[2] = missImg3;

        //적기 이미지 로딩
        Bitmap enemyImg1 = BitmapFactory.decodeResource(getResources(), R.drawable.silver1);
        Bitmap enemyImg2 = BitmapFactory.decodeResource(getResources(), R.drawable.silver2);
        Bitmap enemyImg3 = BitmapFactory.decodeResource(getResources(), R.drawable.gold1);
        Bitmap enemyImg4 = BitmapFactory.decodeResource(getResources(), R.drawable.gold2);

        //적기 이미지 사이즈 조절
        enemyImg1 = Bitmap.createScaledBitmap(enemyImg1, unitSize, unitSize, false);
        enemyImg2 = Bitmap.createScaledBitmap(enemyImg2, unitSize, unitSize, false);
        enemyImg3 = Bitmap.createScaledBitmap(enemyImg3, unitSize, unitSize, false);
        enemyImg4 = Bitmap.createScaledBitmap(enemyImg4, unitSize, unitSize, false);

        //적기 이미지 배열에 저장
        enemyImgs[0][0] = enemyImg1; //0행 0열 silver1
        enemyImgs[0][1] = enemyImg2; //0행 1열 silver2
        enemyImgs[1][0] = enemyImg3; //1행 0열 gold1
        enemyImgs[1][1] = enemyImg4; //1행 1열 gold2
        //적기의 x 좌표를 구해서 배열에 저장한다.
        for(int i = 0; i < 5; i++){
            enemyX[i] = i * unitSize + unitSize/2;
        }

        //Handler 객체에 메세지를 보내서 화면이 주기적으로 갱신되는 구조로 바꾼다.
        handler.sendEmptyMessageDelayed(0, 20);
    }

    //View가 활성화될 때 최초 한 번 호출되고 View의 사이즈가 바뀌면 다시 호출된다.
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        //view가 차지하고 있는 폭과 높이가 px 단위로 w, h에 전달된다.
        width = w;
        height = h;

        //unitSize 는 화면 폭의 1/5로 설정
        unitSize = w/5;
        //좌표 부여
        dragonX = w/2;
        dragonY = height - unitSize / 2;

        //배경이미지의 초기 좌표
        back1Y = 0;
        back2Y = -height;

        missSize = unitSize/4;

        //미사일의 속도의 높이/50
        speedMissile = h/50;

        //초기화 메소드 호출
        init();
    }

    @Override
    protected void onDraw(Canvas canvas) {

        // 배경이미지 2개 그리기
        canvas.drawBitmap(backImg, 0, back1Y, null);
        canvas.drawBitmap(backImg, 0, back2Y, null);

        //미사일 그리기
        for(Missile tmp:missList){
            canvas.drawBitmap(missImgs[0], tmp.x - missSize/2, tmp.y - missSize/2, null);
        }

        //적기 그리기
        for(Enemy tmp:enemyList){
            if(tmp.isFall){
                //canvas의 정상 상태(변화하기 전)
                canvas.save();
                //적기의 위치로 평행이동
                canvas.translate(tmp.x, tmp.y);
                canvas.rotate(tmp.angle);
                Bitmap scaled = Bitmap.createScaledBitmap(enemyImgs[tmp.type][tmp.imageIndex],
                        tmp.size, tmp.size, false);
                //적기를 원점에 그린다.
                canvas.drawBitmap(scaled, 50 -unitSize / 2, -unitSize / 2 , null);

                //저장했던 정상 상태로 되돌린다.
                canvas.restore();
            }else{ //정상 상태의 적기
                canvas.drawBitmap(enemyImgs[tmp.type][tmp.imageIndex], tmp.x - unitSize/2, tmp.y - unitSize/2, null);
            }
        }

        //글자 출력을 위한 paint 객체 생성
        Paint textP = new Paint();
        textP.setColor(Color.YELLOW);
        textP.setTextSize(50);
        //점수 출력하기 .drawText( 출력할 문자열, 좌하단의 x, 좌하단 y, paint 객체 )
        canvas.drawText("Point : " + point, 10, 60, textP);

        //draw dragon
        canvas.drawBitmap(dragonImgs[dragonIndex], dragonX - unitSize / 2, dragonY - unitSize / 2, null);

        back1Y += 5;
        back2Y += 5;

        //배경1의 좌표가 아래로 벗어나면
        if(back1Y >= height){
            //배경1을 다시 상단으로 보낸다.
            back1Y = -height;
            //배경2와 오차가 생기지 않게 복원하기
            back2Y = 0;
        }
        if(back2Y >= height){
            back2Y = -height;
            back1Y = 0;
        }

        count++;
        if(count%10 == 0) {
            dragonIndex++;
            if (dragonIndex == 4) {
                dragonIndex = 0;
                count = 0;
            }
        }

        missileService();
        enemyService();
        checkStrike();
    }

    //미사일 관련 처리
    public void missileService(){
        //미사일 만들기
        if(count % 5 == 0){
            //미사일 발사음 재생
            soundManager.playSound(GameActivity.SOUND_LAZER);
            missList.add(new Missile(dragonX, dragonY- unitSize / 3));
        }

        //미사일 움직이기
        for(Missile tmp:missList){
            tmp.y -= speedMissile;
            if(tmp.y < -missSize/2){
                tmp.isDead = true;
            }
        }

        //미사일 객체를 모두 체크해서 배열에서 제거할 객체는 제거하기(반복문의 역순으로 돌아야 한다)
        for(int i = missList.size() - 1; i >= 0; i--){
            Missile tmp = missList.get(i);
            if(tmp.isDead){
                missList.remove(i);
            }
        }
    }

    //적과 미사일의 충돌 검사 하기
    public void checkStrike(){
        for (int i = 0; i < missList.size(); i++){
            //i번째 미사일 객체
            Missile m = missList.get(i);
            for(int j = 0; j < enemyList.size(); j++){
                //j번째 적 객체
                Enemy e = enemyList.get(j);
                //i번째 미사일이 j번째 적의 4각형 영역 안에 있는지 여부
                boolean isStrike = m.x > e.x - unitSize/2 &&
                                   m.x < e.x + unitSize/2 &&
                                   m.y > e.y - unitSize/2 &&
                                   m.y < e.y + unitSize/2;

                if(isStrike && !e.isFall){
                    //효과음 재생
                    soundManager.playSound(GameActivity.SOUND_SHOOT);

                    //적의 에너지 줄이기
                    e.energy -= 50;
                    //미사일 없애기
                    m.isDead = true;
                    //적의 에너지가 0 이하라면 적 제거
                    if(e.energy <= 0){
                        //e.isDead = true;
                        e.isFall = true; //바로 제거하지 않고 적기가 추락상태가 되도록 한다.
                        //점수 올리기
                        point += e.type == 0 ? 100 : 200;
                    }
                }
            }
        }
    }

    //적 관련 처리
    public void enemyService(){

        if(count%10 == 0){
            for(Enemy tmp:enemyList){
                tmp.imageIndex++;
                if(tmp.imageIndex == 2){
                    tmp.imageIndex = 0;
                }
            }
        }

        //임의의 시점에 적기가 5개 만들어지도록
        postCount++;
        eCount += ran.nextInt(10);
        //적 만들기
        if(eCount % 100 == 0 && postCount > 30){
            postCount = 0;
            for(int i = 0; i < 5; i++){
                Enemy tmp = new Enemy();
                tmp.x = enemyX[i]; //x 좌표는 배열에 미리 준비된 x 좌표
                tmp.y = unitSize/2; // 임시 y좌표
                tmp.type = ran.nextInt(2); //적기의 type은 0 또는 1
                tmp.energy = tmp.type == 0 ? 50 : 100;
                tmp.size = unitSize; // 적기의 초기 크기
                //만든 적기를 제거목록 에 담기
                enemyList.add(tmp);
            }

            if(eCount > 100){
                eCount = 0;
            }
        }

        //적 움직이기
        for(Enemy tmp:enemyList){
            if(tmp.isFall){
                //크기를 줄이고
                tmp.size -= 1;
                //회전값을 증가 시키디
                tmp.angle += 10;
                if(tmp.size <= 0){
                    tmp.isDead = true;
                }
            }else{
                tmp.y += speedMissile/3;
            }
        }

        for(int i = enemyList.size() - 1; i >= 0; i--){
            Enemy tmp = enemyList.get(i);
            if(tmp.isDead){
                enemyList.remove(i);
            }
        }
    }

    //View에 터치 이벤특 발생하면 호출되는 메소드
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //터치한곳의 좌표를 드래곤의 x좌표에 반영하기
        dragonX = (int)event.getX();
        return true;
    }

    Handler handler = new Handler(){
        @Override
        public void handleMessage(@NonNull Message msg) {
            //화면 갱신하기
            invalidate();
            //handler 객체에 빈 메세지를 20/1000초 이후에 다시 보내기
            handler.sendEmptyMessageDelayed(0, 20);
        }
    };
}
