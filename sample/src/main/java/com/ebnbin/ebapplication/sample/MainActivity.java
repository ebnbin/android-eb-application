package com.ebnbin.ebapplication.sample;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import com.ebnbin.ebapplication.base.EBActivity;

public final class MainActivity extends EBActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SampleActionBarFragment sampleActionBarFragment = new SampleActionBarFragment();
        getFragmentManagerHelper().add(sampleActionBarFragment, null, true, false);

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                getSupportFragmentManager().beginTransaction()
                        .add(android.R.id.content, new SampleFragment(), "test").addToBackStack(null).commit();
            }
        }, 3000L);
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Fragment fragment = getSupportFragmentManager().findFragmentByTag("test");
                getSupportFragmentManager().beginTransaction().remove(fragment).commit();
            }
        }, 8000L);
    }
}
