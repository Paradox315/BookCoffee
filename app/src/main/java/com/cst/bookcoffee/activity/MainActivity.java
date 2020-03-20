package com.cst.bookcoffee.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager.widget.ViewPager;

import com.cst.bookcoffee.fragment.ConversationFragment;
import com.cst.bookcoffee.fragment.FriendListFragment;
import com.cst.bookcoffee.fragment.UserInfoFragment;
import com.cst.bookcoffee.activity.friend.AddFriendActivity;
import com.cst.bookcoffee.adapter.MyFragmentPagerAdapter;
import com.cst.bookcoffee.R;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

import cn.jpush.im.android.api.ContactManager;
import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.android.api.callback.GetAvatarBitmapCallback;
import cn.jpush.im.android.api.callback.GetUserInfoListCallback;
import cn.jpush.im.android.api.model.UserInfo;

public class MainActivity extends FragmentActivity {

    private ImageView addFriend;
    private RadioGroup mTabRadioGroup;
    private ViewPager mViewPaper;
    private TextView title;
    private MyFragmentPagerAdapter mAdapter;
    private List<Fragment> fragments = new ArrayList<>();
    private SharedPreferences sp;
    private String USER_AVATAR="user_avatar";
    private UserInfo myinfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        initData();
    }
    @Override
    protected void onResume(){
        super.onResume();

    }
    private void initView() {
        setContentView(R.layout.activity_main);
        mViewPaper=findViewById(R.id.viewpaper);
        mTabRadioGroup=findViewById(R.id.tabs);
        addFriend=findViewById(R.id.iv_add);
        title=findViewById(R.id.menu_title);
    }
    private void initData(){

        myinfo=JMessageClient.getMyInfo();
        ContactManager.getFriendList(new GetUserInfoListCallback() {
            @Override
            public void gotResult(int i, String s, List<UserInfo> list) {
                if(i==0){
                    list.add(myinfo);
                    sp=getApplicationContext().getSharedPreferences(USER_AVATAR,MODE_PRIVATE);
                    final SharedPreferences.Editor editor=sp.edit();
                    for(final UserInfo user:list){
                        user.getAvatarBitmap(new GetAvatarBitmapCallback() {
                            @Override
                            public void gotResult(int i, String s, Bitmap bitmap) {
                                if(i==0){
                                    ByteArrayOutputStream byteArrayOutputStream=new ByteArrayOutputStream();
                                    bitmap.compress(Bitmap.CompressFormat.JPEG, 50, byteArrayOutputStream);
                                    String headPicBase64=new String(Base64.encodeToString(byteArrayOutputStream.toByteArray(),Base64.DEFAULT));
                                    editor.putString(user.getUserName(),headPicBase64);
                                    editor.commit();
                                    Log.d("Avatar result","ok!");
                                }
                            }
                        });
                    }
                    Fragment fragment=new ConversationFragment();FriendListFragment friendListFragment=new FriendListFragment();
                    UserInfoFragment userInfoFragment=new UserInfoFragment();
                    fragments.add(fragment);fragments.add(friendListFragment);fragments.add(userInfoFragment);
                    mAdapter =new MyFragmentPagerAdapter(getSupportFragmentManager(),fragments);
                    mViewPaper.setAdapter(mAdapter);
                    mViewPaper.addOnPageChangeListener(mPageChangeListener);
                    mTabRadioGroup.setOnCheckedChangeListener(mOnCheckedChangeListener);
                    addFriend.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            startActivity(new Intent(MainActivity.this, AddFriendActivity.class));
                        }
                    });

                }
                else {
                    Toast.makeText(getApplicationContext(), "获取失败", Toast.LENGTH_SHORT).show();
                    Log.i("FriendListFragment", "ContactManager.getFriendList" + ", responseCode = " + i + " ; LoginDesc = " + s);
                }
            }
        });
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        mViewPaper.removeOnPageChangeListener(mPageChangeListener);
        JMessageClient.unRegisterEventReceiver(this);
        //JMessageClient.logout();
    }

    private ViewPager.OnPageChangeListener mPageChangeListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
            RadioButton radioButton = (RadioButton) mTabRadioGroup.getChildAt(position);
            radioButton.setChecked(true);
            switch (position){
                case 0:
                    title.setText("消  息");
                    addFriend.setVisibility(View.INVISIBLE);
                    break;
                case 1:
                    title.setText("联系人");
                    addFriend.setVisibility(View.VISIBLE);
                    break;
                case 2:
                    title.setText("我");
                    addFriend.setVisibility(View.INVISIBLE);
                    break;
                default:
                    break;
            }
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    };
    private RadioGroup.OnCheckedChangeListener mOnCheckedChangeListener = new RadioGroup.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId) {
            for (int i = 0; i < group.getChildCount(); i++) {
                if (group.getChildAt(i).getId() == checkedId) {
                    mViewPaper.setCurrentItem(i);
                    return;
                }
            }
        }
    };
}
