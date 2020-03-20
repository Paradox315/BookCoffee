package com.cst.bookcoffee.activity.friend;


import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.cst.bookcoffee.R;
import com.cst.bookcoffee.adapter.BookAdapter;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.android.api.callback.GetUserInfoCallback;
import cn.jpush.im.android.api.model.UserInfo;

public class FriendBookActivity extends Activity {
    private ImageView back;

    private TextView warn;
    private RecyclerView book_list;
    private LinearLayoutManager layoutManager;
    private List<String> books=new LinkedList<>();
    private String extra;
    private String id;
    private boolean isFirst=true;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        id=getIntent().getStringExtra("id");
        JMessageClient.getUserInfo(id, new GetUserInfoCallback() {
            @Override
            public void gotResult(int i, String s, UserInfo userInfo) {
                if(i==0){
                    initView();
                    extra = userInfo.getExtra("book");
                    if(extra!=null){
                        String[] extras= userInfo.getExtra("book").split("\n");
                        books.addAll(Arrays.asList(extras));
                        book_list.setLayoutManager(layoutManager);
                        if(isFirst){
                            book_list.addItemDecoration(new DividerItemDecoration(getApplicationContext(), DividerItemDecoration.VERTICAL));
                            Log.d("draw","fgm2");
                            isFirst=false;
                        }
                        final BookAdapter bookAdapter=new BookAdapter(getApplicationContext(),books);
                        book_list.setAdapter(bookAdapter);

                    }
                    else {
                        warn.setVisibility(View.VISIBLE);
                    }
                    initData();
                }
            }
        });
    }
    private void initView() {
        setContentView(R.layout.activity_book_friend_info);
        back=findViewById(R.id.iv_back);
        book_list=findViewById(R.id.book_list);
        layoutManager=new LinearLayoutManager(this);
        warn=findViewById(R.id.warn);
    }
    private void initData() {


        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });


    }

}

