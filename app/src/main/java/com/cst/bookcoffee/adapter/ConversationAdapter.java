package com.cst.bookcoffee.adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.text.Layout;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.cst.bookcoffee.R;

import java.util.List;

import cn.jpush.im.android.api.content.TextContent;
import cn.jpush.im.android.api.model.Conversation;
import cn.jpush.im.android.api.model.Message;
import cn.jpush.im.android.api.model.UserInfo;

public class ConversationAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private LayoutInflater mLayoutInflater;
    private Context mContext;
    private List<Conversation> mConversationList;
    private OnItemClickListener mOnItemClickListener;//联系人的点击接口
    private SharedPreferences sp;
    private String USER_AVATAR="user_avatar";
    public ConversationAdapter(Context context,List<Conversation> conversationList){
        mContext=context;
        mLayoutInflater = LayoutInflater.from(context);
        sp=context.getSharedPreferences(USER_AVATAR,Context.MODE_PRIVATE);
        mConversationList=conversationList;
    }
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ConversationHolder(mLayoutInflater.inflate(R.layout.item_conversation,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, int position) {
        Object userinfo=mConversationList.get(position).getTargetInfo();
        if(userinfo instanceof UserInfo){
            String name=((UserInfo) userinfo).getNickname();
            String id=((UserInfo) userinfo).getUserName();
            String msg= mConversationList.get(position).getLatestText();
            int msgCount=mConversationList.get(position).getUnReadMsgCnt();
            Bitmap pic=decodePic(sp.getString(id,""));
            ((ConversationHolder) holder).name.setText(name);
            ((ConversationHolder) holder).id.setText(id);
            ((ConversationHolder) holder).msg.setText(msg);
            if (msgCount>0){
                ((ConversationHolder) holder).msgCount.setText(String.valueOf(msgCount));
                ((ConversationHolder) holder).msgCount.setVisibility(View.VISIBLE);
            }

            if(pic!=null){
                ((ConversationHolder) holder).pic.setImageBitmap(resizePic(pic));
            }
        }
        if (mOnItemClickListener!=null){
           holder.itemView.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View view) {
                   int pos=holder.getLayoutPosition();
                   mOnItemClickListener.onItemClick(holder.itemView,pos);
               }
           });
       }
    }

    @Override
    public int getItemCount() {
        return mConversationList == null ? 0 : mConversationList.size();
    }

    public interface OnItemClickListener {
        void onItemClick(View view, int position);
        void onItemLongClick(View view, int position);
    }
    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        mOnItemClickListener = onItemClickListener;
    }
    public class ConversationHolder extends RecyclerView.ViewHolder{
        ImageView pic;
        TextView name;
        TextView id;
        TextView msg;
        TextView msgCount;
        ConversationHolder(View view){
            super(view);
            pic=view.findViewById(R.id.contact_image);
            name=view.findViewById(R.id.contact_name);
            id=view.findViewById(R.id.contact_id);
            msg=view.findViewById(R.id.contact_msg);
            msgCount=view.findViewById(R.id.msg_cnt);
        }
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
