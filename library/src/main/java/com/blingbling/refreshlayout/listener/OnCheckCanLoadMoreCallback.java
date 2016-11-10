package com.blingbling.refreshlayout.listener;

import android.view.View;

import com.blingbling.refreshlayout.BaseRefreshLayout;


/**
 * 检查是否可以加载更多接口
 * 当使用过程中需要自己控制是否需要加载时可以实现这个回调接口
 * Created by zhouweilong on 2016/10/27.
 */

public interface OnCheckCanLoadMoreCallback {
    boolean checkCanLoadMore(BaseRefreshLayout parent, View child);
}
