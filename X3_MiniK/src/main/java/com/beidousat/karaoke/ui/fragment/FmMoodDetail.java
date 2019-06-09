//package com.beidousat.karaoke.ui.fragment;
//
//import android.os.Bundle;
//import android.support.annotation.Nullable;
//import android.text.TextUtils;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.ImageView;
//import android.widget.TextView;
//
//
//import com.beidousat.karaoke.interf.OnPageScrollListener;
//import com.beidousat.karaoke.interf.OnPreviewSongListener;
//import com.beidousat.karaoke.interf.OnSongSelectListener;
//import com.beidousat.karaoke.widget.WidgetBasePage;
//import com.beidousat.karaoke.widget.WidgetSongPager;
//import com.bumptech.glide.Glide;
//
//import java.util.List;
//import java.util.Map;
//
//import jp.wasabeef.glide.transformations.RoundedCornersTransformation;
//
///**
// * Created by J Wong on 2015/12/17 17:34.
// */
//public class FmMoodDetail extends BaseFragment implements OnPageScrollListener, OnPreviewSongListener, WidgetPage.OnPageChangedListener, OnSongSelectListener {
//
//    private String mTopicId;
//    private View mRootView;
//    private WidgetSongPager mSongPager;
//    private TextView mTvRankingName2, mTvSupporter;
//    private ImageView ivAd;
//    private WidgetPage mWidgetPage;
//    private Map<String, String> mRequestParam;
//
//    public static FmMoodDetail newInstance(String topicId) {
//        FmMoodDetail fragment = new FmMoodDetail();
//        Bundle args = new Bundle();
//        args.putString("topic", topicId);
//        fragment.setArguments(args);
//        return fragment;
//    }
//
//    @Override
//    public void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        if (getArguments() != null) {
//            mTopicId = getArguments().getString("topic");
//        }
//    }
//
//
//    @Nullable
//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//        super.onCreateView(inflater, container, savedInstanceState);
//
//        mRootView = inflater.inflate(R.layout.fm_song_ranking, null);
//        mSongPager = (WidgetSongPagerV4) mRootView.findViewById(R.id.songPager);
//        mTvRankingName2 = (TextView) mRootView.findViewById(R.id.tv_ranking_name2);
//
//        mWidgetPage = (WidgetPage) mRootView.findViewById(R.id.w_page);
//        mWidgetPage.setOnPageChangedListener(this);
//
//        mTvSupporter = (TextView) mRootView.findViewById(R.id.tv_supporter);
//        ivAd = (ImageView) mRootView.findViewById(R.id.iv_ad);
//
//        mSongPager.setOnPagerScrollListener(this);
//        mSongPager.setOnPreviewSongListener(this);
//        mSongPager.setOnSongSelectListener(this);
//
//        if (mTopicId != null) {
//            requestTopicsDetail();
//            recordTopicClick();
//        }
//        return mRootView;
//    }
//
//    private void recordTopicClick() {
//        TopicRequestBody body = new TopicRequestBody(mTopicId, mTvSupporter.getText().toString());
//        StatisticsHelper.getInstance(getContext().getApplicationContext()).recordTopicClick(body);
//    }
//
//    private void requestTopicsDetail() {
//        HttpRequestV4 r = initRequestV4(RequestMethod.VOD_TOPIC_SONG);
//        r.addParam("topic_id", mTopicId);
//        r.addParam("per_page", String.valueOf(8));
//        r.addParam("current_page", String.valueOf(1));
//        r.setConvert2Class(TopicsDetail.class);
//        mRequestParam = r.getParams();
//        r.get();
//    }
//
//
//    public void initSongPager(int totalPage, List<SongItem> firstPageSong, Map<String, String> params) {
//        mWidgetPage.setPageCurrent(0);
//        mWidgetPage.setPageTotal(totalPage);
//        mSongPager.initPager(RequestMethod.VOD_TOPIC_SONG, totalPage, firstPageSong, params);
//    }
//
//    @Override
//    public void onPrePageClick(int before, int current) {
//        mSongPager.setCurrentItem(current);
//    }
//
//    @Override
//    public void onNextPageClick(int before, int current) {
//        mSongPager.setCurrentItem(current);
//
//    }
//
//    @Override
//    public void onFirstPageClick(int before, int current) {
//        mSongPager.setCurrentItem(current);
//        mWidgetPage.setPrePressed(false);
//        mWidgetPage.setNextPressed(false);
//    }
//
//    @Override
//    public void onPagerSelected(int position, boolean isLeft) {
//        mWidgetPage.setPageCurrent(position);
//        mSongPager.runLayoutAnimation(isLeft);
//        mWidgetPage.setPrePressed(false);
//        mWidgetPage.setNextPressed(false);
//    }
//
//    @Override
//    public void onPageScrollRight() {
//        mWidgetPage.setPrePressed(false);
//        mWidgetPage.setNextPressed(true);
//    }
//
//    @Override
//    public void onPageScrollLeft() {
//        mWidgetPage.setPrePressed(true);
//        mWidgetPage.setNextPressed(false);
//    }
//
//
//    @Override
//    public void onPreviewSong(SongItem song, int ps) {
//        new PreviewDialog(getActivity(), song, ps).show();
//    }
//
//    @Override
//    public void onSongSelectListener(SongItem song) {
//        Logger.d(getClass().getSimpleName(), "从小编推荐中点歌：" + song.id);
//        recordTopicClick();
//    }
//
//    @Override
//    public void onFailed(String method, String error) {
//        if (isAdded()) {
//            if (RequestMethod.VOD_TOPIC_SONG.equalsIgnoreCase(method)) {
//                mTvSupporter.setText(R.string.company_name);
//                ivAd.setImageResource(R.drawable.ad_banner_default);
//            }
//        }
//        super.onFailed(method, error);
//    }
//
//    @Override
//    public void onSuccess(String method, Object object) {
//        if (isAdded()) {
//            if (RequestMethod.VOD_TOPIC_SONG.equalsIgnoreCase(method)) {
//                if (object != null && object instanceof TopicsDetail) {
//                    TopicsDetail detail = (TopicsDetail) object;
//                    if (detail != null && detail.song != null && detail.song.data != null) {
//                        initSongPager(detail.song.last_page, detail.song.data, mRequestParam);
//                    }
//                    if (detail != null && detail.topic != null && detail.song.data != null) {
//                        mTvRankingName2.setText(detail.topic.TopicsName == null ? "" : detail.topic.TopicsName);
//                        mTvSupporter.setText(!TextUtils.isEmpty(detail.topic.Brand) ? detail.topic.Brand : getText(R.string.company_name));
//                        if (!TextUtils.isEmpty(detail.topic.RecommendImg)) {
//                            Glide.with(this).load(ServerFileUtil.getImageUrl(detail.topic.RecommendImg)).error(R.drawable.ad_banner_default)
//                                    .bitmapTransform(new RoundedCornersTransformation(getContext(), 5, 0, RoundedCornersTransformation.CornerType.ALL)).into(ivAd);
//                        }
//                    }
//                }
//            }
//        }
//        super.onSuccess(method, object);
//    }
//}
