package com.recyclerview.demo;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

/**
 * Created by big on 2019/7/22.
 */

public class FocusRecyclerView extends RecyclerView {

    public FocusRecyclerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public static abstract class FocuseManager {
        public abstract View focusSearch(android.support.v7.widget.RecyclerView parent, View focused, View next, int direction);
    }

    public static class LinearFocuseManager extends FocuseManager {

        @Override
        public View focusSearch(android.support.v7.widget.RecyclerView parent, View focused, View next, int direction) {
            if (next == null) {
                return focused;
            }
            LinearLayoutManager layoutManager = (LinearLayoutManager) parent.getLayoutManager();
            if (layoutManager.getOrientation() == OrientationHelper.VERTICAL) {
                if (direction == View.FOCUS_DOWN) {
                    Log.d("big", "position:" + layoutManager.getPosition(next) + ",last:" + layoutManager.findLastVisibleItemPosition() + ",state:" + parent.getScrollState());
                    if (parent != next.getParent() || layoutManager.getPosition(next) > layoutManager.findLastVisibleItemPosition()
                        && parent.getScrollState() != RecyclerView.SCROLL_STATE_IDLE
                        || next.getBottom() == focused.getBottom()) {
                        return focused;
                    }
                } else if (direction == View.FOCUS_UP) {
                    if (parent != next.getParent() || layoutManager.getPosition(next) < layoutManager.findFirstVisibleItemPosition()
                        && parent.getScrollState() != RecyclerView.SCROLL_STATE_IDLE
                        || next.getTop() == focused.getTop()) {
                        return focused;
                    }
                }
            } else {
                if (direction == View.FOCUS_RIGHT) {
                    if (parent != next.getParent() || layoutManager.getPosition(next) > layoutManager.findLastVisibleItemPosition()
                        && parent.getScrollState() != RecyclerView.SCROLL_STATE_IDLE
                        || next.getRight() == focused.getRight()) {
                        return focused;
                    }
                } else if (direction == View.FOCUS_LEFT) {
                    if (parent != next.getParent() || layoutManager.getPosition(next) < layoutManager.findFirstVisibleItemPosition()
                        && parent.getScrollState() != RecyclerView.SCROLL_STATE_IDLE
                        || next.getLeft() == focused.getLeft()) {
                        return focused;
                    }
                }
            }

            return next;
        }
    }

    private FocuseManager mFocuseManager;

    public void setFocuseManager(FocuseManager focuseManager) {
        mFocuseManager = focuseManager;
    }

    @Override
    public View focusSearch(View focused, int direction) {
        View next = super.focusSearch(focused, direction);
        if (mFocuseManager != null) {
            return mFocuseManager.focusSearch(this, focused, next, direction);
        }
        return next;
    }
}
