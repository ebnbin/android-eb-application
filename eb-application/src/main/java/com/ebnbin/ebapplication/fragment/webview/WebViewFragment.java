package com.ebnbin.ebapplication.fragment.webview;

import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;

import com.ebnbin.ebapplication.context.ui.EBActionBarFragment;

import im.delight.android.webview.AdvancedWebView;

/**
 * A fragment that loads a url using {@link AdvancedWebView}.
 */
public final class WebViewFragment extends EBActionBarFragment {
    //*****************************************************************************************************************
    // Arguments.

    private static final String ARG_URL = "url";

    public static WebViewFragment newInstance(@NonNull String url) {
        Bundle args = new Bundle();
        args.putString(ARG_URL, url);

        WebViewFragment webViewFragment = new WebViewFragment();
        webViewFragment.setArguments(args);

        return webViewFragment;
    }

    private String mUrl;

    public String getUrl() {
        return mUrl;
    }

    @Override
    protected void onInitArguments(@NonNull Bundle args) {
        super.onInitArguments(args);

        mUrl = args.getString(ARG_URL);
    }

    //*****************************************************************************************************************
    // Content view.

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        WebViewContentFragment webViewContentFragment = WebViewContentFragment.newInstance(mUrl);
        if (getFragmentHelper().canAdd(webViewContentFragment)) {
            FragmentTransaction ft = getChildFragmentManager().beginTransaction();
            getFragmentHelper()
                    .beginTransaction(ft)
                    .add(COORDINATOR_LAYOUT_CONTENT_CONTAINER_ID, webViewContentFragment)
                    .endTransaction();
            ft.commit();
        }
    }
}
