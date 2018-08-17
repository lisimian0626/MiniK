package com.beidousat.karaoke.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.beidousat.karaoke.R;
import com.beidousat.karaoke.model.Song;

import java.util.List;

/**
 * author: Hanson
 * date:   2017/4/1
 * describe:
 */

public class ShareAdapter extends RecyclerView.Adapter<ShareAdapter.ShareViewHolder> {
    Context mContext;
    View.OnClickListener mItemClickListener;
    List<Song> mSongs;

    public ShareAdapter(Context context, List<Song> songs, View.OnClickListener itemClickListener) {
        mContext = context;
        mSongs = songs;
        mItemClickListener = itemClickListener;
    }

    @Override
    public ShareViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.list_item_share, parent, false);
        view.setOnClickListener(mItemClickListener);
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

   final class ShareViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView mMusicName;
        TextView mSinger;
        ImageView mShare;
        ImageView mAudition;

        Song mSong;

        public ShareViewHolder(View itemView) {
            super(itemView);

            mMusicName = (TextView) itemView.findViewById(R.id.tv_music_name);
            mSinger = (TextView) itemView.findViewById(R.id.tv_singer);
            mShare = (ImageView) itemView.findViewById(R.id.iv_share);
            mAudition = (ImageView) itemView.findViewById(R.id.iv_audition);

            mShare.setOnClickListener(this);
            mAudition.setOnClickListener(this);
        }

       public void onBind(int position, Song song) {
           mSong = song;

           if (position % 2 != 0) {
                itemView.setBackgroundColor(mContext.getResources().getColor(R.color.share_item));
           } else {
               itemView.setBackgroundColor(mContext.getResources().getColor(android.R.color.white));
           }

           mMusicName.setText(song.SimpName);
           mSinger.setText(song.SingerName);
       }

       @Override
       public void onClick(View v) {
           v.setTag(mSong);
           if (mItemClickListener != null) {
               mItemClickListener.onClick(v);
           }
       }
   }
}
