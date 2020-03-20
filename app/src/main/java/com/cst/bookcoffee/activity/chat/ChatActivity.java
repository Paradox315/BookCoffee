package com.cst.bookcoffee.activity.chat;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.cst.bookcoffee.R;
import com.cst.bookcoffee.adapter.ChatAdapter;

import java.util.ArrayList;
import java.util.List;

import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.android.api.content.TextContent;
import cn.jpush.im.android.api.enums.MessageDirect;
import cn.jpush.im.android.api.event.MessageEvent;
import cn.jpush.im.android.api.model.Conversation;
import cn.jpush.im.android.api.model.Message;
import cn.jpush.im.android.api.model.UserInfo;
import cn.jpush.im.api.BasicCallback;

public class ChatActivity extends Activity {
    private String target;
    private EditText edt_msg;
    private Button btn_send;
    private RecyclerView recyc;
    private ImageView btn_back;
    private TextView title;
    private Conversation conversation;
    private List<ChatItem> data=new ArrayList<>();
    private ChatAdapter adapter;
    private SharedPreferences sp;
    private String USER_AVATAR="user_avatar";
    private Bitmap receiver=null;
    private Bitmap sender=null;
    private UserInfo myinfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        JMessageClient.registerEventReceiver(this);
        initView();
        initData();
    }
    @Override
    public void onResume(){
        super.onResume();
        JMessageClient.enterSingleConversation(target);

    }
    @Override
    public void onPause(){
        super.onPause();
        JMessageClient.exitConversation();
    }
    @Override
    public void onDestroy(){
        super.onDestroy();
        JMessageClient.unRegisterEventReceiver(this);
    }
    private void initView(){
        setContentView(R.layout.activity_chat);
        edt_msg=findViewById(R.id.et_msg);
        btn_send=findViewById(R.id.btn_send);
        recyc =findViewById(R.id.msg_list);
        btn_back=findViewById(R.id.iv_back);
        title=findViewById(R.id.tv_target_account);
    }
    private void initData(){
        String s=getIntent().getStringExtra("target");
        String info[]=s.split(" ");
        title.setText(info[1]);
        target=info[0];
        sp=getApplicationContext().getSharedPreferences(USER_AVATAR,Context.MODE_PRIVATE);
        myinfo=JMessageClient.getMyInfo();
        String path_rec=sp.getString(target,"");
        String path_m=sp.getString(myinfo.getUserName(),"");
        if(decodePic(path_m)!=null){
            sender=resizePic(decodePic(path_m));
        }
        if(decodePic(path_rec)!=null){
            receiver=resizePic(decodePic(path_rec));
        }

        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        btn_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(TextUtils.isEmpty(edt_msg.getText().toString())){
                    showToast(getApplicationContext(),"请输入发送内容");
                }
                //Message msg=JMessageClient.createSingleTextMessage(target,edt_msg.getText().toString());
                final String content=edt_msg.getText().toString();
                TextContent textContent=new TextContent(content);
                Message message=conversation.createSendMessage(textContent);
                message.setOnSendCompleteCallback(new BasicCallback() {
                    @Override
                    public void gotResult(int i, String s) {
                        if(i==0){
                            data.add(new ChatItem(ChatItem.SEND,content));
                            adapter.notifyDataSetChanged();

                            recyc.scrollToPosition(data.size()-1);
                        }
                    }
                });
                JMessageClient.sendMessage(message);
                edt_msg.setText("");
            }
        });
        conversation=JMessageClient.getSingleConversation(target);
        if(conversation==null){
            conversation=Conversation.createSingleConversation(target);
        }
        if(conversation.getAllMessage()!=null){
            for (Message bean:conversation.getAllMessage()){
                if(bean.getDirect()==MessageDirect.send){
                    data.add(new ChatItem(ChatItem.SEND,((TextContent)bean.getContent()).getText()));
                }
                else {
                    data.add(new ChatItem(ChatItem.RECEIVE,((TextContent)bean.getContent()).getText()));
                }
            }
        }
        adapter=new ChatAdapter(data,sender,receiver);
        recyc.setLayoutManager(new LinearLayoutManager(this));
        recyc.setAdapter(adapter);
        recyc.scrollToPosition(data.size()-1);
    }
    public void onEvent(final MessageEvent event){
        runOnUiThread(
                new Runnable() {
                    @Override
                    public void run() {
                        if(event.getMessage().getDirect()== MessageDirect.send)
                            data.add(new ChatItem(ChatItem.SEND,((TextContent)event.getMessage().getContent()).getText()));
                        else
                            data.add(new ChatItem(ChatItem.RECEIVE,((TextContent)event.getMessage().getContent()).getText()));
                        adapter.notifyDataSetChanged();
                        recyc.scrollToPosition(data.size()-1);
                    }
                }
        );
    }
    private void showToast(Context context, String line){
        Toast.makeText(context,line,Toast.LENGTH_SHORT).show();
    }
    private Bitmap decodePic(String path){
        Bitmap bitmap=null;
        if (!path.equals("")) {
            byte[] bytes = Base64.decode(path.getBytes(), 1);
            //  byte[] bytes =headPic.getBytes();
            bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
            return bitmap;
        }
        else
            return null;
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
