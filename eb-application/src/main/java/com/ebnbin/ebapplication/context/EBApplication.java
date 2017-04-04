package com.ebnbin.ebapplication.context;

import android.app.Application;

import com.ebnbin.ebapplication.net.NetHelper;

/**
 * Base {@link Application}.
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
