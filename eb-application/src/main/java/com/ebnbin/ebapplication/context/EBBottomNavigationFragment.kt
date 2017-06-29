package com.ebnbin.ebapplication.context

import android.os.Bundle
import android.support.v7.widget.RecyclerView
import android.view.View
import com.aurelhubert.ahbottomnavigation.AHBottomNavigation
import com.ebnbin.eb.util.EBUtil
import com.ebnbin.ebapplication.R

abstract class EBBottomNavigationFragment : EBFragment() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initFragmentHelper()
    }

    private fun initFragmentHelper() {
        fragmentHelper.defGroup = EBBottomNavigationFragment.fragmentContainerId
    }

    override fun overrideContentViewLayout(): Int {
        return R.layout.eb_fragment_bottom_navigation
    }

    val bottomNavigation: AHBottomNavigation by lazy {
        stateView.findViewById<AHBottomNavigation>(R.id.eb_bottom_navigation)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        bottomNavigation.titleState = AHBottomNavigation.TitleState.ALWAYS_SHOW
        bottomNavigation.defaultBackgroundColor = EBUtil.getColorAttr(context, R.attr.ebColorCard)
        bottomNavigation.accentColor = EBUtil.getColorAttr(context, R.attr.colorAccent)
        bottomNavigation.inactiveColor = EBUtil.getColorAttr(context, R.attr.ebColorIconDisabled)

        bottomNavigation.setOnTabSelectedListener { position, _ ->
            val item = bottomNavigation.getItem(position)
            if (item is EBBottomNavigationItem) {
                fragmentHelper.replace(item.fragment, item.getTitle(context))
            }
            true
        }
    }

    private val recyclerViewOnScrollListener = object : RecyclerView.OnScrollListener() {
        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)

            if (dy > 0f) {
                bottomNavigation.hideBottomNavigation(true)
            } else if (dy < 0f) {
                bottomNavigation.restoreBottomNavigation(true)
            }
        }
    }

    fun addScrollableView(scrollableView: View) {
        if (scrollableView is RecyclerView) {
            scrollableView.addOnScrollListener(recyclerViewOnScrollListener)
        }
    }

    fun removeScrollableView(scrollableView: View) {
        if (scrollableView is RecyclerView) {
            scrollableView.removeOnScrollListener(recyclerViewOnScrollListener)
        }
    }

    companion object {
        val fragmentContainerId = R.id.eb_fragment_container
    }
}
