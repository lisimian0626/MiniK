package com.beidousat.libwidget.image;

import android.content.Context;
import android.util.AttributeSet;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by J Wong on 2017/6/26.
 */

public class RecyclerCImageView extends CircleImageView {

    public RecyclerCImageView(Context context) {
        super(context);
    }

    public RecyclerCImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public RecyclerCImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        setImageDrawable(null);
    }
}
