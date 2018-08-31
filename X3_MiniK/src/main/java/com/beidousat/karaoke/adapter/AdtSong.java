package com.beidousat.karaoke.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.beidousat.karaoke.R;
import com.beidousat.karaoke.interf.OnPreviewSongListener;
import com.beidousat.karaoke.interf.OnSongSelectListener;
import com.beidousat.karaoke.model.Song;
import com.beidousat.karaoke.model.StarInfo;
import com.beidousat.karaoke.player.ChooseSongs;
import com.beidousat.karaoke.ui.fragment.FmSingerDetail;
import com.beidousat.karaoke.util.DiskFileUtil;
import com.beidousat.libbns.model.Common;
import com.beidousat.libbns.util.FragmentUtil;
import com.beidousat.libbns.util.Logger;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by J Wong on 2015/12/17 08:54.
 */
public class AdtSong extends RecyclerView.Adapter<AdtSong.ViewHolder> {

    private Context mContext;
    private LayoutInflater mInflater;
    private List<Song> mData = new ArrayList<Song>();
    private ChooseSongs mChooseSongs;
    private OnPreviewSongListener mOnPreviewSongListener;
    private boolean mIsShowSingerButton;

    public AdtSong(Context context, boolean isShowSingerButton) {
        mContext = context;
        mIsShowSingerButton = isShowSingerButton;
        mInflater = LayoutInflater.from(context);
        mChooseSongs = ChooseSongs.getInstance(context);
    }


    public void setData(List<Song> data) {
        this.mData = data;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public View contentView;
        public TextView tvName;
        public TextView tvSort;
        public TextView tvSinger;
        public TextView ivTop;
        public TextView ivPreview;
        public TextView tvVersion;
        public View tvScoreTag;

        public ViewHolder(View view) {
            super(view);
        }
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = mInflater.inflate(R.layout.list_item_song, viewGroup, false);
        ViewHolder viewHolder = new ViewHolder(view);
        viewHolder.contentView = view.findViewById(com.beidousat.karaoke.R.id.contentView);
        viewHolder.tvName = (TextView) view.findViewById(com.beidousat.karaoke.R.id.tv_name);
        viewHolder.tvSort = (TextView) view.findViewById(com.beidousat.karaoke.R.id.tv_sort);
        viewHolder.tvSinger = (TextView) view.findViewById(com.beidousat.karaoke.R.id.tv_singer);
        viewHolder.ivPreview = (TextView) view.findViewById(com.beidousat.karaoke.R.id.tv_preview);
        viewHolder.tvVersion = (TextView) view.findViewById(com.beidousat.karaoke.R.id.tv_version);
        viewHolder.ivTop = (TextView) view.findViewById(com.beidousat.karaoke.R.id.tv_top);
        viewHolder.tvScoreTag = view.findViewById(R.id.tv_score);
        return viewHolder;
    }


    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final Song song = mData.get(position);
        holder.tvName.setText(song.SimpName);
        final String singer = TextUtils.isEmpty(song.SingerName) ? "" : song.SingerName.replace("false", "");
        holder.tvSinger.setText(singer);
        holder.tvSinger.setEnabled(mIsShowSingerButton && !TextUtils.isEmpty(song.SingerID));
        boolean isScore = false;
        boolean isHd = song.IsClear == 1;

        if (!TextUtils.isEmpty(song.IsGradeLib)) {
            try {
                isScore = Integer.valueOf(song.IsGradeLib) == 1;
            } catch (Exception e) {
                Logger.d(getClass().getSimpleName(), "ex:" + e.toString());
            }
        }
        holder.tvScoreTag.setVisibility(isScore || isHd ? View.VISIBLE : View.GONE);
        int tagImg = 0;
        if (isScore && isHd) {
            tagImg = Common.isEn?R.drawable.ic_song_list_hdscore_tag_en:R.drawable.ic_song_list_hdscore_tag;
        } else if (isScore && !isHd) {
            tagImg = Common.isEn?R.drawable.ic_song_list_score_tag_en:R.drawable.ic_song_list_score_tag;
        } else if (isHd && !isScore) {
            tagImg = Common.isEn?R.drawable.ic_song_list_hd_tag_en:R.drawable.ic_song_list_hd_tag;
        }
        holder.tvScoreTag.setBackgroundResource(tagImg);

        File file = com.beidousat.libbns.util.DiskFileUtil.getDiskFileByUrl(song.SongFilePath);
        holder.ivTop.setText(mContext.getString(file == null ? R.string.download : R.string.priority));

        String sort = mChooseSongs.getSongPriorities(song);
        holder.tvSort.setText(sort);
        holder.tvSort.setVisibility(TextUtils.isEmpty(sort) ? View.GONE : View.VISIBLE);
        holder.tvVersion.setText(!TextUtils.isEmpty(song.SongVersion) ? song.SongVersion : "");
        holder.tvName.setSelected(!TextUtils.isEmpty(sort));

        holder.contentView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSongClick(song);
            }
        });

        holder.ivTop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onToTop(song);
            }
        });

        holder.ivPreview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onPreview(song);
            }
        });

        holder.tvSinger.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!TextUtils.isEmpty(song.SingerID)) {
                    StarInfo starInfo = new StarInfo();
                    starInfo.ID = song.SingerID;
                    starInfo.SimpName = singer;
                    FmSingerDetail fmSingerDetail = FmSingerDetail.newInstance(starInfo);
                    FragmentUtil.addFragment(fmSingerDetail);
                }
            }
        });
    }

    private void onPreview(Song info) {
        if (mOnPreviewSongListener != null)
            mOnPreviewSongListener.onPreviewSong(info);
    }

    public void setOnPreviewSongListener(OnPreviewSongListener listener) {
        this.mOnPreviewSongListener = listener;
    }


    private void onToTop(Song info) {
        if (ChooseSongs.getInstance(mContext.getApplicationContext()).add2Top(info) && mOnSongSelectListener != null) {
            mOnSongSelectListener.onSongSelectListener(info);
        }
    }

    private void onSongClick(final Song info) {


        if (ChooseSongs.getInstance(mContext.getApplicationContext()).addSong(info) && mOnSongSelectListener != null) {
            mOnSongSelectListener.onSongSelectListener(info);
        }
    }

    private OnSongSelectListener mOnSongSelectListener;

    public void setOnSongSelectListener(OnSongSelectListener listener) {
        this.mOnSongSelectListener = listener;
    }
}
