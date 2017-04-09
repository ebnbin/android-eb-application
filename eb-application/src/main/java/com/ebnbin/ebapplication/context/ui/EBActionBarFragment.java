package com.ebnbin.ebapplication.context.ui;

import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.view.NestedScrollingChild;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.FrameLayout;

import com.ebnbin.ebapplication.R;

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

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setAppBarLayoutCanDrag(mAppBarScrollable);
        invalidateNestedScrollingChildren();

        AppCompatActivity activity = getAppCompatActivity();
        if (activity != null) {
            activity.setSupportActionBar(mToolbar);
        }
    }

    // TODO: Need refactor.
    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);

        if (hidden) {
            return;
        }

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

    private boolean mAppBarScrollable = false;

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
            nestedScrollingChild.setNestedScrollingEnabled(mAppBarScrollable);
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
}
