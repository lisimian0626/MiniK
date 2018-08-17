package com.beidousat.karaoke.ui.dlg;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.beidousat.karaoke.R;
import com.beidousat.karaoke.adapter.DownloaderAdapter;
import com.beidousat.karaoke.model.DownloadProgress;
import com.beidousat.karaoke.util.MyDownloader;
import com.beidousat.libbns.evenbus.BusEvent;
import com.beidousat.libbns.evenbus.DownloadBusEvent;
import com.beidousat.libbns.evenbus.EventBusId;
import com.beidousat.libbns.util.ServerFileUtil;

import java.util.List;

import de.greenrobot.event.EventBus;

/**
 * author: Hanson
 * date:   2017/5/10
 * describe:
 */

public class FmDownloader extends FmBaseDialog {
    RecyclerView mRecyclerView;
    DownloaderAdapter mAdapter;
    List<DownloadProgress> mSongs;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.fm_downloader, container, false);
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    void initData() {
        mSongs = MyDownloader.getInstance().getSongsProgress();
    }

    @Override
    void initView() {
        mRecyclerView = findViewById(R.id.recycler_view);
        mAdapter = new DownloaderAdapter(getContext(), mSongs);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerView.setAdapter(mAdapter);
    }

    @Override
    void setListener() {

    }

    @Override
    public void onResume() {
        super.onResume();

        EventBus.getDefault().register(this);
    }

    @Override
    public void onPause() {
        super.onPause();

        EventBus.getDefault().unregister(this);
    }

    public void onEventMainThread(BusEvent event) {
        DownloadBusEvent de;
        switch (event.id) {
            case EventBusId.Download.PROGRESS:
                de = (DownloadBusEvent) event;
                for (DownloadProgress item : mSongs) {
                    if (ServerFileUtil.getFileUrl(item.song.SongFilePath).equals(de.url)) {
                        item.percent = (int) de.percent;
                        break;
                    }
                }
                mAdapter.notifyDataSetChanged();
                break;
            case EventBusId.Download.FINISH:
                /*de = (DownloadBusEvent) event;
                for (DownloadProgress item : mSongs) {
                    if (ServerFileUtil.getFileUrl(item.song.SongFilePath).equals(de.url)) {
                        item.percent = (int) de.percent;
                        break;
                    }
                }*/
                break;
            case EventBusId.Download.ERROR:
                de = (DownloadBusEvent) event;
                for (DownloadProgress item : mSongs) {
                    if (ServerFileUtil.getFileUrl(item.song.SongFilePath).equals(de.url)) {
                        item.song.downloadErro = de.msg;
                        break;
                    }
                }
                mAdapter.notifyDataSetChanged();
                break;
        }
    }
}
