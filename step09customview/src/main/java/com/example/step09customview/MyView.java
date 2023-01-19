package com.example.step09customview;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;

public class MyView extends View {
    //색상을 나타내는 상수값을 미리 int[]에 준비
    int[] colors = {Color.GREEN, Color.RED, Color.BLUE};
    //인덱스로 사용할 필드
    int index;
    //이미지의 x좌표
    int x = 100;

    //생성자1
    public MyView(Context context) {
        super(context);
    }
    //생성자2
    public MyView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    //view가 차지하고있는 화면에 Canvas 객체를 이용해서 그림 그리기(화면 구성하기)
    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawColor(colors[index]);

        //BitmapFactory 클래스의 static 메소드를 이용해서 이미지 로딩
        //Bitmap type으로 만들기
        Bitmap image = BitmapFactory.decodeResource(getResources(), R.drawable.korea);
        //Bitmap의 크기를 변환하기
        Bitmap scaledImage = Bitmap.createScaledBitmap(image, 100, 100, false);
        //canvas 객체를 이용해서 이미지의 좌상단의 좌표를 지정하고 그린다.
        canvas.drawBitmap(scaledImage, x, 100, null);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //인덱스를 1 증가시키고
        index++;
        //없는 인덱스라면
        if(index == 3){
            index = 0; // 0으로 초기화 하기
        }

        x += 10;

        //화면 갱신하는 메소드 호출 (결과적으로 view가 무효화되고 onDraw() 가 다시 호출됨)
        invalidate();
        return super.onTouchEvent(event);
    }
}
