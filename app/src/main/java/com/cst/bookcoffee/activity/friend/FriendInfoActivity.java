package com.cst.bookcoffee.activity.friend;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.cst.bookcoffee.R;
import com.cst.bookcoffee.activity.chat.ChatActivity;
import com.cst.bookcoffee.activity.conversation.ConversationActivity;

import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.android.api.callback.GetUserInfoCallback;
import cn.jpush.im.android.api.model.UserInfo;

public class FriendInfoActivity extends Activity {
    private TextView fri_name;
    private TextView fri_id;
    private TextView getbook;
    private ImageView fri_avatar;
    private ImageView btn_back;
    private Button chat;
    private SharedPreferences sp;
    private String USER_AVATAR="user_avatar";
    private String uid;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        String id=getIntent().getStringExtra("id");
        uid=id.substring(3);
        Log.d("contact_friend",uid);
        JMessageClient.getUserInfo(uid, new GetUserInfoCallback() {
            @Override
            public void gotResult(int i, String s, UserInfo userInfo) {
                if(i==0){
                    initData();
                    fri_id.setText("id:"+userInfo.getUserName());
                    fri_name.setText(userInfo.getNickname());
                    String p=sp.getString(userInfo.getUserName(),"");
                    fri_avatar.setImageBitmap(resizePic(decodePic(p)));
                }
            }
        });
    }
    private void initView() {
        setContentView(R.layout.activity_friend_info);
        fri_name=findViewById(R.id.friend_name);
        fri_id=findViewById(R.id.friend_id);
        fri_avatar=findViewById(R.id.friend_avatar);
        btn_back=findViewById(R.id.iv_back);
        chat=findViewById(R.id.btn_msg);
        getbook=findViewById(R.id.book_info);
    }
    private void initData() {
        sp=getApplicationContext().getSharedPreferences(USER_AVATAR,MODE_PRIVATE);
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        chat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), ChatActivity.class).putExtra("target",uid+" "+fri_name.getText().toString()));
                finish();
            }
        });
        getbook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(),FriendBookActivity.class).putExtra("id",uid));
                finish();
            }
        });
    }
    private Bitmap decodePic(String path){
        Bitmap bitmap=null;
        if (!path.equals("")) {
            byte[] bytes = Base64.decode(path.getBytes(), 1);
            //  byte[] bytes =headPic.getBytes();
            bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
        }
        return bitmap;
    }
    private Bitmap resizePic(Bitmap bitmap){
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        // 设置想要的大小
        int newWidth = 144;
        int newHeight = 144;
        // 计算缩放比例
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);
        Bitmap mbitmap = Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, true);
        return  mbitmap;
    }

}
