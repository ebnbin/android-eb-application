package com.ebnbin.ebapplication.sample;

import android.os.Bundle;
import android.support.annotation.Nullable;

import com.ebnbin.ebapplication.base.EBActivity;
import com.ebnbin.ebapplication.fragment.WebViewFragment;

public final class MainActivity extends EBActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        WebViewFragment webViewFragment = (WebViewFragment) getSupportFragmentManager()
                .findFragmentByTag(WebViewFragment.TAG);
        if (webViewFragment == null) {
            String url = "http://gank.io";
            webViewFragment = WebViewFragment.newInstance(url);
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(android.R.id.content, webViewFragment, WebViewFragment.TAG)
                    .commit();
        }
    }
}
