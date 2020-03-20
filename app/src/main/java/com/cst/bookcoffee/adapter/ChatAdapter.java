package com.cst.bookcoffee.adapter;


import android.graphics.Bitmap;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.Nullable;

import com.chad.library.adapter.base.BaseMultiItemQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.cst.bookcoffee.R;
import com.cst.bookcoffee.activity.chat.ChatItem;

import java.util.List;


public class ChatAdapter extends BaseMultiItemQuickAdapter<ChatItem, BaseViewHolder> {
    Bitmap sender;
    Bitmap receiver;
    public ChatAdapter(@Nullable List<ChatItem> data,Bitmap sender,Bitmap receiver) {
        super(data);
        addItemType(ChatItem.SEND, R.layout.item_chat_send);
        addItemType(ChatItem.RECEIVE,R.layout.item_chat_receive);
        this.sender=sender;
        this.receiver=receiver;
    }

    @Override
    protected void convert(BaseViewHolder helper,ChatItem item) {

        switch (helper.getItemViewType()){
            case ChatItem.SEND:
                if(sender!=null)
                    helper.setImageBitmap(R.id.me,sender);
                if(TextUtils.isEmpty(item.getContent())){
                    helper.setText(R.id.tv_content," ");
                }else{
                    helper.setText(R.id.tv_content,item.getContent());
                }
                break;

            case ChatItem.RECEIVE:
                if(receiver!=null)
                    helper.setImageBitmap(R.id.target,receiver);
                if(TextUtils.isEmpty(item.getContent())){
                    helper.setText(R.id.tv_content," ");
                }else{
                    helper.setText(R.id.tv_content,item.getContent());
                }
                break;
        }


    }
}
