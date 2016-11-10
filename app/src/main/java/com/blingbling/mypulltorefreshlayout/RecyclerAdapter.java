package com.blingbling.mypulltorefreshlayout;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by BlingBling on 2016/11/8.
 */

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.Holder>{

    private List<String> mData;
    public RecyclerAdapter(){
        mData=new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            mData.add("string--->"+i);
        }
    }

    @Override public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(android.R.layout.simple_list_item_1,parent, false);
        final Holder holder=new Holder(view);
        view.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View view) {
                Log.e("TAG","onClick----->"+holder.getLayoutPosition());
            }
        });
        view.setOnLongClickListener(new View.OnLongClickListener() {
            @Override public boolean onLongClick(View view) {
                Log.e("TAG","onLongClick----->"+holder.getLayoutPosition());
                return true;
            }
        });
        return holder;
    }

    @Override public void onBindViewHolder(Holder holder, int position) {
        holder.tv.setText(mData.get(position));
    }

    @Override public int getItemCount() {
        return mData.size();
    }

    public static class Holder extends RecyclerView.ViewHolder{

        TextView tv;
        public Holder(View itemView) {
            super(itemView);
            tv= (TextView) itemView.findViewById(android.R.id.text1);
        }
    }
}
