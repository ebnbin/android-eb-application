package com.ebnbin.ebapplication.view.webview

import android.content.Context
import android.support.v4.view.MotionEventCompat
import android.support.v4.view.NestedScrollingChild
import android.support.v4.view.NestedScrollingChildHelper
import android.support.v4.view.ViewCompat
import android.util.AttributeSet
import android.view.MotionEvent

/**
 * Copy from [NestedScrollWebView](https://github.com/rhlff/NestedScrollWebView).
 */
open class NestedScrollWebView : SupportAdvancedWebView, NestedScrollingChild {
    private var lastMotionY: Int = 0

    private val scrollOffset = IntArray(2)
    private val scrollConsumed = IntArray(2)

    private var nestedYOffset: Int = 0

    private var childHelper: NestedScrollingChildHelper? = null

    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init()
    }

    private fun init() {
        childHelper = NestedScrollingChildHelper(this)
        isNestedScrollingEnabled = true
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        var result = false

        val trackedEvent = MotionEvent.obtain(event)

        @Suppress("DEPRECATION")
        val action = MotionEventCompat.getActionMasked(event)

        if (action == MotionEvent.ACTION_DOWN) {
            nestedYOffset = 0
        }

        val y = event.y.toInt()

        event.offsetLocation(0f, nestedYOffset.toFloat())

        when (action) {
            MotionEvent.ACTION_DOWN -> {
                lastMotionY = y
                startNestedScroll(ViewCompat.SCROLL_AXIS_VERTICAL)
                result = super.onTouchEvent(event)
            }
            MotionEvent.ACTION_MOVE -> {
                var deltaY = lastMotionY - y

                if (dispatchNestedPreScroll(0, deltaY, scrollConsumed, scrollOffset)) {
                    deltaY -= scrollConsumed[1]
                    trackedEvent.offsetLocation(0f, scrollOffset[1].toFloat())
                    nestedYOffset += scrollOffset[1]
                }

                val oldY = scrollY
                lastMotionY = y - scrollOffset[1]
                if (deltaY < 0) {
                    val newScrollY = Math.max(0, oldY + deltaY)
                    deltaY -= newScrollY - oldY
                    if (dispatchNestedScroll(0, newScrollY - deltaY, 0, deltaY, scrollOffset)) {
                        lastMotionY -= scrollOffset[1]
                        trackedEvent.offsetLocation(0f, scrollOffset[1].toFloat())
                        nestedYOffset += scrollOffset[1]
                    }
                }

                result = super.onTouchEvent(trackedEvent)
                trackedEvent.recycle()
            }
            MotionEvent.ACTION_POINTER_DOWN, MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                stopNestedScroll()
                result = super.onTouchEvent(event)
            }
        }
        return result
    }

    //*****************************************************************************************************************
    // NestedScrollingChild

    override fun setNestedScrollingEnabled(enabled: Boolean) {
        childHelper!!.isNestedScrollingEnabled = enabled
    }

    override fun isNestedScrollingEnabled(): Boolean {
        return childHelper!!.isNestedScrollingEnabled
    }

    override fun startNestedScroll(axes: Int): Boolean {
        return childHelper!!.startNestedScroll(axes)
    }

    override fun stopNestedScroll() {
        childHelper!!.stopNestedScroll()
    }

    override fun hasNestedScrollingParent(): Boolean {
        return childHelper!!.hasNestedScrollingParent()
    }

    override fun dispatchNestedScroll(dxConsumed: Int, dyConsumed: Int, dxUnconsumed: Int, dyUnconsumed: Int,
            offsetInWindow: IntArray?): Boolean {
        return childHelper!!.dispatchNestedScroll(dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed, offsetInWindow)
    }

    override fun dispatchNestedPreScroll(dx: Int, dy: Int, consumed: IntArray?, offsetInWindow: IntArray?): Boolean {
        return childHelper!!.dispatchNestedPreScroll(dx, dy, consumed, offsetInWindow)
    }

    override fun dispatchNestedFling(velocityX: Float, velocityY: Float, consumed: Boolean): Boolean {
        return childHelper!!.dispatchNestedFling(velocityX, velocityY, consumed)
    }

    override fun dispatchNestedPreFling(velocityX: Float, velocityY: Float): Boolean {
        return childHelper!!.dispatchNestedPreFling(velocityX, velocityY)
    }
}
