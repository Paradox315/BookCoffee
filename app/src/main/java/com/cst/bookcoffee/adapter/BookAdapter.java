package com.cst.bookcoffee.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.cst.bookcoffee.R;

import java.util.ArrayList;
import java.util.List;

public class BookAdapter extends RecyclerView.Adapter<BookAdapter.BookHolder> {
    private LayoutInflater mLayoutInflater;
    private final Context mContext;
    private List<String> mBookNamesList;
    private BookAdapter.OnItemClickListener mOnItemClickListener;//点击接口
    public interface OnItemClickListener {
        void onItemClick(View view, int position);
        void onItemLongClick(View view, int position);
    }
    public void setOnItemClickListener(BookAdapter.OnItemClickListener onItemClickListener) {
        mOnItemClickListener = onItemClickListener;
    }
    public BookAdapter(Context context,List<String> BookNameList){
        mContext=context;
        mLayoutInflater = LayoutInflater.from(context);
        mBookNamesList=BookNameList;
    }
    @Override
    public BookHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new BookHolder(mLayoutInflater.inflate(R.layout.item_book,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull final BookAdapter.BookHolder holder, int position) {
        ((BookHolder)holder).Book_name.setText(mBookNamesList.get(position));
        if (mOnItemClickListener != null)
        {
            ((BookHolder) holder).Book_name.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    int pos = holder.getLayoutPosition();
                    mOnItemClickListener.onItemClick(((BookHolder) holder).Book_name, pos);
                }
            });

        }
    }

    @Override
    public int getItemCount() {
        return mBookNamesList == null ? 0 : mBookNamesList.size();
    }

    public class BookHolder extends RecyclerView.ViewHolder {
        TextView Book_name;
        BookHolder (View view) {
            super(view);
            Book_name=(TextView)view.findViewById(R.id.book_name);
        }
    }
}
