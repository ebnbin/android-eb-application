package com.ebnbin.ebapplication.context.ui;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.VectorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StyleRes;
import android.support.v7.app.AppCompatActivity;

import com.ebnbin.ebapplication.R;
import com.ebnbin.ebapplication.fragment.webview.WebViewFragment;

/**
 * Base {@link Activity}.
 */
public abstract class EBActivity extends AppCompatActivity {
    //*****************************************************************************************************************
    // Lifecycle.

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        initTheme();
        initTaskDescription();

        super.onCreate(savedInstanceState);

        initFragmentHelper(savedInstanceState);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        fragmentHelperOnSaveInstanceState(outState);
    }

    //*****************************************************************************************************************
    // Overrides.

    @Override
    public void onBackPressed() {
        if (fragmentHelperOnBackPressed()) {
            return;
        }

        super.onBackPressed();

        // Can't know whether popped, always check after calling super.
        mFragmentHelper.onPopped();
    }

    //*****************************************************************************************************************
    // Contexts.

    /**
     * Returns current instance as a {@link Context}.
     */
    @NonNull
    public final Context getContext() {
        return this;
    }

    /**
     * Returns current instance as an {@link Activity}.
     */
    @NonNull
    public final Activity getActivity() {
        return this;
    }

    /**
     * Returns current instance as an {@link AppCompatActivity}.
     */
    @NonNull
    public final AppCompatActivity getAppCompatActivity() {
        return this;
    }

    /**
     * Returns current instance as an {@link EBActivity}.
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

    @NonNull
    public Handler getHandler() {
        return handler;
    }

    //*****************************************************************************************************************
    // Custom theme.

    /**
     * Default theme id.
     */
    @StyleRes
    private static final int DEFAULT_THEME_ID = R.style.EBLightTheme;

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
     * Overrides this method to set a custom theme.
     *
     * @return Sets the default custom theme if {@code 0} is returned, not to set a custom theme if {@code -1} is
     * returned.
     */
    @StyleRes
    protected int overrideTheme() {
        return 0;
    }

    //*****************************************************************************************************************
    // TaskDescription.

    private void initTaskDescription() {
        setTaskDescription(getDefTaskDescription());
    }

    private static ActivityManager.TaskDescription sTaskDescription;

    /**
     * Returns a default {@link ActivityManager.TaskDescription} used in
     * {@link com.ebnbin.ebapplication.context.ui.EBActivity}.
     */
    public ActivityManager.TaskDescription getDefTaskDescription() {
        if (sTaskDescription != null) {
            return sTaskDescription;
        }

        Bitmap icon = null;
        VectorDrawable vectorDrawable = (VectorDrawable) getDrawable(R.drawable.eb);
        if (vectorDrawable != null) {
            vectorDrawable.setTint(Color.WHITE);

            int size = getResources().getDimensionPixelSize(R.dimen.eb_task_description_icon_size);
            icon = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(icon);
            vectorDrawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
            vectorDrawable.draw(canvas);
        }

        @ColorInt
        int colorPrimary = getColor(R.color.eb_primary_light);

        sTaskDescription = new ActivityManager.TaskDescription(null, icon, colorPrimary);
        return sTaskDescription;
    }

    //*****************************************************************************************************************
    // FragmentHelper.

    private FragmentHelper mFragmentHelper;

    @NonNull
    public FragmentHelper getFragmentHelper() {
        return mFragmentHelper;
    }

    private void initFragmentHelper(@Nullable Bundle savedInstanceState) {
        mFragmentHelper = new FragmentHelper(getSupportFragmentManager(), android.R.id.content);

        mFragmentHelper.onRestoreInstanceState(savedInstanceState);
    }

    private void fragmentHelperOnSaveInstanceState(@Nullable Bundle outState) {
        mFragmentHelper.onSaveInstanceState(outState);
    }

    /**
     * @return Whether handled. Call super if {@code false} is returned.
     */
    private boolean fragmentHelperOnBackPressed() {
        EBFragment topVisibleFragment = mFragmentHelper.topVisible();
        if (topVisibleFragment != null) {
            if (topVisibleFragment.onBackPressed()) {
                return true;
            }
        }

        return false;
    }

    //*****************************************************************************************************************
    // Uses WebView to load urls.

    public void webViewLoadUrl(@NonNull String url) {
        WebViewFragment webViewFragment = WebViewFragment.newInstance(url);
        getFragmentHelper().push(webViewFragment);
    }
}
