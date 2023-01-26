package com.example.step14sqlite

import android.content.DialogInterface
import android.os.Bundle
import android.view.View
import android.widget.*
import android.widget.AdapterView.OnItemLongClickListener
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() , View.OnClickListener, OnItemLongClickListener{

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
        //version 값이 증가되면 onUpgrade() 메소드가 자동으로 호출되면서 db가 초기화 된다.
        helper = DBHelper(this, "MyDB.sqlite", null, 2)
        //할 일 목록 얻어오기
        val list = TodoDao(helper).getList()
        //ListView 동작 준비하고, 할 일 목록 출력하기
        adapter = TodoAdapter(this, R.layout.listview_cell, list)
        //ListView에 Adapter 연결하기
        listView.adapter = adapter
        //listView에 LongClickListener 등록하기
        listView.setOnItemLongClickListener(this)
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
        //8. ListView의 가장 아래쪽이 화면에 보이도록 부드럽게 스크롤 시키기
        listView.smoothScrollToPosition(adapter.count)
    }

    //ListView의 cell을 LongClick 시 호출되는 메소드
    override fun onItemLongClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long): Boolean {
        // position은 클릭한 cell의 인덱스
        // id는 클릭한 cell의 아이디 (Todo의 primary key)
        // id에 전달되는 값은 TodoAdapter의 getItemId() 메소드에서 리턴한 값

        AlertDialog.Builder(this)
                .setTitle("알림")
                .setMessage("삭제 하시겠습니까?")
                .setPositiveButton("네", DialogInterface.OnClickListener { dialog, which ->
                    val dao = TodoDao(helper)
                    dao.delete(id.toInt())
                    //목록을 새로 얻기
                    val list = TodoDao(helper).getList()
                    //Adapter에 넣고
                    adapter.list = list
                    //ListView가 업데이트 되도록 한다.
                    adapter.notifyDataSetChanged()
                })
                .setNegativeButton("아니오", null)
                .create()
                .show()

        return false
    }
}