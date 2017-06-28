package com.ebnbin.ebapplication.sample

import android.os.Bundle
import android.view.View
import com.ebnbin.ebapplication.context.EBBottomNavigationFragment
import com.ebnbin.ebapplication.context.EBBottomNavigationItem

class TestBottomNavigationFragment : EBBottomNavigationFragment() {
    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        bottomNavigation.addItem(EBBottomNavigationItem("首页", R.drawable.bottom_navigation_home, TestActionBarFragment()))
        bottomNavigation.addItem(EBBottomNavigationItem("每日", R.drawable.bottom_navigation_today, TestActionBarFragment()))
        bottomNavigation.addItem(EBBottomNavigationItem("分类", R.drawable.bottom_navigation_subject, TestActionBarFragment()))
        bottomNavigation.addItem(EBBottomNavigationItem("我的", R.drawable.bottom_navigation_person, TestActionBarFragment()))

        bottomNavigation.setCurrentItem(0, true)
    }
}
