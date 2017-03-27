package com.ebnbin.ebapplication.sample;

import android.os.Bundle;
import android.support.annotation.Nullable;

import com.ebnbin.ebapplication.base.EBActivity;

public final class MainActivity extends EBActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SampleFragment sampleFragment = (SampleFragment) getSupportFragmentManager()
                .findFragmentByTag(SampleFragment.class.getName());
        if (sampleFragment == null) {
            sampleFragment = new SampleFragment();
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(android.R.id.content, sampleFragment, SampleFragment.class.getName())
                    .commit();
        }
    }
}
