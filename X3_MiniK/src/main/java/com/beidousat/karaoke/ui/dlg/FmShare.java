package com.beidousat.karaoke.ui.dlg;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.beidousat.karaoke.R;
import com.beidousat.karaoke.adapter.ShareAdapter;
import com.beidousat.karaoke.model.Song;
import com.beidousat.karaoke.player.ChooseSongs;
import com.beidousat.karaoke.ui.Main;
import com.beidousat.karaoke.widget.CountDownTextView;
import com.beidousat.libbns.evenbus.EventBusUtil;
import com.beidousat.libbns.util.BnsConfig;

import java.util.ArrayList;
import java.util.List;

/**
 * author: Hanson
 * date:   2017/3/31
 * describe:
 */

public class FmShare extends FmBaseDialog implements View.OnClickListener {
    CountDownTextView mCountDown;
    RecyclerView mRecylerView;
    TextView mRenew, mExit;
    ShareAdapter mAdapter;

    List<Song> mSongs = new ArrayList<>();

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.fm_share, container, false);
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    void initData() {

    }

    @Override
    void initView() {
        mCountDown = findViewById(R.id.countdown);
        mRecylerView = findViewById(R.id.recycler_view);
        mRenew = findViewById(R.id.renew);
        mExit = findViewById(R.id.exit);

        mSongs.addAll(ChooseSongs.getInstance(getContext()).getHasSungSons());
        mAdapter = new ShareAdapter(getContext(), mSongs, this);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        mRecylerView.setLayoutManager(layoutManager);
        mRecylerView.setAdapter(mAdapter);
    }

    @Override
    void setListener() {
        mCountDown.setMaxTimerSec(BnsConfig.CNT_FINISH + (mSongs.size() - 1) * 15);
        mCountDown.setFinishedListener(new CountDownTextView.TimerFinished() {
            @Override
            public void onFinish() {
                EventBusUtil.postRoomClose(null);
                mAttached.dismiss();
            }
        });
        mRenew.setOnClickListener(this);
        mExit.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Song song = (Song) v.getTag();
        switch (v.getId()) {
            case R.id.renew:
                CommonDialog commonDialog = CommonDialog.getInstance();
                commonDialog.setShowClose(true);
                commonDialog.setContent(FmPayMeal.createMealFragment(FmPayMeal.TYPE_CNT_RENEW,null));
                if (!commonDialog.isAdded()) {
                    commonDialog.show(getChildFragmentManager(), "commonDialog");
                }
                break;
            case R.id.exit:
                EventBusUtil.postRoomClose(null);
                mAttached.dismiss();
                break;
            case R.id.iv_share:
//                DlgShare dlgShare = new DlgShare(Main.mMainActivity, song);
//                dlgShare.show();
                DialogHelper.showShareDialog(Main.mMainActivity, song);
                break;
            case R.id.iv_audition:
                DlgAudioPlayer dlgAudioPlayer = new DlgAudioPlayer(Main.mMainActivity, song);
                dlgAudioPlayer.show();
                break;
        }
    }

    @Override
    public void onStart() {
        super.onStart();

        mCountDown.start();
    }

    @Override
    public void onStop() {
        super.onStop();

        mCountDown.stop();
    }
}
