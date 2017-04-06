package com.ebnbin.ebapplication.context.ui;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.ColorInt;
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

        mFragmentManagerHelper.onRestoreInstanceState(savedInstanceState);
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
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.eb);
        Bitmap icon = tintBitmap(bitmap, Color.WHITE);
        int colorPrimary = getColor(R.color.eb_primary_light);
        ActivityManager.TaskDescription taskDescription = new ActivityManager.TaskDescription(null, icon,
                colorPrimary);
        setTaskDescription(taskDescription);
    }

    // TODO: Moves to library.
    /**
     * Tints a {@link Bitmap} with the given color.
     *
     * @return Result {@link Bitmap}.
     */
    @NonNull
    private static Bitmap tintBitmap(@NonNull Bitmap bitmap, @ColorInt int tintColor) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        Bitmap.Config config = Bitmap.Config.ARGB_8888;
        Bitmap resultBitmap = Bitmap.createBitmap(width, height, config);

        Canvas canvas = new Canvas(resultBitmap);

        Paint paint = new Paint();

        ColorFilter filter = new PorterDuffColorFilter(tintColor, PorterDuff.Mode.SRC_IN);
        paint.setColorFilter(filter);

        canvas.drawBitmap(bitmap, 0f, 0f, paint);

        return resultBitmap;
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

        mFragmentManagerHelper.onPopped();
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
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        mFragmentManagerHelper.onSaveInstanceState(outState);
    }
}