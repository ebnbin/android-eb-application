package com.ebnbin.ebapplication.sample;

import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.ebnbin.ebapplication.context.ui.EBActionBarFragment;

public final class SampleActionBarFragment extends EBActionBarFragment {
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        SampleActionBarContentBFragment sampleActionBarContentBFragment = new SampleActionBarContentBFragment();
        if (getFragmentHelper().canAdd(sampleActionBarContentBFragment)) {
            FragmentTransaction ft = getFragmentManager().beginTransaction();
            getFragmentHelper()
                    .beginTransaction(ft)
                    .add(COORDINATOR_LAYOUT_CONTENT_CONTAINER_ID, sampleActionBarContentBFragment)
                    .endTransaction();
            ft.commit();
        }
    }
}
