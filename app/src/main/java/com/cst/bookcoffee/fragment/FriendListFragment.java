package com.cst.bookcoffee.fragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.cst.bookcoffee.R;
import com.cst.bookcoffee.Tools.ContactRightView;
import com.cst.bookcoffee.activity.friend.FriendInfoActivity;
import com.cst.bookcoffee.activity.friend.ShowFriendReasonActivity;
import com.cst.bookcoffee.adapter.ContactAdapter;
import com.wx.roundimageview.RoundImageView;


import java.util.ArrayList;
import java.util.List;

import cn.jpush.im.android.api.ContactManager;
import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.android.api.callback.GetUserInfoListCallback;
import cn.jpush.im.android.api.event.ContactNotifyEvent;
import cn.jpush.im.android.api.model.UserInfo;

import static androidx.constraintlayout.widget.Constraints.TAG;
import static cn.jpush.im.android.api.jmrtc.JMRTCInternalUse.getApplicationContext;


public class FriendListFragment extends Fragment {
    private RecyclerView contactList;
    private String[] contactNames;
    private String[] contactIds;
    private LinearLayoutManager layoutManager;
    private ContactRightView letterView;
    private ContactAdapter adapter;
    private ProgressDialog mProgressDialog;
    private SharedPreferences sp;
    private String USER_AVATAR="user_avatar";
    private boolean isFirst=true;
    View rootView;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView: fgm2");
        mProgressDialog = ProgressDialog.show(getActivity(), "提示：", "正在加载中……");
        mProgressDialog.setCanceledOnTouchOutside(false);
        if (rootView == null) {
            rootView = inflater.inflate(R.layout.fragment_friend_list, container, false);
        }
        return rootView;

    }
    @Override
    public void onActivityCreated(Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);
        JMessageClient.registerEventReceiver(this);
    }
    @Override
    public void onResume(){
        super.onResume();
        initView();

        ContactManager.getFriendList(new GetUserInfoListCallback() {
            @Override
            public void gotResult(int i, String s, List<UserInfo> list) {
                sp=getActivity().getSharedPreferences(USER_AVATAR,Context.MODE_PRIVATE);
                if (i == 0) {
                    StringBuilder sb = new StringBuilder();
                    StringBuilder sd = new StringBuilder();
                    List<String> path=new ArrayList<>();
                    for (UserInfo info : list) {
                        sb.append(info.getNickname());
                        sb.append("\n");

                        sd.append(info.getUserName());
                        sd.append("\n");

                        String p=sp.getString(info.getUserName(),"");
                        path.add(p);
                    }
                    if (sb.length() == 0) {
                        mProgressDialog.dismiss();
                        Toast.makeText(getActivity(),"没有好友",Toast.LENGTH_SHORT).show();
                        initData();
                    }
                    else{
                        contactNames=sb.toString().split("\n");
                        contactIds=sd.toString().split("\n");
                        adapter = new ContactAdapter(getActivity(), contactNames,contactIds,path);
                        mProgressDialog.dismiss();
                        initData();
                    }
                    //Toast.makeText(getApplicationContext(), "获取成功", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getApplicationContext(), "获取失败", Toast.LENGTH_SHORT).show();
                    Log.i("FriendListFragment", "ContactManager.getFriendList" + ", responseCode = " + i + " ; LoginDesc = " + s);
                }
            }
        });
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        JMessageClient.unRegisterEventReceiver(this);
        Log.d(TAG, "onDestroyView: fgm2");
        if (rootView != null) {
            ((ViewGroup) rootView.getParent()).removeView(rootView);
        }
    }
    private void initView(){
        contactList = getActivity().findViewById(R.id.contact_list);
        letterView = getActivity().findViewById(R.id.letter_view);
        layoutManager = new LinearLayoutManager(getActivity());
    }
    private void initData(){
        Log.d("data_create","fgm2");
        letterView.getBackground().setAlpha(10);
        contactList.setLayoutManager(layoutManager);
        if(isFirst){
            contactList.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL));
            Log.d("draw","fgm2");
            isFirst=false;
        }
        contactList.setAdapter(adapter);
        adapter.setOnItemClickListener(new ContactAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                startActivity(new Intent(getActivity(), FriendInfoActivity.class).putExtra("id",((TextView)view).getText().toString()));
            }

            @Override
            public void onItemLongClick(View view, int position) {
            }
        });
        letterView.setUpdateListView(new ContactRightView.UpdateListView() {
            @Override
            public void updateListView(String currentChar) {
                if(!(currentChar.hashCode()==0)){
                    layoutManager.scrollToPositionWithOffset(adapter.getScrollPosition(currentChar),0);
                }
                else
                    layoutManager.scrollToPositionWithOffset(0, 0);
            }
        });
    }
    public void onEvent(ContactNotifyEvent event) {
        String reason = event.getReason();
        String fromUsername = event.getFromUsername();
        String appkey = event.getfromUserAppKey();

        Intent intent = new Intent(getApplicationContext(), ShowFriendReasonActivity.class);
        intent.putExtra(ShowFriendReasonActivity.EXTRA_TYPE, event.getType().toString());
        switch (event.getType()) {
            case invite_received://收到好友邀请
                intent.putExtra("invite_received", "fromUsername = " + fromUsername + "\nfromUserAppKey" + appkey + "\nreason = " + reason);
                intent.putExtra("username", fromUsername);
                intent.putExtra("appkey", appkey);
                startActivity(intent);
                break;
            case invite_accepted://对方接收了你的好友邀请
                intent.putExtra("invite_accepted", "对方接受了你的好友邀请");
                startActivity(intent);
                break;
            case invite_declined://对方拒绝了你的好友邀请
                intent.putExtra("invite_declined", "对方拒绝了你的好友邀请\n拒绝原因:" + event.getReason());
                startActivity(intent);
                break;
            case contact_deleted://对方将你从好友中删除
                intent.putExtra("contact_deleted", "对方将你从好友中删除");
                startActivity(intent);
                break;
            case contact_updated_by_dev_api://好友关系更新，由api管理员操作引起
                intent.putExtra("contact_updated_by_dev_api", "好友关系被管理员更新");
                startActivity(intent);
                break;
            default:
                break;
        }
    }

}
