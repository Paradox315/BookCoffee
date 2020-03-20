package com.cst.bookcoffee.activity.scanbook;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.baidu.ocr.sdk.OCR;
import com.baidu.ocr.sdk.OnResultListener;
import com.baidu.ocr.sdk.exception.OCRError;
import com.baidu.ocr.sdk.model.AccessToken;
import com.baidu.ocr.sdk.model.GeneralResult;
import com.baidu.ocr.ui.camera.CameraActivity;
import com.cst.bookcoffee.R;
import com.cst.bookcoffee.utils.JsonUtils;
import com.cst.bookcoffee.utils.PicUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.android.api.model.UserInfo;
import cn.jpush.im.api.BasicCallback;

public class ScanBookActivity extends Activity {
    final static String TAG = "OCR";
    private TextView result;
    private EditText et_result;
    private Button upload;
    private Button scan;
    private ImageView back;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_scan);
        result=findViewById(R.id.result);
        scan=findViewById(R.id.scanbook);
        et_result=findViewById(R.id.edt_result);
        upload=findViewById(R.id.uploadbook);
        back=findViewById(R.id.iv_back);

        OCR.getInstance(this).initAccessToken(new OnResultListener<AccessToken>() {
            @Override
            public void onResult(AccessToken result) {
                // 调用成功，返回AccessToken对象
                String token = result.getAccessToken();
                Log.d(TAG,result.toString());
            }
            @Override
            public void onError(OCRError error) {
                // 调用失败，返回OCRError子类SDKError对象
                Log.e(TAG,error.toString());
            }
        }, getApplicationContext());

        scan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 生成intent对象
                Intent intent = new Intent(getApplicationContext(), CameraActivity.class);

                // 设置临时存储
                intent.putExtra(CameraActivity.KEY_OUTPUT_FILE_PATH, PicUtil.getSaveFile(getApplication()).getAbsolutePath());

                // 调用除银行卡，身份证等识别的activity
                intent.putExtra(CameraActivity.KEY_CONTENT_TYPE, CameraActivity.CONTENT_TYPE_GENERAL);

                startActivityForResult(intent, 111);
            }
        });
        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UserInfo myinfo= JMessageClient.getMyInfo();
                String extra=myinfo.getExtra("book");
                String []extras=extra.split("\n");
                List<String> books = new ArrayList<>();
                books.addAll(Arrays.asList(extras));
                if(!TextUtils.isEmpty(et_result.getText().toString())){
                    String []res=et_result.getText().toString().trim().split("\n");
                    for(String line : res){
                        if(!books.contains("《"+line+"》"))
                            extra=extra+"《"+line+"》"+"\n";
                        else
                            Toast.makeText(getApplicationContext(),line+"已经在书库中，请不要重复添加",Toast.LENGTH_SHORT).show();
                    }
                    myinfo.setUserExtras("book",extra);
                    updateUserExtra(myinfo);
                }
                else {
                    Toast.makeText(getApplicationContext(),"请输入书名！",Toast.LENGTH_SHORT).show();
                }

            }
        });
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 111 && resultCode == Activity.RESULT_OK) {
            // 获取调用参数
            String contentType = data.getStringExtra(CameraActivity.KEY_CONTENT_TYPE);
            // 通过临时文件获取拍摄的图片
            String filePath = PicUtil.getSaveFile(getApplicationContext()).getAbsolutePath();


            OCRManager.recognizeAccurateBasic(this, filePath, new OCRManager.OCRCallBack<GeneralResult>() {
                @Override
                public void succeed(GeneralResult data) {
                    // 调用成功，返回GeneralResult对象
                    String content = OCRManager.getResult(data);
                    List<String> jsonList= JsonUtils.getJsonList(content);
                    if(jsonList!=null&&jsonList.size()>0){
                        result.append(String.format("获取的书名字符串共有：%d个",jsonList.size())+"\n"+"结果如下"+"\n");
                        for (String book:jsonList){
                            et_result.append(book);
                        }
                    }else {
                        result.setText("未识别到图书");
                    }
                    Log.d(TAG,content + "");
                }

                @Override
                public void failed(OCRError error) {
                    // 调用失败，返回OCRError对象
                    Log.e(TAG,"错误信息：" + error.getMessage());
                }
            });

        }
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
