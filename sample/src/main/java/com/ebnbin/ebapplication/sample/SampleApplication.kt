package com.ebnbin.ebapplication.sample

import com.ebnbin.ebapplication.context.EBApplication

class SampleApplication : EBApplication() {
    override fun applicationId() = BuildConfig.APPLICATION_ID
}
