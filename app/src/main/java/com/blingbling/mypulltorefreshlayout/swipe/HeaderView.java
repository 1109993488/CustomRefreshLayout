package com.blingbling.mypulltorefreshlayout.swipe;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.animation.Animation;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.blingbling.mypulltorefreshlayout.R;


/**
 * Created by BlingBling on 2016/10/28.
 */

public class HeaderView extends FrameLayout {

    private Animation.AnimationListener mListener;
    private TextView tv;

    public HeaderView(Context context) {
        this(context, null);
    }

    public HeaderView(Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater.from(context).inflate(R.layout.refresh_header, this);

        tv= (TextView) findViewById(R.id.tv);
    }

    public void setAnimationListener(Animation.AnimationListener listener) {
        mListener = listener;
    }

    @Override
    public void onAnimationStart() {
        super.onAnimationStart();
        if (mListener != null) {
            mListener.onAnimationStart(getAnimation());
        }
    }

    @Override
    public void onAnimationEnd() {
        super.onAnimationEnd();
        if (mListener != null) {
            mListener.onAnimationEnd(getAnimation());
        }
    }




    //手指移动还没有达到刷新要求
    public void onRefreshBefore(float mTotalDragDistance, float overscrollTop) {
        tv.setText("下拉刷新");
    }

    //手指移动达到刷新要求
    public void onRefreshAfter(float mTotalDragDistance, float overscrollTop) {
        tv.setText("松手刷新");

    }

    //准备刷新
    public void onRefreshReady(float mTotalDragDistance, float overscrollTop) {
        tv.setText("准备刷新");
    }
    //松手->刷新，达到刷新的要求了
    public void onRefreshing(float mTotalDragDistance, float overscrollTop) {
        tv.setText("正在刷新");
    }

    //松手->取消，没有达到要求
    public void onRefreshCancel(float mTotalDragDistance, float overscrollTop) {
        tv.setText("取消刷新");

    }

    public void onRefreshComplete(boolean isRefreshSuccess,float mTotalDragDistance, float overscrollTop){
        if(isRefreshSuccess){
            tv.setText("刷新成功");
        }else {
            tv.setText("刷新失败");
        }
    }

//    REFRESH_BEFORE,   // 下拉刷新中  还没有达到可以刷新之前的时候
//    REFRESH_AFTER,    // 松开刷新    下拉已经到达可以刷新的时候
//    REFRESH_READY,    // 准备刷新状态 达到可以刷新的时候松开手指回到刷新的位置状态
//    REFRESH_DOING,    // 正在刷新中
//    REFRESH_CANCEL,   // 取消刷新    没有超过可刷新的距离
//    REFRESH_COMPLETE, // 刷新完成    分刷新成功和失败
}
