package com.cst.bookcoffee.application;

import android.app.Application;
import android.util.Log;

import com.baidu.ocr.sdk.OCR;
import com.baidu.ocr.sdk.OnResultListener;
import com.baidu.ocr.sdk.exception.OCRError;
import com.baidu.ocr.sdk.model.AccessToken;
import com.cst.bookcoffee.Tools.GlobalEventListener;

import cn.jpush.im.android.api.JMessageClient;

public class BC_Application extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Log.i("IMDebugApplication", "init");
        JMessageClient.setDebugMode(true);
        JMessageClient.init(getApplicationContext(), true);
        //注册全局事件监听类
        JMessageClient.registerEventReceiver(new GlobalEventListener(getApplicationContext()));
        /*OCR调用
        OCR.getInstance(this).initAccessTokenWithAkSk(new OnResultListener<AccessToken>() {
            @Override
            public void onResult(AccessToken result) {
                // 调用成功，返回AccessToken对象
                String token = result.getAccessToken();
            }
            @Override
            public void onError(OCRError error) {
                // 调用失败，返回OCRError子类SDKError对象
            }
        }, getApplicationContext(), "si48qjzl9UnFxnGWIwxALrjI", "5HYTG0aNuNk5hGAjxPpwXmlkv68nZ2QI");*/
    }
}
