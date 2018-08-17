package com.beidousat.karaoke.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.beidousat.karaoke.R;
import com.beidousat.karaoke.model.DownloadProgress;

import java.util.List;

/**
 * author: Hanson
 * date:   2017/4/1
 * describe:
 */

public class DownloaderAdapter extends RecyclerView.Adapter<DownloaderAdapter.ShareViewHolder> {
    Context mContext;
    List<DownloadProgress> mSongs;

    public DownloaderAdapter(Context context, List<DownloadProgress> songs) {
        mContext = context;
        mSongs = songs;
    }

    @Override
    public ShareViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.list_item_downloader, parent, false);
        return new ShareViewHolder(view);
    }

    @Override
    public int getItemCount() {
        return mSongs == null ? 0 : mSongs.size();
    }

    @Override
    public void onBindViewHolder(ShareViewHolder holder, int position) {
        holder.onBind(position, mSongs.get(position));
    }

    final class ShareViewHolder extends RecyclerView.ViewHolder {
        TextView mMusicName;
        TextView mSinger;
        TextView mProgress;

        DownloadProgress mItem;

        public ShareViewHolder(View itemView) {
            super(itemView);

            mMusicName = (TextView) itemView.findViewById(R.id.tv_music_name);
            mSinger = (TextView) itemView.findViewById(R.id.tv_singer);
            mProgress = (TextView) itemView.findViewById(R.id.tv_progress);
        }

        public void onBind(int position, DownloadProgress data) {
            mItem = data;

            if (position % 2 != 0) {
                itemView.setBackgroundColor(mContext.getResources().getColor(R.color.share_item));
            } else {
                itemView.setBackgroundColor(mContext.getResources().getColor(android.R.color.white));
            }

            mMusicName.setText(data.song.SimpName);
            mSinger.setText(data.song.SingerName);
            if (!TextUtils.isEmpty(data.song.downloadErro)) {
                mProgress.setText(data.song.downloadErro);
            } else {
                if(data.percent==0){
                    mProgress.setText(mContext.getString(R.string.download_waiting));
                }else{
                    mProgress.setText(String.valueOf(data.percent) + "%");
                }
            }
        }
    }
}
