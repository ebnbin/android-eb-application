package com.ebnbin.ebapplication.context

import com.aurelhubert.ahbottomnavigation.AHBottomNavigationItem

class EBBottomNavigationItem(title: String, resource: Int, val fragment: EBFragment)
    : AHBottomNavigationItem(title, resource)
