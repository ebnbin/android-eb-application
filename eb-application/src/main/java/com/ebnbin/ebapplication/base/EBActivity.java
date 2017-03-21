package com.ebnbin.ebapplication.base;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StyleRes;
import android.support.v7.app.AppCompatActivity;

import com.ebnbin.ebapplication.R;

/**
 * Base {@link Activity} with custom theme.
 */
public abstract class EBActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        initTheme();

        super.onCreate(savedInstanceState);
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
     * Returns current instance as an {@link AppCompatActivity}.
     *
     * @return Current instance.
     */
    @NonNull
    public final AppCompatActivity getAppCompatActivity() {
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
    // Custom theme.

    /**
     * Default theme id.
     */
    @StyleRes
    private static final int DEFAULT_THEME_ID = R.style.EBTheme;

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
}
