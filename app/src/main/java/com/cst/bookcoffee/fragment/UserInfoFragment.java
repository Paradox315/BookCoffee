package com.cst.bookcoffee.fragment;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.cst.bookcoffee.R;
import com.cst.bookcoffee.activity.LoginActivity;
import com.cst.bookcoffee.activity.scanbook.ScanBookActivity;
import com.cst.bookcoffee.activity.setting.GetMyInfoActivity;

import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.android.api.callback.GetAvatarBitmapCallback;
import cn.jpush.im.android.api.model.UserInfo;

import static androidx.constraintlayout.widget.Constraints.TAG;

public class UserInfoFragment extends Fragment {
    private TextView user_name;
    private TextView user_id;
    private TextView scan;
    private ImageView user_avatar;
    private Button user_logout;
    private ProgressDialog mProgressDialog;
    View rootView;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView: fgm3");
        mProgressDialog = ProgressDialog.show(getActivity(), "提示：", "正在加载中……");
        mProgressDialog.setCanceledOnTouchOutside(false);
        if (rootView == null) {
            rootView = inflater.inflate(R.layout.fragment_user_info, container, false);
        }
        return rootView;
    }
    @Override
    public void onActivityCreated(Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);

    }
    @Override
    public void onResume(){
        super.onResume();
        initView();
        initData();
    }
    private void initView(){
        user_id=getActivity().findViewById(R.id.user_id);
        user_name=getActivity().findViewById(R.id.user_name);
        user_avatar=getActivity().findViewById(R.id.user_avatar);
        user_logout=getActivity().findViewById(R.id.btn_logout);
        scan=getActivity().findViewById(R.id.scanbook);
    }
    private void initData(){
        UserInfo mUserInfo=JMessageClient.getMyInfo();
        user_id.setText("账 号："+mUserInfo.getUserName());
        user_name.setText(mUserInfo.getNickname());

        mUserInfo.getAvatarBitmap(new GetAvatarBitmapCallback() {
            @Override
            public void gotResult(int i, String s, Bitmap bitmap) {
                mProgressDialog.dismiss();
                if (i==0){
                    user_avatar.setImageBitmap(bitmap);
                }
            }
        });
        user_id.setOnClickListener(new mOnClickListener());
        user_name.setOnClickListener(new mOnClickListener());
        user_avatar.setOnClickListener(new mOnClickListener());
        user_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                JMessageClient.logout();
                startActivity(new Intent(getActivity(), LoginActivity.class));
                getActivity().finish();
            }
        });
        scan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), ScanBookActivity.class));
            }
        });
    }
    class mOnClickListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            startActivity(new Intent(getActivity(), GetMyInfoActivity.class));
        }
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        JMessageClient.unRegisterEventReceiver(this);
        if (rootView != null) {
            ((ViewGroup) rootView.getParent()).removeView(rootView);
        }
    }
}
