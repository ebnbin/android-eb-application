package com.ebnbin.ebapplication.view.viewpager

import android.content.Context
import android.support.v4.view.ViewPager
import android.util.AttributeSet
import android.view.MotionEvent

open class EBViewPager : ViewPager {
    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)

    var pagingEnabled = true

    override fun onTouchEvent(ev: MotionEvent?): Boolean {
        if (pagingEnabled) {
            return super.onTouchEvent(ev)
        }

        return false
    }

    override fun onInterceptTouchEvent(ev: MotionEvent?): Boolean {
        if (pagingEnabled) {
            return super.onInterceptTouchEvent(ev)
        }

        return false
    }
}
