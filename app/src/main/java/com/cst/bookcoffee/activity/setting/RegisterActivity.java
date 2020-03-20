package com.cst.bookcoffee.activity.setting;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.cst.bookcoffee.R;

import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.android.api.options.RegisterOptionalUserInfo;
import cn.jpush.im.api.BasicCallback;

public class RegisterActivity extends Activity implements View.OnClickListener {
    private ImageView ivBack;
    private EditText edtUsername;
    private EditText edtPassword;
    private EditText edtNickname;
    private Button btnRegister;
    private RegisterOptionalUserInfo registerOptionalUserInfo;
    private ProgressDialog mProgressDialog = null;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        initView();

    }

    private void initView() {
        registerOptionalUserInfo=new RegisterOptionalUserInfo();
        ivBack = (ImageView) findViewById(R.id.iv_back);
        edtUsername = (EditText) findViewById(R.id.edt_username);
        edtPassword = (EditText) findViewById(R.id.edt_password);
        edtNickname = (EditText) findViewById(R.id.edt_nickname);

        btnRegister = (Button) findViewById(R.id.btn_register);

        btnRegister.setOnClickListener(this);
        ivBack.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        String username = edtUsername.getText().toString().trim();
        String password = edtPassword.getText().toString().trim();
        switch (v.getId()) {
            case R.id.btn_register:
                mProgressDialog = ProgressDialog.show(RegisterActivity.this, "提示：", "正在加载中。。。");
                if (!setRegisterOptionalParameters()) {
                    mProgressDialog.dismiss();
                    Toast.makeText(getApplicationContext(), "注册失败", Toast.LENGTH_SHORT).show();
                    return;
                }
                /**=================     调用SDK注册接口    =================*/
                JMessageClient.register(username, password, registerOptionalUserInfo,new BasicCallback() {
                    @Override
                    public void gotResult(int i, String s) {
                        if (i == 0) {
                            mProgressDialog.dismiss();
                            Toast.makeText(RegisterActivity.this, "注册成功！请登陆。", Toast.LENGTH_LONG).show();

                        } else {
                            mProgressDialog.dismiss();
                            Toast.makeText(RegisterActivity.this, "错误原因:" + s, Toast.LENGTH_LONG).show();
                        }

                    }
                });
                break;
            case R.id.iv_back:
                finish();
                break;

        }
    }
    private boolean setRegisterOptionalParameters() {
        registerOptionalUserInfo = new RegisterOptionalUserInfo();
        if (!TextUtils.isEmpty(edtNickname.getText())) {
            registerOptionalUserInfo.setNickname(edtNickname.getText().toString());
        }
        return true;
    }



}
