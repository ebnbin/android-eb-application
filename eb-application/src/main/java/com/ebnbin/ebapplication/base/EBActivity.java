package com.ebnbin.ebapplication.base;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StyleRes;

import com.ebnbin.ebapplication.R;

/**
 * Base {@link Activity} with custom theme.
 */
public abstract class EBActivity extends Activity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        initTheme();
        initTaskDescription();

        super.onCreate(savedInstanceState);

        mFragmentManagerHelper = new FragmentManagerHelper(getFragmentManager(), android.R.id.content);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    //*****************************************************************************************************************
    // Contexts.

    /**
     * Returns current instance as a {@link Context}.
     *
     * @return Current instance.
     */
    @NonNull
    public final Context getContext() {
        return this;
    }

    /**
     * Returns current instance as an {@link Activity}.
     *
     * @return Current instance.
     */
    @NonNull
    public final Activity getActivity() {
        return this;
    }

    /**
     * Returns current instance as an {@link EBActivity}.
     *
     * @return Current instance.
     */
    @NonNull
    public final EBActivity getEBActivity() {
        return this;
    }

    //*****************************************************************************************************************
    // Handler.

    /**
     * Handler with main {@link Looper}.
     */
    protected final Handler handler = new Handler(Looper.getMainLooper());

    //*****************************************************************************************************************
    // TaskDescription.

    /**
     * Initializes {@link ActivityManager.TaskDescription}.
     */
    private void initTaskDescription() {
        Bitmap icon = BitmapFactory.decodeResource(getResources(), R.drawable.eb_logo);
        int colorPrimary = getColor(R.color.eb_primary_light);
        ActivityManager.TaskDescription taskDescription = new ActivityManager.TaskDescription(null, icon,
                colorPrimary);
        setTaskDescription(taskDescription);
    }

    //*****************************************************************************************************************
    // Custom theme.

    /**
     * Default theme id.
     */
    @StyleRes
    private static final int DEFAULT_THEME_ID = R.style.EBLightTheme;

    /**
     * Initializes custom theme.
     */
    private void initTheme() {
        @StyleRes
        int themeId = overrideTheme();

        if (themeId == -1) {
            return;
        }

        if (themeId == 0) {
            themeId = DEFAULT_THEME_ID;
        }

        setTheme(themeId);
    }

    /**
     * Overrides this method to set custom theme.
     *
     * @return Style resource id. Use the default value if 0 is returned, not to set custom theme if -1 is returned.
     */
    @StyleRes
    protected int overrideTheme() {
        return 0;
    }

    //*****************************************************************************************************************
    // OnBackPressed.

    @Override
    public void onBackPressed() {
        boolean childShouldPop;

        EBFragment topFragment = mFragmentManagerHelper.top();
        if (topFragment != null) {
            childShouldPop = topFragment.onBackPressed();
            if (!childShouldPop) {
                return;
            }
        }

        // Pops.
        super.onBackPressed();

        mFragmentManagerHelper.onBackPressed();
    }

    //*****************************************************************************************************************
    // FragmentManagerHelper.

    private FragmentManagerHelper mFragmentManagerHelper;

    public FragmentManagerHelper getFragmentManagerHelper() {
        return mFragmentManagerHelper;
    }

    //*****************************************************************************************************************
    // Instance state.

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        mFragmentManagerHelper.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        mFragmentManagerHelper.onSaveInstanceState(outState);
    }
}
