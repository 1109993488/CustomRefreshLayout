package com.blingbling.customrefreshlayoutlibrary.simple;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.blingbling.customrefreshlayout.R;
import com.blingbling.customrefreshlayoutlibrary.BaseHeaderView;

import java.text.SimpleDateFormat;
import java.util.Date;


/**
 * Created by BlingBling on 2016/10/31.
 */

public class SimpleHeaderView extends BaseHeaderView {

    private View mStatusLayout;
    private View mCompleteStatusLayout;
    private ImageView mArrow;
    private ProgressBar mProgress;
    private TextView mStatus;
    private TextView mTime;
    private ImageView mCompleteStatusIv;
    private TextView mCompleteStatusTv;

    public SimpleHeaderView(Context context) {
        this(context, null);
    }

    public SimpleHeaderView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mStatusLayout = findViewById(R.id.simple_refresh_status_layout);
        mCompleteStatusLayout = findViewById(R.id.simple_refresh_complete_status_layout);
        mArrow = (ImageView) findViewById(R.id.simple_refresh_arrow);
        mProgress = (ProgressBar) findViewById(R.id.simple_refresh_progress);
        mStatus = (TextView) findViewById(R.id.simple_refresh_status);
        mTime = (TextView) findViewById(R.id.simple_refresh_time);
        mCompleteStatusIv = (ImageView) findViewById(R.id.simple_refresh_complete_status_iv);
        mCompleteStatusTv = (TextView) findViewById(R.id.simple_refresh_complete_status_tv);
    }

    @Override protected int getHeaderLayoutId() {
        return R.layout.view_simple_refresh_header;
    }

    @Override protected int getHeaderContentId() {
        return 0;
    }

    @Override public void onRefreshBefore(float progress) {
        mStatus.setText("下拉刷新");
        mArrow.setImageResource(R.drawable.ic_simple_refresh_arrow_down);
        mProgress.setVisibility(GONE);
        mArrow.setVisibility(VISIBLE);
        mStatusLayout.setVisibility(VISIBLE);
        mCompleteStatusLayout.setVisibility(GONE);
        if (TextUtils.isEmpty(mTime.getText())) {
            mTime.setVisibility(GONE);
        } else {
            mTime.setVisibility(VISIBLE);
        }
    }

    @Override public void onRefreshAfter(float progress) {
        mStatus.setText("松开刷新");
        mArrow.setImageResource(R.drawable.ic_simple_refresh_arrow_up);
    }

    @Override public void onRefreshReady(float progress) {
        mStatus.setText("准备刷新");
        mArrow.setVisibility(GONE);
        mProgress.setVisibility(VISIBLE);
    }

    @Override public void onRefreshing() {
        mStatus.setText("正在刷新");
        mArrow.setVisibility(GONE);
        mProgress.setVisibility(VISIBLE);
        mStatusLayout.setVisibility(VISIBLE);
        mCompleteStatusLayout.setVisibility(GONE);
    }

    @Override public void onRefreshCancel(float progress) {
        mStatus.setText("取消刷新");
    }

    @Override public void onRefreshComplete(boolean isSuccess, float progress) {
        if (isSuccess) {
            mCompleteStatusIv.setImageResource(R.drawable.ic_simple_refresh_succeed);
            mCompleteStatusTv.setText("刷新成功");
            mTime.setText(new SimpleDateFormat("最近更新：HH:mm").format(new Date()));
            mTime.setVisibility(VISIBLE);
        } else {
            mCompleteStatusIv.setImageResource(R.drawable.ic_simple_refresh_failed);
            mCompleteStatusTv.setText("刷新失败");
        }
        mProgress.setVisibility(GONE);
        mStatusLayout.setVisibility(GONE);
        mCompleteStatusLayout.setVisibility(VISIBLE);
    }
}
