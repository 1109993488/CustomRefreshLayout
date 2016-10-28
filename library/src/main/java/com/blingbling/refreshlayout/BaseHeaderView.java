package com.blingbling.refreshlayout;

import android.content.Context;
import android.support.annotation.IdRes;
import android.support.annotation.LayoutRes;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.FrameLayout;


/**
 * Created by BlingBling on 2016/10/28.
 */

public abstract class BaseHeaderView extends FrameLayout {

    public BaseHeaderView(Context context) {
        this(context, null);
    }

    public BaseHeaderView(Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater.from(context).inflate(getHeaderLayoutId(), this);
    }

    protected abstract @LayoutRes int getHeaderLayoutId();

    /**
     * HeaderView刷新的头部布局ID
     * @return 0表示整个布局计算刷新
     */
    protected abstract @IdRes int getHeaderContentId();

    /**
     * 下拉刷新，还没有达到可以刷新状态，从开始位置移动到当前位置
     * @param progress (<tt>progress &gt;= 0 && progress &lt; 1</tt>)
     */
    protected abstract void onRefreshBefore(float progress);

    /**
     * 松开刷新，手指移动达到刷新要求，从刷新位置移动到当前位置
     * @param progress (<tt>progress &gt;= 1</tt>)
     */
    protected abstract void onRefreshAfter(float progress);

    /**
     * 准备刷新，达到可以刷新状态，松手从当前位置移动到刷新位置
     * @param progress (<tt>progress &gt; 1</tt>)
     */
    protected abstract void onRefreshReady(float progress);

    /**
     * 正在刷新中
     */
    protected abstract void onRefreshing();

    /**
     * 取消刷新，从当前位置移动到初始位置，没有超过可刷新的距离
     * @param progress (<tt>progress &gt;= 0 && progress &lt; 1</tt>)
     */
    protected abstract void onRefreshCancel(float progress);

    /**
     * 刷新完成，从刷新位置移动到初始位置，分刷新成功和失败
     * @param isSuccess true为刷新结果成功，false为刷新结果失败
     * @param progress (<tt>progress &gt;= 0 && progress &lt; 1</tt>)
     */
    protected abstract void onRefreshComplete(boolean isSuccess, float progress);
}
