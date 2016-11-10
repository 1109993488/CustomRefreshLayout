package com.blingbling.mypulltorefreshlayout.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

import com.blingbling.mypulltorefreshlayout.R;
import com.blingbling.refreshlayout.BaseFooterView;
import com.blingbling.refreshlayout.BaseHeaderView;
import com.blingbling.refreshlayout.BaseRefreshLayout;

/**
 * Created by BlingBling on 2016/11/1.
 */

public class SimpleRefreshLayout extends BaseRefreshLayout {
    public SimpleRefreshLayout(Context context) {
        super(context);
    }

    public SimpleRefreshLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override protected BaseHeaderView onCreateHeaderView() {
        return new SimpleHeaderView(getContext());
    }

    @Override protected BaseFooterView onCreateFooterView() {
        return null;
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

        @Override public void onRefreshBefore(int scrollY, int refreshHeight, int headerHeight) {
            tv.setText("下拉刷新");
        }

        @Override public void onRefreshAfter(int scrollY, int refreshHeight, int headerHeight) {
            tv.setText("松开刷新");
        }

        @Override public void onRefreshReady(int scrollY, int refreshHeight, int headerHeight) {
            tv.setText("准备刷新");
        }

        @Override public void onRefreshing(int scrollY, int refreshHeight, int headerHeight) {
            tv.setText("正在刷新");
        }

        @Override public void onRefreshComplete(boolean isSuccess, int scrollY, int refreshHeight, int headerHeight) {
            tv.setText(isSuccess?"刷新成功":"刷新失败");
        }

        @Override public void onRefreshCancel(int scrollY, int refreshHeight, int headerHeight) {
            tv.setText("取消刷新");
        }
    }
}
