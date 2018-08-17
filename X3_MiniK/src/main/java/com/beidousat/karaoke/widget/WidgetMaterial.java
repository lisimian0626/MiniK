package com.beidousat.karaoke.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;

import com.andexert.library.RippleView;
import com.beidousat.karaoke.R;
import com.beidousat.libwidget.image.RecyclerImageView;
import com.bumptech.glide.Glide;

/**
 * Created by J Wong on 2015/10/15 13:35.
 */
public class WidgetMaterial extends RippleView {

    private View mRootView;
    private RecyclerImageView ivBgImage;

    public WidgetMaterial(Context context) {
        super(context);
        initView();
    }

    public WidgetMaterial(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initView();
        readAttr(attrs);
    }

    public WidgetMaterial(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
        readAttr(attrs);
    }


    private void initView() {
        mRootView = LayoutInflater.from(getContext()).inflate(R.layout.widget_material, this);
        ivBgImage = (RecyclerImageView) mRootView.findViewById(android.R.id.background);
        setRippleType(RippleType.SIMPLE);
        setRippleDuration(getResources().getInteger(R.integer.ripple_duration));
    }


    private void readAttr(AttributeSet attrs) {
//        if (attrs != null) {
//            TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.WidgetMaterial);
//            this.mDrBg = a.getDrawable(R.styleable.WidgetMaterial_mater_bg);
//
//        }
//
//        if (mDrBg != null) {
//            setBackground(mDrBg);
//        }

    }


    public void setBackgroundIcon(int resId) {
//        ivBgImage.setImageResource(resId);
        Glide.with(getContext()).load(resId).into(ivBgImage);
    }
}
