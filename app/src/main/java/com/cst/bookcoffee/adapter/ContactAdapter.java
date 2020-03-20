package com.cst.bookcoffee.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.cst.bookcoffee.Tools.Contact;
import com.cst.bookcoffee.R;
import com.cst.bookcoffee.Tools.ContactComparator;
import com.cst.bookcoffee.Tools.UserStruct;
import com.cst.bookcoffee.utils.Utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;




public class ContactAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private LayoutInflater mLayoutInflater;
    private Context mContext;
    private String[] mContactNames; // 联系人名称字符串数组
    private String[] mContactIds;
    private List<String> mContactPicPath;
    private HashMap<String,String> userInfoHashMap=new HashMap<>();
    private List<UserStruct> mContactList; // 联系人名称List（转换成拼音）
    private List<Contact> resultList; // 最终结果（包含分组的字母）
    private List<String> characterList; // 字母List
    private OnItemClickListener mOnItemClickListener;//联系人的点击接口
    public enum ITEM_TYPE {
        ITEM_TYPE_CHARACTER,
        ITEM_TYPE_CONTACT
    }
    public interface OnItemClickListener {
        void onItemClick(View view, int position);
        void onItemLongClick(View view, int position);
    }
    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        mOnItemClickListener = onItemClickListener;
    }
    public ContactAdapter(Context context, String[] contactNames,String[] contactIds,List<String> contactPicPath) {
        mContext = context;
        mLayoutInflater = LayoutInflater.from(context);
        mContactNames = contactNames;
        mContactIds=contactIds;
        mContactPicPath=contactPicPath;
        if(mContactPicPath!=null){
            for (int i = 0; i < mContactIds.length; i++){
                userInfoHashMap.put(mContactIds[i],mContactPicPath.get(i));
            }
        }
        handleContact();
    }
    private void handleContact() {
        mContactList = new ArrayList<>();
        for (int i = 0;i < mContactIds.length; i++){
            String pinyin=Utils.getPingYin(mContactNames[i]);
            UserStruct now=new UserStruct(mContactIds[i],mContactNames[i],pinyin);
            mContactList.add(now);
        }
        Collections.sort(mContactList,new ContactComparator());

        resultList = new ArrayList<>();
        characterList = new ArrayList<>();

        for (int i = 0; i < mContactList.size(); i++) {
            String name = mContactList.get(i).getPinyin();
            String character = (name.charAt(0) + "").toUpperCase(Locale.ENGLISH);
            if (!characterList.contains(character)) {
                if (character.hashCode() >= "A".hashCode() && character.hashCode() <= "Z".hashCode()) { // 是字母
                    characterList.add(character);
                    resultList.add(new Contact(character, ITEM_TYPE.ITEM_TYPE_CHARACTER.ordinal()));
                } else {
                    if (!characterList.contains("#")) {
                        characterList.add("#");
                        resultList.add(new Contact("#", ITEM_TYPE.ITEM_TYPE_CHARACTER.ordinal()));
                    }
                }
            }

            resultList.add(new Contact(mContactList.get(i).getName()+" "+mContactList.get(i).getId(), ITEM_TYPE.ITEM_TYPE_CONTACT.ordinal()));
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == ITEM_TYPE.ITEM_TYPE_CHARACTER.ordinal()) {
            return new CharacterHolder(mLayoutInflater.inflate(R.layout.item_character, parent, false));
        } else {
            return new ContactHolder(mLayoutInflater.inflate(R.layout.item_contact, parent, false));
        }
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        if (holder instanceof CharacterHolder) {
            ((CharacterHolder) holder).mTextView.setText(resultList.get(position).getmName());
            if (mOnItemClickListener != null)
            {
                ((CharacterHolder) holder).mTextView.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        int pos = holder.getLayoutPosition();
                        mOnItemClickListener.onItemClick(((CharacterHolder) holder).mTextView, pos);
                    }
                });

                ((CharacterHolder) holder).mTextView.setOnLongClickListener(new View.OnLongClickListener()
                {
                    @Override
                    public boolean onLongClick(View v)
                    {
                        int pos = holder.getLayoutPosition();
                        mOnItemClickListener.onItemLongClick(((CharacterHolder) holder).mTextView, pos);
                        return false;
                    }
                });
            }
        } else if (holder instanceof ContactHolder) {
            final String id=getID(resultList.get(position).getmName());
            final String name=getName(resultList.get(position).getmName());
            ((ContactHolder) holder).User_name.setText(name);
            ((ContactHolder) holder).User_id.setText("id:"+id);
            String path=userInfoHashMap.get(id);
            if(decodePic(path)!=null){
                Bitmap bitmap=resizePic(decodePic(path));
                ((ContactHolder) holder).mImageView.setImageBitmap(bitmap);
            }


            if (mOnItemClickListener != null)
            {
                ((ContactHolder) holder).User_name.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        int pos = holder.getLayoutPosition();
                        mOnItemClickListener.onItemClick(((ContactHolder) holder).User_id, pos);
                    }
                });

                ((ContactHolder) holder).User_id.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        int pos = holder.getLayoutPosition();
                        mOnItemClickListener.onItemClick(((ContactHolder) holder).User_id, pos);
                    }
                });
            }
        }

    }

    @Override
    public int getItemViewType(int position) {
        return resultList.get(position).getmType();
    }

    @Override
    public int getItemCount() {
        return resultList == null ? 0 : resultList.size();
    }

    public class CharacterHolder extends RecyclerView.ViewHolder {
        TextView mTextView;

        CharacterHolder(View view) {
            super(view);
            mTextView = (TextView) view.findViewById(R.id.character);
        }
    }

    public class ContactHolder extends RecyclerView.ViewHolder {
        TextView User_name;
        TextView User_id;
        ImageView mImageView;
        ContactHolder(View view) {
            super(view);
            mImageView=(ImageView) view.findViewById(R.id.contact_image);
            User_name = (TextView) view.findViewById(R.id.contact_name);
            User_id=(TextView)view.findViewById(R.id.contact_id);
        }
    }

    public int getScrollPosition(String character) {
        if (characterList.contains(character)) {
            for (int i = 0; i < resultList.size(); i++) {
                if (resultList.get(i).getmName().equals(character)) {
                    return i;
                }
            }
        }

        return -1; // -1不会滑动
    }
    private String getID(String info){
        String[] line=info.split(" ");
        return line[1];
    }
    private String getName(String info){
        String[] line=info.split(" ");
        return line[0];
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
