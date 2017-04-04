package com.ebnbin.ebapplication.context.ui;

import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.app.FragmentManager;
import android.app.FragmentTransaction;

import java.util.Arrays;
import java.util.Stack;

/**
 * Helper for {@link FragmentManager} used in {@link EBActivity} and {@link EBFragment}.
 */
public final class FragmentManagerHelper {
    private final FragmentManager mFm;
    @IdRes
    private final int mContainerViewId;

    private final Stack<String> mTagStack = new Stack<>();

    FragmentManagerHelper(@NonNull FragmentManager fm, @IdRes int containerViewId) {
        mFm = fm;
        mContainerViewId = containerViewId;
    }

    public boolean empty() {
        return mTagStack.isEmpty();
    }

    public int count() {
        return mTagStack.size();
    }

    @Nullable
    public EBFragment find(@Nullable String tag) {
        if (tag == null) {
            return null;
        }

        return (EBFragment) mFm.findFragmentByTag(tag);
    }

    public boolean exist(@Nullable String tag) {
        return find(tag) != null;
    }

    @Nullable
    public String tag(int index) {
        if (empty()) {
            return null;
        }

        int count = count();
        int validIndex = (index % count + count) % count;
        return mTagStack.get(validIndex);
    }

    /**
     * 返回 fragment 栈中某个 index 的 fragment. 如果 fragment 栈为空, 则返回 {@code null}.
     *
     * @param index
     *         例: Fragment 栈 size 为 3, 则 {@code -6}, {@code -3}, {@code 0}, {@code 3}, {@code 6} 都表示 index
     *         {@code 0}, {@code -5}, {@code -2}, {@code 1}, {@code 4}, {@code 7} 都表示 index {@code 1}, {@code -4},
     *         {@code -1}, {@code 2}, {@code 5}, {@code 8} 都表示 index {@code 2}.
     *
     * @return 找到的 fragment.
     */
    @Nullable
    public EBFragment get(int index) {
        String tag = tag(index);
        return find(tag);
    }

    public EBFragment top() {
        return get(-1);
    }

    public EBFragment bottom() {
        return get(0);
    }

    public boolean add(@NonNull EBFragment fragment, @Nullable String tag, boolean hide, boolean push) {
        String validTag = tag == null ? fragment.getClass().getName() : tag;

        if (mTagStack.contains(validTag) || exist(validTag)) {
            return false;
        }

        FragmentTransaction ft = mFm.beginTransaction();

        ft.add(mContainerViewId, fragment, validTag);

        if (hide) {
            hide(ft);
        }

        if (push) {
            ft.addToBackStack(null);
        }

        ft.commit();

        mTagStack.push(validTag);

        return true;
    }

    private FragmentTransaction hide(@NonNull FragmentTransaction ft) {
        for (int i = count() - 1; i >= 0; i--) {
            EBFragment fragment = get(i);
            if (fragment == null || fragment.isHidden()) {
                continue;
            }

            ft.hide(fragment);
        }

        return ft;
    }

    public void show(int index, boolean hide, boolean push) {
        String tag = tag(index);
        show(tag, hide, push);
    }

    public void show(@Nullable String tag, boolean hide, boolean push) {
        EBFragment fragment = find(tag);
        if (fragment == null) {
            return;
        }

        FragmentTransaction ft = mFm.beginTransaction();

        if (hide) {
            hide(ft);
        }

        ft.show(fragment);

        if (push) {
            ft.addToBackStack(null);
        }

        ft.commit();
    }

    public boolean pop() {
        return pop(-1);
    }

    /**
     * 出栈.
     *
     * @param index
     *         如果 >= 0, 表示出栈后剩余的 transaction 个数, 否则绝对值表示出栈的 transaction 个数.
     *         例: {@code 0} 表示出栈后剩余 0 个 transaction, {@code 1} 表示出栈后剩余 1 个 transaction, {@code -1} 表示出栈 1
     *         个 transaction, {@code -2} 表示出栈 2 个 transaction.
     *
     * @return Whether there is something popped.
     */
    public boolean pop(int index) {
        int count;
        if (index < 0) {
            count = Math.abs(index);
        } else {
            count = mFm.getBackStackEntryCount() - index;
        }

        boolean popped = false;
        for (int i = 0; i < count; i++) {
            if (mFm.popBackStackImmediate()) {
                popped = true;
            }
        }

        onBackPressed();

        return popped;
    }

    public void onBackPressed() {
        Stack<String> newTagStack = new Stack<>();

        for (int i = 0; i < count(); i++) {
            String tag = mTagStack.get(i);
            EBFragment fragment = find(tag);
            if (fragment == null) {
                continue;
            }

            newTagStack.push(tag);
        }

        mTagStack.clear();
        mTagStack.addAll(newTagStack);
    }

    public void remove() {
        remove(-1);
    }

    public void remove(int index) {
        String tag = tag(index);
        remove(tag);
    }

    public void remove(@Nullable String tag) {
        EBFragment fragment = find(tag);
        if (fragment == null) {
            return;
        }

        mFm
                .beginTransaction()
                .remove(fragment)
                .commit();

        mTagStack.remove(tag);
    }

    public void removeAll(int index) {
        int count;
        if (index < 0) {
            count = Math.abs(index);
        } else {
            count = count() - index;
        }

        FragmentTransaction ft = mFm.beginTransaction();
        for (int i = count() - 1; i >= count() - 1 - count; i--) {
            EBFragment fragment = get(i);
            if (fragment == null) {
                continue;
            }

            ft.remove(fragment);

            mTagStack.pop();
        }

        ft.commit();
    }

    public void removeAll() {
        FragmentTransaction ft = mFm.beginTransaction();
        for (int i = count() - 1; i >= 0; i--) {
            EBFragment fragment = get(i);
            if (fragment == null) {
                continue;
            }

            ft.remove(fragment);

            mTagStack.pop();
        }

        ft.commit();
    }

    private static final String INSTANCE_STATE_TAGS = "tags";

    public void onRestoreInstanceState(@Nullable Bundle savedInstanceState) {
        if (savedInstanceState == null) {
            return;
        }

        String[] tags = savedInstanceState.getStringArray(INSTANCE_STATE_TAGS);
        if (tags == null) {
            return;
        }

        mTagStack.clear();
        mTagStack.addAll(Arrays.asList(tags));
    }

    public void onSaveInstanceState(@Nullable Bundle outState) {
        if (outState == null) {
            return;
        }

        outState.putStringArray(INSTANCE_STATE_TAGS, mTagStack.toArray(new String[]{}));
    }
}
