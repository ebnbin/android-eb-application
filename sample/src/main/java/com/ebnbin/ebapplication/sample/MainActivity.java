package com.ebnbin.ebapplication.sample;

import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.ebnbin.ebapplication.context.ui.EBActivity;

public final class MainActivity extends EBActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SampleActionBarFragment sampleActionBarFragment = new SampleActionBarFragment();

        if (getFragmentHelper().canAdd(sampleActionBarFragment)) {
            FragmentTransaction ft = getFragmentManager().beginTransaction();
            getFragmentHelper()
                    .beginTransaction(ft)
                    .add(sampleActionBarFragment)
                    .endTransaction();
            ft.commit();
        }
    }
}
