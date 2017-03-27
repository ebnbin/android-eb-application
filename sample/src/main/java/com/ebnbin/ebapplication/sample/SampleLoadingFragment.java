package com.ebnbin.ebapplication.sample;

import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ProgressBar;

import com.ebnbin.ebapplication.base.EBFragment;

public final class SampleLoadingFragment extends EBFragment {
    @Nullable
    @Override
    protected View overrideContentView() {
        return new ProgressBar(getContext());
    }
}
