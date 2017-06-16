package com.ebnbin.ebapplication.context.ui;

import android.animation.AnimatorInflater;
import android.animation.StateListAnimator;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.IntDef;
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

import com.ebnbin.eb.util.EBRuntimeException;
import com.ebnbin.ebapplication.R;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Base fragment with ActionBar.
 */
public abstract class EBActionBarFragment extends EBFragment {
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initFragmentHelper(savedInstanceState);
    }

    private void initFragmentHelper(@Nullable Bundle savedInstanceState) {
        getFragmentHelper().setDefGroup(COORDINATOR_LAYOUT_CONTENT_CONTAINER_ID);
    }

    @Override
    protected int overrideContentViewLayout() {
        return R.layout.eb_fragment_action_bar;
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

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mCoordinatorLayout = view.findViewById(R.id.eb_coordinator_layout);
        mAppBarLayout = view.findViewById(R.id.eb_app_bar_layout);
        mCollapsingToolbarLayout = view.findViewById(R.id.eb_collapsing_toolbar_layout);
        mCollapsingToolbarLayoutContentContainerFrameLayout = view
                .findViewById(R.id.eb_collapsing_toolbar_layout_content_container);
        mToolbar = view.findViewById(R.id.eb_toolbar);
        mCoordinatorLayoutContentContainerFrameLayout = view
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
                        && mActionBarMode == ACTION_BAR_MODE_SCROLL) {
                    mAppBarLayout.setStateListAnimator(mIgnoreExpandedAppBarStateListAnimator);
                } else {
                    mAppBarLayout.setStateListAnimator(mDefaultAppBarStateListAnimator);
                }

                mActionBarModeExpanded = mAppBarLayoutVisibleHeight != 0;
            }
        });

        actionBarModeOnRestoreInstanceState(savedInstanceState);
    }

    private StateListAnimator mDefaultAppBarStateListAnimator;
    private StateListAnimator mIgnoreExpandedAppBarStateListAnimator;

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

    public static final int ACTION_BAR_MODE_STANDARD = 0;
    public static final int ACTION_BAR_MODE_SCROLL = 1;

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({ACTION_BAR_MODE_STANDARD, ACTION_BAR_MODE_SCROLL})
    public @interface ActionBarMode {
    }

    @ActionBarMode
    private int mActionBarMode = -1;

    private boolean mActionBarModeExpanded = true;

    @ActionBarMode
    public int getActionBarMode() {
        return mActionBarMode;
    }

    public void setActionBarMode(@ActionBarMode int actionBarMode, boolean forceInvalidate, @Nullable Boolean expanded,
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

        setActionBarModeByConstants(actionBarMode);

        if (animate) {
            mCollapsingToolbarLayout.postDelayed(mSetCollapsingToolbarLayoutScrollFlagsRunnable,
                    APP_BAR_LAYOUT_ANIMATION_DURATION_STANDARD);
        } else {
            mSetCollapsingToolbarLayoutScrollFlagsRunnable.run();
        }
    }

    private void setActionBarModeByConstants(@ActionBarMode int actionBarMode) {
        if (mActionBarMode == actionBarMode) {
            return;
        }

        ActionBarModeConstants actionBarModeConstants = ACTION_BAR_MODE_CONSTANTS_ARRAY_MAP.get(actionBarMode);
        if (actionBarModeConstants == null) {
            return;
        }

        mCollapsingToolbarLayoutContentContainerFrameLayout.setVisibility(
                actionBarModeConstants.collapsingToolbarLayoutContentContainerFrameLayoutVisibility);
        setAppBarLayoutCanDrag(actionBarModeConstants.appBarLayoutCanDrag);
        setNestedScrollingEnabled(actionBarModeConstants.nestedScrollingEnabled);
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
        @ActionBarMode
        private final int mActionBarMode;

        public SetCollapsingToolbarLayoutScrollFlagsRunnable(@ActionBarMode int actionBarMode) {
            mActionBarMode = actionBarMode;
        }

        @Override
        public void run() {
            mAppBarLayout.removeCallbacks(this);

            mSetCollapsingToolbarLayoutScrollFlagsRunnable = null;

            setCollapsingToolbarLayoutScrollFlagsByConstants(mActionBarMode);

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
    // CollapsingToolbarLayout scroll flags.

    private int mCollapsingToolbarLayoutScrollFlags = -1;

    private void setCollapsingToolbarLayoutScrollFlags(int flags) {
        if (mCollapsingToolbarLayoutScrollFlags == flags) {
            return;
        }

        mCollapsingToolbarLayoutScrollFlags = flags;

        AppBarLayout.LayoutParams params = (AppBarLayout.LayoutParams) mCollapsingToolbarLayout.getLayoutParams();
        params.setScrollFlags(mCollapsingToolbarLayoutScrollFlags);
    }

    private void setCollapsingToolbarLayoutScrollFlagsByConstants(@ActionBarMode int actionBarMode) {
        if (mActionBarMode == actionBarMode) {
            return;
        }

        ActionBarModeConstants actionBarModeConstants = ACTION_BAR_MODE_CONSTANTS_ARRAY_MAP.get(actionBarMode);
        if (actionBarModeConstants == null) {
            return;
        }

        setCollapsingToolbarLayoutScrollFlags(actionBarModeConstants.collapsingToolbarLayoutScrollFlags);
    }

    //*****************************************************************************************************************
    // Instance state.

    private static final String INSTANCE_STATE_ACTION_BAR_MODE = "action_bar_mode";
    private static final String INSTANCE_STATE_ACTION_BAR_MODE_EXPANDED = "action_bar_mode_expanded";

    private void actionBarModeOnRestoreInstanceState(@Nullable Bundle savedInstanceState) {
        if (savedInstanceState == null) {
            setActionBarMode(ACTION_BAR_MODE_STANDARD, true, true, false);
            return;
        }

        int actionBarMode = savedInstanceState.getInt(INSTANCE_STATE_ACTION_BAR_MODE, ACTION_BAR_MODE_STANDARD);
        @ActionBarMode
        int tmpActionBarMode;
        switch (actionBarMode) {
            case ACTION_BAR_MODE_STANDARD: {
                tmpActionBarMode = ACTION_BAR_MODE_STANDARD;

                break;
            }
            case ACTION_BAR_MODE_SCROLL: {
                tmpActionBarMode = ACTION_BAR_MODE_SCROLL;

                break;
            }
            default: {
                throw new EBRuntimeException();
            }
        }
        boolean actionBarModeExpanded = savedInstanceState.getBoolean(INSTANCE_STATE_ACTION_BAR_MODE_EXPANDED);

        setActionBarMode(tmpActionBarMode, true, actionBarModeExpanded, false);
    }

    private void actionBarModeOnSaveInstanceState(@Nullable Bundle outState) {
        if (outState == null) {
            return;
        }

        outState.putInt(INSTANCE_STATE_ACTION_BAR_MODE, mActionBarMode);
        outState.putBoolean(INSTANCE_STATE_ACTION_BAR_MODE_EXPANDED, mActionBarModeExpanded);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        actionBarModeOnSaveInstanceState(outState);
    }

    //*****************************************************************************************************************
    // ActionBarMode constants.

    private static final ArrayMap<Integer, ActionBarModeConstants> ACTION_BAR_MODE_CONSTANTS_ARRAY_MAP
            = new ArrayMap<>();

    static {
        int standardCollapsingToolbarLayoutScrollFlags = AppBarLayout.LayoutParams.SCROLL_FLAG_SCROLL
                | AppBarLayout.LayoutParams.SCROLL_FLAG_EXIT_UNTIL_COLLAPSED;
        ActionBarModeConstants standardActionBarModeConstants = new ActionBarModeConstants(
                standardCollapsingToolbarLayoutScrollFlags, false, false, View.GONE);
        ACTION_BAR_MODE_CONSTANTS_ARRAY_MAP.put(ACTION_BAR_MODE_STANDARD, standardActionBarModeConstants);

        int scrollCollapsingToolbarLayoutScrollFlags = AppBarLayout.LayoutParams.SCROLL_FLAG_SCROLL
                | AppBarLayout.LayoutParams.SCROLL_FLAG_ENTER_ALWAYS
                | AppBarLayout.LayoutParams.SCROLL_FLAG_SNAP;
        ActionBarModeConstants scrollActionBarModeConstants = new ActionBarModeConstants(
                scrollCollapsingToolbarLayoutScrollFlags, false, true, View.GONE);
        ACTION_BAR_MODE_CONSTANTS_ARRAY_MAP.put(ACTION_BAR_MODE_SCROLL, scrollActionBarModeConstants);
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
