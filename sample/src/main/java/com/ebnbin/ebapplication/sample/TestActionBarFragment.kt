package com.ebnbin.ebapplication.sample

import android.os.Bundle
import android.view.View
import com.ebnbin.ebapplication.context.EBActionBarFragment

class TestActionBarFragment : EBActionBarFragment() {
    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        appCompatActivity.supportActionBar!!.title = "" + Math.random()
    }
}
