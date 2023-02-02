package com.example.step17httprequest;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity {
    EditText editText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editText = findViewById(R.id.editText);

        EditText inputUrl = findViewById(R.id.inputUrl);
        //요청 버튼을 클릭 했을 때 동작할 준비
        Button requestBtn = findViewById(R.id.requestBtn);
        requestBtn.setOnClickListener(v -> {
            //입력한 요청 url을 읽어온다.
            String url = inputUrl.getText().toString();
            // http 요청은 시간이 오래걸릴 수 있는 불확실한 작업이다 => 비동기 task에서 작업해야 한다.
            new RequestTask().execute(url);
        });
    }

    class RequestTask extends AsyncTask<String, Void, String>{

        @Override
        protected String doInBackground(String... strings) {
            //문자열을 누적시킬 객체
            StringBuilder builder = new StringBuilder();

            //strings의 0번 방에 요청 url이 들어있다.
            try {
                //요청 url을 생성자의 인자로 전달해서 URL 객체를 생성한다.
                URL url = new URL(strings[0]);
                //URLConnection 객체를 원래 type (자식 type)으로 casting
                HttpURLConnection conn = (HttpURLConnection)url.openConnection();

                //정상적으로 연결이 됐다면
                if(conn != null){
                    conn.setConnectTimeout(20000); //응답을 기다리는 최대 대기 시간
                    conn.setRequestMethod("GET"); //요청 메소드 설정 (Default는 GET)
                    conn.setUseCaches(false); //캐쉬 사용 여부
                    //응답 코드를 읽어온다.
                    int responseCode = conn.getResponseCode();
                    if(responseCode == HttpURLConnection.HTTP_OK){ //(200번 코드)정상 응답이라면
                        //문자열을 읽어들일 수 있는 객체의 참조값 얻어오기
                        BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));

                        while (true) {
                            //문자열을 한 줄씩 읽어들인다.
                            String line = br.readLine();
                            if(line == null)break;
                            //StringBuilder 객체에 읽어들인 문자열을 누적 시킨다.
                            builder.append(line);
                        }
                    }
                }

            } catch (Exception e) {
                Log.e("RequestTask 클래스", e.getMessage());
            }
            //StringBuilder 객체에 누적된 문자열을 String type으로 한 번에 얻어내서 리턴해준다.
            return builder.toString();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            editText.setText(s);
        }
    }

}