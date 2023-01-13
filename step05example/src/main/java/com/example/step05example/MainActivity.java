package com.example.step05example;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{
    //필드
    EditText editText;
    List<String> names;
    ArrayAdapter<String> adapter;
    ListView listView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        listView = findViewById(R.id.listView);

        Button addBtn = findViewById(R.id.addBtn);
        addBtn.setOnClickListener(this);

        names = new ArrayList<>();

        adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1,
                names);

        listView.setAdapter(adapter);
    }

    @Override
    public void onClick(View view) {
        editText = findViewById(R.id.editText);
        String msg = this.editText.getText().toString();
        names.add(msg);
        adapter.notifyDataSetChanged();
        editText.setText("");
    }
}