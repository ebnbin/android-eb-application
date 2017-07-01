package com.ebnbin.ebapplication.context

import android.support.annotation.DrawableRes
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentStatePagerAdapter

abstract class EBBottomNavigationPagerAdapter(fm: FragmentManager) : FragmentStatePagerAdapter(fm) {
    override abstract fun getPageTitle(position: Int): CharSequence

    @DrawableRes abstract fun getPageIcon(position: Int): Int
}
