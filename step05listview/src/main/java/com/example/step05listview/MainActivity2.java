package com.example.step05listview;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

public class MainActivity2 extends AppCompatActivity
        implements AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener {
    //필드
    List<String> names;
    ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //ListView의 참조값
        ListView listView = findViewById(R.id.listView);

        //Listview에 출력할 sample data
        names = new ArrayList<>();
        names.add("kim");
        names.add("lee");
        names.add("park");
        for(int i = 0; i < 100; i++){
            names.add("etc"+i);
        }

        //ListView에 연결할 아답타 객체
        //new ArrayAdapter<>(Context, layout resource, 모델)
        adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1,
                names);
        //ListView에 어뎁터 연결하기
        listView.setAdapter(adapter);
        //activity를 아이템 클릭 리스너로 등록하기
        listView.setOnItemClickListener(this);
        listView.setOnItemLongClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        // int i 는 클릭한 아이템의 인덱스가 들어있다.
        String name = names.get(i);
        Toast.makeText(this, name, Toast.LENGTH_SHORT).show();
    }

    //DialogInterface.OnClickListener type 필드
    DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialogInterface, int i) {
            //눌러진 버튼이 Negative 버튼인지 Positive 버튼인지 구별할 숫자값이 매개변수 i에 전달된다.
            if(i == DialogInterface.BUTTON_POSITIVE){
                //필드에 저장된 값을 활용해서 데이터를 삭제
                names.remove(selectedIndex);
                adapter.notifyDataSetChanged();
            }else if(i == DialogInterface.BUTTON_NEGATIVE){

            }
        }
    };

    int selectedIndex;

    @Override
    public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
        //롱 클릭 한 셀에 출력된 이름 읽어오기
        String name = names.get(i);
        //익명 클래스에서 사용가능하도록 필드에 참조값을 담아둔다.
        selectedIndex = i;

        new AlertDialog.Builder(this)
                .setTitle("alert")
                .setMessage(name+" delete?")
                .setNegativeButton("no", this.listener)//this는 생략 가능
                .setPositiveButton("yes", listener)
                .create()
                .show();
        //이벤트 전파를 여기서 막기
        return true;
    }
}