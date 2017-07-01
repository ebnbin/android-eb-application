package com.ebnbin.ebapplication.context

import android.os.Bundle
import android.view.View
import com.aurelhubert.ahbottomnavigation.AHBottomNavigation
import com.ebnbin.eb.util.EBUtil
import com.ebnbin.ebapplication.R

abstract class EBBottomNavigationFragment : EBFragment() {
    override fun overrideContentViewLayout(): Int {
        return R.layout.eb_fragment_bottom_navigation
    }

    val bottomNavigationViewPager: EBBottomNavigationViewPager by lazy {
        stateView.findViewById(R.id.eb_bottom_navigation_view_pager) as EBBottomNavigationViewPager
    }

    val bottomNavigation: EBBottomNavigation by lazy {
        stateView.findViewById(R.id.eb_bottom_navigation) as EBBottomNavigation
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        bottomNavigationViewPager
        bottomNavigation

        bottomNavigationViewPager.offscreenPageLimit = 0

        bottomNavigation.titleState = AHBottomNavigation.TitleState.ALWAYS_SHOW
        bottomNavigation.defaultBackgroundColor = EBUtil.getColorAttr(context, R.attr.ebColorCard)
        bottomNavigation.accentColor = EBUtil.getColorAttr(context, R.attr.colorAccent)
        bottomNavigation.inactiveColor = EBUtil.getColorAttr(context, R.attr.ebColorIconDisabled)
    }
}
