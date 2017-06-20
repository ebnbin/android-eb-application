package com.ebnbin.ebapplication.fragment.webview

import android.app.AlertDialog
import android.app.Dialog
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v4.app.FragmentManager
import com.ebnbin.eb.util.EBUtil
import com.ebnbin.ebapplication.R

/**
 * Shows WebView info.
 */
class WebViewDialogFragment : DialogFragment() {
    private val url: String? by lazy {
        arguments.getString(ARG_URL)
    }

    private val title: String? by lazy {
        arguments.getString(ARG_TITLE)
    }

    private val favicon: Bitmap? by lazy {
        arguments.getParcelable<Bitmap>(ARG_FAVICON)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(context)

        builder.setMessage(url)
        if (url != null) {
            builder.setNeutralButton(R.string.eb_web_view_copy_url) { _, _ ->
                EBUtil.cilp(context, url!!)
            }
        }
        if (title != null) {
            builder.setTitle(title)
        }
        if (favicon != null) {
            builder.setIcon(BitmapDrawable(resources, favicon))
        }
        builder.setPositiveButton(R.string.eb_ok) { _, _ -> dismiss() }

        return builder.create()
    }

    companion object {
        private val ARG_URL = "url"
        private val ARG_TITLE = "title"
        private val ARG_FAVICON = "favicon"

        fun showDialog(fm: FragmentManager, url: String?, title: String?, favicon: Bitmap?) {
            val args = Bundle()
            args.putString(ARG_URL, url)
            args.putString(ARG_TITLE, title)
            args.putParcelable(ARG_FAVICON, favicon)

            val fragment = WebViewDialogFragment()
            fragment.arguments = args

            fragment.show(fm, null)
        }
    }
}
