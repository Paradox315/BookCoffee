package com.cst.bookcoffee.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.GlideDrawableImageViewTarget;
import com.cst.bookcoffee.R;
import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.android.api.model.UserInfo;


public class WelcomeActivity extends Activity {
    private Context mContext;
    private ImageView mImage;
    private Button skip;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        mImage=findViewById(R.id.welcome_image);
        skip=findViewById(R.id.jump);
        skip.getBackground().setAlpha(75);
        mContext = this;
        loadingGif();
        initData();
    }

    private void initData() {
        final Thread wel=new WelThread();
        wel.start();
        skip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                wel.interrupt();
                UserInfo myInfo = JMessageClient.getMyInfo();
                if (myInfo == null) {
                    goToRegisterAndLoginActivity();
                }else {
                    goToMainActivity();
                }
                finish();
            }
        });

    }
    private void loadingGif(){
        Glide.with(this).load(R.drawable.loading).into(new GlideDrawableImageViewTarget(mImage,1)); //加载一次
    }

    private void goToMainActivity() {
        startActivity(new Intent(mContext, MainActivity.class).putExtra("id",0));
        finish();
    }

    private void goToRegisterAndLoginActivity() {
        startActivity(new Intent(mContext, LoginActivity.class));
        finish();
    }
    public class WelThread extends Thread{
        public void run() {
            try {
                //等待
                sleep(4000);
                //跳转进入APP主页面
                //检测账号是否登陆
                UserInfo myInfo = JMessageClient.getMyInfo();
                if (myInfo == null) {
                    goToRegisterAndLoginActivity();
                }else {
                    goToMainActivity();
                }
                finish();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
