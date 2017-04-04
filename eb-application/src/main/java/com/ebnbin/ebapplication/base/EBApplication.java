package com.ebnbin.ebapplication.base;

import android.app.Application;

import com.ebnbin.ebapplication.net.NetHelper;

/**
 * Base {@link Application} for initializations.
 */
public abstract class EBApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        initNetHelper();
    }

    private void initNetHelper() {
        NetHelper.init();
    }
}
