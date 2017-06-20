package com.ebnbin.ebapplication.sample

import android.os.Bundle
import com.ebnbin.ebapplication.context.EBActivity

class MainActivity : EBActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        fragmentHelper.set(TestActionBarFragment())
    }
}
