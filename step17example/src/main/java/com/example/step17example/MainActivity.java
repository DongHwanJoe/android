package com.example.step17example;

import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.step17example.databinding.ActivityMainBinding;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
/*
    view binding 사용법
    1. build.gradle 파일에 아래의 설정 추가
        buildFeatures {
            viewBinding = true
        }
    2. gradle sync (우상단 sync now)로 설정 적용
    3. Layout xml 문서에 이름대로 클래스가 만들어진다.
        activity_main.xml  ->  ActivityMainBinding
        activity_sub.xml   ->  ActivitySubBinding
 */
public class MainActivity extends AppCompatActivity implements Util.RequestListener, AdapterView.OnItemLongClickListener {

    List<TodoDto> todo = new ArrayList<>();
    TodoAdapter adapter;
    ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //setContentView(R.layout.activity_main);
        //R.layout.activity_main.xml 문서를 전개해서 View를 만들기
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        //전개된 layout에서 root를 얻어내서 화면을 구성한다. (여기서는 Linearlayout)
        setContentView(binding.getRoot());

        adapter = new TodoAdapter(this, R.layout.listview_cell, todo);
        binding.listView.setAdapter(adapter);

        binding.addBtn.setOnClickListener(v -> {
            String content = binding.inputText.getText().toString();
            Map<String, String> map = new HashMap<>();
            map.put("content", content);
            Util.sendPostRequest(
                    AppConstants.REQUEST_TODO_INSERT,
                    AppConstants.BASE_URL+"/todo/insert",
                    map,
                    this
            );
            binding.inputText.setText("");
        });

        binding.listView.setOnItemLongClickListener(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        //MainActivity가 활성화되는 시점에 List 불러오기
        Util.sendGetRequest(
                AppConstants.REQUEST_TODO_LIST,
                AppConstants.BASE_URL+"/todo/list",
                null,
                this
        );
    }

    @Override
    public void onSuccess(int requestId, Map<String, Object> result) {
        String jsonStr = (String)result.get("data");

        if (requestId == AppConstants.REQUEST_TODO_INSERT){
            Log.d("MainActivity onSuccess()", jsonStr);
            Util.sendGetRequest(
                    AppConstants.REQUEST_TODO_LIST,
                    AppConstants.BASE_URL+"/todo/list",
                    null,
                    this
            );
            binding.listView.smoothScrollToPosition(adapter.getCount());
        }else if(requestId == AppConstants.REQUEST_TODO_LIST){
            todo.clear();
            try {
                JSONArray arr = new JSONArray(jsonStr);
                for(int i = 0; i < arr.length(); i++){
                    JSONObject tmp = arr.getJSONObject(i);

                    int num = tmp.getInt("num");
                    String content = tmp.getString("content");
                    String regdate = tmp.getString("regdate");
                    todo.add(new TodoDto(num, content, regdate));
                }
                adapter.notifyDataSetChanged();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }else if(requestId == AppConstants.REQUEST_TODO_DELETE){
            Util.sendGetRequest(
                    AppConstants.REQUEST_TODO_LIST,
                    AppConstants.BASE_URL+"/todo/list",
                    null,
                    this
            );
        }
    }

    @Override
    public void onFail(int requestId, Map<String, Object> result) {
        if(requestId == AppConstants.REQUEST_TODO_INSERT){

        }else if(requestId == AppConstants.REQUEST_TODO_LIST){

        }else if(requestId == AppConstants.REQUEST_TODO_DELETE){

        }
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        String num = Long.toString(id);

        new AlertDialog.Builder(this)
                .setTitle("알림")
                .setMessage("삭제하시겠습니까?")
                .setPositiveButton("네", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Map<String, String> map = new HashMap<>();
                        map.put("num", num);
                        Util.sendPostRequest(
                                AppConstants.REQUEST_TODO_DELETE,
                                AppConstants.BASE_URL+"/todo/delete",
                                map,
                                MainActivity.this
                        );
                    }
                })
                .setNegativeButton("아니오", null)
                .create()
                .show();
        return false;
    }
}