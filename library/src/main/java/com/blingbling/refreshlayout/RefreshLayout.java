package com.blingbling.refreshlayout;


import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

/**
 * 自定义viewgrop 添加头部和底部 默认隐藏头和底部
 * Created by zhouweilong on 2016/10/19.
 */
abstract class RefreshLayout extends ViewGroup {

    //头部布局
    protected BaseHeaderView mHeaderView;
    //头部content
    protected View mHeaderContent;
    //底部布局
    protected BaseFooterView mFooterView;
    protected View mTarget;

    //是否可以加载更多
    protected boolean mIsCanLoadMore;
    //是否可以下拉刷新
    protected boolean mIsCanRefresh;

    public RefreshLayout(Context context) {
        super(context);
    }

    public RefreshLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        mHeaderView = onCreateHeaderView();
        if (mHeaderView != null) {
            mIsCanRefresh = true;
            addView(mHeaderView, LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
            mHeaderContent = mHeaderView.findViewById(mHeaderView.getHeaderContentId());
            if (mHeaderContent == null) {
                mHeaderContent = mHeaderView;
            }
        }
        mFooterView = onCreateFooterView();
        if (mFooterView != null) {
            mIsCanLoadMore = true;
            addView(mFooterView, LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (mTarget == null) {
            ensureTarget();
        }
        if (mTarget == null) {
            return;
        }
        int width = getMeasuredWidth() - getPaddingLeft() - getPaddingRight();
        int height = getMeasuredHeight() - getPaddingTop() - getPaddingBottom();
        if (mHeaderView != null) {
            mHeaderView.measure(MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY),
                    MeasureSpec.makeMeasureSpec(height, MeasureSpec.AT_MOST));
        }
        mTarget.measure(MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY),
                MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY));
        if (mFooterView != null) {
            mFooterView.measure(MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY),
                    MeasureSpec.makeMeasureSpec(height, MeasureSpec.AT_MOST));
        }

    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        if (mTarget == null) {
            return;
        }
        final int width = getMeasuredWidth();
        final int height = getMeasuredHeight();
        final int childLeft = getPaddingLeft();
        final int childTop = getPaddingTop();
        final int childWidth = width - getPaddingLeft() - getPaddingRight();
        final int childHeight = height - getPaddingTop() - getPaddingBottom();

        if (mHeaderView != null) {
            mHeaderView.layout(childLeft, childTop - mHeaderView.getMeasuredHeight(), childLeft + mHeaderView.getMeasuredWidth(), childTop);
        }
        mTarget.layout(childLeft, childTop, childLeft + childWidth, childTop + childHeight);
        if (mFooterView != null) {
            mFooterView.layout(childLeft, childTop + childHeight, childLeft + mFooterView.getMeasuredWidth(), childTop + childHeight + mFooterView.getMeasuredHeight());
        }
    }

    private void ensureTarget() {
        // Don't bother getting the parent height if the parent hasn't been laid
        // out yet.
        if (mTarget == null) {
            for (int i = 0; i < getChildCount(); i++) {
                View child = getChildAt(i);
                if (!child.equals(mHeaderView) && !child.equals(mFooterView)) {
                    mTarget = child;
                    break;
                }
            }
        }
    }


    protected abstract BaseHeaderView onCreateHeaderView();

    protected abstract BaseFooterView onCreateFooterView();

    /**
     * 设置是否支持下拉刷新
     *
     * @param isCanRefresh 是否可以下拉刷新
     */
    public void setCanRefresh(boolean isCanRefresh) {
        this.mIsCanRefresh = isCanRefresh;
    }

    /**
     * 设置是否支持加载更多
     *
     * @param isCanLoad 是否可以加载更多
     */
    public void setCanLoadMore(boolean isCanLoad) {
        this.mIsCanLoadMore = isCanLoad;
    }
}
