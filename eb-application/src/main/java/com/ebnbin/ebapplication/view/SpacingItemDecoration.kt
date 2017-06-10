package com.ebnbin.ebapplication.view

import android.graphics.Rect
import android.support.annotation.Px
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.ArrayMap
import android.view.View

/**
 * 自定义 item 间隔的 [RecyclerView.ItemDecoration]. `header` 为某类型的第一个 item 的 top spacing, `divider` 为两个同类型的
 * item 的间隔, `footer` 为某类型的最后一个 item 的 bottom spacing. 需要在 [RecyclerView.setLayoutManager] 和
 * [RecyclerView.setAdapter] 之后调用 [RecyclerView.addItemDecoration], 且 [RecyclerView.LayoutManager] 必须为
 * [LinearLayoutManager] 类型的.
 */
open class SpacingItemDecoration : RecyclerView.ItemDecoration() {
    /**
     * 默认类型的 item 间隔.
     */
    private val defSpacing = IntArray(3)

    /**
     * 设置默认类型的 item 间隔.
     */
    fun setDefTypeSpacing(@Px header: Int, @Px divider: Int, @Px footer: Int) {
        defSpacing[0] = header
        defSpacing[1] = divider
        defSpacing[2] = footer
    }

    /**
     * 各个类型的 item 间隔.
     */
    private val spacingTypes = ArrayMap<Int, IntArray>()

    /**
     * 添加某一类型的 item 间隔.
     */
    fun addSpacingType(type: Int, @Px header: Int, @Px divider: Int, @Px footer: Int) {
        spacingTypes.put(type, intArrayOf(header, divider, footer))
    }

    /**
     * 移除某一类型的 item 间隔.
     */
    fun removeSpacingType(type: Int) {
        spacingTypes.remove(type)
    }

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State?) {
        super.getItemOffsets(outRect, view, parent, state)

        val adapter = parent.adapter ?: return

        val position = parent.getChildLayoutPosition(view)
        if (position < 0 || position >= adapter.itemCount) {
            return
        }

        val layoutManager = parent.layoutManager as? LinearLayoutManager ?: return

        val orientation = layoutManager.orientation

        val vertical: Boolean
        if (orientation == LinearLayoutManager.HORIZONTAL) {
            vertical = false
        } else if (orientation == LinearLayoutManager.VERTICAL) {
            vertical = true
        } else {
            return
        }

        val header: Int
        val divider: Int
        val footer: Int

        val type = adapter.getItemViewType(position)
        if (spacingTypes.containsKey(type)) {
            val spacing = spacingTypes[type]
            header = spacing!![0]
            divider = spacing[1]
            footer = spacing[2]
        } else {
            header = defSpacing[0]
            divider = defSpacing[1]
            footer = defSpacing[2]
        }

        val prevPosition = position - 1
        val nextPosition = position + 1

        var left = 0
        var top = 0
        var right = 0
        var bottom = 0

        val isFirst = position == 0 || type != adapter.getItemViewType(prevPosition)
        if (isFirst) {
            if (vertical) {
                top = header
            } else {
                left = header
            }
        } else {
            if (vertical) {
                top = divider
            } else {
                left = divider
            }
        }

        val isLast = position == adapter.itemCount - 1 || type != adapter.getItemViewType(nextPosition)
        if (isLast) {
            if (vertical) {
                bottom = footer
            } else {
                right = footer
            }
        }

        outRect.set(left, top, right, bottom)
    }
}
