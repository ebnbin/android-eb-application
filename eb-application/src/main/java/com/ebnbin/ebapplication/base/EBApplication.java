package com.ebnbin.ebapplication.base;

import android.app.Application;

import com.ebnbin.ebapplication.net.NetHelper;

/**
 * Base {@link Application} for initializations.
 */
public abstract class EBApplication extends com.ebnbin.eb.base.EBApplication {
    @Override
    public void onCreate() {
        super.onCreate();

        NetHelper.init();
    }

    @Override
    public void onTerminate() {
        NetHelper.getInstance().dispose();

        super.onTerminate();
    }
}
