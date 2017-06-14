package com.ebnbin.ebapplication.context.ui

import android.os.Bundle
import android.support.annotation.IdRes
import android.support.v4.app.FragmentManager
import android.support.v4.util.ArrayMap
import java.util.*

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
     * Is found.
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
        NONE,
        HIDE,
        REMOVE
    }

    /**
     * Sets a fragment and removes others in the same group.
     */
    @JvmOverloads fun set(fragment: EBFragment, tag: String? = null, @IdRes group: Int = -1): Boolean {
        return internalAdd(fragment, tag, group, Others.REMOVE, false)
    }

    /**
     * Pushes a fragment, hides others in the same group and adds to back stack.
     */
    @JvmOverloads fun push(fragment: EBFragment, tag: String? = null, @IdRes group: Int = -1): Boolean {
        return internalAdd(fragment, tag, group, Others.HIDE, true)
    }

    /**
     * Transaction add.
     */
    private fun internalAdd(fragment: EBFragment, tag: String?, @IdRes group: Int, others: Others,
            push: Boolean): Boolean {
        val validTag = tag ?: fragment.javaClass.name
        if (found(validTag)) {
            return false
        }

        @IdRes val validGroup = if (group == -1) defGroup else group

        val ft = fm.beginTransaction()
        ft.add(validGroup, fragment, validTag)
        if (others != Others.NONE) {
            for (tmpTag in fragments.keys) {
                val tmpGroup = fragments[tmpTag]
                if (tmpGroup == validGroup) {
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

        fragments.put(validTag, validGroup)

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
