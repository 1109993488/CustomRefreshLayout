package com.blingbling.refreshlayout.listener;

import android.view.View;

import com.blingbling.refreshlayout.BaseRefreshLayout;


/**
 * 检查是否可以刷新接口
 * 当使用过程中需要自己控制是否需要刷新时可以实现这个回调接口
 * Created by zhouweilong on 2016/10/27.
 */

public interface OnCheckCanRefreshCallback {
    boolean checkCanRefresh(BaseRefreshLayout parent, View child);
}
