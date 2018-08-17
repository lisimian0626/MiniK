package com.beidousat.karaoke.ui.dlg;

import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.beidousat.karaoke.R;
import com.beidousat.libwidget.image.RecyclerImageView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

/**
 * Created by J Wong on 2017/5/22.
 */

public class DlgGuide extends DialogFragment implements View.OnClickListener {

    private RecyclerImageView mRivAd1;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, android.R.style.Theme_Black_NoTitleBar_Fullscreen);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.widget_ad_screen, container);
//        View root = view.findViewById(R.id.rootView);
//        root.setBackgroundColor(Color.argb(40, 0, 0, 0));
        mRivAd1 = (RecyclerImageView) view.findViewById(R.id.riv_ad_screen1);
        mRivAd1.setOnClickListener(this);

//        mRivAd2 = (RecyclerImageView) view.findViewById(R.id.riv_ad_screen2);
//        mRivAd2.setVisibility(View.VISIBLE);
//        mRivAd2.setOnClickListener(this);

        Glide.with(this).load(R.drawable.bg_guide).skipMemoryCache(true).diskCacheStrategy(DiskCacheStrategy.NONE).override(1280, 720).into(mRivAd1);
//        mRivAd1.setImageResource(R.drawable.bg_guide);
//        Glide.with(this).load(R.drawable.bg_guide).override(1280, 720).into(mRivAd2);
//        mRivAd2.setBackgroundColor(Color.argb(179, 0, 0, 0));

        return view;
    }

    private OnDismissListener mOnDismissListener;

    public void setOnDismissListener(OnDismissListener listener) {
        mOnDismissListener = listener;
    }

    @Override
    public void onClick(View v) {
        if (mOnDismissListener != null) {
            mOnDismissListener.onDismiss();
        }

        dismiss();
    }

    public interface OnDismissListener {
        void onDismiss();
    }
}
