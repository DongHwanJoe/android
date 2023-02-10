package com.example.step20contentprovider;

import android.Manifest;
import android.content.ContentResolver;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class MainActivity extends AppCompatActivity {

    EditText inputName, console;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        inputName = findViewById(R.id.inputName);
        console = findViewById(R.id.console);

        Button getBtn = findViewById(R.id.getBtn);
        getBtn.setOnClickListener(v -> {
            int permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS);

            if(permissionCheck != PackageManager.PERMISSION_GRANTED){
                //권한허용이 되지 않았다면 필요한 목록을 배열에 담는다
                String[] permission = {Manifest.permission.READ_CONTACTS};
                //배열을 전달해서 해당 권한을 부여하도록 요청
                ActivityCompat.requestPermissions(this, permission, 0);
                return;
            }
            //연락처 정보 얻어오기
            getContacts();
        });
    }

    //퍼미션 요청의 결과가 전달되는 메소드 재정의하기
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode){
            case 0 :
                if(grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    getContacts();
                }else{
                    Toast.makeText(this, "연락처 접근 권한이 필요합니다.", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    //contentPrvider로부터 ContentResolver 객체를 이용해서 연락처 정보를 얻어내는 메소드
    public void getContacts(){
        //입력한 검색어
        String ketword = inputName.getText().toString();
        //ContentResolver 객체의 참조값 얻기
        ContentResolver resolver = getContentResolver();
        //연락처 정보를 자칭할 수 있는 Uri 객체 얻기
        Uri contactUri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
        //select 할 칼럼
        String[] columns = {
                ContactsContract.CommonDataKinds.Phone.CONTACT_ID,
                ContactsContract.CommonDataKinds.Phone.NUMBER,
                ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME
        };

        //where display_name like '%키워드%'
        String where = ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME+" LIKE ?";
        //order by contact_id asc
        String order = ContactsContract.CommonDataKinds.Phone.CONTACT_ID+" ASC";
        // ? 에 바인딩 할 인자
        String[] args = {"%"+ketword+"%"};
        //원하는 정보를 얻어낸다.
        Cursor cursor = resolver.query(contactUri, //table name
                columns, // column nmae
                where, // where
                args, //selection args
                order); // order by

        while(cursor.moveToNext()){
            int id = (int)cursor.getLong(0);
            String phoneNumber = cursor.getString(1);
            String name = cursor.getString(2);

            //결과를 한 줄의 문자열로 구성
            String result = id + " | " + phoneNumber + " | " + name;
            console.append(result + "\n");
        }
    }
}