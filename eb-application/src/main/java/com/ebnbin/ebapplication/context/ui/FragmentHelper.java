package com.ebnbin.ebapplication.context.ui;

import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.util.ArrayMap;

import java.util.ArrayList;

/**
 * Helper for {@link FragmentManager} and {@link FragmentTransaction} used in {@link EBActivity} and
 * {@link EBFragment}.
 */
public final class FragmentHelper {
    private final FragmentManager mFm;

    /**
     * Default containerViewId.
     */
    @IdRes
    public int defGroup;

    FragmentHelper(@NonNull FragmentManager fm) {
        this(fm, 0);
    }

    FragmentHelper(@NonNull FragmentManager fm, @IdRes int defGroup) {
        mFm = fm;

        this.defGroup = defGroup;
    }

    //*****************************************************************************************************************
    // Instance state.

    private static final String INSTANCE_STATE_FRAGMENTS_KEYS = "fragments_keys";
    private static final String INSTANCE_STATE_FRAGMENTS_VALUES = "fragments_values";

    public void onRestoreInstanceState(@Nullable Bundle savedInstanceState) {
        if (savedInstanceState == null) {
            return;
        }

        ArrayList<String> fragmentsKeys = savedInstanceState.getStringArrayList(INSTANCE_STATE_FRAGMENTS_KEYS);
        ArrayList<Integer> fragmentsValues = savedInstanceState.getIntegerArrayList(INSTANCE_STATE_FRAGMENTS_VALUES);
        if (fragmentsKeys != null && fragmentsValues != null && fragmentsKeys.size() == fragmentsValues.size()) {
            mFragments.clear();
            for (int i = 0; i < fragmentsKeys.size(); i++) {
                mFragments.put(fragmentsKeys.get(i), fragmentsValues.get(i));
            }
        }
    }

    public void onSaveInstanceState(@Nullable Bundle outState) {
        if (outState == null) {
            return;
        }

        outState.putStringArrayList(INSTANCE_STATE_FRAGMENTS_KEYS, new ArrayList<>(mFragments.keySet()));
        outState.putIntegerArrayList(INSTANCE_STATE_FRAGMENTS_VALUES, new ArrayList<>(mFragments.values()));
    }

    //*****************************************************************************************************************
    // Fragment entry.

    private final ArrayMap<String, Integer> mFragments = new ArrayMap<>();

    //*****************************************************************************************************************
    // Generates default tag.

    @NonNull
    public String validTag(@NonNull EBFragment fragment) {
        return validTag(null, fragment);
    }

    @NonNull
    public String validTag(@Nullable String tag, @NonNull EBFragment fragment) {
        return tag == null ? fragment.getClass().getName() : tag;
    }

    //*****************************************************************************************************************
    // Count.

    public int count() {
        return mFragments.size();
    }

    public int count(@IdRes int group) {
        int count = 0;

        for (int tmpGroup : mFragments.values()) {
            if (tmpGroup == group) {
                ++count;
            }
        }

        return count;
    }

    //*****************************************************************************************************************
    // Is empty.

    public boolean empty() {
        return mFragments.isEmpty();
    }

    public boolean empty(@IdRes int group) {
        for (int tmpGroup : mFragments.values()) {
            if (tmpGroup == group) {
                return false;
            }
        }

        return true;
    }

    //*****************************************************************************************************************
    // Is exist.

    public boolean exist(@NonNull String tag) {
        return mFragments.containsKey(tag);
    }

    public boolean exist(@NonNull String tag, @IdRes int group) {
        return exist(tag) && mFragments.get(tag) == group;
    }

    //*****************************************************************************************************************
    // Gets tag by index.

    @Nullable
    private String tag(int index) {
        return mFragments.keyAt(index);
    }

    @Nullable
    private String tag(int index, @IdRes int group) {
        int i = 0;
        for (int tmpGroup : mFragments.values()) {
            if (tmpGroup == group) {
                if (i == index) {
                    return mFragments.keyAt(i);
                }
                ++i;
            }
        }

        return null;
    }

    //*****************************************************************************************************************
    // Finds fragment by tag.

    @Nullable
    public EBFragment find(@NonNull String tag) {
        return (EBFragment) mFm.findFragmentByTag(tag);
    }

    //*****************************************************************************************************************
    // Is found.

    public boolean found(@NonNull String tag) {
        return find(tag) != null;
    }

    //*****************************************************************************************************************
    // Gets fragment by index.

    @Nullable
    public EBFragment get(int index) {
        String tag = tag(index);
        if (tag == null) {
            return null;
        }

        return find(tag);
    }

    @Nullable
    public EBFragment get(int index, @IdRes int group) {
        String tag = tag(index, group);
        if (tag == null) {
            return null;
        }

        return find(tag);
    }

    //*****************************************************************************************************************
    // Gets fragment at stack top or bottom.

    @Nullable
    public EBFragment top() {
        return get(-1);
    }

    @Nullable
    public EBFragment top(@IdRes int group) {
        return get(-1, group);
    }

    @Nullable
    public EBFragment bottom() {
        return get(0);
    }

    @Nullable
    public EBFragment bottom(@IdRes int group) {
        return get(0, group);
    }

    //*****************************************************************************************************************
    // Gets first visible fragment at stack top.

    @Nullable
    public EBFragment topVisible() {
        for (int i = count() - 1; i >= 0; i--) {
            EBFragment fragment = get(i);
            if (fragment != null && !fragment.isHidden()) {
                return fragment;
            }
        }

        return null;
    }

    @Nullable
    public EBFragment topVisible(@IdRes int group) {
        for (int i = count(group) - 1; i >= 0; i--) {
            EBFragment fragment = get(i, group);
            if (fragment != null && !fragment.isHidden()) {
                return fragment;
            }
        }

        return null;
    }

    //*****************************************************************************************************************
    // On popped.

    public void onPopped() {
        ArrayList<String> removedTags = new ArrayList<>();
        for (String tag : mFragments.keySet()) {
            if (!found(tag)) {
                removedTags.add(tag);
            }
        }

        mFragments.removeAll(removedTags);
    }

    //*****************************************************************************************************************
    // Pops.

    public boolean pop() {
        boolean popped = mFm.popBackStackImmediate();
        if (popped) {
            onPopped();
        }

        return popped;
    }

    //*****************************************************************************************************************
    // New transactions.

    public enum Others {
        NONE,
        HIDE,
        REMOVE
    }

    public boolean set(@NonNull EBFragment fragment) {
        return internalAdd(fragment, null, -1, Others.REMOVE, false);
    }

    public boolean set(@NonNull EBFragment fragment, @Nullable String tag) {
        return internalAdd(fragment, tag, -1, Others.REMOVE, false);
    }

    public boolean set(@NonNull EBFragment fragment, @IdRes int group) {
        return internalAdd(fragment, null, group, Others.REMOVE, false);
    }

    public boolean set(@NonNull EBFragment fragment, @Nullable String tag, @IdRes int group) {
        return internalAdd(fragment, tag, group, Others.REMOVE, false);
    }

    public boolean push(@NonNull EBFragment fragment) {
        return internalAdd(fragment, null, -1, Others.HIDE, true);
    }

    public boolean push(@NonNull EBFragment fragment, @Nullable String tag) {
        return internalAdd(fragment, tag, -1, Others.HIDE, true);
    }

    public boolean push(@NonNull EBFragment fragment, @IdRes int group) {
        return internalAdd(fragment, null, group, Others.HIDE, true);
    }

    public boolean push(@NonNull EBFragment fragment, @Nullable String tag, @IdRes int group) {
        return internalAdd(fragment, tag, group, Others.HIDE, true);
    }

    private boolean internalAdd(@NonNull EBFragment fragment, @Nullable String tag, @IdRes int group,
            @NonNull Others others, boolean push) {
        String validTag = validTag(tag, fragment);
        if (found(validTag)) {
            return false;
        }

        @IdRes int validGroup = group == -1 ? defGroup : group;

        FragmentTransaction ft = mFm.beginTransaction();
        ft.add(validGroup, fragment, validTag);
        if (others != Others.NONE) {
            for (String tmpTag : mFragments.keySet()) {
                int tmpGroup = mFragments.get(tmpTag);
                if (tmpGroup == validGroup) {
                    EBFragment tmpFragment = find(tmpTag);
                    if (tmpFragment != null) {
                        if (others == Others.HIDE) {
                            ft.hide(tmpFragment);
                        } else if (others == Others.REMOVE) {
                            ft.remove(tmpFragment);
                        }
                    }
                }
            }
        }
        if (push) {
            ft.addToBackStack(null);
        }
        ft.commit();

        mFragments.put(validTag, validGroup);

        return true;
    }
}
