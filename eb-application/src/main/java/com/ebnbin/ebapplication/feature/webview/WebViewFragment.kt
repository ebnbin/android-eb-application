package com.ebnbin.ebapplication.feature.webview

import android.content.ActivityNotFoundException
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.view.View
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.widget.Toast
import com.ebnbin.eb.util.EBRuntimeException
import com.ebnbin.eb.util.EBUtil
import com.ebnbin.ebapplication.R
import com.ebnbin.ebapplication.context.EBActionBarFragment
import com.ebnbin.ebapplication.context.EBFragment
import com.ebnbin.ebapplication.view.EBSwipeRefreshLayout
import com.ebnbin.ebapplication.view.StateView
import com.ebnbin.ebapplication.view.webview.EBWebView
import im.delight.android.webview.AdvancedWebView

/**
 * A fragment loads a url using [EBWebView].
 */
class WebViewFragment : EBFragment(), AdvancedWebView.Listener {
    private val url: String by lazy {
        arguments.getString(ARG_URL)
    }

    override fun overrideContentViewLayout(): Int {
        return R.layout.eb_web_view_fragment
    }

    private val swipeRefreshLayout: EBSwipeRefreshLayout by lazy {
        stateView.findViewById(R.id.eb_swipe_refresh_layout) as EBSwipeRefreshLayout
    }

    private val webView: EBWebView by lazy {
        stateView.findViewById(R.id.eb_web_view) as EBWebView
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initActionBar()

        webView.setListener(this, this)
        webView.settings.builtInZoomControls = true
        webView.settings.displayZoomControls = false
        webView.setWebChromeClient(object : WebChromeClient() {
            override fun onProgressChanged(view: WebView?, newProgress: Int) {
                super.onProgressChanged(view, newProgress)

                stateView.stateProgressing(newProgress, true)
            }
        })

        webViewOnRestoreInstanceState(savedInstanceState)

        if (actionBarParentFragment != null) {
            actionBarParentFragment!!.setNestedScrollingChild(webView)
            actionBarParentFragment!!.setActionBarMode(EBActionBarFragment.ActionBarMode.SCROLL, true,
                    if (savedInstanceState == null) true else null, false)
            actionBarParentFragment!!.toolbar.setOnClickListener {
                WebViewDialogFragment.showDialog(childFragmentManager, webView.url, webView.title, webView.favicon)
            }
            actionBarParentFragment!!.toolbar.setOnLongClickListener {
                EBUtil.cilp(context, webView.url)

                true
            }
        }

        swipeRefreshLayout.setOnRefreshListener { webView.reload() }
    }

    private fun webViewOnRestoreInstanceState(savedInstanceState: Bundle?) {
        if (savedInstanceState == null || webView.restoreState(savedInstanceState) == null) {
            webView.loadUrl(url)
        }
    }

    //*****************************************************************************************************************
    // ActionBar.

    private fun initActionBar() {
        ebActivity.supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val toolbar = actionBarParentFragment?.toolbar ?: return

        toolbar.setNavigationIcon(R.drawable.eb_close)
        toolbar.setNavigationOnClickListener { rootFragmentHelper.pop() }
        toolbar.inflateMenu(R.menu.eb_fragment_web_view)
        toolbar.menu.findItem(R.id.eb_open_in_browser).setOnMenuItemClickListener({
            openInBrowser()
            true
        })
    }

    private fun openInBrowser() {
        try {
            if (!AdvancedWebView.Browsers.hasAlternative(context)) {
                throw EBRuntimeException()
            }

            AdvancedWebView.Browsers.openUrl(activity, webView.url)
        } catch (e: EBRuntimeException) {
            openInBrowserException()
        } catch (e: ActivityNotFoundException) {
            openInBrowserException()
        }
    }

    private fun openInBrowserException() {
        Toast.makeText(context, R.string.eb_web_view_url_error, Toast.LENGTH_SHORT).show()
    }

    //*****************************************************************************************************************

    override fun onResume() {
        super.onResume()

        webView.onResume()
    }

    override fun onPause() {
        webView.onPause()

        super.onPause()
    }

    override fun onDestroy() {
        webView.onDestroy()

        super.onDestroy()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        webView.onActivityResult(requestCode, resultCode, data)
    }

    override fun onBackPressed(): Boolean {
        return !webView.onBackPressed() || super.onBackPressed()
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)

        webView.saveState(outState)
    }

    //*****************************************************************************************************************
    // Listeners.

    override fun onPageStarted(url: String, favicon: Bitmap?) {
        actionBarParentFragment?.toolbar?.title = webView.url

        stateView.stateProgressing()
    }

    override fun onPageFinished(url: String) {
        actionBarParentFragment?.toolbar?.title = webView.title

        stateView.clearState()

        swipeRefreshLayout.isRefreshing = false
    }

    override fun onPageError(errorCode: Int, description: String, failingUrl: String) {
        actionBarParentFragment?.appBarLayout?.setExpanded(true, true)

        stateView.stateFailure(object : StateView.OnRefreshListener {
            override fun onRefresh() {
                webView.reload()
            }
        })

        swipeRefreshLayout.isRefreshing = false
    }

    override fun onDownloadRequested(url: String, suggestedFilename: String, mimeType: String, contentLength: Long,
            contentDisposition: String, userAgent: String) {
        stateView.clearState()

        swipeRefreshLayout.isRefreshing = false

        openInBrowser()
    }

    override fun onExternalPageRequest(url: String) {
        stateView.clearState()

        swipeRefreshLayout.isRefreshing = false

        openInBrowser()
    }

    //*****************************************************************************************************************

    companion object {
        private const val ARG_URL = "url"

        fun newInstance(url: String): WebViewFragment {
            val args = Bundle()
            args.putString(ARG_URL, url)

            val fragment = WebViewFragment()
            fragment.arguments = args

            return fragment
        }
    }
}
