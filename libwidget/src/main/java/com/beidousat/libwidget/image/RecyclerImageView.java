package com.beidousat.libwidget.image;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;

/**
 * Created by J Wong on 2017/2/21.
 */

public class RecyclerImageView extends ImageView {

    public RecyclerImageView(Context context) {
        super(context);
    }

    public RecyclerImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        setImageDrawable(null);
    }
}
