package com.ebnbin.ebapplication.base;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.view.NestedScrollingChild;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.ebnbin.ebapplication.R;

/**
 * This fragment uses {@link CoordinatorLayout}, {@link AppBarLayout}, {@link CollapsingToolbarLayout} and
 * {@link Toolbar} to custom {@link ActionBar}.
 */
public abstract class EBActionBarFragment extends EBFragment {
    @Override
    protected int overrideContentViewLayout() {
        return R.layout.eb_action_bar_fragment;
    }

    private CoordinatorLayout mCoordinatorLayout;
    private AppBarLayout mAppBarLayout;
    private CollapsingToolbarLayout mCollapsingToolbarLayout;
    private FrameLayout mCollapsingToolbarLayoutContentViewContainerFrameLayout;
    private Toolbar mToolbar;
    private FrameLayout mAppbarScrollingViewContainerFrameLayout;

    @Override
    protected void onInitContentView(@NonNull View contentView) {
        super.onInitContentView(contentView);

        mCoordinatorLayout = (CoordinatorLayout) contentView.findViewById(R.id.eb_coordinator_layout);
        mAppBarLayout = (AppBarLayout) contentView.findViewById(R.id.eb_app_bar_layout);
        mCollapsingToolbarLayout = (CollapsingToolbarLayout) contentView
                .findViewById(R.id.eb_collapsing_toolbar_layout);
        mCollapsingToolbarLayoutContentViewContainerFrameLayout = (FrameLayout) contentView
                .findViewById(R.id.eb_collapsing_toolbar_layout_content_view_container);
        mToolbar = (Toolbar) contentView.findViewById(R.id.eb_toolbar);
        mAppbarScrollingViewContainerFrameLayout = (FrameLayout) contentView
                .findViewById(R.id.eb_appbar_scrolling_view_container);
    }

    public CoordinatorLayout getCoordinatorLayout() {
        return mCoordinatorLayout;
    }

    public AppBarLayout getAppBarLayout() {
        return mAppBarLayout;
    }

    public CollapsingToolbarLayout getCollapsingToolbarLayout() {
        return mCollapsingToolbarLayout;
    }

    public FrameLayout getCollapsingToolbarLayoutContentViewContainerFrameLayout() {
        return mCollapsingToolbarLayoutContentViewContainerFrameLayout;
    }

    public Toolbar getToolbar() {
        return mToolbar;
    }

    /**
     * Gets this view and calls {@link ViewGroup#addView(View)} to set custom layout.
     */
    public FrameLayout getAppbarScrollingViewContainerFrameLayout() {
        return mAppbarScrollingViewContainerFrameLayout;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setAppBarLayoutCanDrag(false);

        AppCompatActivity appCompatActivity = getAppCompatActivity();
        if (appCompatActivity != null) {
            appCompatActivity.setSupportActionBar(mToolbar);
        }
    }

    /**
     * Sets whether {@link AppBarLayout} can drag.
     */
    public void setAppBarLayoutCanDrag(final boolean canDrag) {
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
                        return canDrag;
                    }
                });
            }
        });
    }

    private NestedScrollingChild mNestedScrollingChild;

    /**
     * Sets the {@link NestedScrollingChild}, and sets {@link NestedScrollingChild#setNestedScrollingEnabled(boolean)}
     * {@code false} as default.
     */
    public void setNestedScrollingChild(@Nullable NestedScrollingChild nestedScrollingChild) {
        if (mNestedScrollingChild == nestedScrollingChild) {
            return;
        }

        mNestedScrollingChild = nestedScrollingChild;

        if (mNestedScrollingChild == null) {
            return;
        }

        mNestedScrollingChild.setNestedScrollingEnabled(false);
    }
}
