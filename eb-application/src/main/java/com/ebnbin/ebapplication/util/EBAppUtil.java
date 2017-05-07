package com.ebnbin.ebapplication.util;

import android.app.ActivityManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.VectorDrawable;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;

import com.ebnbin.eb.util.EBUtil;
import com.ebnbin.ebapplication.R;

import java.lang.reflect.Field;

/**
 * Utility class for application. All fields and methods in this class are static.
 */
public final class EBAppUtil {
    private static int sVersionCode;
    private static String sVersionName;

    public static int getVersionCode() {
        return sVersionCode;
    }

    public static String getVersionName() {
        return sVersionName;
    }

    public static void init(@NonNull String applicationId) {
        try {
            Class buildConfigClass = Class.forName(applicationId + ".BuildConfig");

            Field debugField = buildConfigClass.getField("DEBUG");
            EBUtil.debug = debugField.getBoolean(null);

            Field versionCodeField = buildConfigClass.getField("VERSION_CODE");
            sVersionCode = versionCodeField.getInt(null);

            Field versionNameField = buildConfigClass.getField("VERSION_NAME");
            sVersionName = (String) versionNameField.get(null);
        } catch (ClassNotFoundException | NoSuchFieldException | IllegalAccessException e) {
            EBUtil.log(e);
        }
    }

    private static ActivityManager.TaskDescription sTaskDescription;

    /**
     * Returns a default {@link ActivityManager.TaskDescription} used in
     * {@link com.ebnbin.ebapplication.context.ui.EBActivity}.
     */
    public static ActivityManager.TaskDescription getDefTaskDescription(@NonNull Context context) {
        if (sTaskDescription != null) {
            return sTaskDescription;
        }

        Bitmap icon = null;
        VectorDrawable vectorDrawable = (VectorDrawable) context.getDrawable(R.drawable.eb);
        if (vectorDrawable != null) {
            vectorDrawable.setTint(Color.WHITE);

            int size = context.getResources().getDimensionPixelSize(R.dimen.eb_task_description_icon_size);
            icon = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(icon);
            vectorDrawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
            vectorDrawable.draw(canvas);
        }

        @ColorInt
        int colorPrimary = context.getColor(R.color.eb_primary_light);

        sTaskDescription = new ActivityManager.TaskDescription(null, icon, colorPrimary);
        return sTaskDescription;
    }
}
