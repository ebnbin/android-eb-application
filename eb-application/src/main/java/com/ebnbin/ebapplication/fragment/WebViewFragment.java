package com.ebnbin.ebapplication.fragment;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.Toast;

import com.ebnbin.eb.util.EBUtil;
import com.ebnbin.ebapplication.R;
import com.ebnbin.ebapplication.context.ui.EBFragment;
import com.ebnbin.ebapplication.context.ui.FragmentHelper;
import com.ebnbin.ebapplication.view.StateFrameLayout;

import im.delight.android.webview.AdvancedWebView;

/**
 * A fragment that loads a url using {@link AdvancedWebView}.
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
    protected int overrideContentViewLayout() {
        return R.layout.eb_fragment_web_view;
    }

    private AdvancedWebView mWebView;

    public AdvancedWebView getWebView() {
        return mWebView;
    }

    private void webViewOnSaveInstanceState(@Nullable Bundle outState) {
        mWebView.saveState(outState);
    }

    @Override
    protected void onInitContentView(@NonNull View contentView) {
        super.onInitContentView(contentView);

        mWebView = (AdvancedWebView) contentView.findViewById(R.id.eb_web_view);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initActionBar();

        mWebView.setListener(this, this);

        mWebView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);

                StateFrameLayout stateFrameLayout = getStateFrameLayout();
                if (stateFrameLayout != null) {
                    stateFrameLayout.setProgress(newProgress);
                }
            }
        });

        if (savedInstanceState == null) {
            mWebView.loadUrl(mUrl);
        } else {
            mWebView.restoreState(savedInstanceState);
        }
    }

    //*****************************************************************************************************************
    // Current url.

    private static final String INSTANCE_STATE_CURRENT_URL = "current_url";

    private String mCurrentUrl;

    public String getCurrentUrl() {
        return mCurrentUrl;
    }

    private void currentUrlOnRestoreInstanceState(@Nullable Bundle savedInstanceState) {
        if (savedInstanceState == null) {
            return;
        }

        mCurrentUrl = savedInstanceState.getString(INSTANCE_STATE_CURRENT_URL);
    }

    private void currentUrlOnSaveInstanceState(@Nullable Bundle outState) {
        if (outState == null) {
            return;
        }

        outState.putString(INSTANCE_STATE_CURRENT_URL, mCurrentUrl);
    }

    //*****************************************************************************************************************
    // Lifecycle.

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        initOptionsMenus();
    }

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

    //*****************************************************************************************************************
    // Overrides.

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        mWebView.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onViewStateRestored(Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);

        currentUrlOnRestoreInstanceState(savedInstanceState);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        currentUrlOnSaveInstanceState(outState);
        webViewOnSaveInstanceState(outState);
    }

    @Override
    public boolean onBackPressed() {
        return !mWebView.onBackPressed() || super.onBackPressed();
    }

    //*****************************************************************************************************************
    // ActionBar.

    private void initActionBar() {
        ActionBar actionBar = getActionBar();
        if (actionBar == null) {
            return;
        }

        actionBar.setTitle(null);

        actionBar.setDisplayHomeAsUpEnabled(true);
    }

    //*****************************************************************************************************************
    // Options menu.

    private void initOptionsMenus() {
        setHasOptionsMenu(true);

        setRestoreActionBarTitle(true);
        setRestoreActionBarDisplayHomeAsUp(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

        inflater.inflate(R.menu.eb_fragment_web_view, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();

        if (itemId == android.R.id.home) {
            FragmentHelper fragmentHelper = getActivityFragmentHelper();
            if (fragmentHelper != null) {
                fragmentHelper.pop();
            }

            return true;
        } else if (itemId == R.id.eb_open_in_browser) {
            if (AdvancedWebView.Browsers.hasAlternative(getContext())) {
                Activity activity = getActivity();
                if (activity != null) {
                    try {
                        AdvancedWebView.Browsers.openUrl(activity, mCurrentUrl);
                    } catch (ActivityNotFoundException e) {
                        EBUtil.log(e);

                        Toast.makeText(getContext(), R.string.eb_fragment_web_view_url_error, Toast.LENGTH_SHORT)
                                .show();
                    }
                }
            }

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    //*****************************************************************************************************************
    // Callbacks.

    @Override
    public void onPageStarted(String url, Bitmap favicon) {
        mCurrentUrl = url;

        ActionBar actionBar = getActionBar();
        if (actionBar != null) {
            actionBar.setTitle(mCurrentUrl);
        }

        StateFrameLayout stateFrameLayout = getStateFrameLayout();
        if (stateFrameLayout != null) {
            stateFrameLayout.switchProgressingState(StateFrameLayout.SWITCH_MODE_KEEP);
        }
    }

    @Override
    public void onPageFinished(String url) {
        ActionBar actionBar = getActionBar();
        if (actionBar != null) {
            actionBar.setTitle(mWebView.getTitle());
        }

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
                    mWebView.loadUrl(mCurrentUrl);
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
