package com.ebnbin.ebapplication.context

import android.app.Application
import com.ebnbin.eb.util.EBUtil
import com.ebnbin.ebapplication.net.NetHelper

/**
 * Base [Application].
 */
abstract class EBApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        initEBUtil()
        initNetHelper()
    }

    private fun initEBUtil() {
        EBUtil.init(applicationId())
    }

    /**
     * Returns application's `BuildConfig.APPLICATION_ID`.
     */
    protected abstract fun applicationId(): String

    private fun initNetHelper() {
        NetHelper.init()
    }
}
