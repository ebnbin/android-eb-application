package com.ebnbin.ebapplication.sample

import com.ebnbin.eb.util.EBUtil
import com.ebnbin.ebapplication.context.EBApplication

class SampleApplication : EBApplication() {
    override fun onCreate() {
        super.onCreate()

        initEBUtil()
    }

    private fun initEBUtil() {
        EBUtil.init(BuildConfig.APPLICATION_ID)
    }
}
