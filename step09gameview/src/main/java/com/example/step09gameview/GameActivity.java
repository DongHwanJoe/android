package com.example.step09gameview;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class GameActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //GameView 객체를 생성
        GameView view = new GameView(this);
        //MainActivity의 화면을 GameView로 모두 채운다.
        setContentView(view);
    }

    //옵션 메뉴를 만드는 메소드
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //메뉴 전개자 객체를 얻어와서
        MenuInflater inflater = getMenuInflater();
        //res/menu/menu_option.xml 문서를 전개해서 메뉴로 구성한다.
        inflater.inflate(R.menu.menu_option, menu);
        return true;
    }

    //옵션 메뉴를 선택했을 때 호출되는 메소드
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.off: //off를 눌렀을 때 해야 할 동작

                break;
            case R.id.on: //on를 눌렀을 때 해야 할 동작

                break;
        }
        return super.onOptionsItemSelected(item);
    }
}