package com.ebnbin.ebapplication.sample;

import android.os.Bundle;
import android.support.annotation.Nullable;

import com.ebnbin.ebapplication.context.ui.EBActivity;
import com.ebnbin.ebapplication.view.StateFrameLayout;

public final class MainActivity extends EBActivity {
    private StateFrameLayout mStateFrameLayout;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        mStateFrameLayout = (StateFrameLayout) findViewById(R.id.state_view);
        mStateFrameLayout.switchLoadingState();
    }
}
