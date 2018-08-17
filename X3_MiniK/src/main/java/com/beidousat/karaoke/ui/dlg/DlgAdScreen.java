package com.beidousat.karaoke.ui.dlg;

import android.app.DialogFragment;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.beidousat.karaoke.R;
import com.beidousat.karaoke.data.PrefData;
import com.beidousat.karaoke.ui.BaseActivity;
import com.beidousat.libbns.ad.AdBillHelper;
import com.beidousat.libbns.amin.MoveAnimation;
import com.beidousat.libbns.model.Ad;
import com.beidousat.libbns.util.Logger;
import com.beidousat.libbns.util.ServerFileUtil;
import com.beidousat.libwidget.image.RecyclerImageView;
import com.bumptech.glide.Glide;

/**
 * Created by J Wong on 2017/5/22.
 */

public class DlgAdScreen extends DialogFragment implements View.OnClickListener {

    private RecyclerImageView mRivAd1, mRivAd2;
    private Ad mAd;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, android.R.style.Theme_Black_NoTitleBar_Fullscreen);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.widget_ad_screen, container);
        mRivAd1 = (RecyclerImageView) view.findViewById(R.id.riv_ad_screen1);
        mRivAd2 = (RecyclerImageView) view.findViewById(R.id.riv_ad_screen2);
        mRivAd1.setOnClickListener(this);
        mRivAd2.setOnClickListener(this);

        if (mAd != null) {
            showAdScreen(mAd);
        }
        return view;
    }

    @Override
    public void onClick(View v) {
        BaseActivity.mLastTouchTime = System.currentTimeMillis();
        dismiss();
    }


    public void showAdScreen(Ad ad) {
        mAd = ad;
        if (mRivAd1 == null || mRivAd2 == null) {
            Logger.d("DlgAdScreen", "showAdScreen mRivAd1 || mRivAd2 ==null");
            return;
        }
        Uri uri = ServerFileUtil.getImageUrl(mAd.ADContent);
        Logger.d("DlgAdScreen", "showAdScreen uri:" + uri.getPath());

        if (mRivAd1.getVisibility() == View.VISIBLE) {
            Glide.with(this).load(uri).override(1280, 720).thumbnail(0.3F).into(mRivAd2);
            mRivAd1.startAnimation(MoveAnimation.create(MoveAnimation.LEFT, false, 500));
            mRivAd2.startAnimation(MoveAnimation.create(MoveAnimation.LEFT, true, 500));
            mRivAd1.setVisibility(View.GONE);
            mRivAd2.setVisibility(View.VISIBLE);
        } else {
            Glide.with(this).load(uri).override(1280, 720).thumbnail(0.3F).into(mRivAd1);
            mRivAd1.startAnimation(MoveAnimation.create(MoveAnimation.LEFT, true, 500));
            mRivAd2.startAnimation(MoveAnimation.create(MoveAnimation.LEFT, false, 500));
            mRivAd1.setVisibility(View.VISIBLE);
            mRivAd2.setVisibility(View.GONE);
        }
        try {
            AdBillHelper.getInstance(getActivity().getApplicationContext()).billAd(mAd.ID, "P2", PrefData.getRoomCode(getActivity().getApplicationContext()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
