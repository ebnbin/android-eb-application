package com.ebnbin.ebapplication.view

import android.content.Context
import android.support.v4.widget.SwipeRefreshLayout
import android.util.AttributeSet
import com.ebnbin.eb.util.EBUtil
import com.ebnbin.ebapplication.R

/**
 * Themed [SwipeRefreshLayout].
 */
class EBSwipeRefreshLayout : SwipeRefreshLayout {
    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init()
    }

    private fun init() {
        setColorSchemeColors(EBUtil.getColorAttr(context, R.attr.colorAccent))
    }
}
