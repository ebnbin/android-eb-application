package com.ebnbin.ebapplication.view;

import android.content.Context;
import android.support.annotation.AttrRes;
import android.support.annotation.IntDef;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StyleRes;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.ebnbin.ebapplication.R;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * A FrameLayout that switches states easily.
 */
public final class StateFrameLayout extends FrameLayout {
    private final LayoutInflater mLayoutInflater;

    public StateFrameLayout(@NonNull Context context) {
        super(context);

        mLayoutInflater = LayoutInflater.from(context);
    }

    public StateFrameLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        mLayoutInflater = LayoutInflater.from(context);
    }

    public StateFrameLayout(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        mLayoutInflater = LayoutInflater.from(context);
    }

    public StateFrameLayout(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr,
            @StyleRes int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);

        mLayoutInflater = LayoutInflater.from(context);
    }

    //*****************************************************************************************************************
    // States.

    /**
     * No state.
     */
    private static final int STATE_NONE = 0;
    /**
     * Loading state.
     */
    private static final int STATE_LOADING = 1;
    /**
     * Failure state.
     */
    private static final int STATE_FAILURE = 2;
    /**
     * Progressing state.
     */
    private static final int STATE_PROGRESSING = 3;

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({STATE_NONE, STATE_LOADING, STATE_FAILURE, STATE_PROGRESSING})
    public @interface State {
    }

    @State
    private int mState;

    //*****************************************************************************************************************

    /**
     * Saves visibilities of child views for restoring if they are set {@link #GONE}.
     */
    private int[] mVisibilities;

    /**
     * Saves visibilities of child views and sets them {@link #GONE}.
     */
    private void hideChildViews() {
        if (mVisibilities != null) {
            return;
        }

        int count = getChildCount();
        mVisibilities = new int[count];

        for (int i = 0; i < count; i++) {
            View childView = getChildAt(i);

            mVisibilities[i] = childView.getVisibility();
            childView.setVisibility(GONE);
        }
    }

    /**
     * Restores visibilities of child views.
     */
    private void showChildViews() {
        if (mVisibilities == null || mVisibilities.length != getChildCount()) {
            return;
        }

        for (int i = 0; i < mVisibilities.length; i++) {
            getChildAt(i).setVisibility(mVisibilities[i]);
        }

        mVisibilities = null;
    }

    //*****************************************************************************************************************
    // Switch mode.

    /**
     * Keeps visibilities of child views.
     */
    public static final int SWITCH_MODE_KEEP = 0;
    /**
     * Shows child views.
     */
    public static final int SWITCH_MODE_OVERLAY = 1;
    /**
     * Hides child views.
     */
    public static final int SWITCH_MODE_REPLACE = 2;

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({SWITCH_MODE_KEEP, SWITCH_MODE_OVERLAY, SWITCH_MODE_REPLACE})
    public @interface SwitchMode {
    }

    private void setSwitchMode(@SwitchMode int switchMode) {
        switch (switchMode) {
            case SWITCH_MODE_OVERLAY: {
                showChildViews();

                break;
            }
            case SWITCH_MODE_REPLACE: {
                hideChildViews();

                break;
            }
            case SWITCH_MODE_KEEP:
            default: {
                break;
            }
        }
    }

    //*****************************************************************************************************************
    // Clears state and shows child views.

    public void clearState() {
        if (mState == STATE_NONE) {
            return;
        }

        removeViewAt(getChildCount() - 1);

        showChildViews();

        mState = STATE_NONE;
    }

    //*****************************************************************************************************************
    // Loading state.

    private FrameLayout mLoadingFrameLayout;

    public void switchLoadingState() {
        switchLoadingState(SWITCH_MODE_REPLACE);
    }

    public void switchLoadingState(@SwitchMode int switchMode) {
        if (!switchState(STATE_LOADING, switchMode)) {
            return;
        }

        if (mLoadingFrameLayout == null) {
            mLoadingFrameLayout = (FrameLayout) mLayoutInflater.inflate(R.layout.eb_view_state_loading, this, false);
        }

        addView(mLoadingFrameLayout);
    }

    //*****************************************************************************************************************
    // Failure state.

    private FrameLayout mFailureFrameLayout;
    private ImageView mRefreshImageView;

    public void switchFailureState(@Nullable OnClickListener onRefreshClickListener) {
        switchFailureState(onRefreshClickListener, SWITCH_MODE_REPLACE);
    }

    public void switchFailureState(@Nullable OnClickListener onRefreshClickListener, @SwitchMode int switchMode) {
        if (!switchState(STATE_FAILURE, switchMode)) {
            return;
        }

        if (mFailureFrameLayout == null) {
            mFailureFrameLayout = (FrameLayout) mLayoutInflater.inflate(R.layout.eb_view_state_failure, this, false);
            mRefreshImageView = (ImageView) mFailureFrameLayout.findViewById(R.id.eb_refresh);
        }
        mRefreshImageView.setOnClickListener(onRefreshClickListener);

        addView(mFailureFrameLayout);
    }

    //*****************************************************************************************************************
    // Progressing state.

    private FrameLayout mProgressingFrameLayout;
    private ProgressBar mProgressBar;

    public void switchProgressingState() {
        switchProgressingState(SWITCH_MODE_REPLACE);
    }

    public void switchProgressingState(@SwitchMode int switchMode) {
        if (!switchState(STATE_PROGRESSING, switchMode)) {
            return;
        }

        if (mProgressingFrameLayout == null) {
            mProgressingFrameLayout = (FrameLayout) mLayoutInflater.inflate(R.layout.eb_view_state_progressing, this,
                    false);
            mProgressBar = (ProgressBar) mProgressingFrameLayout.findViewById(R.id.progress_bar);
        }

        addView(mProgressingFrameLayout);
    }

    public void setProgress(int progress) {
        if (mState != STATE_PROGRESSING) {
            return;
        }

        if (progress == -1) {
            mProgressBar.setIndeterminate(true);
        } else {
            mProgressBar.setIndeterminate(false);
            mProgressBar.setProgress(progress);
        }
    }

    //*****************************************************************************************************************
    // Not none state.

    /**
     * General method for all states except {@link #STATE_NONE}.
     *
     * @return Whether state has changed.
     */
    private boolean switchState(@State int state, @SwitchMode int switchMode) {
        if (mState == state) {
            return false;
        }

        if (mState != STATE_NONE) {
            removeViewAt(getChildCount() - 1);
        }

        mState = state;

        setSwitchMode(switchMode);

        return true;
    }
}
