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

        String tag = getFragmentManagerHelper().validTag(sampleFragment);
        if (getFragmentManagerHelper().canAdd(tag)) {
            FragmentTransaction ft = getFragmentManager().beginTransaction();
            getFragmentManagerHelper().beginTransaction(ft);

            getFragmentManagerHelper().add(tag, sampleFragment);
            getFragmentManagerHelper().push();

            getFragmentManagerHelper().endTransaction();
            ft.commit();
        }
    }
}
