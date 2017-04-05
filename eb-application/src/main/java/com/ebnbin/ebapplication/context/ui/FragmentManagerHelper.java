package com.ebnbin.ebapplication.context.ui;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.ebnbin.eb.base.EBRuntimeException;

import java.util.ArrayList;

/**
 * Helper for {@link FragmentManager} and {@link FragmentTransaction} used in {@link EBActivity} and
 * {@link EBFragment}.
 */
public final class FragmentManagerHelper {
    private final FragmentManager mFm;

    /**
     * Default containerViewId.
     */
    @IdRes
    public int defGroup;

    FragmentManagerHelper(FragmentManager fm) {
        this(fm, 0);
    }

    FragmentManagerHelper(@NonNull FragmentManager fm, @IdRes int defGroup) {
        mFm = fm;

        this.defGroup = defGroup;
    }

    //*****************************************************************************************************************
    // Instance state.

    private static final String INSTANCE_STATE_FRAGMENT_ENTRY_ARRAY_LIST = "fragment_entry_array_list";

    public void onRestoreInstanceState(@Nullable Bundle savedInstanceState) {
        if (savedInstanceState == null) {
            return;
        }

        ArrayList<FragmentEntry> fragmentEntryArrayList = savedInstanceState
                .getParcelableArrayList(INSTANCE_STATE_FRAGMENT_ENTRY_ARRAY_LIST);
        if (fragmentEntryArrayList != null) {
            mFragmentEntryArrayList.clear();
            mFragmentEntryArrayList.addAll(fragmentEntryArrayList);
        }
    }

    public void onSaveInstanceState(@Nullable Bundle outState) {
        if (outState == null) {
            return;
        }

        outState.putParcelableArrayList(INSTANCE_STATE_FRAGMENT_ENTRY_ARRAY_LIST, mFragmentEntryArrayList);
    }

    //*****************************************************************************************************************
    // Fragment entry.

    private final ArrayList<FragmentEntry> mFragmentEntryArrayList = new ArrayList<>();

    private static final class FragmentEntry implements Parcelable {
        /**
         * Unique.
         */
        public final String tag;
        /**
         * ContainerViewId.
         */
        @IdRes
        public final int group;

        private FragmentEntry(@NonNull String tag, @IdRes int group) {
            this.tag = tag;
            this.group = group;
        }

        protected FragmentEntry(Parcel in) {
            tag = in.readString();
            group = in.readInt();
        }

        public static final Creator<FragmentEntry> CREATOR = new Creator<FragmentEntry>() {
            @Override
            public FragmentEntry createFromParcel(Parcel in) {
                return new FragmentEntry(in);
            }

            @Override
            public FragmentEntry[] newArray(int size) {
                return new FragmentEntry[size];
            }
        };

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(tag);
            dest.writeInt(group);
        }
    }

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
        return mFragmentEntryArrayList.size();
    }

    public int count(@IdRes int group) {
        int count = 0;

        for (FragmentEntry fragmentEntry : mFragmentEntryArrayList) {
            if (fragmentEntry.group == group) {
                ++count;
            }
        }

        return count;
    }

    //*****************************************************************************************************************
    // Is empty.

    public boolean empty() {
        return mFragmentEntryArrayList.isEmpty();
    }

    public boolean empty(@IdRes int group) {
        for (FragmentEntry fragmentEntry : mFragmentEntryArrayList) {
            if (fragmentEntry.group == group) {
                return false;
            }
        }

        return true;
    }

    //*****************************************************************************************************************
    // Gets FragmentEntry by index.

    @Nullable
    private FragmentEntry fragmentEntry(int index) {
        int count = count();
        if (count == 0) {
            return null;
        }

        int validIndex = (index % count + count) % count;
        return mFragmentEntryArrayList.get(validIndex);
    }

    @Nullable
    private FragmentEntry fragmentEntry(int index, @IdRes int group) {
        int count = count(group);
        if (count == 0) {
            return null;
        }

        int validIndex = (index % count + count) % count;

        int i = 0;
        for (FragmentEntry fragmentEntry : mFragmentEntryArrayList) {
            if (fragmentEntry.group == group) {
                if (i == validIndex) {
                    return fragmentEntry;
                }

                ++i;
            }
        }

        throw new EBRuntimeException();
    }

    //*****************************************************************************************************************
    // Gets FragmentEntry by tag.

    @Nullable
    private FragmentEntry fragmentEntry(@NonNull String tag) {
        for (FragmentEntry fragmentEntry : mFragmentEntryArrayList) {
            if (fragmentEntry.tag.equals(tag)) {
                return fragmentEntry;
            }
        }

        return null;
    }

    @Nullable
    private FragmentEntry fragmentEntry(@NonNull String tag, @IdRes int group) {
        for (FragmentEntry fragmentEntry : mFragmentEntryArrayList) {
            if (fragmentEntry.group == group && fragmentEntry.tag.equals(tag)) {
                return fragmentEntry;
            }
        }

        return null;
    }

    //*****************************************************************************************************************
    // Is exist.

    public boolean exist(@NonNull String tag) {
        return fragmentEntry(tag) != null;
    }

    public boolean exist(@NonNull String tag, @IdRes int group) {
        return fragmentEntry(tag, group) != null;
    }

    //*****************************************************************************************************************
    // Gets tag by index.

    @Nullable
    private String tag(int index) {
        FragmentEntry fragmentEntry = fragmentEntry(index);
        if (fragmentEntry == null) {
            return null;
        }

        return fragmentEntry.tag;
    }

    @Nullable
    private String tag(int index, @IdRes int group) {
        FragmentEntry fragmentEntry = fragmentEntry(index, group);
        if (fragmentEntry == null) {
            return null;
        }

        return fragmentEntry.tag;
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
    // Can add.

    public boolean canAdd(@NonNull String tag) {
        return !exist(tag) && !found(tag);
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
    // On popped.

    public void onPopped() {
        ArrayList<FragmentEntry> fragmentEntryArrayList = new ArrayList<>();

        for (FragmentEntry fragmentEntry : mFragmentEntryArrayList) {
            if (!found(fragmentEntry.tag)) {
                continue;
            }

            fragmentEntryArrayList.add(fragmentEntry);
        }

        mFragmentEntryArrayList.clear();
        mFragmentEntryArrayList.addAll(fragmentEntryArrayList);
    }

    //*****************************************************************************************************************
    // Pops.

    public void pop() {
        mFm.popBackStackImmediate();

        onPopped();
    }

    //*****************************************************************************************************************
    // Transaction runnable.

    // No need saving instance state.
    /**
     * Transactions that change {@link #mFragmentEntryArrayList}.
     */
    private final ArrayList<Runnable> mTransactions = new ArrayList<>();

    //*****************************************************************************************************************
    // Transaction.

    private FragmentTransaction mFt;

    /**
     * Call this method after {@link FragmentManager#beginTransaction()}.
     */
    public void beginTransaction(@NonNull FragmentTransaction ft) {
        mFt = ft;
    }

    /**
     * Call this method before {@link FragmentTransaction#commit()}.
     */
    public void endTransaction() {
        mFt = null;

        for (Runnable transaction : mTransactions) {
            transaction.run();
        }

        mTransactions.clear();
    }

    //*****************************************************************************************************************
    // Add transaction.

    /**
     * Calls {@link #add(String, int, EBFragment)} with tag {@code null} and group {@code -1}.
     */
    public void add(@NonNull EBFragment fragment) {
        add(null, -1, fragment);
    }

    /**
     * Calls {@link #add(String, int, EBFragment)} with group {@code -1}.
     */
    public void add(@Nullable String tag, @NonNull EBFragment fragment) {
        add(tag, -1, fragment);
    }

    /**
     * Calls {@link #add(String, int, EBFragment)} with tag {@code null}.
     */
    public void add(@IdRes int group, @NonNull EBFragment fragment) {
        add(null, group, fragment);
    }

    /**
     * {@link FragmentTransaction#add(int, Fragment, String)}.
     *
     * @param tag If {@code null}, {@code fragment.getClass().getName()} will be used. If exist,
     * {@link EBRuntimeException} will be thrown.
     * @param group If {@code -1}, {@link #defGroup} will be used.
     * @param fragment Target {@link EBFragment}.
     */
    public void add(@Nullable String tag, @IdRes int group, @NonNull EBFragment fragment) {
        final String validTag = validTag(tag, fragment);
        if (!canAdd(validTag)) {
            throw new EBRuntimeException();
        }

        @IdRes final
        int validGroup = group == -1 ? defGroup : group;

        mFt.add(validGroup, fragment, validTag);

        mTransactions.add(new Runnable() {
            @Override
            public void run() {
                FragmentEntry fragmentEntry = new FragmentEntry(validTag, validGroup);
                mFragmentEntryArrayList.add(fragmentEntry);
            }
        });
    }

    //*****************************************************************************************************************
    // Show or hide transaction.

    /**
     * {@link FragmentTransaction#show(Fragment)} or {@link FragmentTransaction#hide(Fragment)}.
     */
    private void show(boolean show, @NonNull String tag) {
        EBFragment fragment = find(tag);
        if (fragment == null) {
            return;
        }

        show(show, fragment);
    }

    /**
     * {@link FragmentTransaction#show(Fragment)} or {@link FragmentTransaction#hide(Fragment)}.
     */
    private void show(boolean show, @NonNull EBFragment fragment) {
        if (fragment.isHidden() != show) {
            return;
        }

        if (show) {
            mFt.show(fragment);
        } else {
            mFt.hide(fragment);
        }
    }

    /**
     * Shows or hides all except.
     */
    private void showAll(boolean show, @NonNull String... exceptTags) {
        showAll(show, -1, exceptTags);
    }

    /**
     * Shows or hides all except.
     */
    private void showAll(boolean show, @NonNull EBFragment... exceptFragments) {
        showAll(show, -1, exceptFragments);
    }

    /**
     * Shows or hides group all except.
     */
    private void showAll(boolean show, @IdRes int group, @NonNull String... exceptTags) {
        ArrayList<EBFragment> exceptFragmentArrayList = new ArrayList<>();
        for (String exceptTag : exceptTags) {
            EBFragment fragment = find(exceptTag);
            if (fragment == null) {
                continue;
            }

            exceptFragmentArrayList.add(fragment);
        }

        EBFragment[] exceptFragments = exceptFragmentArrayList.toArray(new EBFragment[] {});
        showAll(show, group, exceptFragments);
    }

    /**
     * Shows or hides group all except.
     */
    private void showAll(boolean show, @IdRes int group, @NonNull EBFragment... exceptFragments) {
        if (group == 0) {
            return;
        }

        OUTER: for (FragmentEntry fragmentEntry : mFragmentEntryArrayList) {
            if (group != -1 && fragmentEntry.group != group) {
                continue;
            }

            EBFragment foundFragment = find(fragmentEntry.tag);
            if (foundFragment == null) {
                continue;
            }

            for (EBFragment exceptFragment : exceptFragments) {
                if (foundFragment == exceptFragment) {
                    continue OUTER;
                }
            }

            show(show, foundFragment);
        }
    }

    //*****************************************************************************************************************
    // Show transaction.

    public void show(@NonNull String tag) {
        show(true, tag);
    }

    public void show(@NonNull EBFragment fragment) {
        show(true, fragment);
    }

    public void showAll(@NonNull String... exceptTags) {
        showAll(true, exceptTags);
    }

    public void showAll(@NonNull EBFragment... exceptFragments) {
        showAll(true, exceptFragments);
    }

    public void showAll(@IdRes int group, @NonNull String... exceptTags) {
        showAll(true, group, exceptTags);
    }

    public void showAll(@IdRes int group, @NonNull EBFragment... exceptFragments) {
        showAll(true, group, exceptFragments);
    }

    //*****************************************************************************************************************
    // Hide transaction.

    public void hide(@NonNull String tag) {
        show(false, tag);
    }

    public void hide(@NonNull EBFragment fragment) {
        show(false, fragment);
    }

    public void hideAll(@NonNull String... exceptTags) {
        showAll(false, exceptTags);
    }

    public void hideAll(@NonNull EBFragment... exceptFragments) {
        showAll(false, exceptFragments);
    }

    public void hideAll(@IdRes int group, @NonNull String... exceptTags) {
        showAll(false, group, exceptTags);
    }

    public void hideAll(@IdRes int group, @NonNull EBFragment... exceptFragments) {
        showAll(false, group, exceptFragments);
    }

    //*****************************************************************************************************************
    // Add to back stack transaction.

    /**
     * {@link FragmentTransaction#addToBackStack(String)}.
     */
    public void push() {
        mFt.addToBackStack(null);
    }
}
