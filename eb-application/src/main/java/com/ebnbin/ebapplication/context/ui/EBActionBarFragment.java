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
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;

import com.ebnbin.eb.base.EBRuntimeException;
import com.ebnbin.ebapplication.R;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;

/**
 * Base fragment with ActionBar.
 */
public abstract class EBActionBarFragment extends EBFragment {
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

    @Override
    protected void onInitContentView(@NonNull View contentView) {
        super.onInitContentView(contentView);

        mCoordinatorLayout = (CoordinatorLayout) contentView.findViewById(R.id.eb_coordinator_layout);
        mAppBarLayout = (AppBarLayout) contentView.findViewById(R.id.eb_app_bar_layout);
        mCollapsingToolbarLayout = (CollapsingToolbarLayout) contentView
                .findViewById(R.id.eb_collapsing_toolbar_layout);
        mCollapsingToolbarLayoutContentContainerFrameLayout = (FrameLayout) contentView
                .findViewById(R.id.eb_collapsing_toolbar_layout_content_container);
        mToolbar = (Toolbar) contentView.findViewById(R.id.eb_toolbar);
        mCoordinatorLayoutContentContainerFrameLayout = (FrameLayout) contentView
                .findViewById(R.id.eb_coordinator_layout_content_container);
    }

    private int mAppBarLayoutVisibleHeight;

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

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
                        && mActionBarMode == ACTION_BAR_MODE_STANDARD_SCROLL_ALWAYS) {
                    mAppBarLayout.setStateListAnimator(mIgnoreExpandedAppBarStateListAnimator);
                } else {
                    mAppBarLayout.setStateListAnimator(mDefaultAppBarStateListAnimator);
                }
            }
        });

        actionBarModeOnRestoreInstanceState(savedInstanceState);

        setActionBarMode(mActionBarMode, true);
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

    /**
     * Sets whether {@link AppBarLayout} can drag.
     */
    public void invalidateAppBarLayoutCanDrag() {
        mAppBarLayout.post(new Runnable() {
            @Override
            public void run() {
                if (!ViewCompat.isLaidOut(mAppBarLayout)) {
                    mAppBarLayout.post(this);

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
                        return mAppBarScrollable;
                    }
                });
            }
        });
    }

    private boolean mAppBarScrollable = false;
    private boolean mAppBarContentScrollable = false;

    private void setAppBarScrollable(boolean scrollable, boolean contentScrollable) {
        mAppBarScrollable = scrollable;
        mAppBarContentScrollable = contentScrollable;

        invalidateAppBarLayoutCanDrag();
        invalidateNestedScrollingChildren();
    }

    private final ArrayList<NestedScrollingChild> mNestedScrollingChildArrayList = new ArrayList<>();

    private void addNestedScrollingChild(@NonNull NestedScrollingChild nestedScrollingChild) {
        mNestedScrollingChildArrayList.add(nestedScrollingChild);

        invalidateNestedScrollingChildren();
    }

    private void removeNestedScrollingChild(@NonNull NestedScrollingChild nestedScrollingChild) {
        mNestedScrollingChildArrayList.remove(nestedScrollingChild);
    }

    private void invalidateNestedScrollingChildren() {
        for (NestedScrollingChild nestedScrollingChild : mNestedScrollingChildArrayList) {
            nestedScrollingChild.setNestedScrollingEnabled(mAppBarContentScrollable);

            if (nestedScrollingChild instanceof View) {
                View view = (View) nestedScrollingChild;
                view.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        if (event.getActionMasked() != MotionEvent.ACTION_UP
                                && event.getActionMasked() != MotionEvent.ACTION_CANCEL) {
                            return false;
                        }

                        if (mAppBarLayoutVisibleHeight > mToolbar.getHeight()) {
                            return false;
                        }

                        if (mAppBarLayoutVisibleHeight < mToolbar.getHeight() / 2f) {
                            mAppBarLayout.setExpanded(false, true);
                        } else {
                            mAppBarLayout.setExpanded(true, true);
                        }

                        return false;
                    }
                });
            }
        }
    }

    public static void addNestedScrollingChild(@NonNull EBFragment fragment,
            @NonNull NestedScrollingChild nestedScrollingChild) {
        EBActionBarFragment actionBarFragment = getTParent(EBActionBarFragment.class, fragment);
        if (actionBarFragment == null) {
            return;
        }

        actionBarFragment.addNestedScrollingChild(nestedScrollingChild);
    }

    public static void removeNestedScrollingChild(@NonNull EBFragment fragment,
            @NonNull NestedScrollingChild nestedScrollingChild) {
        EBActionBarFragment actionBarFragment = getTParent(EBActionBarFragment.class, fragment);
        if (actionBarFragment == null) {
            return;
        }

        actionBarFragment.removeNestedScrollingChild(nestedScrollingChild);
    }

    //*****************************************************************************************************************
    // ActionBar mode.

    public static final int ACTION_BAR_MODE_STANDARD = 0;
    public static final int ACTION_BAR_MODE_STANDARD_SCROLL_ALWAYS = 1;

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({ACTION_BAR_MODE_STANDARD, ACTION_BAR_MODE_STANDARD_SCROLL_ALWAYS})
    public @interface ActionBarMode {
    }

    @ActionBarMode
    private int mActionBarMode = ACTION_BAR_MODE_STANDARD;

    private Boolean mActionBarModeExpanded = null;

    @ActionBarMode
    public int getActionBarMode() {
        return mActionBarMode;
    }

    public void setActionBarMode(@ActionBarMode int actionBarMode, boolean forceInvalidate) {
        if (!forceInvalidate && mActionBarMode == actionBarMode) {
            return;
        }

        mActionBarMode = actionBarMode;

        switch (mActionBarMode) {
            case ACTION_BAR_MODE_STANDARD: {
                mCollapsingToolbarLayoutContentContainerFrameLayout.setVisibility(View.GONE);

                AppBarLayout.LayoutParams params = (AppBarLayout.LayoutParams) mCollapsingToolbarLayout
                        .getLayoutParams();
                params.setScrollFlags(AppBarLayout.LayoutParams.SCROLL_FLAG_SCROLL
                        | AppBarLayout.LayoutParams.SCROLL_FLAG_EXIT_UNTIL_COLLAPSED);

                setAppBarScrollable(false, false);

                AppCompatActivity activity = getAppCompatActivity();
                if (activity != null) {
                    activity.setSupportActionBar(mToolbar);
                }

                break;
            }
            case ACTION_BAR_MODE_STANDARD_SCROLL_ALWAYS: {
                mCollapsingToolbarLayoutContentContainerFrameLayout.setVisibility(View.GONE);

                AppBarLayout.LayoutParams params = (AppBarLayout.LayoutParams) mCollapsingToolbarLayout
                        .getLayoutParams();
                params.setScrollFlags(AppBarLayout.LayoutParams.SCROLL_FLAG_SCROLL
                        | AppBarLayout.LayoutParams.SCROLL_FLAG_ENTER_ALWAYS
                        | AppBarLayout.LayoutParams.SCROLL_FLAG_ENTER_ALWAYS_COLLAPSED);

                setAppBarScrollable(false, true);

                AppCompatActivity activity = getAppCompatActivity();
                if (activity != null) {
                    activity.setSupportActionBar(mToolbar);
                }

                break;
            }
            default: {
                throw new EBRuntimeException();
            }
        }
    }

    private static final String INSTANCE_STATE_ACTION_BAR_MODE = "action_bar_mode";
    private static final String INSTANCE_STATE_ACTION_BAR_MODE_EXPANDED = "action_bar_mode_expanded";

    private void actionBarModeOnRestoreInstanceState(@Nullable Bundle savedInstanceState) {
        if (savedInstanceState == null) {
            return;
        }

        int actionBarMode = savedInstanceState.getInt(INSTANCE_STATE_ACTION_BAR_MODE, ACTION_BAR_MODE_STANDARD);
        switch (actionBarMode) {
            case ACTION_BAR_MODE_STANDARD: {
                mActionBarMode = ACTION_BAR_MODE_STANDARD;

                break;
            }
            case ACTION_BAR_MODE_STANDARD_SCROLL_ALWAYS: {
                mActionBarMode = ACTION_BAR_MODE_STANDARD_SCROLL_ALWAYS;

                break;
            }
            default: {
                break;
            }
        }
        mActionBarModeExpanded = savedInstanceState.getBoolean(INSTANCE_STATE_ACTION_BAR_MODE_EXPANDED);
    }

    private void actionBarModeOnSaveInstanceState(@Nullable Bundle outState) {
        if (outState == null) {
            return;
        }

        outState.putInt(INSTANCE_STATE_ACTION_BAR_MODE, mActionBarMode);
        if (mActionBarModeExpanded != null) {
            outState.putBoolean(INSTANCE_STATE_ACTION_BAR_MODE_EXPANDED, mActionBarModeExpanded);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        actionBarModeOnSaveInstanceState(outState);
    }
}
