package com.example.step10viewpager;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.example.step10viewpager.databinding.ActivityMainBinding;
import com.example.step10viewpager.ui.main.SectionsPagerAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //view binding을 이용해서 화면 구성하기
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        //ViewPager에 연결할 Adapter 객체 생성
        SectionsPagerAdapter sectionsPagerAdapter = new SectionsPagerAdapter(this, getSupportFragmentManager());
        //ViewPager의 참조값 얻기
        ViewPager viewPager = binding.viewPager;
        //Adapter 연결
        viewPager.setAdapter(sectionsPagerAdapter);

        //상단텝의 참조값 얻기
        TabLayout tabs = binding.tabs;
        //ViewPage와 연동되도록 연결
        tabs.setupWithViewPager(viewPager);
        //Action버튼의 참조값 얻기
        FloatingActionButton fab = binding.fab;
        //해당 버튼의 클릭 여부를 알기 위한 리스너
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //하단에서 잠시 올라왔다가 사라지는 Snakebar 띄우기기
               Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }
}