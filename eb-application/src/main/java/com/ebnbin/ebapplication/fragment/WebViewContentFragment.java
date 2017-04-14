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
import com.ebnbin.ebapplication.context.ui.EBActionBarFragment;
import com.ebnbin.ebapplication.context.ui.EBFragment;
import com.ebnbin.ebapplication.context.ui.FragmentHelper;
import com.ebnbin.ebapplication.view.StateFrameLayout;

import im.delight.android.webview.AdvancedWebView;

/**
 * A fragment that loads a url using {@link AdvancedWebView}.
 */
public class WebViewContentFragment extends EBFragment implements AdvancedWebView.Listener {
    //*****************************************************************************************************************
    // Arguments.

    private static final String ARG_URL = "url";

    public static WebViewContentFragment newInstance(@NonNull String url) {
        Bundle args = new Bundle();
        args.putString(ARG_URL, url);

        WebViewContentFragment webViewFragment = new WebViewContentFragment();
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

    private CustomWebView mWebView;

    public CustomWebView getWebView() {
        return mWebView;
    }

    private void webViewOnSaveInstanceState(@Nullable Bundle outState) {
        mWebView.saveState(outState);
    }

    @Override
    protected int overrideContentViewLayout() {
        return R.layout.eb_fragment_web_view;
    }

    @Override
    protected void onInitContentView(@NonNull View contentView) {
        super.onInitContentView(contentView);

        mWebView = (CustomWebView) contentView.findViewById(R.id.eb_web_view);
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

        mWebView.getSettings().setBuiltInZoomControls(true);
        mWebView.getSettings().setDisplayZoomControls(false);

        if (savedInstanceState == null) {
            mWebView.loadUrl(mUrl);
        } else {
            if (mWebView.restoreState(savedInstanceState) == null) {
                mWebView.loadUrl(mUrl);
            }
        }

        EBActionBarFragment.addNestedScrollingChild(this, mWebView);
    }

    @Override
    public void onDestroyView() {
        EBActionBarFragment.removeNestedScrollingChild(this, mWebView);

        super.onDestroyView();
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
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

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
                        AdvancedWebView.Browsers.openUrl(activity, mWebView.getUrl());
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
        ActionBar actionBar = getActionBar();
        if (actionBar != null) {
            actionBar.setTitle(url);
        }

        StateFrameLayout stateFrameLayout = getStateFrameLayout();
        if (stateFrameLayout != null) {
            stateFrameLayout.switchProgressingState(StateFrameLayout.SWITCH_MODE_KEEP);
        }

        EBActionBarFragment actionBarFragment = getActionBarParentFragment();
        if (actionBarFragment != null) {
            actionBarFragment.setActionBarMode(EBActionBarFragment.ACTION_BAR_MODE_STANDARD, true);
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

        EBActionBarFragment actionBarFragment = getActionBarParentFragment();
        if (actionBarFragment != null) {
            actionBarFragment.setActionBarMode(EBActionBarFragment.ACTION_BAR_MODE_STANDARD_SCROLL_ALWAYS, true);
        }
    }

    @Override
    public void onPageError(int errorCode, String description, final String failingUrl) {
        StateFrameLayout stateFrameLayout = getStateFrameLayout();
        if (stateFrameLayout != null) {
            stateFrameLayout.switchFailureState(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mWebView.loadUrl(failingUrl);
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