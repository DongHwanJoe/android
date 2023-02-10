package com.example.step19callphone;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class MainActivity extends AppCompatActivity {

    EditText inputPhone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button dialBtn = findViewById(R.id.dialBtn);
        inputPhone = findViewById(R.id.inputPhone);

        dialBtn.setOnClickListener(v -> {
            String phoneNumber = inputPhone.getText().toString();
            //전화를 걸겠다는 Intent(의도) 객체 생성
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_DIAL); // 전화의도를 Intent객체에 담기
            //전화번호룰 uri객체에 포장
            Uri uri = Uri.parse("tel:"+phoneNumber);
            //Intent 객체에 담기
            intent.setData(uri);
            //운영체제에 액티비티 실행 요청
            startActivity(intent);
        });

        Button callBtn = findViewById(R.id.callBtn);
        callBtn.setOnClickListener(v -> {
            //전화를 걸기전에 전화걸기 허용을 했는지 확인
            //전화걸기 권한이 체크 됐는지 상수값 얻기
            int permissionCheck = ContextCompat.checkSelfPermission(MainActivity.this,
                    Manifest.permission.CALL_PHONE);
            //만일 권한이 허용되지 않았다면
            if(permissionCheck != PackageManager.PERMISSION_GRANTED){
                //권한을 허용하도록 유도한다.

                //권한이 필요한 목록을 배열에 담는다.
                String[] permissions={android.Manifest.permission.CALL_PHONE};
                //배열을 전달해서 해당 권한을 부여하도록 요청한다.
                ActivityCompat.requestPermissions(MainActivity.this,
                        permissions,
                        0); //요청의 아이디
                return; //메소드는 여기서 종료
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == 0){
            //권한을 부여 받았다면
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED){
                //전화 걸기
            }else {//부여받지 않았다면
                Toast.makeText(this, "권한이 필요합니다.", Toast.LENGTH_SHORT).show();
            }
        }
    }
}