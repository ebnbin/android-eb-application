package com.ebnbin.ebapplication.fragment;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.webkit.WebView;

import com.ebnbin.eb.base.EBRuntimeException;
import com.ebnbin.eb.util.Util;
import com.ebnbin.ebapplication.base.EBFragment;
import com.ebnbin.ebapplication.view.EBWebView;

/**
 * A fragment that loads a url by {@link WebView}.
 */
public class WebViewFragment extends EBFragment implements EBWebView.Listener {
    public static final String TAG = WebViewFragment.class.getName();

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

    @Override
    protected void onInitArguments(@NonNull Bundle args) {
        super.onInitArguments(args);

        mUrl = args.getString(ARG_URL);
    }

    //*****************************************************************************************************************

    private EBWebView mWebView;

    @Nullable
    @Override
    protected View overrideContentView() {
        mWebView = new EBWebView(getContext());

        onInitWebView(mWebView);

        return mWebView;
    }

    /**
     * Overrides this method to set up {@link EBWebView}.
     */
    protected void onInitWebView(@NonNull EBWebView webView) {
    }

    @NonNull
    public EBWebView getWebView() {
        if (mWebView == null) {
            throw new EBRuntimeException();
        }

        return mWebView;
    }

    //*****************************************************************************************************************

    @Override
    public void onResume() {
        super.onResume();

        mWebView.onResume();
    }

    @Override
    public void onPause() {
        mWebView.onPause();

        super.onPause();
    }

    @Override
    public void onDestroy() {
        mWebView.onDestroy();

        super.onDestroy();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        mWebView.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * Handles back pressed.
     */
    public boolean onBackPressed() {
        return mWebView.onBackPressed();
    }

    //*****************************************************************************************************************
    // Instance state.

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);

        if (savedInstanceState == null) {
            if (!Util.isEmpty(mUrl)) {
                mWebView.loadUrl(mUrl);
            }

            return;
        }

        mWebView.restoreState(savedInstanceState);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        if (outState == null) {
            return;
        }

        mWebView.saveState(outState);
    }

    //*****************************************************************************************************************

    @Override
    public void onPageStarted(String url, Bitmap favicon) {
    }

    @Override
    public void onPageFinished(String url) {
    }

    @Override
    public void onPageError(int errorCode, String description, String failingUrl) {
    }

    @Override
    public void onDownloadRequested(String url, String suggestedFilename, String mimeType, long contentLength,
            String contentDisposition, String userAgent) {
    }

    @Override
    public void onExternalPageRequest(String url) {
    }
}
