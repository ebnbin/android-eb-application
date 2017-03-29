package com.ebnbin.ebapplication.sample;

import android.os.Bundle;
import android.support.annotation.Nullable;

import com.ebnbin.ebapplication.base.EBActivity;

public final class MainActivity extends EBActivity {
    private SampleActionBarFragment mSampleHomeFragment;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mSampleHomeFragment = (SampleActionBarFragment) getSupportFragmentManager()
                .findFragmentByTag(SampleActionBarFragment.class.getName());
        if (mSampleHomeFragment == null) {
            mSampleHomeFragment = new SampleActionBarFragment();
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(android.R.id.content, mSampleHomeFragment, SampleActionBarFragment.class.getName())
                    .commit();
        }
    }
}
