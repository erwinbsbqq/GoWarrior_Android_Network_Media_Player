package com.gowarrior.nmp;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;

/**
 * Created by GoWarrior on 2015/6/24.
 */
public class HomeMenu extends RelativeLayout {
    private final String TAG = "HomeMenu";

    public HomeMenu(Context context, AttributeSet attrs) {
        super(context, attrs);
        setChildrenDrawingOrderEnabled(true);
    }

    @Override
    protected int getChildDrawingOrder(int childCount, int i) {
        View focus = getFocusedChild();
        View current = getChildAt(i);

        if (i == childCount -1) {
            int j;
            View v;
            for (j = 0; j < childCount; j++) {
                v = getChildAt(j);
                if (v == focus) {
                    break;
                }
            }
            if (j < childCount) {
                return j;
            } else {
                return i;
            }
        } else if (focus == current) {
            return childCount-1;
        }

        return i;
    }

    @Override
    protected boolean drawChild(Canvas canvas, View child, long drawingTime) {
        //Log.d(TAG, "draw "+child.getContentDescription().toString());
        return super.drawChild(canvas, child, drawingTime);
    }
}
