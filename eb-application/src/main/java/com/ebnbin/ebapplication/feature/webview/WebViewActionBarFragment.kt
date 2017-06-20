package com.ebnbin.ebapplication.feature.webview

import android.os.Bundle
import android.view.View
import com.ebnbin.ebapplication.context.EBActionBarFragment

/**
 * A fragment loads a url using [WebViewFragment] with an ActionBar.
 */
class WebViewActionBarFragment : EBActionBarFragment() {
    private val url: String by lazy {
        arguments.getString(ARG_URL)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val fragment = WebViewFragment.newInstance(url)
        fragmentHelper.set(fragment)
    }

    //*****************************************************************************************************************

    companion object {
        private const val ARG_URL = "url"

        fun newInstance(url: String): WebViewActionBarFragment {
            val args = Bundle()
            args.putString(ARG_URL, url)

            val fragment = WebViewActionBarFragment()
            fragment.arguments = args

            return fragment
        }
    }
}
