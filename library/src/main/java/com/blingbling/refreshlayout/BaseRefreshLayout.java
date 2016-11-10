package com.blingbling.refreshlayout;


import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;

import com.blingbling.refreshlayout.listener.OnCheckCanLoadMoreCallback;
import com.blingbling.refreshlayout.listener.OnCheckCanRefreshCallback;
import com.blingbling.refreshlayout.listener.OnRefreshListener;


/**
 * Created by zhouweilong on 2016/10/19.
 */

public abstract class BaseRefreshLayout extends RefreshLayout {
    //阻尼系数
    private static final float DAMP = 0.5f;

    private static final int ANIMATE_DURATION = 300;

    private final int mTouchSlop;
    // 用于计算滑动距离的Y坐标中介
    public int lastYMove;
    // 用于判断是否拦截触摸事件的Y坐标中介
    public int lastYIntercept;
    //操作状态  0是默认的状态   1刷新   2加载
    protected int actionStatus = 0;

    // 事件监听接口
    private OnRefreshListener listener;
    // Layout状态
    private RefreshStatus status = RefreshStatus.DEFAULT;
    //是否刷新完成
    private boolean isRefreshSuccess = false;
    //是否加载完成
    private boolean isLoadSuccess = false;
    //正在刷新中
    private boolean isRefreshing = false;
    //正在加载中
    private boolean isLoading = false;

    public BaseRefreshLayout(Context context) {
        this(context, null);
    }

    public BaseRefreshLayout(Context context, AttributeSet attrs) {
        super(context, attrs);

        mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        if (mTarget == null) {
            return super.onInterceptTouchEvent(event);
        }
        if (isRefreshing || isLoading) {
            return false;
        }
        boolean intercept = false;
        // 记录此次触摸事件的y坐标
        int y = (int) event.getY();
        // 判断触摸事件类型
        switch (event.getAction()) {
            // Down事件
            case MotionEvent.ACTION_DOWN: {
                // 记录下本次系列触摸事件的起始点Y坐标
                lastYIntercept = y;
                // 不拦截ACTION_DOWN，因为当ACTION_DOWN被拦截，后续所有触摸事件都会被拦截
                intercept = false;
                actionStatus = 0;
                break;
            }
            // Move事件
            case MotionEvent.ACTION_MOVE: {
                final float yDiff = y - lastYIntercept;
                if (yDiff > mTouchSlop) {
                    lastYMove = y;
                    intercept = checkCanRefreshIntercept();
                    actionStatus = 1;
                } else if (yDiff < -mTouchSlop) {
                    lastYMove = y;
                    intercept = checkCanLoadMoreIntercept();
                    actionStatus = 2;
                }
                break;
            }
            // Up事件
            case MotionEvent.ACTION_UP: {
                intercept = false;
                break;
            }
        }

        return intercept;
    }

    @Override public void requestDisallowInterceptTouchEvent(boolean disallowIntercept) {
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (mTarget == null) {
            return super.onTouchEvent(event);
        }
        int y = (int) event.getY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                lastYMove = y;
                break;
            case MotionEvent.ACTION_MOVE:
                // 计算本次滑动的Y轴增量(距离)
                int dy = lastYMove - y;
                // 如果getScrollY<0，即下拉操作
                if (actionStatus == 1) {//下拉
                    if (getScrollY() + dy < 0) {
                        moveRefresh(dy);
                        lastYMove = y;
                    }
                } else if (actionStatus == 2) {//上拉
                    if (getScrollY() + dy > 0) {
                        moveLoad(dy);
                        lastYMove = y;
                    }
                }
                break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                // 判断本次触摸系列事件结束时,Layout的状态
                switch (status) {
                    //下拉刷新
                    case REFRESH_BEFORE:
                        scrollToDefaultStatus(RefreshStatus.REFRESH_CANCEL);
                        break;
                    case REFRESH_AFTER:
                        scrollToRefreshStatus();
                        break;
                    //上拉加载更多
                    case LOAD_BEFORE:
                        scrollToDefaultStatus(RefreshStatus.LOAD_CANCEL);
                        break;
                    case LOAD_AFTER:
                        scrollToLoadStatus();
                        break;
                }
                actionStatus = 0;
                break;
        }
        return true;
    }

    /**
     * 能不能下拉刷新
     *
     * @return
     */
    private boolean checkCanRefreshIntercept() {
        boolean intercept = false;
        if (mHeaderView != null && mIsCanRefresh) {
            if (mOnCheckCanLoadMoreCallback != null) {
                intercept = mOnCheckCanLoadMoreCallback.checkCanLoadMore(this, mTarget);
            } else {
                intercept = !ViewCompat.canScrollVertically(mTarget, -1);
            }
        }
        return intercept;
    }

    /**
     * 能不能加载
     *
     * @return
     */
    private boolean checkCanLoadMoreIntercept() {
        boolean intercept = false;
        if (mFooterView != null && mIsCanLoadMore) {
            if (mOnCheckCanLoadMoreCallback != null) {
                intercept = mOnCheckCanLoadMoreCallback.checkCanLoadMore(this, mTarget);
            } else {
                intercept = !ViewCompat.canScrollVertically(mTarget, 1);
            }
        }
        return intercept;
    }

    /**
     * 检查是否可以下拉刷新
     * 当使用过程中需要自己控制是否需要刷新时可以实现这个回调接口
     */
    private OnCheckCanRefreshCallback mOnCheckCanRefreshCallback;

    public void setOnCheckCanRefreshCallback(OnCheckCanRefreshCallback callback) {
        this.mOnCheckCanRefreshCallback = callback;
    }

    /**
     * 检查是否可以加载更多
     * 当使用过程中需要自己控制是否需要加载更多时可以实现这个回调接口
     */
    private OnCheckCanLoadMoreCallback mOnCheckCanLoadMoreCallback;

    public void setOnCheckCanLoadMoreCallback(OnCheckCanLoadMoreCallback callback) {
        this.mOnCheckCanLoadMoreCallback = callback;
    }

    /**
     * 自动刷新
     */
    public void autoRefresh() {
        if (isRefreshing){
            return;
        }
        isRefreshing = true;
        measureView(mHeaderView);
        int end = mHeaderContent.getMeasuredHeight();
        performAnim(0, -end, new AnimListener() {
            @Override
            public void onGoing() {
                updateStatus(RefreshStatus.REFRESH_READY);
            }

            @Override
            public void onEnd() {
                updateStatus(RefreshStatus.REFRESH_DOING);
            }
        });

    }

    /**
     * 测量view
     *
     * @param v
     */
    public void measureView(View v) {
        if (v == null) {
            return;
        }
        int w = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
        int h = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
        v.measure(w, h);
    }

    /**
     * 设置接口回调
     *
     * @param listener
     */
    public void setOnRefreshListener(OnRefreshListener listener) {
        this.listener = listener;
    }

    /**
     * 去刷新
     *
     * @param dy
     */
    private void moveRefresh(int dy) {
        performScroll(dy);
        if (Math.abs(getScrollY()) >= mHeaderContent.getMeasuredHeight()) {
            updateStatus(RefreshStatus.REFRESH_AFTER);
        } else {
            updateStatus(RefreshStatus.REFRESH_BEFORE);
        }
    }

    /**
     * 去加载
     *
     * @param dy
     */
    private void moveLoad(int dy) {
        // 进行Y轴上的滑动
        performScroll(dy);
        if (getScrollY() >= mFooterView.getMeasuredHeight()) {
            updateStatus(RefreshStatus.LOAD_AFTER);
        } else {
            updateStatus(RefreshStatus.LOAD_BEFORE);
        }
    }

    /**
     * 滚动到刷新状态
     */
    private void scrollToRefreshStatus() {
        isRefreshing = true;
        int start = getScrollY();
        int end = -mHeaderContent.getMeasuredHeight();
        performAnim(start, end, new AnimListener() {
            @Override
            public void onGoing() {
                updateStatus(RefreshStatus.REFRESH_READY);
            }

            @Override
            public void onEnd() {
                updateStatus(RefreshStatus.REFRESH_DOING);
            }
        });
    }

    /**
     * 滚动到加载状态
     */
    private void scrollToLoadStatus() {
        isLoading = true;
        int start = getScrollY();
        int end = mFooterView.getMeasuredHeight();
        performAnim(start, end, new AnimListener() {
            @Override
            public void onGoing() {
                updateStatus(RefreshStatus.LOAD_READY);
            }

            @Override
            public void onEnd() {
                updateStatus(RefreshStatus.LOAD_DOING);
            }
        });
    }

    /**
     * 滚动到默认状态
     *
     * @param startStatus
     */
    private void scrollToDefaultStatus(final RefreshStatus startStatus) {
        int start = getScrollY();
        int end = 0;
        performAnim(start, end, new AnimListener() {
            @Override
            public void onGoing() {
                updateStatus(startStatus);
            }

            @Override
            public void onEnd() {
                updateStatus(RefreshStatus.DEFAULT);
            }
        });
    }

    /**
     * 停止刷新
     *
     * @param isSuccess 刷新结果
     */
    public void stopRefresh(boolean isSuccess) {
        isRefreshSuccess = isSuccess;
        scrollToDefaultStatus(RefreshStatus.REFRESH_COMPLETE);
    }

    /**
     * 停止加载更多
     *
     * @param isSuccess 加载更多结果
     */
    public void stopLoadMore(boolean isSuccess) {
        isLoadSuccess = isSuccess;
        scrollToDefaultStatus(RefreshStatus.LOAD_COMPLETE);
    }

    /**
     * 执行滑动
     *
     * @param dy
     */
    private void performScroll(int dy) {
        scrollBy(0, (int) (dy * DAMP));
    }

    /**
     * 执行动画
     *
     * @param start
     * @param end
     * @param listener
     */
    private void performAnim(int start, int end, final AnimListener listener) {
        ValueAnimator animator = ValueAnimator.ofInt(start, end);
        animator.setDuration(ANIMATE_DURATION);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int value = (int) animation.getAnimatedValue();
                scrollTo(0, value);
                listener.onGoing();
            }
        });
        animator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                listener.onEnd();
            }

            @Override
            public void onAnimationCancel(Animator animation) {
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
            }
        });
        animator.start();
    }

    /**
     * 刷新状态
     *
     * @param status
     */
    private void updateStatus(RefreshStatus status) {
        this.status = status;
        int scrollY = Math.abs(getScrollY());
        // 判断本次触摸系列事件结束时,Layout的状态
        switch (status) {
            //默认状态
            case DEFAULT:
                isRefreshSuccess = false;
                isLoadSuccess = false;
                isRefreshing = false;
                isLoading = false;
                break;
            //下拉刷新
            case REFRESH_BEFORE:
                mHeaderView.onRefreshBefore(scrollY, mHeaderContent.getMeasuredHeight(), mHeaderView.getMeasuredHeight());
                break;
            //松手刷新
            case REFRESH_AFTER:
                mHeaderView.onRefreshAfter(scrollY, mHeaderContent.getMeasuredHeight(), mHeaderView.getMeasuredHeight());
                break;
            //准备刷新
            case REFRESH_READY:
                mHeaderView.onRefreshReady(scrollY, mHeaderContent.getMeasuredHeight(), mHeaderView.getMeasuredHeight());
                break;
            //刷新中
            case REFRESH_DOING:
                mHeaderView.onRefreshing(scrollY, mHeaderContent.getMeasuredHeight(), mHeaderView.getMeasuredHeight());
                if (listener != null){
                    listener.onRefresh();
                }
                break;
            //刷新完成
            case REFRESH_COMPLETE:
                mHeaderView.onRefreshComplete(isRefreshSuccess, scrollY, mHeaderContent.getMeasuredHeight(), mHeaderView.getMeasuredHeight());
                break;
            //取消刷新
            case REFRESH_CANCEL:
                mHeaderView.onRefreshCancel(scrollY, mHeaderContent.getMeasuredHeight(), mHeaderView.getMeasuredHeight());
                break;
            //上拉加载更多
            case LOAD_BEFORE:
                mFooterView.onLoadBefore(scrollY);
                break;
            //松手加载
            case LOAD_AFTER:
                mFooterView.onLoadAfter(scrollY);
                break;
            //准备加载
            case LOAD_READY:
                mFooterView.onLoadReady(scrollY);
                break;
            //加载中
            case LOAD_DOING:
                mFooterView.onLoading(scrollY);
                if (listener != null){
                    listener.onLoadMore();
                }
                break;
            //加载完成
            case LOAD_COMPLETE:
                mFooterView.onLoadComplete(isLoadSuccess, scrollY);
                break;
            //取消加载
            case LOAD_CANCEL:
                mFooterView.onLoadCancel(scrollY);
                break;
        }
    }

    interface AnimListener {
        void onGoing();

        void onEnd();
    }

}
