package com.example.step17httprequest3

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import org.json.JSONArray
import org.json.JSONObject

class MainActivity : AppCompatActivity() , Util.RequestListener{
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //EditText 객체의 참조값 얻어오기
        val editText = findViewById<EditText>(R.id.inputMsg)
        //Button 객체의 참조값 얻어오기
        val sendBtn = findViewById<Button>(R.id.sendBtn)
        sendBtn.setOnClickListener {
            //EditText에 입력한 문자열을 읽어와서
            val msg = editText.text.toString()
            Util.sendPostRequest(
                    999,
                    "http://192.168.0.29:9000/boot07/api/send",
                    mapOf("msg" to msg), //"msg" 라는 키 값으로 입력한 메세지를 담은 Map
                    this
            )
        }

        val getListBtn = findViewById<Button>(R.id.getListBtn)
        getListBtn.setOnClickListener {
            Util.sendGetRequest(
                    1000,
                    "http://192.168.0.29:9000/boot07/api/list",
                    mapOf("pageNum" to "1"),
                    this
            )
        }
    }

    override fun onSuccess(requestId: Int, result: Map<String, Any?>?) {
        if(requestId == 999){
            //웹서버가 응답한 json 문자열 {}
            var jsonStr = result?.get("data").toString()
            Log.d("#### json 문자열 ####", jsonStr)
            // JSONObject 객체를 생성하면서 생성자의 인자로 json 문자열을 전달
            val obj = JSONObject(jsonStr)
            val isSuccess = obj.getBoolean("isSuccess")
            val response = obj.getString("response")
            val num = obj.getInt("num")
        }else if(requestId == 1000){
            val jsonStr = result?.get("data").toString()
            Log.d("#### json 문자열 ####", jsonStr)
            val arr = JSONArray(jsonStr)
            //i 값을 0에서 부터 JSONArray의 사이즈 -1 까지 변화시키며 반복
            for(i in 0 .. arr.length() - 1){
                val tmp = arr.getString(i)
                Log.d("json array", tmp)
            }
        }
    }

    override fun onFail(requestId: Int, result: Map<String, Any?>?) {

    }
}