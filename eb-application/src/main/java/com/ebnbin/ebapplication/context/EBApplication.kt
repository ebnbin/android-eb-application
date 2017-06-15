package com.ebnbin.ebapplication.context

import android.app.Application

import com.ebnbin.ebapplication.net.NetHelper

/**
 * Base [Application].
 */
abstract class EBApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        initNetHelper()
    }

    private fun initNetHelper() {
        NetHelper.init()
    }
}
