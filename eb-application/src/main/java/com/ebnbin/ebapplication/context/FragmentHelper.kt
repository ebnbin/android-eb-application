package com.ebnbin.ebapplication.context

import android.os.Bundle
import android.support.annotation.IdRes
import android.support.v4.app.FragmentManager
import android.support.v4.util.ArrayMap

/**
 * Helper for [android.support.v4.app.Fragment], [FragmentManager] and [android.support.v4.app.FragmentTransaction]
 * used in [EBActivity] and [EBFragment].
 *
 * @param defGroup Default container view id.
 */
class FragmentHelper @JvmOverloads internal constructor(private val fm: FragmentManager,
        @IdRes var defGroup: Int = 0) {
    /**
     * Saved fragment tags and groups.
     */
    private val fragments = ArrayMap<String, Int>()

    fun onRestoreInstanceState(savedInstanceState: Bundle?) {
        if (savedInstanceState == null) {
            return
        }

        val fragmentsKeys = savedInstanceState.getStringArrayList(INSTANCE_STATE_FRAGMENTS_KEYS)
        val fragmentsValues = savedInstanceState.getIntegerArrayList(INSTANCE_STATE_FRAGMENTS_VALUES)
        if (fragmentsKeys != null && fragmentsValues != null && fragmentsKeys.size == fragmentsValues.size) {
            fragments.clear()
            for (i in fragmentsKeys.indices) {
                fragments.put(fragmentsKeys[i], fragmentsValues[i])
            }
        }
    }

    fun onSaveInstanceState(outState: Bundle?) {
        if (outState == null) {
            return
        }

        outState.putStringArrayList(INSTANCE_STATE_FRAGMENTS_KEYS, ArrayList(fragments.keys))
        outState.putIntegerArrayList(INSTANCE_STATE_FRAGMENTS_VALUES, ArrayList(fragments.values))
    }

    //*****************************************************************************************************************

    /**
     * Finds fragment by tag.
     */
    private fun find(tag: String): EBFragment? {
        return fm.findFragmentByTag(tag) as EBFragment?
    }

    /**
     * Whether found.
     */
    private fun found(tag: String): Boolean {
        return find(tag) != null
    }

    /**
     * Gets fragment by index.
     */
    private fun get(index: Int): EBFragment? {
        val tag = fragments.keyAt(index) ?: return null
        return find(tag)
    }

    /**
     * Gets first visible fragment at stack top.
     */
    fun topVisible(): EBFragment? {
        return (fragments.size - 1 downTo 0)
                .map { get(it) }
                .firstOrNull { it != null && !it.isHidden }
    }

    /**
     * On popped.
     */
    fun onPopped() {
        val removedTags = fragments.keys.filterNot { found(it) }
        fragments.removeAll(removedTags)
    }

    /**
     * Pops.
     *
     * @return Whether popped.
     */
    fun pop(): Boolean {
        val popped = fm.popBackStackImmediate()
        if (popped) {
            onPopped()
        }

        return popped
    }

    //*****************************************************************************************************************
    // Transactions.

    /**
     * Operators for other fragments in transactions.
     */
    private enum class Others {
        /**
         * Do nothing.
         */
        NONE,
        /**
         * Hides other fragments in the same group.
         */
        HIDE,
        /**
         * Removes other fragments in the same group.
         */
        REMOVE
    }

    /**
     * Sets a fragment and removes others in the same group.
     *
     * @param tag Should be unique. If not set, class name of fragment will be used.
     * @param group Container view id. If not set, default container view id will be used.
     *
     * @return Whether successful.
     */
    @JvmOverloads fun set(fragment: EBFragment, tag: String = fragment.javaClass.name,
            @IdRes group: Int = defGroup): Boolean {
        return internalAdd(fragment, tag, group, Others.REMOVE, false)
    }

    /**
     * Pushes a fragment, hides others in the same group and adds to back stack.
     *
     * @param tag Should be unique. If not set, class name of fragment will be used.
     * @param group Container view id. If not set, default container view id will be used.
     *
     * @return Whether successful.
     */
    @JvmOverloads fun push(fragment: EBFragment, tag: String = fragment.javaClass.name,
            @IdRes group: Int = defGroup): Boolean {
        return internalAdd(fragment, tag, group, Others.HIDE, true)
    }

    /**
     * Transaction add.
     *
     * @param tag Should be unique. If not set, class name of fragment will be used.
     * @param group Container view id. If not set, default container view id will be used.
     * @param others Operators for other fragments.
     * @param push Whether to transact [android.support.v4.app.FragmentTransaction.addToBackStack].
     *
     * @return Whether successful.
     */
    private fun internalAdd(fragment: EBFragment, tag: String, @IdRes group: Int, others: Others,
            push: Boolean): Boolean {
        if (found(tag)) {
            return false
        }

        val ft = fm.beginTransaction()
        ft.add(group, fragment, tag)
        if (others != Others.NONE) {
            for (tmpTag in fragments.keys) {
                val tmpGroup = fragments[tmpTag]
                if (tmpGroup == group) {
                    val tmpFragment = find(tmpTag)
                    if (tmpFragment != null) {
                        if (others == Others.HIDE) {
                            ft.hide(tmpFragment)
                        } else if (others == Others.REMOVE) {
                            ft.remove(tmpFragment)
                        }
                    }
                }
            }
        }
        if (push) {
            ft.addToBackStack(null)
        }
        ft.commit()

        fragments.put(tag, group)

        return true
    }

    //*****************************************************************************************************************

    companion object {
        //*************************************************************************************************************
        // Instance state.

        private val INSTANCE_STATE_FRAGMENTS_KEYS = "fragments_keys"
        private val INSTANCE_STATE_FRAGMENTS_VALUES = "fragments_values"
    }
}
