package com.ebnbin.ebapplication.view;

import android.content.Context;
import android.support.annotation.AttrRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.annotation.StyleRes;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.ebnbin.ebapplication.R;

/**
 * A FrameLayout that switches states easily.
 */
public final class StateView extends FrameLayout {
    private final LayoutInflater mLayoutInflater;

    public StateView(@NonNull Context context) {
        super(context);

        mLayoutInflater = LayoutInflater.from(context);
    }

    public StateView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        mLayoutInflater = LayoutInflater.from(context);
    }

    public StateView(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        mLayoutInflater = LayoutInflater.from(context);
    }

    public StateView(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr,
            @StyleRes int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);

        mLayoutInflater = LayoutInflater.from(context);
    }

    //*****************************************************************************************************************
    // States.

    private enum State {
        /**
         * No state.
         */
        NONE,
        /**
         * Loading state.
         */
        LOADING,
        /**
         * Failure state.
         */
        FAILURE,
        /**
         * Progressing state.
         */
        PROGRESSING,
        /**
         * No data state.
         */
        NO_DATA
    }

    @NonNull
    private State mState = State.NONE;

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

    private enum SwitchMode {
        /**
         * Keeps visibilities of child views.
         */
        KEEP,
        /**
         * Shows child views.
         */
        OVERLAY,
        /**
         * Hides child views.
         */
        REPLACE
    }

    private void setSwitchMode(@NonNull SwitchMode switchMode) {
        switch (switchMode) {
            case OVERLAY: {
                showChildViews();

                break;
            }
            case REPLACE: {
                hideChildViews();

                break;
            }
            case KEEP:
            default: {
                break;
            }
        }
    }

    //*****************************************************************************************************************
    // Clears state and shows child views.

    public void stateNone() {
        if (mState == State.NONE) {
            return;
        }

        removeViewAt(getChildCount() - 1);

        showChildViews();

        mState = State.NONE;
    }

    //*****************************************************************************************************************
    // Loading state.

    private FrameLayout mLoadingFrameLayout;

    public void stateLoading() {
        if (!switchState(State.LOADING, SwitchMode.REPLACE)) {
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
    private TextView mFailureTextView;

    public void stateFailure(@Nullable OnClickListener onRefreshClickListener) {
        stateFailure(onRefreshClickListener, null);
    }

    public void stateFailure(@Nullable OnClickListener onRefreshClickListener, @StringRes int failureStringId) {
        stateFailure(onRefreshClickListener, failureStringId == -1 ? null
                : failureStringId == 0 ? "" : getContext().getString(failureStringId));
    }

    public void stateFailure(@Nullable OnClickListener onRefreshClickListener, @Nullable String failureString) {
        if (!switchState(State.FAILURE, SwitchMode.REPLACE)) {
            return;
        }

        if (mFailureFrameLayout == null) {
            mFailureFrameLayout = (FrameLayout) mLayoutInflater.inflate(R.layout.eb_view_state_failure, this, false);
            mRefreshImageView = (ImageView) mFailureFrameLayout.findViewById(R.id.eb_refresh);
            mFailureTextView = mFailureFrameLayout.findViewById(R.id.eb_failure);
        }
        mRefreshImageView.setOnClickListener(onRefreshClickListener);
        if (failureString != null) {
            mFailureTextView.setText(failureString);
        }

        addView(mFailureFrameLayout);
    }

    //*****************************************************************************************************************
    // No data state.

    private FrameLayout mNoDataFrameLayout;
    private TextView mNoDataTextView;

    public void stateNoData() {
        stateNoData(null);
    }

    public void stateNoData(@StringRes int noDataStringId) {
        stateNoData(noDataStringId == -1 ? null : noDataStringId == 0 ? "" : getContext().getString(noDataStringId));
    }

    public void stateNoData(@Nullable String noDataString) {
        if (!switchState(State.NO_DATA, SwitchMode.KEEP)) {
            return;
        }

        if (mNoDataFrameLayout == null) {
            mNoDataFrameLayout = (FrameLayout) mLayoutInflater.inflate(R.layout.eb_view_state_no_data, this, false);
            mNoDataTextView = mNoDataFrameLayout.findViewById(R.id.eb_no_data);
        }
        if (noDataString != null) {
            mNoDataTextView.setText(noDataString);
        }

        addView(mNoDataFrameLayout);
    }

    //*****************************************************************************************************************
    // Progressing state.

    private FrameLayout mProgressingFrameLayout;
    private ProgressBar mProgressBar;

    public void stateProgressing() {
        if (!switchState(State.PROGRESSING, SwitchMode.OVERLAY)) {
            return;
        }

        if (mProgressingFrameLayout == null) {
            mProgressingFrameLayout = (FrameLayout) mLayoutInflater.inflate(R.layout.eb_view_state_progressing, this,
                    false);
            mProgressBar = (ProgressBar) mProgressingFrameLayout.findViewById(R.id.progress_bar);
        }

        addView(mProgressingFrameLayout);
    }

    public void stateProgressing(int progress) {
        if (mState != State.PROGRESSING) {
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
     * General method for all states except {@link State#NONE}.
     *
     * @return Whether state has changed.
     */
    private boolean switchState(@NonNull State state, @NonNull SwitchMode switchMode) {
        if (mState == state) {
            return false;
        }

        if (mState != State.NONE) {
            removeViewAt(getChildCount() - 1);
        }

        mState = state;

        setSwitchMode(switchMode);

        return true;
    }
}
