package com.cst.bookcoffee.fragment;

import android.app.ProgressDialog;
import android.content.Intent;
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
import com.cst.bookcoffee.activity.chat.ChatActivity;
import com.cst.bookcoffee.adapter.ConversationAdapter;

import java.util.List;

import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.android.api.model.Conversation;

import static androidx.constraintlayout.widget.Constraints.TAG;
import static cn.jpush.im.android.api.jmrtc.JMRTCInternalUse.getApplicationContext;

public class ConversationFragment extends Fragment {
    private RecyclerView conver_list;
    private TextView warn;
    private LinearLayoutManager layoutManager;
    private ConversationAdapter adapter;
    private List<Conversation> conversationList;
    View rootView;
    private boolean isFirst=true;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView: fgm1");
        if (rootView == null) {
            rootView = inflater.inflate(R.layout.fragment_conversation, container, false);
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
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (rootView != null) {
            ((ViewGroup) rootView.getParent()).removeView(rootView);
        }
    }
    private void initView(){
        conver_list=getActivity().findViewById(R.id.conversation);
        warn=getActivity().findViewById(R.id.warn);
        layoutManager=new LinearLayoutManager(getActivity());
    }
    private void initData(){
        conversationList = JMessageClient.getConversationList();
        if(conversationList!=null && conversationList.size()>0){
            Log.d("state","true");
            adapter=new ConversationAdapter(getActivity(),conversationList);
            conver_list.setLayoutManager(layoutManager);
            conver_list.setAdapter(adapter);
            if(isFirst){
                conver_list.addItemDecoration(new DividerItemDecoration(getActivity(),DividerItemDecoration.VERTICAL));
                isFirst=false;
            }
            //conver_list.setAdapter(adapter);
            adapter.setOnItemClickListener(new ConversationAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(View view, int position) {
                    view=conver_list.getChildAt(position);
                    TextView id=view.findViewById(R.id.contact_id);
                    TextView name=view.findViewById(R.id.contact_name);
                    //Toast.makeText(getActivity(),id.getText().toString()+" "+name.getText().toString(),Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(getApplicationContext(), ChatActivity.class).putExtra("target",id.getText().toString()+" "+name.getText().toString()));
                }

                @Override
                public void onItemLongClick(View view, int position) {

                }
            });
        }
        else{
            Log.d("state","false");
            conver_list.setVisibility(View.INVISIBLE);
            warn.setVisibility(View.VISIBLE);
        }
    }

}
