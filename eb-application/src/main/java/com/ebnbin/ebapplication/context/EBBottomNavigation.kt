package com.ebnbin.ebapplication.context

import android.content.Context
import android.support.v4.view.PagerAdapter
import android.support.v4.view.ViewPager
import android.util.AttributeSet
import com.aurelhubert.ahbottomnavigation.AHBottomNavigation
import com.aurelhubert.ahbottomnavigation.AHBottomNavigationItem
import com.ebnbin.eb.util.EBRuntimeException

class EBBottomNavigation : AHBottomNavigation {
    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    private var viewPager: EBBottomNavigationViewPager? = null

    private val viewPagerOnPageChangeListener = ViewPagerOnPageChangeListener()

    private inner class ViewPagerOnPageChangeListener : ViewPager.OnPageChangeListener {
        override fun onPageScrollStateChanged(state: Int) {
        }

        override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
        }

        override fun onPageSelected(position: Int) {
            setCurrentItem(position, true)
        }
    }

    private val viewPagerOnAdapterChangeListener = ViewPagerOnAdapterChangeListener()

    private inner class ViewPagerOnAdapterChangeListener : ViewPager.OnAdapterChangeListener {
        override fun onAdapterChanged(viewPager: ViewPager, oldAdapter: PagerAdapter?, newAdapter: PagerAdapter?) {
            setupWithViewPager(viewPager as EBBottomNavigationViewPager)
        }
    }

    fun setupWithViewPager(viewPager: EBBottomNavigationViewPager?) {
        if (this.viewPager != null) {
            this.viewPager!!.removeOnPageChangeListener(viewPagerOnPageChangeListener)
            this.viewPager!!.removeOnAdapterChangeListener(viewPagerOnAdapterChangeListener)
        }

        this.viewPager = viewPager

        if (this.viewPager == null) {
            removeAllItems()
            setOnTabSelectedListener(null)

            return
        }

        if (this.viewPager!!.pagerAdapter == null) {
            throw EBRuntimeException()
        }

        this.viewPager!!.addOnPageChangeListener(viewPagerOnPageChangeListener)
        this.viewPager!!.addOnAdapterChangeListener(viewPagerOnAdapterChangeListener)

        val pagerAdapter = this.viewPager!!.pagerAdapter as EBBottomNavigationPagerAdapter
        for (i in 0..pagerAdapter.count - 1) {
            addItem(AHBottomNavigationItem(pagerAdapter.getPageTitle(i).toString(), pagerAdapter.getPageIcon(i)))
        }

        setOnTabSelectedListener(OnTabSelectedListener { position, wasSelected ->
            if (wasSelected) return@OnTabSelectedListener false

            viewPager!!.setCurrentItem(position, false)

            return@OnTabSelectedListener true
        })
    }
}
