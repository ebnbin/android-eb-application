package com.ebnbin.ebapplication.fragment.webview;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
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
public final class WebViewContentFragment extends EBFragment implements AdvancedWebView.Listener {
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

    private SwipeRefreshLayout mSwipeRefreshLayout;
    private CustomWebView mWebView;

    public CustomWebView getWebView() {
        return mWebView;
    }

    private void webViewOnSaveInstanceState(@Nullable Bundle outState) {
        mWebView.saveState(outState);
    }

    @Override
    protected int overrideContentViewLayout() {
        return R.layout.eb_fragment_web_view_content;
    }

    @Override
    protected void onInitContentView(@NonNull View contentView) {
        super.onInitContentView(contentView);

        mSwipeRefreshLayout = (SwipeRefreshLayout) contentView.findViewById(R.id.eb_swipe_refresh_layout);
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

        EBActionBarFragment actionBarFragment = getActionBarParentFragment();
        if (actionBarFragment != null) {
            actionBarFragment.addNestedScrollingView(mWebView);
            actionBarFragment.setActionBarMode(EBActionBarFragment.ACTION_BAR_MODE_SCROLL, true,
                    savedInstanceState == null ? true : null, false);

            actionBarFragment.getToolbar().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    WebViewDialogFragment.showDialog(getChildFragmentManager(), mWebView.getTitle(), mWebView.getUrl(),
                            mWebView.getFavicon());
                }
            });

            actionBarFragment.getToolbar().setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    String url = mWebView.getUrl();

                    ClipData clipData = ClipData.newPlainText(url, url);

                    ClipboardManager cm = (ClipboardManager) getContext().getSystemService(Context.CLIPBOARD_SERVICE);
                    cm.setPrimaryClip(clipData);

                    Toast.makeText(getContext(), R.string.eb_dialog_fragment_web_view_copy_url_success,
                            Toast.LENGTH_SHORT).show();

                    return true;
                }
            });
        }

        mSwipeRefreshLayout.setColorSchemeColors(EBUtil.getColorAttr(getContext(), R.attr.colorAccent));
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mWebView.reload();
            }
        });
    }

    @Override
    public void onDestroyView() {
        EBActionBarFragment actionBarFragment = getActionBarParentFragment();
        if (actionBarFragment != null) {
            actionBarFragment.removeNestedScrollingView(mWebView);
        }

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
            openInBrowser();

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void openInBrowser() {
        if (AdvancedWebView.Browsers.hasAlternative(getContext())) {
            Activity activity = getActivity();
            if (activity != null) {
                try {
                    AdvancedWebView.Browsers.openUrl(activity, mWebView.getUrl());
                } catch (ActivityNotFoundException e) {
                    EBUtil.log(e);

                    Toast.makeText(getContext(), R.string.eb_fragment_web_view_url_error, Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(getContext(), R.string.eb_fragment_web_view_url_error, Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(getContext(), R.string.eb_fragment_web_view_url_error, Toast.LENGTH_SHORT).show();
        }
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

        mSwipeRefreshLayout.setRefreshing(false);
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

        mSwipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public void onDownloadRequested(final String url, String suggestedFilename, String mimeType, long contentLength,
            String contentDisposition, String userAgent) {
        openInBrowser();

        mSwipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public void onExternalPageRequest(final String url) {
        openInBrowser();

        mSwipeRefreshLayout.setRefreshing(false);
    }
}
