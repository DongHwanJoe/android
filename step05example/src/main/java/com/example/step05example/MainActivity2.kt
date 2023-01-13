package com.example.step05example

import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity

/*
    extends AppCompatActivity (상속) => : AppCompatActivity
    implements View.OnClickListener 인터페이스 구현 => , View.OnClickListener
 */
class MainActivity2 : AppCompatActivity(), View.OnClickListener {
    //null로 초기화 하기 위해서는 type 뒤에 ? 가 필요하다.
    var editText:EditText? = null
    var names:MutableList<String>? = null
    var adapter:ArrayAdapter<String>? = null
    lateinit var listView:ListView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        listView = findViewById(R.id.listView)
        editText = findViewById(R.id.editText)
        //findViewById의 type
        val addBtn = findViewById<Button>(R.id.addBtn)
        addBtn.setOnClickListener(this)
        //모델 생성
        names = mutableListOf()
        adapter = ArrayAdapter(this,
            android.R.layout.simple_list_item_1,
                names!!)
        /*
            [java]
            .setxxx(value)
            [kotlin]
            .xxx = value
         */
        listView.adapter = adapter
    }

    override fun onClick(p0: View?) {
        // editText?.text 는 editText 안에 값이 null이 아니면 .text를 참조하겠다는 의미
        val inputName:String = editText?.text.toString()
        names?.add(inputName)
        adapter?.notifyDataSetChanged()
        editText?.setText("")
    }
}