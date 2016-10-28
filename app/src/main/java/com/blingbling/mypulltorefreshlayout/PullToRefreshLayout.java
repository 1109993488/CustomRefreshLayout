package com.blingbling.mypulltorefreshlayout;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.v4.view.NestedScrollingChild;
import android.support.v4.view.NestedScrollingChildHelper;
import android.support.v4.view.NestedScrollingParent;
import android.support.v4.view.NestedScrollingParentHelper;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;

/**
 * Created by BlingBling on 2016/10/27.
 */

public class PullToRefreshLayout extends LinearLayout implements NestedScrollingParent, NestedScrollingChild {

    private static final String TAG = "TAG";
    private NestedScrollingParentHelper mParentHelper;
    private NestedScrollingChildHelper mNestedScrollingChildHelper;

    public PullToRefreshLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        setOrientation(LinearLayout.VERTICAL);
        mParentHelper = new NestedScrollingParentHelper(this);

        mNestedScrollingChildHelper = new NestedScrollingChildHelper(this);
        setNestedScrollingEnabled(true);
    }

    @Override public boolean onInterceptTouchEvent(MotionEvent ev) {
        startNestedScroll(ViewCompat.SCROLL_AXIS_VERTICAL);
        return super.onInterceptTouchEvent(ev);
    }

    @Override public boolean onTouchEvent(MotionEvent event) {
        return super.onTouchEvent(event);
    }


    // parent

    @Override
    public boolean onStartNestedScroll(View child, View target, int nestedScrollAxes) {
        Log.d(TAG, "----父布局onStartNestedScroll----------------");
        Log.d(TAG, "child==target:" + (child == target));

        return (nestedScrollAxes & ViewCompat.SCROLL_AXIS_VERTICAL) != 0;
    }

    @SuppressLint("NewApi") @Override
    public void onNestedScrollAccepted(View child, View target, int nestedScrollAxes) {
        Log.d(TAG, "----父布局onNestedScrollAccepted---------------");
        mParentHelper.onNestedScrollAccepted(child, target, nestedScrollAxes);
        startNestedScroll(ViewCompat.SCROLL_AXIS_VERTICAL);
    }

    @Override
    public void onStopNestedScroll(View target) {
        Log.d(TAG, "----父布局onStopNestedScroll----------------");
        mParentHelper.onStopNestedScroll(target);
        stopNestedScroll();
    }

    @Override
    public void onNestedScroll(View target, int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed) {
        Log.d(TAG, "----父布局onNestedScroll----------------");
//        final int myConsumed = moveBy(dyUnconsumed);
//        final int myUnconsumed = dyUnconsumed - myConsumed;
//        dispatchNestedScroll(0, myConsumed, 0, myUnconsumed, null);
    }

    @Override
    public void onNestedPreScroll(View target, int dx, int dy, int[] consumed) {
        Log.d(TAG, "----父布局onNestedPreScroll----------------");
        scrollBy(0, -dy);

        consumed[0] = 0;
        consumed[1] = 10; // 把消费的距离放进去
    }

    @Override
    public boolean onNestedFling(View target, float velocityX, float velocityY, boolean consumed) {
        Log.d(TAG, "----父布局onNestedFling----------------");
        return true;
    }

    @Override
    public boolean onNestedPreFling(View target, float velocityX, float velocityY) {
        Log.d(TAG, "----父布局onNestedPreFling----------------");
        return true;
    }

    @Override
    public int getNestedScrollAxes() {
        Log.d(TAG, "----父布局getNestedScrollAxes----------------");
        return mParentHelper.getNestedScrollAxes();
    }

    // child

    @Override public void setNestedScrollingEnabled(boolean enabled) {
        Log.e(TAG,"child----setNestedScrollingEnabled-");
        mNestedScrollingChildHelper.setNestedScrollingEnabled(enabled);
    }

    @Override public boolean isNestedScrollingEnabled() {
        Log.e(TAG,"child----isNestedScrollingEnabled-");
        return mNestedScrollingChildHelper.isNestedScrollingEnabled();
    }

    @Override public boolean startNestedScroll(int axes) {
        Log.e(TAG,"child----startNestedScroll-");
        return mNestedScrollingChildHelper.startNestedScroll(axes);
    }

    @Override public void stopNestedScroll() {
        Log.e(TAG,"child----stopNestedScroll-");
        mNestedScrollingChildHelper.stopNestedScroll();
    }

    @Override public boolean hasNestedScrollingParent() {
        Log.e(TAG,"child----hasNestedScrollingParent-");
        return mNestedScrollingChildHelper.hasNestedScrollingParent();
    }

    @Override public boolean dispatchNestedScroll(int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed, int[] offsetInWindow) {
        Log.e(TAG,"child----dispatchNestedScroll-");
        return mNestedScrollingChildHelper.dispatchNestedScroll(dxConsumed,dyConsumed,dxUnconsumed,dyUnconsumed,offsetInWindow);
    }

    @Override public boolean dispatchNestedPreScroll(int dx, int dy, int[] consumed, int[] offsetInWindow) {
        Log.e(TAG,"child----dispatchNestedPreScroll-");
        return mNestedScrollingChildHelper.dispatchNestedPreScroll(dx,dy,consumed,offsetInWindow);
    }

    @Override public boolean dispatchNestedFling(float velocityX, float velocityY, boolean consumed) {
        Log.e(TAG,"child----dispatchNestedFling-");
        return mNestedScrollingChildHelper.dispatchNestedFling(velocityX,velocityY,consumed);
    }

    @Override public boolean dispatchNestedPreFling(float velocityX, float velocityY) {
        Log.e(TAG,"child----dispatchNestedPreFling-");
        return mNestedScrollingChildHelper.dispatchNestedPreFling(velocityX,velocityY);
    }
}

