package com.cst.bookcoffee.activity.setting;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.cst.bookcoffee.R;
import com.cst.bookcoffee.activity.friend.FriendInfoActivity;
import com.cst.bookcoffee.adapter.BookAdapter;
import com.cst.bookcoffee.adapter.ContactAdapter;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.android.api.model.UserInfo;
import cn.jpush.im.api.BasicCallback;

import static cn.jmessage.biz.httptask.task.AbstractTask.TAG;

public class GetBookInfoActivity extends Activity {
    private ImageView back;
    private ImageView clear;
    private TextView warn;
    private UserInfo myInfo;
    private RecyclerView book_list;
    private LinearLayoutManager layoutManager;
    private List<String> books=new LinkedList<>();
    private String extra;
    private boolean isFirst=true;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        initData();
    }
    private void initView() {
        setContentView(R.layout.activity_book_info);
        back=findViewById(R.id.iv_back);
        book_list=findViewById(R.id.book_list);
        myInfo=JMessageClient.getMyInfo();
        layoutManager=new LinearLayoutManager(this);
        warn=findViewById(R.id.warn);
        clear=findViewById(R.id.clear);
    }
    private void initData() {
        extra=myInfo.getExtra("book");
        if(extra!=null){
            String[] extras=myInfo.getExtra("book").split("\n");
            books.addAll(Arrays.asList(extras));
            book_list.setLayoutManager(layoutManager);
            if(isFirst){
                book_list.addItemDecoration(new DividerItemDecoration(getApplicationContext(), DividerItemDecoration.VERTICAL));
                Log.d("draw","fgm2");
                isFirst=false;
            }
            final BookAdapter bookAdapter=new BookAdapter(getApplicationContext(),books);
            book_list.setAdapter(bookAdapter);
            bookAdapter.setOnItemClickListener(new ContactAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(final View view, int position) {

                    final AlertDialog.Builder builder=new AlertDialog.Builder(GetBookInfoActivity.this);
                    builder.setTitle("要删除这本书吗？")
                            .setIcon(android.R.drawable.ic_dialog_info)
                            .setNegativeButton("取消", null)
                            .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    String name=((TextView)view).getText().toString();
                                    books.remove(name);
                                    extra=delete(extra,name);
                                    Log.d("delete",extra);
                                    myInfo.setUserExtras("book",extra);
                                    updateUserExtra(myInfo);
                                    bookAdapter.notifyDataSetChanged();
                                }
                            });
                    builder.show();
                }

                @Override
                public void onItemLongClick(View view, int position) {
                }
            });
        }
        else {
            warn.setVisibility(View.VISIBLE);
            clear.setVisibility(View.INVISIBLE);
        }

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final AlertDialog.Builder builder=new AlertDialog.Builder(GetBookInfoActivity.this);
                builder.setTitle("要清空个人书库吗？")
                        .setIcon(android.R.drawable.ic_dialog_info)
                        .setNegativeButton("取消", null)
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                myInfo.setUserExtras("book",null);
                                updateUserExtra(myInfo);
                                book_list.setVisibility(View.INVISIBLE);
                                warn.setVisibility(View.VISIBLE);
                            }
                        });
                builder.show();
            }
        });

    }
    private String delete(String var1,String var2){
        int index=var1.indexOf(var2);
        return var1.substring(0,index)+var1.substring(index+var2.length()+1);
    }
    private void updateUserExtra(UserInfo userInfo) {
        JMessageClient.updateMyInfo(UserInfo.Field.extras, userInfo, new BasicCallback() {
            @Override
            public void gotResult(int responseCode, String responseMessage) {
                Log.d(TAG, "responseCode: " + responseCode + "responseMessage: " + responseMessage);
                String result = 0 == responseCode ? "更新成功" : "更新失败";
                Toast.makeText(getApplicationContext(), result, Toast.LENGTH_SHORT).show();
            }
        });
    }

}
