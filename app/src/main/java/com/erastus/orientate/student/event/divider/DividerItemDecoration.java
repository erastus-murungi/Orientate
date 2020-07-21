package com.erastus.orientate.student.event.divider;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.view.View;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.jetbrains.annotations.NotNull;

public class DividerItemDecoration extends RecyclerView.ItemDecoration{
    private Drawable mDrawable;

    public DividerItemDecoration(@NonNull Context context, @DrawableRes Integer drawableId) {
        mDrawable = context.getDrawable(drawableId);
    }

    public DividerItemDecoration(Drawable drawable) {
        mDrawable = drawable;
    }

    /**
     * First, getItemOffsets.
     * We need to provide offsets between list items so that we’re not drawing dividers on top of our child views.
     * getItemOffsets is called for each child of your RecyclerView.
     */
    @Override
    public void getItemOffsets(@NonNull Rect outRect,
                               @NonNull View view,
                               @NonNull RecyclerView parent,
                               @NonNull RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);
        if (parent.getChildAdapterPosition(view) == 0) {
            return;
        }
        outRect.top = mDrawable.getIntrinsicHeight();
    }

    @Override
    public void onDraw(@NotNull Canvas canvas,
                       RecyclerView parent,
                       @NotNull RecyclerView.State state) {

        int dividerLeft = parent.getPaddingLeft();
        int dividerRight = parent.getWidth() - parent.getPaddingRight();

        int childCount = parent.getChildCount();
        for (int i = 0; i < childCount - 1; i++) {
            View child = parent.getChildAt(i);

            RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();

            int dividerTop = child.getBottom() + params.bottomMargin;
            int dividerBottom = dividerTop + mDrawable.getIntrinsicHeight();

            mDrawable.setBounds(dividerLeft, dividerTop, dividerRight, dividerBottom);
            mDrawable.draw(canvas);
        }
    }
}
