package com.example.step03view;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    //필드
    private EditText inputMsg;
    private TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // res/layout/activity_main.xml 문서를 전개해서 화면 구성하기
        setContentView(R.layout.activity_main);
        /*
            위의 xml문서를 전개하면
            ConstraintLayout, EditText, Button 객체가 생성된다.
            java code에서 해당 UI를 가지고 임의의 동작을 하려면 그 객체의 참조값이 필요하다.
         */

        // id를 이용해서 EditText 객체의 참조값 얻어오기
        inputMsg = findViewById(R.id.inputMsg);

        //id를 이용해서 Button 객체의 참조값 얻어오기
        Button sendBtn = findViewById(R.id.sendBtn);
        //버튼을 클릭했을 때 호출될 메소드를 가지고있는 View.OnClickListner type의 참조값 전달하기
        sendBtn.setOnClickListener(this);

        textView = findViewById(R.id.textView);
    }
    //버튼을 클릭하면 호출되는 메소드
    @Override
    public void onClick(View view) {
        //EditText에 입력한 문자열을 읽어와서 지역변수에 담기
        String msg = this.inputMsg.getText().toString();
        //숫자를 출력하고 시으면 아래와 같은 형태로 출력해야 한다.
        //textView.setText(Integer.toString(100));
        textView.setText(msg);
        // 빈 문자열을 입력해서 입력창 지우기
        inputMsg.setText("");
    }
}