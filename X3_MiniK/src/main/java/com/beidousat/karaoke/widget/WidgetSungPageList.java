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
import com.beidousat.karaoke.model.Song;
import com.beidousat.karaoke.player.ChooseSongs;
import com.beidousat.karaoke.ui.Main;
import com.beidousat.karaoke.ui.dlg.DialogHelper;
import com.beidousat.karaoke.ui.dlg.DlgAudioPlayer;
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

        mAdapter = new AdtSung();
        setAdapter(mAdapter);
    }

    public void setSong(List<Song> songs) {
        mAdapter.setData(songs);
    }


    private static class ViewHolder extends RecyclerView.ViewHolder {

        public View contentView;
        public TextView tvName, tvSinger, ivRestore, tvVersion, tvSave, tvListen;


        public ViewHolder(View view) {
            super(view);
        }
    }

    public class AdtSung extends RecyclerView.Adapter<ViewHolder> {

        private List<Song> mData;

        public void setData(List<Song> songs) {
            this.mData = songs;
        }

        @Override
        public int getItemCount() {
            return mData.size();
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View itemView = LayoutInflater.from(getContext()).inflate(R.layout.list_item_sung_list, viewGroup, false);
            ViewHolder viewHolder = new ViewHolder(itemView);
            viewHolder.contentView = itemView.findViewById(R.id.contentView);
            viewHolder.tvName = (TextView) itemView.findViewById(R.id.tv_name);
            viewHolder.tvSinger = (TextView) itemView.findViewById(R.id.tv_singer);
            viewHolder.ivRestore = (TextView) itemView.findViewById(R.id.tv_restore);
            viewHolder.tvVersion = (TextView) itemView.findViewById(R.id.tv_version);
            viewHolder.tvSave = (TextView) itemView.findViewById(R.id.tv_download);
            viewHolder.tvListen = (TextView) itemView.findViewById(R.id.tv_listen);

            viewHolder.tvSave.setText(getContext().getString(R.string.share));
            return viewHolder;
        }


        @Override
        public void onBindViewHolder(ViewHolder holder, final int position) {
            final Song song = mData.get(position);

            holder.tvVersion.setText(!TextUtils.isEmpty(song.SongVersion) ? song.SongVersion : "");
            holder.tvName.setText(song.SimpName);
            String singer = TextUtils.isEmpty(song.SingerName) ? "" : song.SingerName.replace("false", "");
            holder.tvSinger.setText(singer);
            holder.tvName.setText(song.SimpName);
            boolean isShowBtn = false;
            if (song.playType == 0) {
                File fileRecord = AudioRecordFileUtil.getRecordFile(song.RecordFile);
                isShowBtn = fileRecord.exists();
            }

            holder.ivRestore.setText(song.playType == 0 ? R.string.replay : R.string.replay2);

            holder.tvSave.setVisibility(isShowBtn ? View.VISIBLE : View.GONE);
            holder.tvListen.setVisibility(isShowBtn ? View.VISIBLE : View.GONE);

            holder.ivRestore.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    onRestore(song);
                }
            });


            holder.tvSave.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
//                    DlgShare dlgShare = new DlgShare(Main.mMainActivity, song);
//                    dlgShare.show();
                    DialogHelper.showShareDialog(Main.mMainActivity, song);
                }
            });


            holder.tvListen.setOnClickListener(new OnClickListener() {
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
