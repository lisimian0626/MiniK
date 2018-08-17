package com.beidousat.karaoke.widget;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;
import com.beidousat.karaoke.R;
import com.beidousat.karaoke.adapter.BaseRecyclerAdapter;
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


    public class AdtChoose extends BaseRecyclerAdapter<Song> {

        private int mPageNum;

        public void setPageNum(int pageNum) {
            mPageNum = pageNum;
        }

        @Override
        protected int getItemViewLayoutId() {
            return R.layout.list_item_choose_list;
        }

        @Override
        public void onBindViewHolder(BaseRecyclerAdapter.ViewHolder holder, final int position) {
            Context context = holder.mContext;
            View itemView = holder.getItemView();
            View contentView = itemView.findViewById(R.id.contentView);
            TextView tvNumber = (TextView) itemView.findViewById(R.id.tv_no);
            TextView tvName = (TextView) itemView.findViewById(R.id.tv_name);
            TextView tvSort = (TextView) itemView.findViewById(R.id.tv_sort);
            TextView tvSinger = (TextView) itemView.findViewById(R.id.tv_singer);
            TextView ivPreview = (TextView) itemView.findViewById(R.id.tv_preview);
            TextView ivTop = (TextView) itemView.findViewById(R.id.tv_top);
            TextView ivDel = (TextView) itemView.findViewById(R.id.tv_del);
            TextView tvVersion = (TextView) itemView.findViewById(R.id.tv_version);

            final Song song = getItem(position);
            tvVersion.setText(!TextUtils.isEmpty(song.SongVersion) ? song.SongVersion : "");

            int num = mPageNum * 8 + position + 1;

            tvNumber.setText(String.valueOf(num) + ".");
            tvNumber.setSelected(num == 1);
            tvName.setText(song.SimpName);
            tvName.setSelected(num == 1);
            String singer = TextUtils.isEmpty(song.SingerName) ? "" : song.SingerName.replace("false", "");
            tvSinger.setText(singer);


            String sortText = ChooseSongs.getInstance(context).getSongPriorities(song);
            if (num == 1) {//playing
                ivPreview.setVisibility(INVISIBLE);
                ivTop.setVisibility(INVISIBLE);
                ivDel.setVisibility(INVISIBLE);
                tvSort.setVisibility(VISIBLE);
            } else if (num == 2) {
                ivPreview.setVisibility(VISIBLE);
                ivTop.setVisibility(GONE);
                ivDel.setVisibility(VISIBLE);
                tvSort.setVisibility(GONE);
            } else {
                ivPreview.setVisibility(VISIBLE);
                ivTop.setVisibility(VISIBLE);
                ivDel.setVisibility(VISIBLE);
                tvSort.setVisibility(GONE);
            }

            tvSort.setText(sortText);
            tvSort.setSelected(num == 1);

            ivTop.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onToTop(song);
                }
            });

            ivPreview.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onPreview(song);
                }
            });

            ivDel.setOnClickListener(new OnClickListener() {
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
//            notifyDataSetChanged();
    }

    private void onDelete(Song song) {
        ChooseSongs.getInstance(getContext().getApplicationContext()).remove(song);
    }
}

