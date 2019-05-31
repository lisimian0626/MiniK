package com.beidousat.karaoke.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.beidousat.karaoke.R;
import com.beidousat.libbns.model.Ad;
import com.beidousat.libbns.model.BannerInfo;
import com.beidousat.libbns.util.ServerFileUtil;
import com.bumptech.glide.Glide;

/**
 * Created by J Wong on 2015/12/17 17:34.
 */
public class FmBannerDetail extends BaseFragment {

    private BannerInfo mAdBanner;

    private View mRootView;
    private ImageView mIvBanner;


    public static FmBannerDetail newInstance(Ad adBanner) {
        FmBannerDetail fragment = new FmBannerDetail();
        Bundle args = new Bundle();
        args.putSerializable("banner", adBanner);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mAdBanner = (BannerInfo) getArguments().getSerializable("banner");
        }
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        mRootView = inflater.inflate(R.layout.fm_banner_detail, null);
        mIvBanner = (ImageView) mRootView.findViewById(R.id.iv_banner);

        Glide.with(this).load(ServerFileUtil.getImageUrl(mAdBanner.getData().getImg_url())).override(1200, 500).centerCrop().skipMemoryCache(true).into(mIvBanner);

        return mRootView;
    }
}
