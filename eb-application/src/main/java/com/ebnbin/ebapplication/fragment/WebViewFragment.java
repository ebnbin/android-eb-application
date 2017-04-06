package com.ebnbin.ebapplication.fragment;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;

import com.ebnbin.eb.base.EBRuntimeException;
import com.ebnbin.ebapplication.R;
import com.ebnbin.ebapplication.context.ui.EBFragment;
import com.ebnbin.ebapplication.view.StateFrameLayout;

import im.delight.android.webview.AdvancedWebView;

/**
 * A fragment that loads a url by {@link WebView}.
 */
public class WebViewFragment extends EBFragment implements AdvancedWebView.Listener {
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

    private AdvancedWebView mWebView;

    @Nullable
    @Override
    protected View overrideContentView() {
        mWebView = new AdvancedWebView(getContext());

        return mWebView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mWebView.setListener(this, this);

        onInitWebView(mWebView);
    }

    /**
     * Overrides this method to set up {@link AdvancedWebView}.
     */
    protected void onInitWebView(@NonNull AdvancedWebView webView) {
    }

    @NonNull
    public AdvancedWebView getWebView() {
        if (mWebView == null) {
            throw new EBRuntimeException();
        }

        return mWebView;
    }
    //*****************************************************************************************************************
    // Options menu.

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

        inflater.inflate(R.menu.eb_web_view_fragment, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.eb_open_in_browser) {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(mUrl));
            startActivity(intent);

            return true;
        }

        return super.onOptionsItemSelected(item);
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
    @Override
    public boolean onBackPressed() {
        return mWebView.onBackPressed() && super.onBackPressed();
    }

    //*****************************************************************************************************************
    // Instance state.

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);

        if (savedInstanceState == null) {
            if (!TextUtils.isEmpty(mUrl)) {
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
        StateFrameLayout stateFrameLayout = getStateFrameLayout();
        if (stateFrameLayout != null) {
            stateFrameLayout.switchLoadingState();
        }
    }

    @Override
    public void onPageFinished(String url) {
        StateFrameLayout stateFrameLayout = getStateFrameLayout();
        if (stateFrameLayout != null) {
            stateFrameLayout.clearState();
        }
    }

    @Override
    public void onPageError(int errorCode, String description, String failingUrl) {
        StateFrameLayout stateFrameLayout = getStateFrameLayout();
        if (stateFrameLayout != null) {
            stateFrameLayout.switchFailureState(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mWebView.loadUrl(mUrl);
                }
            });
        }
    }

    @Override
    public void onDownloadRequested(String url, String suggestedFilename, String mimeType, long contentLength,
            String contentDisposition, String userAgent) {
    }

    @Override
    public void onExternalPageRequest(String url) {
    }
}
