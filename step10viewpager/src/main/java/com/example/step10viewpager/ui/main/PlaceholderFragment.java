package com.example.step10viewpager.ui.main;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.step10viewpager.databinding.FragmentMainBinding;

/**
 * A placeholder fragment containing a simple view.
 */
public class PlaceholderFragment extends Fragment {

    private PageViewModel pageViewModel;
    private FragmentMainBinding binding;

    //인자로 전달하는 인덱스에 해당되는 새 Fragment(PlaceholderFragment) 객체를 리턴하는 메소드
    public static PlaceholderFragment newInstance(String ownerName) {
        //Fragment 객체를 생성
        PlaceholderFragment fragment = new PlaceholderFragment();
        //Bundle 객체를 생성
        Bundle bundle = new Bundle();
        //"ownerName"이라는 키값으로 전달된 이름을 담고
        bundle.putString("ownerName", ownerName);
        //Fragment 에 전달
        fragment.setArguments(bundle);
        //해당 Fragment객체를 리턴
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //PageViewModel을 사용할 준비
        pageViewModel = new ViewModelProvider(this).get(PageViewModel.class);

        //Fragment에 전달받은 인자(Bundle)를 얻어낸다.
        Bundle bundle = getArguments();
        //Bundle 객체에 "ownerName" 이라는 키 값으로 담겨있는 이름을 얻어낸다.
        String ownerName = bundle.getString("ownerName");
        //MutableLiveData를 수정한다.
        pageViewModel.setOwnerName(ownerName);
    }

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {

        binding = FragmentMainBinding.inflate(inflater, container, false);
        // fragment_main.xml 문서를 전개해서 만든 View의 참조값
        View root = binding.getRoot();

        //TextView의 참조값을 얻어와서
        final TextView textView = binding.sectionLabel;
        //PageView 모델이 가지고 있는 데이터를 관찰하다 변경되면 UI를 업데이트할 옵저버 등록
        //이 뷰의 주인(fragment or activity) 가 활성화된 상태에서만 동작하겠다는 의미
        pageViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                //TextView에 출력하기
                textView.setText(s);
            }
        });

        //버튼을 눌렀을 때 동작할 리스너 등록
        binding.changeBtn.setOnClickListener(view -> {
            //입력한 이름을 읽어와서
            String newName = binding.inputName.getText().toString();
            //PageViewModel이 가지고있는 라이브 데이터를 업데이트한다.
            pageViewModel.setOwnerName(newName);
        });

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}