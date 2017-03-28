package com.ebnbin.ebapplication.sample;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.Menu;
import android.view.MenuItem;

import com.ebnbin.ebapplication.base.EBActivity;

public final class MainActivity extends EBActivity {
    private SampleFragment mSampleFragment;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mSampleFragment = (SampleFragment) getSupportFragmentManager()
                .findFragmentByTag(SampleFragment.class.getName());
        if (mSampleFragment == null) {
            mSampleFragment = new SampleFragment();
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(android.R.id.content, mSampleFragment, SampleFragment.class.getName())
                    .commit();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_activity, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.a: {
                if (mSampleFragment != null) {
                    String url = "http://gank.io";
                    mSampleFragment.webViewLoadUrl(url);
                }

                return true;
            }
            default: {
                return super.onOptionsItemSelected(item);
            }
        }
    }
}
