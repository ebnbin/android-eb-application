package com.ebnbin.ebapplication.sample;

import android.os.Bundle;
import android.support.annotation.Nullable;

import com.ebnbin.ebapplication.base.EBActivity;

public final class MainActivity extends EBActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SampleWebViewFragment sampleWebViewFragment = (SampleWebViewFragment) getSupportFragmentManager()
                .findFragmentByTag(SampleWebViewFragment.class.getName());
        if (sampleWebViewFragment == null) {
            sampleWebViewFragment = new SampleWebViewFragment();
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(android.R.id.content, sampleWebViewFragment, SampleWebViewFragment.class.getName())
                    .commit();
        }
    }
}
