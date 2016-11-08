package com.blingbling.customrefreshlayoutlibrary;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.Nullable;
import android.support.v4.view.MotionEventCompat;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Transformation;
import android.widget.AbsListView;

/**
 * Created by BlingBling on 2016/10/28.
 */
public abstract class BaseRefreshLayout extends ViewGroup {

    private static final String LOG_TAG = BaseRefreshLayout.class.getSimpleName();

    private static final float DECELERATE_INTERPOLATION_FACTOR = 2f;
    private static final int INVALID_POINTER = -1;
    private static final float DRAG_RATE = .5f;

    private static final int ANIMATE_TO_START_DURATION = 300;
    private static final int ANIMATE_TO_TRIGGER_DURATION = 300;
    private static final int ANIMATE_TO_START_OFFSET_DURATION = 600;

    private View mTarget; // the target of the gesture
    OnRefreshListener mListener;
    boolean mRefreshing = false;
    private int mTouchSlop;

    private float mInitialMotionY;
    private float mInitialDownY;
    private boolean mIsBeingDragged;
    private int mActivePointerId = INVALID_POINTER;

    // Target is returning to its start offset because it was cancelled or a
    // refresh was triggered.
    private boolean mReturningToStart;
    private final DecelerateInterpolator mDecelerateInterpolator;

    private BaseHeaderView mHeaderView;
    private int mHeaderViewIndex = -1;

    private int mTotalDragDistance = -1;
    private int mCurrentTargetOffsetTop;
    protected int mOriginalOffsetTop;
    protected int mFrom;

    private boolean mIsSuccess;
    private boolean mIsCancel;

    private OnChildScrollUpCallback mChildScrollUpCallback;

    private Animation.AnimationListener mRefreshListener = new Animation.AnimationListener() {
        @Override
        public void onAnimationStart(Animation animation) {
        }

        @Override
        public void onAnimationRepeat(Animation animation) {
        }

        @Override
        public void onAnimationEnd(Animation animation) {
            if (mRefreshing) {
                // Make sure the progress view is fully visible
                mHeaderView.onRefreshing();
                if (mListener != null) {
                    mListener.onRefresh();
                }
            } else {
                reset();
            }
        }
    };

    void reset() {
        mRefreshing = false;
        mHeaderView.clearAnimation();
        // Return the circle to its start position
        setTargetOffsetTopAndBottom(mOriginalOffsetTop - mCurrentTargetOffsetTop, false /* requires update */);
        mCurrentTargetOffsetTop = mOriginalOffsetTop;
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        if (!enabled) {
            reset();
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        reset();
    }

    public BaseRefreshLayout(Context context) {
        this(context, null);
    }

    public BaseRefreshLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
        mDecelerateInterpolator = new DecelerateInterpolator(DECELERATE_INTERPOLATION_FACTOR);

        setWillNotDraw(false);
        createProgressView();
        ViewCompat.setChildrenDrawingOrderEnabled(this, true);

        final TypedArray a = context.obtainStyledAttributes(attrs, new int[]{android.R.attr.enabled});
        setEnabled(a.getBoolean(0, true));
        a.recycle();
    }

    @Override
    protected int getChildDrawingOrder(int childCount, int i) {
        if (mHeaderViewIndex < 0) {
            return i;
        } else if (i == childCount - 1) {
            // Draw the selected child last
            return mHeaderViewIndex;
        } else if (i >= mHeaderViewIndex) {
            // Move the children after the selected child earlier one
            return i + 1;
        } else {
            // Keep the children before the selected child the same
            return i;
        }
    }

    private void createProgressView() {
        mHeaderView = onCreateHeaderView();
        addView(mHeaderView);
    }


    /**
     * Set the listener to be notified when a refresh is triggered via the swipe
     * gesture.
     * @param listener 刷新回调
     */
    public void setOnRefreshListener(OnRefreshListener listener) {
        mListener = listener;
    }

    public void setRefreshing(boolean refreshing) {
        setRefreshing(refreshing, true);
    }

    /**
     * Notify the widget that refresh state has changed. Do not call this when
     * refresh is triggered by a swipe gesture.
     * @param refreshing Whether or not the view should show refresh progress.
     * @param isSuccess 刷新成功为true，否则为false(只有当refreshing为false时才有效)
     */
    public void setRefreshing(boolean refreshing, final boolean isSuccess) {
        if (mRefreshing != refreshing) {
            mIsSuccess = isSuccess;
            mRefreshing = refreshing;
            ensureTarget();
            if (mRefreshing) {
                animateOffsetToCorrectPosition(mCurrentTargetOffsetTop);
            } else {
                mIsCancel=false;
                animateOffsetToStartPosition(mCurrentTargetOffsetTop);
            }
        }
    }

    /**
     * @return Whether the SwipeRefreshWidget is actively showing refresh
     * progress.
     */
    public boolean isRefreshing() {
        return mRefreshing;
    }

    private void ensureTarget() {
        // Don't bother getting the parent height if the parent hasn't been laid
        // out yet.
        if (mTarget == null) {
            for (int i = 0; i < getChildCount(); i++) {
                View child = getChildAt(i);
                if (!child.equals(mHeaderView)) {
                    mTarget = child;
                    break;
                }
            }
        }
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        final int width = getMeasuredWidth();
        final int height = getMeasuredHeight();
        if (getChildCount() == 0) {
            return;
        }
        if (mTarget == null) {
            ensureTarget();
        }
        if (mTarget == null) {
            return;
        }
        final View child = mTarget;
        final int childLeft = getPaddingLeft();
        final int childTop = getPaddingTop();
        final int childRight = width - getPaddingRight();
        final int childBottom = height - getPaddingBottom();
        if (scrollHeaderAndContent()) {
            child.layout(childLeft, childTop + (mCurrentTargetOffsetTop - mOriginalOffsetTop), childRight, childBottom + (mCurrentTargetOffsetTop - mOriginalOffsetTop));
        } else {
            child.layout(childLeft, childTop, childRight, childBottom);
        }
        mHeaderView.layout(childLeft, childTop + mCurrentTargetOffsetTop, childRight, childTop + mCurrentTargetOffsetTop + mHeaderView.getMeasuredHeight());
    }

    @Override
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (mTarget == null) {
            ensureTarget();
        }
        if (mTarget == null) {
            return;
        }
        int width = getMeasuredWidth() - getPaddingLeft() - getPaddingRight();
        int height = getMeasuredHeight() - getPaddingTop() - getPaddingBottom();
        mHeaderView.measure(MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY),
                MeasureSpec.makeMeasureSpec(height, MeasureSpec.AT_MOST));
        mTarget.measure(MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY),
                MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY));
        mHeaderViewIndex = -1;
        // Get the index of the headerView.
        for (int index = 0; index < getChildCount(); index++) {
            if (getChildAt(index) == mHeaderView) {
                mHeaderViewIndex = index;
                break;
            }
        }

        if (mOriginalOffsetTop == 0) {
            mCurrentTargetOffsetTop = mOriginalOffsetTop = -mHeaderView.getMeasuredHeight();
            int contentId = mHeaderView.getHeaderContentId();
            if (contentId == 0) {
                mTotalDragDistance = mHeaderView.getMeasuredHeight();
            } else {
                mTotalDragDistance = mHeaderView.findViewById(contentId).getMeasuredHeight();
            }
        }
    }

    /**
     * @return Whether it is possible for the child view of this layout to
     * scroll up. Override this if the child view is a custom view.
     */
    public boolean canChildScrollUp() {
        if (mChildScrollUpCallback != null) {
            return mChildScrollUpCallback.canChildScrollUp(this, mTarget);
        }
        if (android.os.Build.VERSION.SDK_INT < 14) {
            if (mTarget instanceof AbsListView) {
                final AbsListView absListView = (AbsListView) mTarget;
                return absListView.getChildCount() > 0
                        && (absListView.getFirstVisiblePosition() > 0 || absListView.getChildAt(0)
                        .getTop() < absListView.getPaddingTop());
            } else {
                return ViewCompat.canScrollVertically(mTarget, -1) || mTarget.getScrollY() > 0;
            }
        } else {
            return ViewCompat.canScrollVertically(mTarget, -1);
        }
    }

    /**
     * Set a callback to override {@link BaseRefreshLayout#canChildScrollUp()} method. Non-null
     * callback will return the value provided by the callback and ignore all internal logic.
     *
     * @param callback Callback that should be called when canChildScrollUp() is called.
     */
    public void setOnChildScrollUpCallback(@Nullable OnChildScrollUpCallback callback) {
        mChildScrollUpCallback = callback;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        ensureTarget();

        final int action = MotionEventCompat.getActionMasked(ev);
        int pointerIndex;

        if (mReturningToStart && action == MotionEvent.ACTION_DOWN) {
            mReturningToStart = false;
        }

        Log.e("TAG","-->"+ev.getAction()+"  "+ev.getActionMasked()+" "+action+"  "+canChildScrollUp());
        if (!isEnabled() || mReturningToStart || canChildScrollUp()
                || mRefreshing ) {
            // Fail fast if we're not in a state where a swipe is possible
            return false;
        }

        switch (action) {
            case MotionEvent.ACTION_DOWN:
                setTargetOffsetTopAndBottom(mOriginalOffsetTop - mCurrentTargetOffsetTop, true);
                mActivePointerId = ev.getPointerId(0);
                mIsBeingDragged = false;

                pointerIndex = ev.findPointerIndex(mActivePointerId);
                if (pointerIndex < 0) {
                    return false;
                }
                mInitialDownY = ev.getY(pointerIndex);
                break;

            case MotionEvent.ACTION_MOVE:
                if (mActivePointerId == INVALID_POINTER) {
                    Log.e(LOG_TAG, "Got ACTION_MOVE event but don't have an active pointer id.");
                    return false;
                }

                pointerIndex = ev.findPointerIndex(mActivePointerId);
                if (pointerIndex < 0) {
                    return false;
                }
                final float y = ev.getY(pointerIndex);
                startDragging(y);
                break;

            case MotionEventCompat.ACTION_POINTER_UP:
                onSecondaryPointerUp(ev);
                break;

            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                mIsBeingDragged = false;
                mActivePointerId = INVALID_POINTER;
                break;
        }

        return mIsBeingDragged;
    }

    @Override
    public void requestDisallowInterceptTouchEvent(boolean b) {
    }

    private void moveSpinner(float overscrollTop) {
        float originalDragPercent = overscrollTop / Math.abs(mOriginalOffsetTop);

        float dragPercent = Math.min(1f, Math.abs(originalDragPercent));
        float extraOS = Math.abs(overscrollTop) - Math.abs(mOriginalOffsetTop);
        float slingshotDist = Math.abs(mOriginalOffsetTop);
        float tensionSlingshotPercent = Math.max(0, Math.min(extraOS, slingshotDist * 2) / slingshotDist);
        float tensionPercent = (float) ((tensionSlingshotPercent / 4) - Math.pow((tensionSlingshotPercent / 4), 2)) * 2f;
        float extraMove = (slingshotDist) * tensionPercent * 2;

        int targetY = mOriginalOffsetTop + (int) ((slingshotDist * dragPercent) + extraMove);

        setTargetOffsetTopAndBottom(targetY - mCurrentTargetOffsetTop, true /* requires update */);
    }

    private void finishSpinner(float overscrollTop) {
        if (overscrollTop > mTotalDragDistance) {
            mIsCancel = false;
            setRefreshing(true);
        } else {
            // cancel refresh
            mIsCancel = true;
            mRefreshing = false;
            animateOffsetToStartPosition(mCurrentTargetOffsetTop);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        final int action = MotionEventCompat.getActionMasked(ev);
        int pointerIndex = -1;

        if (mReturningToStart && action == MotionEvent.ACTION_DOWN) {
            mReturningToStart = false;
        }

        if (!isEnabled() || mReturningToStart || canChildScrollUp() || mRefreshing) {
            // Fail fast if we're not in a state where a swipe is possible
            return false;
        }

        switch (action) {
            case MotionEvent.ACTION_DOWN:
                mActivePointerId = ev.getPointerId(0);
                mIsBeingDragged = false;
                break;

            case MotionEvent.ACTION_MOVE: {
                pointerIndex = ev.findPointerIndex(mActivePointerId);
                if (pointerIndex < 0) {
                    Log.e(LOG_TAG, "Got ACTION_MOVE event but have an invalid active pointer id.");
                    return false;
                }

                final float y = ev.getY(pointerIndex);
                startDragging(y);

                if (mIsBeingDragged) {
                    final float overscrollTop = (y - mInitialMotionY) * DRAG_RATE;
                    if (overscrollTop > 0) {
                        moveSpinner(overscrollTop);
                    } else {
                        return false;
                    }
                }
                break;
            }
            case MotionEventCompat.ACTION_POINTER_DOWN: {
                pointerIndex = MotionEventCompat.getActionIndex(ev);
                if (pointerIndex < 0) {
                    Log.e(LOG_TAG, "Got ACTION_POINTER_DOWN event but have an invalid action index.");
                    return false;
                }
                mActivePointerId = ev.getPointerId(pointerIndex);
                break;
            }

            case MotionEventCompat.ACTION_POINTER_UP:
                onSecondaryPointerUp(ev);
                break;

            case MotionEvent.ACTION_UP: {
                pointerIndex = ev.findPointerIndex(mActivePointerId);
                if (pointerIndex < 0) {
                    Log.e(LOG_TAG, "Got ACTION_UP event but don't have an active pointer id.");
                    return false;
                }

                if (mIsBeingDragged) {
                    final float y = ev.getY(pointerIndex);
                    final float overscrollTop = (y - mInitialMotionY) * DRAG_RATE;
                    mIsBeingDragged = false;
                    finishSpinner(overscrollTop);
                }
                mActivePointerId = INVALID_POINTER;
                return false;
            }
            case MotionEvent.ACTION_CANCEL:
                return false;
        }

        return true;
    }

    private void startDragging(float y) {
        final float yDiff = y - mInitialDownY;
        if (yDiff > mTouchSlop && !mIsBeingDragged) {
            mInitialMotionY = mInitialDownY + mTouchSlop;
            mIsBeingDragged = true;
        }
    }

    private void animateOffsetToStartPosition(int from) {
        mFrom = from;
        mAnimateToStartPosition.reset();
        mAnimateToStartPosition.setDuration(ANIMATE_TO_START_DURATION);
        mAnimateToStartPosition.setInterpolator(mDecelerateInterpolator);
        if(mIsCancel){
            mAnimateToStartPosition.setStartOffset(0);
        }else {
            mAnimateToStartPosition.setStartOffset(ANIMATE_TO_START_OFFSET_DURATION);
        }
        mHeaderView.clearAnimation();
        mHeaderView.startAnimation(mAnimateToStartPosition);
    }

    private void animateOffsetToCorrectPosition(int from) {
        mFrom = from;
        mAnimateToCorrectPosition.reset();
        mAnimateToCorrectPosition.setDuration(ANIMATE_TO_TRIGGER_DURATION);
        mAnimateToCorrectPosition.setInterpolator(mDecelerateInterpolator);
        mAnimateToCorrectPosition.setAnimationListener(mRefreshListener);
        mHeaderView.clearAnimation();
        mHeaderView.startAnimation(mAnimateToCorrectPosition);
    }

    private final Animation mAnimateToStartPosition = new Animation() {
        @Override
        public void applyTransformation(float interpolatedTime, Transformation t) {
            int targetTop = (mFrom + (int) ((mOriginalOffsetTop - mFrom) * interpolatedTime));
            int offset = targetTop - mCurrentTargetOffsetTop;
            setTargetOffsetTopAndBottom(offset, true /* requires update */);
        }
    };

    private final Animation mAnimateToCorrectPosition = new Animation() {
        @Override
        public void applyTransformation(float interpolatedTime, Transformation t) {
            int endTarget = mTotalDragDistance - Math.abs(mOriginalOffsetTop);
            int targetTop = (mFrom + (int) ((endTarget - mFrom) * interpolatedTime));
            int offset = targetTop - mCurrentTargetOffsetTop;
            setTargetOffsetTopAndBottom(offset, true /* requires update */);

        }
    };

    void setTargetOffsetTopAndBottom(int offset, boolean requiresUpdate) {
        mHeaderView.bringToFront();
        ViewCompat.offsetTopAndBottom(mHeaderView, offset);
        if (scrollHeaderAndContent()) {
            ViewCompat.offsetTopAndBottom(mTarget, offset);
        }
        mCurrentTargetOffsetTop = mHeaderView.getTop() - getPaddingTop();
        if (!requiresUpdate) {
            return;
        }
        int realyTargetOffsetTop = mCurrentTargetOffsetTop - (mOriginalOffsetTop + mTotalDragDistance);
        float progress = 1 - 1.0f * realyTargetOffsetTop / -mTotalDragDistance;
        if (mIsBeingDragged) {
            if (progress >= 1) {
                mHeaderView.onRefreshAfter(progress);
            } else {
                mHeaderView.onRefreshBefore(progress);
            }
        } else {
            if (progress > 1) {
                mHeaderView.onRefreshReady(progress);
            } else {
                if (mIsCancel) {
                    mHeaderView.onRefreshCancel(progress);
                } else {
                    if (mRefreshing) {
                        if (progress < 1) {
                            mHeaderView.onRefreshBefore(progress);
                        }
                    } else {
                        mHeaderView.onRefreshComplete(mIsSuccess, progress);
                    }
                }
            }
        }
    }

    private void onSecondaryPointerUp(MotionEvent ev) {
        final int pointerIndex = MotionEventCompat.getActionIndex(ev);
        final int pointerId = ev.getPointerId(pointerIndex);
        if (pointerId == mActivePointerId) {
            // This was our active pointer going up. Choose a new
            // active pointer and adjust accordingly.
            final int newPointerIndex = pointerIndex == 0 ? 1 : 0;
            mActivePointerId = ev.getPointerId(newPointerIndex);
        }
    }

    /**
     * 移动头部的时候是否移动内容
     * @return 如果头部和内容一起移动就返回true，如果只移动头部返回false
     */
    protected abstract boolean scrollHeaderAndContent();

    protected abstract BaseHeaderView onCreateHeaderView();

    /**
     * Classes that wish to be notified when the swipe gesture correctly
     * triggers a refresh should implement this interface.
     */
    public interface OnRefreshListener {
        /**
         * Called when a swipe gesture triggers a refresh.
         */
        void onRefresh();
    }

    /**
     * Classes that wish to override {@link BaseRefreshLayout#canChildScrollUp()} method
     * behavior should implement this interface.
     */
    public interface OnChildScrollUpCallback {
        /**
         * Callback that will be called when {@link BaseRefreshLayout#canChildScrollUp()} method
         * is called to allow the implementer to override its behavior.
         *
         * @param parent SwipeRefreshLayout that this callback is overriding.
         * @param child  The child view of SwipeRefreshLayout.
         * @return Whether it is possible for the child view of parent layout to scroll up.
         */
        boolean canChildScrollUp(BaseRefreshLayout parent, @Nullable View child);
    }
}