package com.beidousat.karaoke.widget;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.beidousat.karaoke.R;
import com.beidousat.karaoke.interf.OnPreviewSongListener;
import com.beidousat.karaoke.model.Song;
import com.beidousat.karaoke.player.ChooseSongs;
import com.beidousat.libwidget.recycler.HorizontalDividerItemDecoration;
import com.beidousat.libwidget.recycler.VerticalDividerItemDecoration2;

import java.util.List;

/**
 * Created by J Wong on 2015/12/17 18:01.
 */
public class WidgetChoosePageList extends RecyclerView {

    private AdtChoose mAdapter;

    public WidgetChoosePageList(Context context) {
        super(context);
        init();
    }

    public WidgetChoosePageList(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public WidgetChoosePageList(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }


    private void init() {
        setOverScrollMode(OVER_SCROLL_NEVER);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        setLayoutManager(layoutManager);
        HorizontalDividerItemDecoration horDivider = new HorizontalDividerItemDecoration.Builder(getContext())
                .color(Color.TRANSPARENT).size(6).build();

        VerticalDividerItemDecoration2 verDivider = new VerticalDividerItemDecoration2.Builder(getContext())
                .color(Color.TRANSPARENT).size(6).build();

        setLayoutManager(new GridLayoutManager(getContext(), 2));

        addItemDecoration(horDivider);
        addItemDecoration(verDivider);

        mAdapter = new AdtChoose();
        setAdapter(mAdapter);
    }


    public void setSong(int pageNum, List<Song> songs) {
        mAdapter.setPageNum(pageNum);
        mAdapter.setData(songs);
    }

    private OnPreviewSongListener mOnPreviewSongListener;

    public void setOnPreviewSongListener(OnPreviewSongListener l) {
        this.mOnPreviewSongListener = l;
    }

    private static class ViewHolder extends RecyclerView.ViewHolder {

        public View contentView;
        public TextView tvNumber, tvName, tvSort, tvSinger, ivPreview, ivTop, ivDel, tvVersion;


        public ViewHolder(View view) {
            super(view);
        }
    }

    public class AdtChoose extends RecyclerView.Adapter<ViewHolder> {

        private int mPageNum;
        private List<Song> mData;

        public void setPageNum(int pageNum) {
            mPageNum = pageNum;
        }

        public void setData(List<Song> songs) {
            this.mData = songs;
        }

        @Override
        public int getItemCount() {
            return mData.size();
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View itemView = LayoutInflater.from(getContext()).inflate(R.layout.list_item_choose_list, viewGroup, false);
            ViewHolder viewHolder = new ViewHolder(itemView);
            viewHolder.contentView = itemView.findViewById(R.id.contentView);
            viewHolder.tvNumber = (TextView) itemView.findViewById(R.id.tv_no);
            viewHolder.tvName = (TextView) itemView.findViewById(R.id.tv_name);
            viewHolder.tvSort = (TextView) itemView.findViewById(R.id.tv_sort);
            viewHolder.tvSinger = (TextView) itemView.findViewById(R.id.tv_singer);
            viewHolder.ivPreview = (TextView) itemView.findViewById(R.id.tv_preview);
            viewHolder.ivTop = (TextView) itemView.findViewById(R.id.tv_top);
            viewHolder.ivDel = (TextView) itemView.findViewById(R.id.tv_del);
            viewHolder.tvVersion = (TextView) itemView.findViewById(R.id.tv_version);

            return viewHolder;
        }


        @Override
        public void onBindViewHolder(ViewHolder holder, final int position) {
            final Song song = mData.get(position);
            holder.tvVersion.setText(!TextUtils.isEmpty(song.SongVersion) ? song.SongVersion : "");

            int num = mPageNum * 8 + position + 1;

            holder.tvNumber.setText(String.valueOf(num) + ".");
            holder.tvNumber.setSelected(num == 1);
            holder.tvName.setText(song.SimpName);
            holder.tvName.setSelected(num == 1);
            String singer = TextUtils.isEmpty(song.SingerName) ? "" : song.SingerName.replace("false", "");
            holder.tvSinger.setText(singer);


            String sortText = ChooseSongs.getInstance(getContext()).getSongPriorities(song);
            if (num == 1) {//playing
                holder.ivPreview.setVisibility(INVISIBLE);
                holder.ivTop.setVisibility(INVISIBLE);
                holder.ivDel.setVisibility(INVISIBLE);
                holder.tvSort.setVisibility(VISIBLE);
            } else if (num == 2) {
                holder.ivPreview.setVisibility(VISIBLE);
                holder.ivTop.setVisibility(GONE);
                holder.ivDel.setVisibility(VISIBLE);
                holder.tvSort.setVisibility(GONE);
            } else {
                holder.ivPreview.setVisibility(VISIBLE);
                holder.ivTop.setVisibility(VISIBLE);
                holder.ivDel.setVisibility(VISIBLE);
                holder.tvSort.setVisibility(GONE);
            }

            holder.tvSort.setText(sortText);
            holder.tvSort.setSelected(num == 1);

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

            holder.ivDel.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    onDelete(song);
                }
            });
        }
    }


    private void onPreview(Song song) {
        if (mOnPreviewSongListener != null) {
            mOnPreviewSongListener.onPreviewSong(song);
        }
    }

    private void onToTop(Song info) {
        ChooseSongs.getInstance(getContext().getApplicationContext()).add2Top(info);
    }

    private void onDelete(Song song) {
        ChooseSongs.getInstance(getContext().getApplicationContext()).remove(song);
    }
}


