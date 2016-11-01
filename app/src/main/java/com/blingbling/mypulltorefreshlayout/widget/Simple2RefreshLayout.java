package com.blingbling.mypulltorefreshlayout.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

import com.blingbling.mypulltorefreshlayout.R;
import com.blingbling.customrefreshlayoutlibrary.BaseHeaderView;
import com.blingbling.customrefreshlayoutlibrary.BaseRefreshLayout;

/**
 * Created by BlingBling on 2016/11/1.
 */

public class Simple2RefreshLayout extends BaseRefreshLayout{
    public Simple2RefreshLayout(Context context) {
        super(context);
    }

    public Simple2RefreshLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override protected boolean scrollHeaderAndContent() {
        return true;
    }

    @Override protected BaseHeaderView onCreateHeaderView() {
        return new SimpleHeaderView(getContext());
    }

    public static class SimpleHeaderView extends BaseHeaderView{
        private TextView tv;
        public SimpleHeaderView(Context context) {
            this(context,null);
        }

        public SimpleHeaderView(Context context, AttributeSet attrs) {
            super(context, attrs);
            tv = (TextView) findViewById(R.id.tv);
        }

        @Override protected int getHeaderLayoutId() {
            return R.layout.refresh_header;
        }

        @Override protected int getHeaderContentId() {
            return R.id.refresh_header_content;
        }

        @Override protected void onRefreshBefore(float progress) {
            tv.setText("下拉刷新");
        }

        @Override protected void onRefreshAfter(float progress) {
            tv.setText("松开刷新");
        }

        @Override protected void onRefreshReady(float progress) {
            tv.setText("准备刷新");
        }

        @Override protected void onRefreshing() {
            tv.setText("正在刷新");
        }

        @Override protected void onRefreshCancel(float progress) {
            tv.setText("取消刷新");
        }

        @Override protected void onRefreshComplete(boolean isSuccess, float progress) {
            tv.setText(isSuccess?"刷新成功":"刷新失败");
        }
    }
}
