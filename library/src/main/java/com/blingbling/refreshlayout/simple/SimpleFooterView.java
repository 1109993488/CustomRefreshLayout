package com.blingbling.refreshlayout.simple;


import android.content.Context;
import android.widget.TextView;

import com.blingbling.customrefreshlayout.R;
import com.blingbling.refreshlayout.BaseFooterView;

/**
 * 加载更多布局  可以按照这个例子自定义自己的加载更多
 * Created by zhouweilong on 2016/10/19.
 */

public class SimpleFooterView extends BaseFooterView {
    public TextView mLoadTv;

    public SimpleFooterView(Context context) {
        super(context);
        mLoadTv = (TextView) findViewById(R.id.tv);
    }

    @Override protected int getFooterLayoutId() {
        return R.layout.simple_view_refresh_footer;
    }

    @Override
    public void onLoadBefore(int scrollY) {
        mLoadTv.setText("上拉加载");
    }

    @Override
    public void onLoadAfter(int scrollY) {
        mLoadTv.setText("松开加载");
    }

    @Override
    public void onLoadReady(int scrollY) {
        mLoadTv.setText("准备加载");
    }

    @Override
    public void onLoading(int scrollY) {
        mLoadTv.setText("正在加载");
    }

    @Override
    public void onLoadComplete(boolean isLoadSuccess,int scrollY) {
        mLoadTv.setText(isLoadSuccess ? "加载成功" : "加载失败");
    }

    @Override
    public void onLoadCancel(int scrollY) {
        mLoadTv.setText("加载取消");
    }
}
