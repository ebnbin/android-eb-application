package com.ebnbin.ebapplication.context.ui;

import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.Toolbar;

import com.ebnbin.ebapplication.R;

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

        AppCompatActivity activity = getAppCompatActivity();
        if (activity != null) {
            activity.setActionBar(mToolbar);
        }
    }

    @IdRes
    protected static final int COLLAPSING_TOOLBAR_LAYOUT_CONTENT_CONTAINER_ID
            = R.id.eb_collapsing_toolbar_layout_content_container;
    @IdRes
    protected static final int COORDINATOR_LAYOUT_CONTENT_CONTAINER_ID = R.id.eb_coordinator_layout_content_container;
}
