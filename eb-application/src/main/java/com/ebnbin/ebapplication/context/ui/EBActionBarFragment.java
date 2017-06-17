package com.ebnbin.ebapplication.context.ui;

import android.animation.AnimatorInflater;
import android.animation.StateListAnimator;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.view.NestedScrollingChild;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.ArrayMap;
import android.view.View;
import android.widget.FrameLayout;

import com.ebnbin.ebapplication.R;
import com.ebnbin.ebapplication.view.StateView;

/**
 * Base fragment with ActionBar.
 */
public abstract class EBActionBarFragment extends EBFragment {
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initFragmentHelper();
    }

    private CoordinatorLayout mCoordinatorLayout;
    private AppBarLayout mAppBarLayout;
    private CollapsingToolbarLayout mCollapsingToolbarLayout;
    private FrameLayout mCollapsingToolbarLayoutContentContainerFrameLayout;
    private Toolbar mToolbar;
    private FrameLayout mCoordinatorLayoutContentContainerFrameLayout;

    public CoordinatorLayout getCoordinatorLayout() {
        return mCoordinatorLayout;
    }

    public AppBarLayout getAppBarLayout() {
        return mAppBarLayout;
    }

    public CollapsingToolbarLayout getCollapsingToolbarLayout() {
        return mCollapsingToolbarLayout;
    }

    public FrameLayout getCollapsingToolbarLayoutContentContainerFrameLayout() {
        return mCollapsingToolbarLayoutContentContainerFrameLayout;
    }

    public Toolbar getToolbar() {
        return mToolbar;
    }

    public FrameLayout getCoordinatorLayoutContentContainerFrameLayout() {
        return mCoordinatorLayoutContentContainerFrameLayout;
    }

    private int mAppBarLayoutVisibleHeight;

    private StateListAnimator mDefaultAppBarStateListAnimator;
    private StateListAnimator mIgnoreExpandedAppBarStateListAnimator;

    @Override
    protected void onInitContentView(@NonNull StateView stateView, @Nullable Bundle savedInstanceState) {
        View actionBarContentContainer = getLayoutInflater().inflate(R.layout.eb_fragment_action_bar, stateView, false);
        stateView.addView(actionBarContentContainer);

        mCoordinatorLayout = actionBarContentContainer.findViewById(R.id.eb_coordinator_layout);
        mAppBarLayout = actionBarContentContainer.findViewById(R.id.eb_app_bar_layout);
        mCollapsingToolbarLayout = actionBarContentContainer.findViewById(R.id.eb_collapsing_toolbar_layout);
        mCollapsingToolbarLayoutContentContainerFrameLayout = actionBarContentContainer
                .findViewById(R.id.eb_collapsing_toolbar_layout_content_container);
        mToolbar = actionBarContentContainer.findViewById(R.id.eb_toolbar);
        mCoordinatorLayoutContentContainerFrameLayout = actionBarContentContainer
                .findViewById(R.id.eb_coordinator_layout_content_container);

        AppCompatActivity activity = getAppCompatActivity();
        if (activity != null) {
            activity.setSupportActionBar(mToolbar);
        }

        mDefaultAppBarStateListAnimator = mAppBarLayout.getStateListAnimator();
        mIgnoreExpandedAppBarStateListAnimator = AnimatorInflater.loadStateListAnimator(getContext(),
                R.animator.eb_appbar_state_list_animator_ignore_expanded);

        mAppBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                mAppBarLayoutVisibleHeight = appBarLayout.getHeight() + verticalOffset;
                int toolbarHeight = mToolbar.getHeight();

                if (mAppBarLayoutVisibleHeight == toolbarHeight
                        && mActionBarMode == ActionBarMode.SCROLL) {
                    mAppBarLayout.setStateListAnimator(mIgnoreExpandedAppBarStateListAnimator);
                } else {
                    mAppBarLayout.setStateListAnimator(mDefaultAppBarStateListAnimator);
                }

                mActionBarModeExpanded = mAppBarLayoutVisibleHeight != 0;
            }
        });

        actionBarModeOnRestoreInstanceState(savedInstanceState);

        View contentView = overrideContentView();
        if (contentView == null) {
            int contentViewRes = overrideContentViewLayout();
            if (contentViewRes == 0) {
                return;
            }

            contentView = getLayoutInflater().inflate(contentViewRes, stateView, false);
        }

        mCoordinatorLayoutContentContainerFrameLayout.addView(contentView);
    }

    private void initFragmentHelper() {
        getFragmentHelper().setDefGroup(COORDINATOR_LAYOUT_CONTENT_CONTAINER_ID);
    }

    @Override
    protected void onChangeShared() {
        super.onChangeShared();

        AppCompatActivity activity = getAppCompatActivity();
        if (activity != null) {
            activity.setSupportActionBar(mToolbar);
        }
    }

    @IdRes
    protected static final int COLLAPSING_TOOLBAR_LAYOUT_CONTENT_CONTAINER_ID
            = R.id.eb_collapsing_toolbar_layout_content_container;
    @IdRes
    protected static final int COORDINATOR_LAYOUT_CONTENT_CONTAINER_ID = R.id.eb_coordinator_layout_content_container;

    //*****************************************************************************************************************
    // AppBarLayout scrollable.

    private boolean mAppBarCanDrag = true;

    private void setAppBarLayoutCanDrag(boolean appBarCanDrag) {
        if (mAppBarCanDrag == appBarCanDrag) {
            return;
        }

        mAppBarLayout.post(new Runnable() {
            @Override
            public void run() {
                if (!ViewCompat.isLaidOut(mAppBarLayout)) {
                    mAppBarLayout.postDelayed(this, 16L);

                    return;
                }

                CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) mAppBarLayout
                        .getLayoutParams();
                AppBarLayout.Behavior behavior = (AppBarLayout.Behavior) params.getBehavior();
                if (behavior == null) {
                    return;
                }

                behavior.setDragCallback(new AppBarLayout.Behavior.DragCallback() {
                    @Override
                    public boolean canDrag(@NonNull AppBarLayout appBarLayout) {
                        return mAppBarCanDrag;
                    }
                });
            }
        });
    }

    private View mNestedScrollingView;
    private boolean mNestedScrollingEnabled = true;

    public void addNestedScrollingView(@NonNull View nestedScrollingView) {
        invalidateNestedScrollingEnabled(nestedScrollingView, mNestedScrollingEnabled);
    }

    public void removeNestedScrollingView(@NonNull View nestedScrollingView) {
        invalidateNestedScrollingEnabled(mNestedScrollingView == nestedScrollingView ? null : mNestedScrollingView,
                mNestedScrollingEnabled);
    }

    private void setNestedScrollingEnabled(boolean nestedScrollingEnabled) {
        invalidateNestedScrollingEnabled(mNestedScrollingView, nestedScrollingEnabled);
    }

    private void invalidateNestedScrollingEnabled(@Nullable View nestedScrollingView, boolean nestedScrollingEnabled) {
        boolean needInvalidate = false;

        if (mNestedScrollingView != nestedScrollingView) {
            mNestedScrollingView = nestedScrollingView;

            needInvalidate = true;
        }

        if (mNestedScrollingEnabled != nestedScrollingEnabled) {
            mNestedScrollingEnabled = nestedScrollingEnabled;

            needInvalidate = true;
        }

        if (!needInvalidate) {
            return;
        }

        if (nestedScrollingView instanceof NestedScrollingChild
                && !(nestedScrollingView instanceof SwipeRefreshLayout)) {
            NestedScrollingChild nestedScrollingChild = (NestedScrollingChild) nestedScrollingView;
            nestedScrollingChild.setNestedScrollingEnabled(mNestedScrollingEnabled);
        }
    }

    //*****************************************************************************************************************
    // AppBarLayout animation duration.

    private static final long APP_BAR_LAYOUT_ANIMATION_DURATION_STANDARD = 300L;
    private static final long APP_BAR_LAYOUT_ANIMATION_DURATION_MAX = 600L;

    //*****************************************************************************************************************
    // ActionBar mode.

    public enum ActionBarMode {
        STANDARD,
        SCROLL
    }

    private ActionBarMode mActionBarMode = ActionBarMode.STANDARD;

    private boolean mActionBarModeExpanded = true;

    public ActionBarMode getActionBarMode() {
        return mActionBarMode;
    }

    public void setActionBarMode(ActionBarMode actionBarMode, boolean forceInvalidate, @Nullable Boolean expanded,
            boolean animate) {
        if (mSetCollapsingToolbarLayoutScrollFlagsRunnable != null) {
            mSetCollapsingToolbarLayoutScrollFlagsRunnable.run();
        }

        if (!forceInvalidate && mActionBarMode == actionBarMode) {
            return;
        }

        if (expanded != null) {
            mActionBarModeExpanded = expanded;
        }

        mAppBarLayout.setExpanded(mActionBarModeExpanded, animate);

        mSetCollapsingToolbarLayoutScrollFlagsRunnable = new SetCollapsingToolbarLayoutScrollFlagsRunnable(
                actionBarMode);


        ActionBarModeConstants actionBarModeConstants = ACTION_BAR_MODE_CONSTANTS_ARRAY_MAP.get(actionBarMode);

        mCollapsingToolbarLayoutContentContainerFrameLayout.setVisibility(
                actionBarModeConstants.collapsingToolbarLayoutContentContainerFrameLayoutVisibility);
        setAppBarLayoutCanDrag(actionBarModeConstants.appBarLayoutCanDrag);
        setNestedScrollingEnabled(actionBarModeConstants.nestedScrollingEnabled);

        if (animate) {
            mCollapsingToolbarLayout.postDelayed(mSetCollapsingToolbarLayoutScrollFlagsRunnable,
                    APP_BAR_LAYOUT_ANIMATION_DURATION_STANDARD);
        } else {
            mSetCollapsingToolbarLayoutScrollFlagsRunnable.run();
        }
    }

    private void invalidateToolbar() {
        AppCompatActivity activity = getAppCompatActivity();
        if (activity != null) {
            activity.setSupportActionBar(mToolbar);
        }
    }

    //*****************************************************************************************************************
    // SetCollapsingToolbarLayoutScrollFlagsRunnable.

    private SetCollapsingToolbarLayoutScrollFlagsRunnable mSetCollapsingToolbarLayoutScrollFlagsRunnable;

    private class SetCollapsingToolbarLayoutScrollFlagsRunnable implements Runnable {
        private final ActionBarMode mActionBarMode;

        public SetCollapsingToolbarLayoutScrollFlagsRunnable(ActionBarMode actionBarMode) {
            mActionBarMode = actionBarMode;
        }

        @Override
        public void run() {
            mAppBarLayout.removeCallbacks(this);

            mSetCollapsingToolbarLayoutScrollFlagsRunnable = null;

            ActionBarModeConstants actionBarModeConstants = ACTION_BAR_MODE_CONSTANTS_ARRAY_MAP.get(mActionBarMode);

            AppBarLayout.LayoutParams params = (AppBarLayout.LayoutParams) mCollapsingToolbarLayout.getLayoutParams();
            params.setScrollFlags(actionBarModeConstants.collapsingToolbarLayoutScrollFlags);

            EBActionBarFragment.this.mActionBarMode = mActionBarMode;

            invalidateToolbar();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        if (mSetCollapsingToolbarLayoutScrollFlagsRunnable != null) {
            mSetCollapsingToolbarLayoutScrollFlagsRunnable.run();
        }
    }

    //*****************************************************************************************************************
    // Instance state.

    private static final String INSTANCE_STATE_ACTION_BAR_MODE = "action_bar_mode";
    private static final String INSTANCE_STATE_ACTION_BAR_MODE_EXPANDED = "action_bar_mode_expanded";

    private void actionBarModeOnRestoreInstanceState(@Nullable Bundle savedInstanceState) {
        if (savedInstanceState == null) {
            setActionBarMode(ActionBarMode.STANDARD, true, true, false);
            return;
        }

        ActionBarMode actionBarMode = (ActionBarMode) savedInstanceState.getSerializable(INSTANCE_STATE_ACTION_BAR_MODE);
        boolean actionBarModeExpanded = savedInstanceState.getBoolean(INSTANCE_STATE_ACTION_BAR_MODE_EXPANDED);

        setActionBarMode(actionBarMode, true, actionBarModeExpanded, false);
    }

    private void actionBarModeOnSaveInstanceState(@Nullable Bundle outState) {
        if (outState == null) {
            return;
        }

        outState.putSerializable(INSTANCE_STATE_ACTION_BAR_MODE, mActionBarMode);
        outState.putBoolean(INSTANCE_STATE_ACTION_BAR_MODE_EXPANDED, mActionBarModeExpanded);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        actionBarModeOnSaveInstanceState(outState);
    }

    //*****************************************************************************************************************
    // ActionBarMode constants.

    private static final ArrayMap<ActionBarMode, ActionBarModeConstants> ACTION_BAR_MODE_CONSTANTS_ARRAY_MAP
            = new ArrayMap<>();

    static {
        int standardCollapsingToolbarLayoutScrollFlags = AppBarLayout.LayoutParams.SCROLL_FLAG_SCROLL
                | AppBarLayout.LayoutParams.SCROLL_FLAG_EXIT_UNTIL_COLLAPSED;
        ActionBarModeConstants standardActionBarModeConstants = new ActionBarModeConstants(
                standardCollapsingToolbarLayoutScrollFlags, false, false, View.GONE);
        ACTION_BAR_MODE_CONSTANTS_ARRAY_MAP.put(ActionBarMode.STANDARD, standardActionBarModeConstants);

        int scrollCollapsingToolbarLayoutScrollFlags = AppBarLayout.LayoutParams.SCROLL_FLAG_SCROLL
                | AppBarLayout.LayoutParams.SCROLL_FLAG_ENTER_ALWAYS
                | AppBarLayout.LayoutParams.SCROLL_FLAG_SNAP;
        ActionBarModeConstants scrollActionBarModeConstants = new ActionBarModeConstants(
                scrollCollapsingToolbarLayoutScrollFlags, false, true, View.GONE);
        ACTION_BAR_MODE_CONSTANTS_ARRAY_MAP.put(ActionBarMode.SCROLL, scrollActionBarModeConstants);
    }

    private static final class ActionBarModeConstants {
        public final int collapsingToolbarLayoutScrollFlags;
        public final boolean appBarLayoutCanDrag;
        public final boolean nestedScrollingEnabled;
        public final int collapsingToolbarLayoutContentContainerFrameLayoutVisibility;

        private ActionBarModeConstants(int collapsingToolbarLayoutScrollFlags, boolean appBarLayoutCanDrag,
                boolean nestedScrollingEnabled, int collapsingToolbarLayoutContentContainerFrameLayoutVisibility) {
            this.collapsingToolbarLayoutScrollFlags = collapsingToolbarLayoutScrollFlags;
            this.appBarLayoutCanDrag = appBarLayoutCanDrag;
            this.nestedScrollingEnabled = nestedScrollingEnabled;
            this.collapsingToolbarLayoutContentContainerFrameLayoutVisibility
                    = collapsingToolbarLayoutContentContainerFrameLayoutVisibility;
        }
    }
}
