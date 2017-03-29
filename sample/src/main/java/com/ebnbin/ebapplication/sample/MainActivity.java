package com.ebnbin.ebapplication.sample;

import android.os.Bundle;
import android.support.annotation.Nullable;

import com.ebnbin.ebapplication.base.EBActivity;

public final class MainActivity extends EBActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SampleActionBarFragment sampleActionBarFragment = new SampleActionBarFragment();
        setHomeFragment(sampleActionBarFragment);
    }
}
