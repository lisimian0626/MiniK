package com.beidousat.karaoke.widget;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.beidousat.karaoke.R;
import com.beidousat.karaoke.adapter.BaseRecyclerAdapter;
import com.beidousat.karaoke.model.Song;
import com.beidousat.karaoke.player.ChooseSongs;
import com.beidousat.karaoke.ui.Main;
import com.beidousat.karaoke.ui.dlg.DlgAudioPlayer;
import com.beidousat.karaoke.ui.dlg.DlgShare;
import com.beidousat.libwidget.recycler.HorizontalDividerItemDecoration;
import com.beidousat.libwidget.recycler.VerticalDividerItemDecoration2;
import com.czt.mp3recorder.AudioRecordFileUtil;

import java.io.File;
import java.util.List;

/**
 * Created by J Wong on 2015/12/17 18:01.
 */
public class WidgetSungPageList extends RecyclerView {


    private AdtSung mAdapter;

    public WidgetSungPageList(Context context) {
        super(context);
        init();
    }

    public WidgetSungPageList(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public WidgetSungPageList(Context context, AttributeSet attrs, int defStyleAttr) {
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

//        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
//        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
//        setLayoutManager(layoutManager);
        mAdapter = new AdtSung();
        setAdapter(mAdapter);
//        int spacing = getResources().getDimensionPixelSize(R.dimen.grid_spacing);
    }

    public void setSong(List<Song> songs) {
        mAdapter.setData(songs);
    }

    private static class ViewHolder {
        public View contentView;
        public TextView tvName;
        public TextView tvSinger;
        public ImageView ivRestore;
    }

    public class AdtSung extends BaseRecyclerAdapter<Song> {


        @Override
        protected int getItemViewLayoutId() {
            return R.layout.list_item_sung_list;
        }


        @Override
        public void onBindViewHolder(BaseRecyclerAdapter.ViewHolder holder, final int position) {
            final Song song = mData.get(position);
            Context context = holder.mContext;
            View itemView = holder.getItemView();
            View contentView = itemView.findViewById(R.id.contentView);
            TextView tvName = (TextView) itemView.findViewById(R.id.tv_name);
            TextView tvSinger = (TextView) itemView.findViewById(R.id.tv_singer);
            TextView ivRestore = (TextView) itemView.findViewById(R.id.tv_restore);
            TextView tvVersion = (TextView) itemView.findViewById(R.id.tv_version);
            TextView tvSave = (TextView) itemView.findViewById(R.id.tv_download);
            TextView tvListen = (TextView) itemView.findViewById(R.id.tv_listen);

            tvSave.setText(getContext().getString(R.string.share));
            tvVersion.setText(!TextUtils.isEmpty(song.SongVersion) ? song.SongVersion : "");
            tvName.setText(song.SimpName);
            String singer = TextUtils.isEmpty(song.SingerName) ? "" : song.SingerName.replace("false", "");
            tvSinger.setText(singer);
            tvName.setText(song.SimpName);
            boolean isShowBtn = false;
            if (song.playType == 0) {
                File fileRecord = AudioRecordFileUtil.getRecordFile(song.RecordFile);
                isShowBtn = fileRecord.exists();
            }

            ivRestore.setText(song.playType == 0 ? R.string.replay : R.string.replay2);

            tvSave.setVisibility(isShowBtn ? View.VISIBLE : View.GONE);

            ivRestore.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    onRestore(song);
                }
            });


            tvSave.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    DlgShare dlgShare = new DlgShare(Main.mMainActivity, song);
                    dlgShare.show();
                }
            });


            tvListen.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    DlgAudioPlayer dlgAudioPlayer = new DlgAudioPlayer(Main.mMainActivity, song);
                    dlgAudioPlayer.show();
                }
            });
        }
    }

    private void onRestore(Song info) {

        boolean ret = ChooseSongs.getInstance(getContext().getApplicationContext()).addSong(info);
        if (ret)
            ChooseSongs.getInstance(getContext().getApplicationContext()).removeSung(info);
    }
}
