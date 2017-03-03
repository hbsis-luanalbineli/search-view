package com.ambev.searchviewlib;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.widget.RecyclerView;
import android.view.View;

public class DividerDecorationRecyclerView extends RecyclerView.ItemDecoration {

    private final Paint mPaint = new Paint();
    private final int mHeight;

    public DividerDecorationRecyclerView(Context context) {
        final Resources resources = context.getResources();
        mPaint.setColor(ResourcesCompat.getColor(resources, R.color.recycler_view_divider_color,
                context.getTheme()));
        mHeight = resources.getDimensionPixelSize(R.dimen.recycler_view_divider_height);
    }

    @Override
    public void getItemOffsets(final Rect outRect, final View view, final RecyclerView parent,
                               final RecyclerView.State state) {
        outRect.set(0, 0, 0, mHeight);
    }

    @Override
    public void onDraw(final Canvas c, final RecyclerView parent,
                       final RecyclerView.State state) {
        int width = parent.getWidth();
        for (int i = 0, count = parent.getChildCount(); i < count; i++) {
            View child = parent.getChildAt(i);
            int bottom = child.getBottom();
            c.drawRect(0, bottom, width, bottom + mHeight, mPaint);
        }
    }
}
