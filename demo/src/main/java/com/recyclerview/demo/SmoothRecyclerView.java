package com.recyclerview.demo;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.LinearInterpolator;
import android.widget.OverScroller;


/**
 * Created by big on 2019/7/22.
 */

public class SmoothRecyclerView extends FocusRecyclerView {
    private static final String TAG = "SmoothRecyclerView";
    public static final int DEFALUT_DURATION = 50;
    private OverScroller mScroller;
    private int mDuration = DEFALUT_DURATION;
    private int mLastX, mLastY;
    private ViewFlinger mViewFlinger = new ViewFlinger();

    private View mCurrentFocusChild;
    private int mCurrentPosition;

    private final static int DIRECTION_NONE = 0;
    private final static int DIRECTION_UP = -1;
    private final static int DIRECTION_DOWN = 1;
    private final static int DIRECTION_LEFT = -2;
    private final static int DIRECTION_RIGHT = 2;
    private int mScrollDirection = 0;
    private int mScrollState = SCROLL_STATE_IDLE;


    public SmoothRecyclerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        mScroller = new OverScroller(context, new LinearInterpolator());
        init(context);
    }

    private void init(Context context) {
        setFocuseManager(new LinearFocuseManager());
    }

    public void setDuration(int mDuration) {
        this.mDuration = mDuration;
    }

    @Override
    public void smoothScrollBy(int dx, int dy) {
        //don't do anything here
    }

    public boolean scroll(View child, int offset) {
        int position = getChildAdapterPosition(child);
        if (position < 0) return false;
        mScrollDirection = DIRECTION_NONE;
        LinearLayoutManager layoutManager = (LinearLayoutManager) getLayoutManager();
        if (child != mCurrentFocusChild) {
            if (OrientationHelper.VERTICAL == layoutManager.getOrientation()) {
                if (mCurrentFocusChild == null) {
                    mScrollDirection = DIRECTION_DOWN;
                    scroll(0, offset);
                } else if (child.getBottom() > getBottom()) {
                    mScrollDirection = DIRECTION_DOWN;
                    scroll(0, child.getBottom() - getBottom());
                } else if (child.getTop() < getTop()) {
                    mScrollDirection = DIRECTION_UP;
                    scroll(0, child.getTop() - getTop());
                }
            } else {
                if (mCurrentFocusChild == null) {
                    mScrollDirection = DIRECTION_RIGHT;
                    scroll(offset, 0);
                } else if (child.getRight() > getRight()) {
                    mScrollDirection = DIRECTION_RIGHT;
                    scroll(getWidth(), 0);
                } else if (child.getLeft() < getLeft()) {
                    mScrollDirection = DIRECTION_LEFT;
                    scroll(-getWidth(), 0);
                }
            }
        }
        mCurrentFocusChild = child;
        mCurrentPosition = position;
        return mScrollDirection != DIRECTION_NONE;
    }

    public void scroll(int dx, int dy) {
        int currentX = mScroller.getCurrX();
        int currentY = mScroller.getCurrY();
        if (mScroller.getCurrY() + dy < 0) {
            dy = -mScroller.getCurrY();
        }
        if (mScroller.getCurrX() + dx < 0) {
            dx = -mScroller.getCurrX();
        }
        mScroller.startScroll(currentX, currentY, dx, dy, mDuration);
        mViewFlinger.postOnAnimation();
    }

    @Override
    public void onScrollStateChanged(int state) {
        if (state == mScrollState) return;
        Log.d(TAG, "scroll state change:" + state);
        mScrollState = state;
    }

    @Override
    public int getScrollState() {
        return mScroller.isFinished() ? SCROLL_STATE_IDLE : SCROLL_STATE_DRAGGING;
    }

    public interface OnScrollListener {
        void onScroll(int dy);

        void onScrollState(int state);
    }

    private OnScrollListener mOnScrollListener;

    public void setOnScrollListener(OnScrollListener listener) {
        this.mOnScrollListener = listener;
    }

    class ViewFlinger implements Runnable {

        @Override
        public void run() {
            if (isInTouchMode()) return;
            if (mScroller.computeScrollOffset()) {
                scrollBy(mScroller.getCurrX() - mLastX, mScroller.getCurrY() - mLastY);
                mLastX = mScroller.getCurrX();
                mLastY = mScroller.getCurrY();
                onScrollStateChanged(SCROLL_STATE_DRAGGING);
                if (mOnScrollListener != null) {
                    mOnScrollListener.onScroll(mScroller.getCurrY());
                }
                if (mCurrentFocusChild != null && mCurrentFocusChild.getParent() == null) {
                    getRealView();
                }
                if (mCurrentFocusChild != null && !mCurrentFocusChild.isFocused()) {
                    if (mScrollDirection == DIRECTION_UP && mCurrentFocusChild.getBottom() > getTop()
                        || mScrollDirection == DIRECTION_DOWN && mCurrentFocusChild.getTop() < getBottom()
                        || mScrollDirection == DIRECTION_LEFT && mCurrentFocusChild.getRight() > getLeft()
                        || mScrollDirection == DIRECTION_RIGHT && mCurrentFocusChild.getLeft() > getRight()) {
                        getRealView();
                        mCurrentFocusChild.requestFocus();
                    }
                }
                postOnAnimation();
            } else {
                mScrollDirection = DIRECTION_NONE;
                onScrollStateChanged(SCROLL_STATE_IDLE);
            }
            if (mOnScrollListener != null) {
                mOnScrollListener.onScrollState(mScrollState);
            }
        }

        void postOnAnimation() {
            ViewCompat.postOnAnimation(SmoothRecyclerView.this, this);
        }
    }

    private void getRealView() {
        if (getChildAdapterPosition(mCurrentFocusChild) != mCurrentPosition || mCurrentFocusChild.getParent() == null) {
            View realView = getLayoutManager().findViewByPosition(mCurrentPosition);
            if (realView != null) {
                mCurrentFocusChild = realView;
            }
        }
    }
}
