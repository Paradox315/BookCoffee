package com.cst.bookcoffee.activity.setting;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.cst.bookcoffee.R;
import com.cst.bookcoffee.activity.MainActivity;

import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.android.api.callback.GetAvatarBitmapCallback;
import cn.jpush.im.android.api.model.UserInfo;
import cn.jpush.im.api.BasicCallback;

import static com.cst.bookcoffee.utils.AndroidUtils.TAG;

public class GetMyInfoActivity extends Activity {
    private TextView mID;
    private TextView mName;
    private View v1;
    private View v2;
    private ImageView mAvatar;
    private ImageView iv_back;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        initData();
    }

    private void initView() {
        setContentView(R.layout.activity_my_info);
        mID=findViewById(R.id.my_id);
        mName=findViewById(R.id.my_nickname);
        mAvatar=findViewById(R.id.my_avatar);
        iv_back=findViewById(R.id.iv_back);
        v1=findViewById(R.id.click1);
        v2=findViewById(R.id.click2);
    }
    private void initData() {
        final UserInfo mUserInfo= JMessageClient.getMyInfo();
        mName.setText(mUserInfo.getNickname());
        mID.setText(mUserInfo.getUserName());

        v1.setOnClickListener(new mUpdateAvatarListener());
        mAvatar.setOnClickListener(new mUpdateAvatarListener());
        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        mName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final EditText input = new EditText(GetMyInfoActivity.this);
                AlertDialog.Builder builder = new AlertDialog.Builder(GetMyInfoActivity.this);
                builder.setTitle("更新昵称")
                        .setIcon(android.R.drawable.ic_dialog_info)
                        .setView(input)
                        .setNegativeButton("取消", null);
                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        mUserInfo.setNickname(input.getText().toString());
                        updateUserInfo(mUserInfo);
                        mName.setText(input.getText().toString());
                    }
                });
                builder.show();
            }
        });
        v2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(),GetBookInfoActivity.class));
            }
        });

        mUserInfo.getAvatarBitmap(new GetAvatarBitmapCallback() {
            @Override
            public void gotResult(int i, String s, Bitmap bitmap) {
                if (i==0){
                    mAvatar.setImageBitmap(bitmap);
                }
            }
        });
    }
    class mUpdateAvatarListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            startActivity(new Intent(getApplicationContext(),UpdateUserAvatar.class));
            finish();
        }
    }
    private void updateUserInfo(UserInfo userInfo) {
        final ProgressDialog mProgressDialog = ProgressDialog.show(GetMyInfoActivity.this, "提示：", "正在加载中……");
        mProgressDialog.setCanceledOnTouchOutside(true);
        JMessageClient.updateMyInfo(UserInfo.Field.nickname, userInfo, new BasicCallback() {
            @Override
            public void gotResult(int responseCode, String responseMessage) {
                mProgressDialog.dismiss();
                Log.d(TAG, "responseCode: " + responseCode + "responseMessage: " + responseMessage);
                String result = 0 == responseCode ? "更新成功" : "更新失败";
                Toast.makeText(GetMyInfoActivity.this, result, Toast.LENGTH_SHORT).show();
            }
        });
    }

}
