package com.beidousat.karaoke.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.beidousat.libwidget.image.RecyclerImageView;

/**
 * Created by J Wong on 2016/5/3.
 */
public class AdtTopType extends RecyclerView.Adapter<AdtTopType.ViewHolder> {

    private Context mContext;
    private int[] mData;
    private int mFocusTab;

    public AdtTopType(Context context) {
        mContext = context;
    }

    public void setFocusTab(int focusTab) {
        mFocusTab = focusTab;
    }

    public void setData(int[] resId) {
        this.mData = resId;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private RecyclerImageView imageView;

        public ViewHolder(View view) {
            super(view);
        }
    }

    @Override
    public int getItemCount() {
        return mData == null ? 0 : mData.length;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        RecyclerImageView recyclerImageView = new RecyclerImageView(mContext);
        ViewHolder viewHolder = new ViewHolder(recyclerImageView);
        viewHolder.imageView = recyclerImageView;
        return viewHolder;
    }


    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        int resId = mData[position];
        holder.imageView.setImageResource(resId);
        holder.imageView.setSelected(position == mFocusTab);
        holder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int preF = mFocusTab;
                mFocusTab = position;
                notifyItemChanged(position);
                notifyItemChanged(preF);
                if (mOnTopTypeSelectListener != null)
                    mOnTopTypeSelectListener.onTopSelect(position);
            }
        });
    }

    private OnTopTypeSelectListener mOnTopTypeSelectListener;

    public void setOnTopTypeSelectListener(OnTopTypeSelectListener l) {
        mOnTopTypeSelectListener = l;
    }

    public interface OnTopTypeSelectListener {
        void onTopSelect(int position);
    }
}
