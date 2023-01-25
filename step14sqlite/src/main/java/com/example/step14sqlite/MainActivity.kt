package com.example.step14sqlite

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() , View.OnClickListener{

    //필요한 필드 정의하기

    //lateinit 예약어를 활용하면 null을 넣을 필요 없이 값을 나중에 넣을 수 있다.
    lateinit var inputText:EditText
    lateinit var listView:ListView
    lateinit var adapter: TodoAdapter
    lateinit var helper: DBHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //필요한 UI의 참조값을 얻어와서 필드에 저장하기
        inputText = findViewById(R.id.inputText)
        listView = findViewById(R.id.listView)
        //버튼 리스너 등록하기
        findViewById<Button>(R.id.addBtn).setOnClickListener(this)

        //DBHelper 객체의 참조값을 필드에 저장하기
        helper = DBHelper(this, "MyDB.sqlite", null, 1)
        //할 일 목록 얻어오기
        val list = TodoDao(helper).getList()
        //ListView 동작 준비하고, 할 일 목록 출력하기
        adapter = TodoAdapter(this, R.layout.listview_cell, list)
        //ListView에 Adapter 연결하기
        listView.adapter = adapter
    }

    override fun onClick(v: View?) {
        //1. 입력한 문자열을 읽어와서
        val msg = inputText.text.toString()
        //2. Todo 객체에 담기
        val data = Todo(0, msg, "")
        //3. TodoDao 객체를 이용해서 DB에 저장한다,
        TodoDao(helper).insert(data)
        //4. 목록을 새로 얻어오기
        val list = TodoDao(helper).getList()
        //5. Adapter에 넣기
        adapter.list = list
        //6. ListView가 업데이트 되도록 한다.
        adapter.notifyDataSetChanged()
        //7. Toast 띄우기
        Toast.makeText(this, "저장했습니다.", Toast.LENGTH_SHORT).show()
        inputText.setText("")
    }
}