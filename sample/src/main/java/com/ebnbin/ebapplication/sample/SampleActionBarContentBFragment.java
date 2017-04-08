package com.ebnbin.ebapplication.sample;

import android.support.annotation.NonNull;
import android.support.v4.widget.NestedScrollView;
import android.view.View;

import com.ebnbin.ebapplication.context.ui.EBActionBarFragment;
import com.ebnbin.ebapplication.context.ui.EBFragment;

public final class SampleActionBarContentBFragment extends EBFragment {
    @Override
    protected int overrideContentViewLayout() {
        return R.layout.sample_action_bar_content_b_fragment;
    }

    private NestedScrollView mNestedScrollView;

    @Override
    protected void onInitContentView(@NonNull View contentView) {
        super.onInitContentView(contentView);

        mNestedScrollView = (NestedScrollView) contentView;

        EBActionBarFragment.addNestedScrollingChild(this, mNestedScrollView);
    }

    @Override
    public void onDestroyView() {
        EBActionBarFragment.removeNestedScrollingChild(this, mNestedScrollView);

        super.onDestroyView();
    }
}
