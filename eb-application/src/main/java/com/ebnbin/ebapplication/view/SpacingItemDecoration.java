package com.ebnbin.ebapplication.view;

import android.content.Context;
import android.graphics.Rect;
import android.support.annotation.DimenRes;
import android.support.annotation.NonNull;
import android.support.annotation.Px;
import android.support.v4.util.ArrayMap;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import java.util.Map;

/**
 * 自定义 item 间隔的 {@link RecyclerView.ItemDecoration}. {@code header} 为某类型的第一个 item 的 top spacing,
 * {@code divider} 为两个同类型的 item 的间隔, {@code footer} 为某类型的最后一个 item 的 bottom spacing. 需要在
 * {@link RecyclerView#setLayoutManager(RecyclerView.LayoutManager)} 和
 * {@link RecyclerView#setAdapter(RecyclerView.Adapter)} 之后调用
 * {@link RecyclerView#addItemDecoration(RecyclerView.ItemDecoration)}, 且 {@link RecyclerView.LayoutManager} 必须为
 * {@link LinearLayoutManager} 类型的.
 */
public class SpacingItemDecoration extends RecyclerView.ItemDecoration {
    /**
     * 默认类型的 item 间隔.
     */
    private final int[] mDefaultSpacing = new int[3];

    /**
     * 各个类型的 item 间隔.
     */
    private final Map<Integer, int[]> mSpacingTypes = new ArrayMap<>();

    /**
     * 设置默认类型的 item 间隔.
     */
    public void setDefaultTypeSpacing(@Px int header, @Px int divider, @Px int footer) {
        mDefaultSpacing[0] = header;
        mDefaultSpacing[1] = divider;
        mDefaultSpacing[2] = footer;
    }

    /**
     * 设置默认类型的 item 间隔.
     */
    public void setDefaultTypeSpacing(@NonNull Context context, @DimenRes int header, @DimenRes int divider,
            @DimenRes int footer) {
        mDefaultSpacing[0] = header == 0 ? 0 : context.getResources().getDimensionPixelSize(header);
        mDefaultSpacing[1] = divider == 0 ? 0 : context.getResources().getDimensionPixelSize(divider);
        mDefaultSpacing[2] = footer == 0 ? 0 : context.getResources().getDimensionPixelSize(footer);
    }

    /**
     * 添加某一类型的 item 间隔.
     */
    public void addSpacingType(int type, @Px int header, @Px int divider, @Px int footer) {
        mSpacingTypes.put(type, new int[]{header, divider, footer});
    }

    /**
     * 添加某一类型的 item 间隔.
     */
    public void addSpacingType(int type, @NonNull Context context, @DimenRes int header, @DimenRes int divider,
            @DimenRes int footer) {
        mSpacingTypes.put(type, new int[]{
                header == 0 ? 0 : context.getResources().getDimensionPixelSize(header),
                divider == 0 ? 0 : context.getResources().getDimensionPixelSize(divider),
                footer == 0 ? 0 : context.getResources().getDimensionPixelSize(footer)});
    }

    /**
     * 移除某一类型的 item 间隔.
     */
    public void removeSpacingType(int type) {
        mSpacingTypes.remove(type);
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);

        RecyclerView.Adapter adapter = parent.getAdapter();
        if (adapter == null) {
            return;
        }

        int position = parent.getChildLayoutPosition(view);
        if (position < 0 || position >= adapter.getItemCount()) {
            return;
        }

        RecyclerView.LayoutManager layoutManager = parent.getLayoutManager();
        if (!(layoutManager instanceof LinearLayoutManager)) {
            return;
        }

        LinearLayoutManager linearLayoutManager = (LinearLayoutManager) layoutManager;
        int orientation = linearLayoutManager.getOrientation();

        boolean vertical;
        if (orientation == LinearLayoutManager.HORIZONTAL) {
            vertical = false;
        } else if (orientation == LinearLayoutManager.VERTICAL) {
            vertical = true;
        } else {
            return;
        }

        int header;
        int divider;
        int footer;

        int type = adapter.getItemViewType(position);
        if (mSpacingTypes.containsKey(type)) {
            int[] spacing = mSpacingTypes.get(type);
            header = spacing[0];
            divider = spacing[1];
            footer = spacing[2];
        } else {
            header = mDefaultSpacing[0];
            divider = mDefaultSpacing[1];
            footer = mDefaultSpacing[2];
        }

        int prevPosition = position - 1;
        int nextPosition = position + 1;

        int left = 0;
        int top = 0;
        int right = 0;
        int bottom = 0;

        boolean isFirst = position == 0 || type != adapter.getItemViewType(prevPosition);
        if (isFirst) {
            if (vertical) {
                top = header;
            } else {
                left = header;
            }
        } else {
            if (vertical) {
                top = divider;
            } else {
                left = divider;
            }
        }

        boolean isLast = position == adapter.getItemCount() - 1 || type != adapter.getItemViewType(nextPosition);
        if (isLast) {
            if (vertical) {
                bottom = footer;
            } else {
                right = footer;
            }
        }

        outRect.set(left, top, right, bottom);
    }
}
