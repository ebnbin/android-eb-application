package com.ebnbin.ebapplication.sample;

import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.ebnbin.ebapplication.context.ui.EBActivity;
import com.ebnbin.ebapplication.fragment.WebViewFragment;
import com.ebnbin.ebapplication.view.StateFrameLayout;

public final class MainActivity extends EBActivity {
    private StateFrameLayout mStateFrameLayout;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        mStateFrameLayout = (StateFrameLayout) findViewById(R.id.state_view);
        mStateFrameLayout.switchLoadingState();

        mStateFrameLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = "http://ebnbin.com";
                WebViewFragment webViewFragment = WebViewFragment.newInstance(url);

                if (getFragmentManagerHelper().canAdd(url)) {
                    FragmentTransaction ft = getFragmentManager().beginTransaction();
                    getFragmentManagerHelper().beginTransaction(ft);

                    getFragmentManagerHelper().add(url, webViewFragment);
                    getFragmentManagerHelper().push();

                    getFragmentManagerHelper().endTransaction();
                    ft.commit();
                }
            }
        });
    }
}
