package com.ebnbin.ebapplication.base;

import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import java.util.ArrayList;

/**
 * Helper for {@link FragmentManager} used in {@link EBActivity} and {@link EBFragment}.
 */
public final class FragmentManagerHelper {
    private final FragmentManager mFm;

    @IdRes
    private final int mContainerViewId;

    private final ArrayList<String> mTagList = new ArrayList<>();

    FragmentManagerHelper(@NonNull FragmentManager fm, @IdRes int containerViewId) {
        mFm = fm;
        mContainerViewId = containerViewId;
    }

    public int size() {
        return mTagList.size();
    }

    @Nullable
    public EBFragment find(@NonNull String tag) {
        return (EBFragment) mFm.findFragmentByTag(tag);
    }

    @Nullable
    public EBFragment get(int index) {
        if (mTagList.isEmpty()) {
            return null;
        }

        int tagSize = mTagList.size();
        int validIndex = (index % tagSize + tagSize) % tagSize;
        String tag = mTagList.get(validIndex);
        return find(tag);
    }

    public boolean exist(@NonNull String tag) {
        return find(tag) != null;
    }

    public boolean add(@NonNull EBFragment fragment, @Nullable String tag, boolean hide) {
        String validTag = tag == null ? fragment.getClass().getName() : tag;

        if (mTagList.contains(validTag)) {
            return false;
        }

        EBFragment savedFragment = (EBFragment) mFm.findFragmentByTag(validTag);
        if (savedFragment != null) {
            return false;
        }

        FragmentTransaction ft = mFm.beginTransaction();
        ft.add(mContainerViewId, fragment, validTag);

        if (hide) {
            EBFragment topFragment = get(-1);
            if (topFragment != null) {
                ft.hide(topFragment);
            }
        }

        if (!mTagList.isEmpty()) {
            ft.addToBackStack(null);
        }

        ft.commit();

        mTagList.add(validTag);

        return true;
    }

    public void onPop() {
        if (mTagList.isEmpty()) {
            return;
        }

        mTagList.remove(mTagList.size() - 1);
    }

    private static final String INSTANCE_STATE_TAG_LIST = "tag_list";

    public void onRestoreInstanceState(@Nullable Bundle savedInstanceState) {
        if (savedInstanceState == null) {
            return;
        }

        ArrayList<String> tagList = savedInstanceState.getStringArrayList(INSTANCE_STATE_TAG_LIST);
        if (tagList == null) {
            return;
        }

        mTagList.clear();
        mTagList.addAll(tagList);
    }

    public void onSaveInstanceState(@Nullable Bundle outState) {
        if (outState == null) {
            return;
        }

        outState.putStringArrayList(INSTANCE_STATE_TAG_LIST, mTagList);
    }
}
