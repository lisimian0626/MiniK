package com.beidousat.karaoke.adapter;

import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.andexert.library.RippleView;
import com.beidousat.karaoke.R;
import com.beidousat.karaoke.interf.OnSingerClickListener;
import com.beidousat.karaoke.model.StarInfo;
import com.beidousat.libbns.util.DiskFileUtil;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by J Wong on 2015/12/17 08:54.
 */
public class AdtSinger extends RecyclerView.Adapter<AdtSinger.ViewHolder> {

    private Context mContext;
    private LayoutInflater mInflater;
    private List<StarInfo> mData = new ArrayList<StarInfo>();

    public AdtSinger(Context context) {
        mContext = context;
        mInflater = LayoutInflater.from(context);
    }


    public void setData(List<StarInfo> data) {
        this.mData = data;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public RippleView content;
        public ImageView ivCover;
        public TextView tvName;

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
        View view = mInflater.inflate(R.layout.list_item_singer, viewGroup, false);
        ViewHolder viewHolder = new ViewHolder(view);
        viewHolder.content = (RippleView) view.findViewById(android.R.id.content);
        viewHolder.ivCover = (ImageView) view.findViewById(R.id.iv_cover);
        viewHolder.tvName = (TextView) view.findViewById(android.R.id.title);
        return viewHolder;
    }


    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final StarInfo starInfo = mData.get(position);
        holder.tvName.setText(starInfo.SimpName);
        if (TextUtils.isEmpty(starInfo.Img)) {
            holder.ivCover.setImageResource(R.drawable.star_default);
        } else {
            Uri starImg = DiskFileUtil.getSingerThumbnailImg(starInfo.Img);
            Glide.with(mContext).load(starImg).override(140, 140).centerCrop()
//                    .skipMemoryCache(true)
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .placeholder(R.drawable.star_default)
                    .error(R.drawable.star_default).into(holder.ivCover);
        }
        holder.content.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
            @Override
            public void onComplete(RippleView rippleView) {
                if (mOnSingerClickListener != null) {
                    mOnSingerClickListener.onSingerClick(starInfo);
                }
            }
        });
    }

    private OnSingerClickListener mOnSingerClickListener;

    public void setOnSingerClickListener(OnSingerClickListener listener) {
        this.mOnSingerClickListener = listener;
    }
}
