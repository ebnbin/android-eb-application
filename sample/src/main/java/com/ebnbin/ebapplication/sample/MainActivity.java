package com.ebnbin.ebapplication.sample;

import android.os.Bundle;
import android.support.annotation.Nullable;

import com.ebnbin.ebapplication.context.ui.EBActivity;

public final class MainActivity extends EBActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SampleFragment sampleFragment = new SampleFragment();
        getFragmentManagerHelper().add(sampleFragment, null, true, false);
    }
}
