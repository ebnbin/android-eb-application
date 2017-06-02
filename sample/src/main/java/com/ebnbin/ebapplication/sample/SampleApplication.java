package com.ebnbin.ebapplication.sample;

import com.ebnbin.eb.util.EBUtil;
import com.ebnbin.ebapplication.context.EBApplication;

public final class SampleApplication extends EBApplication {
    @Override
    public void onCreate() {
        super.onCreate();

        initEBUtil();
    }

    private void initEBUtil() {
        EBUtil.INSTANCE.init(BuildConfig.APPLICATION_ID);
    }
}
