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
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.util.ArraySet;
import android.support.v7.app.AppCompatActivity;

import com.ebnbin.eb.base.EBRuntimeException;
import com.ebnbin.ebapplication.R;
import com.ebnbin.ebapplication.fragment.WebViewFragment;

import java.util.Arrays;

/**
 * Base {@link Activity} with custom theme.
 */
public abstract class EBActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        initTaskDescription();
        initTheme();

        super.onCreate(savedInstanceState);

        initSupportFragmentManager();
    }

    @Override
    protected void onDestroy() {
        disposeSupportFragmentManager();

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
    // TaskDescription.

    /**
     * Initializes {@link ActivityManager.TaskDescription}.
     */
    private void initTaskDescription() {
        Bitmap icon = BitmapFactory.decodeResource(getResources(), R.drawable.eb_logo);
        int colorPrimary = getColor(R.color.eb_light_primary);
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
        WebViewFragment webViewFragment = (WebViewFragment) getSupportFragmentManager()
                .findFragmentByTag(WebViewFragment.TAG);
        if (webViewFragment != null && !webViewFragment.onBackPressed()) {
            return;
        }

        super.onBackPressed();
    }

    //*****************************************************************************************************************
    // FragmentManager.

    /**
     * For invalidating fragments that added in container {@link android.R.id#content}.
     */
    private FragmentManager.OnBackStackChangedListener mOnBackStackChangedListener
            = new FragmentManager.OnBackStackChangedListener() {
        @Override
        public void onBackStackChanged() {
            if (getSupportFragmentManager().getBackStackEntryCount() == mContentFragmentTagArraySet.size()) {
                return;
            }

            for (String tag : mContentFragmentTagArraySet) {
                EBFragment fragment = (EBFragment) getSupportFragmentManager().findFragmentByTag(tag);
                if (fragment != null) {
                    continue;
                }

                mContentFragmentTagArraySet.remove(tag);
            }

            invalidateFragmentsShowOrHide();
        }
    };

    private void initSupportFragmentManager() {
        getSupportFragmentManager().addOnBackStackChangedListener(mOnBackStackChangedListener);
    }

    private void disposeSupportFragmentManager() {
        getSupportFragmentManager().removeOnBackStackChangedListener(mOnBackStackChangedListener);
    }

    //*****************************************************************************************************************
    // Home fragment.

    /**
     * Tag for home fragment.
     */
    private static final String FRAGMENT_TAG_HOME = "home";

    /**
     * Root fragment added in container {@link android.R.id#content}.
     */
    @Nullable
    public EBFragment getHomeFragment() {
        return (EBFragment) getSupportFragmentManager().findFragmentByTag(FRAGMENT_TAG_HOME);
    }

    /**
     * Sets the home {@link EBFragment} to container {@link android.R.id#content}. If exists, do nothing.
     */
    public void setHomeFragment(@NonNull EBFragment fragment) {
        EBFragment homeFragment = getHomeFragment();
        if (homeFragment != null) {
            return;
        }

        getSupportFragmentManager()
                .beginTransaction()
                .replace(android.R.id.content, fragment, FRAGMENT_TAG_HOME)
                .commit();

        invalidateFragmentsShowOrHide();
    }

    /**
     * Removes the home {@link EBFragment} in container {@link android.R.id#content}.
     */
    public void removeHomeFragment() {
        EBFragment homeFragment = getHomeFragment();
        if (homeFragment == null) {
            return;
        }

        getSupportFragmentManager()
                .beginTransaction()
                .remove(homeFragment)
                .commit();

        invalidateFragmentsShowOrHide();
    }

    //*****************************************************************************************************************
    // Content fragments.

    /**
     * All fragment tags that added to container {@link android.R.id#content} of current activity, except home
     * fragment.
     */
    private final ArraySet<String> mContentFragmentTagArraySet = new ArraySet<>();

    /**
     * Adds an {@link EBFragment} to container {@link android.R.id#content} and adds it to default back stack. If
     * {@code tag} is exist, do nothing.
     *
     * @param tag
     *         If {@code null}, {@code fragment.getClass().getName()} will be used.
     */
    public void addFragment(@NonNull EBFragment fragment, @Nullable String tag) {
        String validTag = tag == null ? fragment.getClass().getName() : tag;

        if (FRAGMENT_TAG_HOME.equals(validTag)) {
            throw new EBRuntimeException();
        }

        if (mContentFragmentTagArraySet.contains(validTag)) {
            return;
        }

        getSupportFragmentManager()
                .beginTransaction()
                .add(android.R.id.content, fragment, validTag)
                .addToBackStack(null)
                .commit();

        mContentFragmentTagArraySet.add(validTag);

        invalidateFragmentsShowOrHide();
    }

    //*****************************************************************************************************************
    // Shows and hides fragments.

    /**
     * Invalidates whether to show or to hide home fragment and content fragments.
     */
    private void invalidateFragmentsShowOrHide() {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();

        EBFragment homeFragment = getHomeFragment();
        if (homeFragment != null) {
            if (mContentFragmentTagArraySet.isEmpty()) {
                ft = ft.show(homeFragment);
            } else {
                ft = ft.hide(homeFragment);
            }
        }

        int size = mContentFragmentTagArraySet.size();
        if (size > 0) {
            String topTag = mContentFragmentTagArraySet.valueAt(size - 1);
            EBFragment topFragment = (EBFragment) getSupportFragmentManager().findFragmentByTag(topTag);
            if (topFragment != null) {
                ft = ft.show(topFragment);
            }
        }
        if (size > 1) {
            String secondTopTag = mContentFragmentTagArraySet.valueAt(size - 2);
            EBFragment secondTopFragment = (EBFragment) getSupportFragmentManager().findFragmentByTag(secondTopTag);
            if (secondTopFragment != null) {
                ft = ft.hide(secondTopFragment);
            }
        }

        ft.commit();
    }

    //*****************************************************************************************************************
    // Instance state.

    private static final String STATE_CONTENT_FRAGMENT_TAGS = "content_fragment_tags";

    private boolean restoreContentFragmentTags(@Nullable Bundle savedInstanceState) {
        if (savedInstanceState == null) {
            return false;
        }

        String[] contentFragmentTags = savedInstanceState.getStringArray(STATE_CONTENT_FRAGMENT_TAGS);
        if (contentFragmentTags == null) {
            return false;
        }

        mContentFragmentTagArraySet.addAll(Arrays.asList(contentFragmentTags));

        return true;
    }

    private void saveContentFragmentTags(@Nullable Bundle outState) {
        if (outState == null) {
            return;
        }

        String[] contentFragmentTags = mContentFragmentTagArraySet.toArray(new String[]{});
        outState.putStringArray(STATE_CONTENT_FRAGMENT_TAGS, contentFragmentTags);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        restoreContentFragmentTags(savedInstanceState);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        saveContentFragmentTags(outState);
    }
}
