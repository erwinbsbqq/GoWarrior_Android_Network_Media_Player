package com.gowarrior.nmp;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

/**
 * Created by GoWarrior on 2015/6/24.
 */
public class HomeItemView extends RelativeLayout {
    private ImageView mImage;
    private ImageView mHover;

    public HomeItemView(Context context) {
        super(context);
        init(context, null);
    }

    public HomeItemView(Context context,AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public HomeItemView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        View.inflate(context, R.layout.homeitem, this);

        mImage = (ImageView) findViewById(R.id.homeItemImage);
        mHover = (ImageView) findViewById(R.id.homeItemHover);

        if (attrs != null) {
            TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.HomeItemView);
            Drawable drawable = array.getDrawable(R.styleable.HomeItemView_android_src);
            mImage.setImageDrawable(drawable);
        }
    }

    public void setHoverVisibility(int visibility) {
        mHover.setVisibility(visibility);
    }
}
