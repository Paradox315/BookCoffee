package com.cst.bookcoffee.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.cst.bookcoffee.R;
import com.cst.bookcoffee.Tools.WordReplacement;
import com.cst.bookcoffee.activity.setting.RegisterActivity;

import java.util.ArrayList;

import java.util.Locale;

import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.api.BasicCallback;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener{
    private EditText edtUsername;
    private EditText edtPassword;
    private Button btnLogin;
    private TextView btnRegister;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        initPermission();
        initView();
    }
    /**
     * android 6.0 以上需要动态申请权限
     */
    private void initPermission() {
        String permissions[] = {
                android.Manifest.permission.RECORD_AUDIO,
                android.Manifest.permission.READ_PHONE_STATE,
                android.Manifest.permission.ACCESS_NETWORK_STATE,
                android.Manifest.permission.READ_EXTERNAL_STORAGE,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                android.Manifest.permission.MODIFY_AUDIO_SETTINGS,
                android.Manifest.permission.ACCESS_WIFI_STATE,
                android.Manifest.permission.WAKE_LOCK,
                Manifest.permission.RECEIVE_BOOT_COMPLETED
        };

        ArrayList<String> toApplyList = new ArrayList<String>();

        for (String perm : permissions) {
            if (PackageManager.PERMISSION_GRANTED != ContextCompat.checkSelfPermission(this, perm)) {
                toApplyList.add(perm);
                //进入到这里代表没有权限.
                Log.e("--------->", "没有权限");
            } else {

                Log.e("--------->", "已经被授权");
            }
        }
        String tmpList[] = new String[toApplyList.size()];
        if (!toApplyList.isEmpty()) {
            ActivityCompat.requestPermissions(this, toApplyList.toArray(tmpList), 123);
        }

    }
    private void initView() {

        edtUsername = (EditText) findViewById(R.id.edt_username);
        edtPassword = (EditText) findViewById(R.id.edt_password);
        btnLogin = (Button) findViewById(R.id.btn_login);
        btnRegister = (TextView) findViewById(R.id.btn_register);

        btnLogin.setOnClickListener(this);
        btnRegister.setOnClickListener(this);

        edtPassword.setTransformationMethod(new WordReplacement());

    }

    @Override
    public void onClick(View v) {
        final String username = edtUsername.getText().toString().trim();
        final String password = edtPassword.getText().toString().trim();
        Log.d("pwd",password);

        switch (v.getId()) {
            case R.id.btn_login:
                final ProgressDialog mProgressDialog = ProgressDialog.show(LoginActivity.this, "提示：", "正在加载中……");
                mProgressDialog.setCanceledOnTouchOutside(true);
                if (!TextUtils.isEmpty(username) && !TextUtils.isEmpty(password)) {

                    JMessageClient.login(username, password, new BasicCallback() {
                        @Override
                        public void gotResult(int i, final String s) {
                            Log.d("result",String.valueOf(i));
                            mProgressDialog.dismiss();
                            switch (i) {
                                case 801003:
                                    showToast(LoginActivity.this, "用户名不存在");
                                    break;
                                case 871301:
                                    showToast(LoginActivity.this, "密码格式错误");
                                    break;
                                case 801004:
                                    showToast(LoginActivity.this, "密码错误");
                                    break;
                                case 0:
                                    Log.d("pwd",password);
                                    showToast(LoginActivity.this, "登录成功");
                                    startActivity(new Intent(LoginActivity.this, MainActivity.class).putExtra("id",0));
                                    finish();
                                    break;
                                default:

                                    break;
                            }
                        }
                    });
                }else{
                    Log.d("state","fail");
                    Toast.makeText(this, "用户名和密码不能为空", Toast.LENGTH_LONG).show();
                }
                break;
            case R.id.btn_register:
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
                break;
        }
    }
    public static String getAppKey(Context context) {
        Bundle metaData = null;
        String appkey = null;
        try {
            ApplicationInfo ai = context.getPackageManager().getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA);
            if (null != ai) {
                metaData = ai.metaData;
            } else {
                return null;
            }
        } catch (PackageManager.NameNotFoundException e) {
            return null;
        }
        if (null != metaData) {
            appkey = metaData.getString("JPUSH_APPKEY");
            if (TextUtils.isEmpty(appkey)) {
                return null;
            } else if (appkey.length() != 24) {
                return null;
            }
            appkey = appkey.toLowerCase(Locale.getDefault());
        }
        return appkey;
    }
    private void showToast(Context context,String line){
        Toast.makeText(context,line,Toast.LENGTH_SHORT).show();
    }

}
