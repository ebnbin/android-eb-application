package com.ebnbin.ebapplication.sample;

import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.ebnbin.ebapplication.context.ui.EBActivity;

public final class MainActivity extends EBActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SampleFragment sampleFragment = new SampleFragment();

        if (getFragmentHelper().canAdd(sampleFragment)) {
            FragmentTransaction ft = getFragmentManager().beginTransaction();
            getFragmentHelper()
                    .beginTransaction(ft)
                    .add(sampleFragment)
                    .endTransaction();
            ft.commit();
        }
    }
}
