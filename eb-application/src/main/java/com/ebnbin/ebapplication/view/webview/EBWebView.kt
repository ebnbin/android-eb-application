package com.ebnbin.ebapplication.view.webview

import android.content.Context
import android.util.AttributeSet

/**
 * Custom [android.webkit.WebView].
 */
open class EBWebView : NestedScrollWebView {
    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr)
}
