package com.ebnbin.ebapplication.context

import android.content.Context
import android.support.v4.view.PagerAdapter
import android.util.AttributeSet
import com.ebnbin.eb.util.EBRuntimeException
import com.ebnbin.ebapplication.view.viewpager.EBViewPager

class EBBottomNavigationViewPager : EBViewPager {
    constructor(context: Context?) : super(context) {
        init()
    }

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {
        init()
    }

    private fun init() {
        pagingEnabled = false
    }

    var pagerAdapter: EBBottomNavigationPagerAdapter? = null

    override fun setAdapter(adapter: PagerAdapter?) {
        if (adapter != null && adapter !is EBBottomNavigationPagerAdapter) {
            throw EBRuntimeException()
        }

        super.setAdapter(adapter)

        pagerAdapter = adapter as EBBottomNavigationPagerAdapter?
    }
}
