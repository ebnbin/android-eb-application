package com.ebnbin.ebapplication.util;

import android.app.ActivityManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;

import com.ebnbin.eb.util.EBUtil;
import com.ebnbin.ebapplication.R;

/**
 * Utility class for application. All fields and methods in this class are static.
 */
public final class EBAppUtil {
    /**
     * Returns a default {@link ActivityManager.TaskDescription} used in
     * {@link com.ebnbin.ebapplication.context.ui.EBActivity}.
     */
    public static ActivityManager.TaskDescription getDefTaskDescription(@NonNull Context context) {
        Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.eb);
        Bitmap icon = EBUtil.tintBitmap(bitmap, Color.WHITE);
        @ColorInt
        int colorPrimary = context.getColor(R.color.eb_primary_light);

        return new ActivityManager.TaskDescription(null, icon, colorPrimary);
    }
}
