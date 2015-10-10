package com.gowarrior.nmp;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.GridView;

/**
 * Created by GoWarrior on 2015/6/29.
 */
public class ProgGridView extends GridView {
    private int position = 0;

    public ProgGridView(Context context) {
        super(context);
    }

    public ProgGridView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setChildrenDrawingOrderEnabled(true);
    }

    @Override
    protected int getChildDrawingOrder(int childCount, int i) {
        position = getSelectedItemPosition() - getFirstVisiblePosition();
        if (position < 0)
            return i;

        if (i == childCount - 1) {
            return position;
        } else if (i == position) {
            return childCount - 1;
        }
        return i;
    }
}

