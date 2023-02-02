package com.example.step17example;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

public class TodoAdapter extends BaseAdapter {

    private Context context;
    private int layoutRes;
    private List<TodoDto> list;

    public TodoAdapter(Context context, int layoutRes, List<TodoDto>list){
        this.context = context;
        this.layoutRes = layoutRes;
        this.list = list;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return list.get(position).getNum();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if(convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(context);
            //해당 문서의 리소스 아이디는 생성자의 인자로 전달되어 필드에 저장해 놓은 상태
            convertView = inflater.inflate(layoutRes, parent, false);
        }

        //View에 모델이 가지고있는 데이터를 출력
        TodoDto dto = list.get(position);

        //listview_cell.xml 안에 있는 TextView의 참조값을 얻어와서 문자열 출력하기
        TextView text_content = convertView.findViewById(R.id.text_content);
        TextView text_regdate = convertView.findViewById(R.id.text_regdate);

        text_content.setText(dto.getContent());
        text_regdate.setText(dto.getRegdate());

        return convertView;
    }
}
