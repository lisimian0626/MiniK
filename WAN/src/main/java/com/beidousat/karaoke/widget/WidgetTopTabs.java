package com.beidousat.karaoke.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.beidousat.karaoke.R;
import com.beidousat.karaoke.adapter.BaseRecyclerAdapter;
import com.beidousat.libbns.util.ListUtil;

import java.util.List;

/**
 * Created by J Wong on 2015/10/15 13:35.
 */
public class WidgetTopTabs extends RelativeLayout {

    private View mRootView;
    private RecyclerView mHlvLeft, mHlvRight;
    private OnTabClickListener mLeftOnItemClickListener, mRightOnItemClickListener;

    private AdapterTabs mAdapterTabsLeft, mAdapterTabsRight;

    public WidgetTopTabs(Context context) {
        super(context);
        initView();
    }

    public WidgetTopTabs(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initView();

        readAttr(attrs);
    }

    public WidgetTopTabs(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();

        readAttr(attrs);
    }

    private void initView() {
        mRootView = LayoutInflater.from(getContext()).inflate(R.layout.widget_top_tabs, this);
        mHlvLeft = (RecyclerView) mRootView.findViewById(R.id.hlv_left);
        mHlvRight = (RecyclerView) mRootView.findViewById(R.id.hlv_right);

        init();
    }

    private void readAttr(AttributeSet attrs) {
        if (attrs != null) {
            TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.WidgetTopTabs);
            float leftWeight = a.getFloat(R.styleable.WidgetTopTabs_leftTabWeight, 3);
            float rightWeight = a.getFloat(R.styleable.WidgetTopTabs_rightTabWeight, 1);

//            setLeftTabWeight(leftWeight);
//            setRightTabWeight(rightWeight);
        }
    }

//    public void setLeftTabWeight(float weight) {
//        LayoutParams paramLeft = new LayoutParams(0, LayoutParams.MATCH_PARENT, weight);
//        mHlvLeft.setLayoutParams(paramLeft);
//    }
//
//    public void setRightTabWeight(float weight) {
//        LayoutParams paramLeft = new LayoutParams(0, LayoutParams.MATCH_PARENT, weight);
//        mHlvRight.setLayoutParams(paramLeft);
//    }


    private void init() {
//        int dividerW = DensityUtil.dip2px(getContext(), 8);

//        VerticalDividerItemDecoration verDivider = new VerticalDividerItemDecoration.Builder(getContext())
//                .color(Color.TRANSPARENT).size(dividerW)
//                .margin(dividerW, dividerW)
//                .build();

        LinearLayoutManager layoutManager2 = new LinearLayoutManager(getContext());
        layoutManager2.setOrientation(LinearLayoutManager.HORIZONTAL);
        mHlvRight.setLayoutManager(layoutManager2);

//        mHlvRight.addItemDecoration(verDivider);
        mAdapterTabsRight = new AdapterTabs(rightOnTabClickListener, false);
        mHlvRight.setAdapter(mAdapterTabsRight);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        mHlvLeft.setLayoutManager(layoutManager);

//        mHlvLeft.addItemDecoration(verDivider);
        mAdapterTabsLeft = new AdapterTabs(leftOnTabClickListener, true);
        mHlvLeft.setAdapter(mAdapterTabsLeft);

    }

    private boolean mIsRightClickSelect = true;

    public void setRightClickSelect(boolean isClickSelect) {
        mIsRightClickSelect = isClickSelect;
    }

    public void setRightTabShow(boolean show) {
        mHlvRight.setVisibility(show ? VISIBLE : GONE);
    }

    public void setLeftTabs(int resId) {
        String[] texts = getResources().getStringArray(resId);
        setLeftTabs(texts);
    }


    public void setLeftTabs(String[] texts) {
        mAdapterTabsLeft.setData(ListUtil.array2List(texts));
        setLeftTabFocus(0);
        mAdapterTabsLeft.notifyDataSetChanged();
    }

    private OnTabClickListener leftOnTabClickListener = new OnTabClickListener() {
        @Override
        public void onTabClick(int position) {
//            if (mAdapterTabsLeft.getFocusedTab() != position) {
            setLeftTabFocus(position);
            if (mLeftOnItemClickListener != null)
                mLeftOnItemClickListener.onTabClick(position);
//            }
        }
    };
    private OnTabClickListener rightOnTabClickListener = new OnTabClickListener() {
        @Override
        public void onTabClick(int position) {
            if (!mIsRightClickSelect || mAdapterTabsRight.getFocusedTab() != position) {
                setRightTabFocus(position);
                if (mRightOnItemClickListener != null)
                    mRightOnItemClickListener.onTabClick(position);
            }
        }
    };


    public void setLeftTabFocus(int position) {
        mAdapterTabsLeft.setFocusedTab(position);
        mAdapterTabsLeft.notifyDataSetChanged();
    }

    public void setRightTabFocus(int position) {
        mAdapterTabsRight.setFocusedTab(position);
        mAdapterTabsRight.notifyDataSetChanged();
    }

    public void setRightTabs(int resId) {
        String[] texts = getResources().getStringArray(resId);
        setRightTabs(texts);
    }

    public void setRightTabs(String[] texts) {
        mAdapterTabsRight.setData(ListUtil.array2List(texts));
        mAdapterTabsRight.notifyDataSetChanged();
    }

    public void setLeftTabClickListener(OnTabClickListener listener) {
        this.mLeftOnItemClickListener = listener;
    }

    public void setRightTabClickListener(OnTabClickListener listener) {
        this.mRightOnItemClickListener = listener;
    }

    public interface OnTabClickListener {
        void onTabClick(int position);
    }

    private static class ViewHolder extends RecyclerView.ViewHolder {

        public View contentView;
        public TextView tvName;


        public ViewHolder(View view) {
            super(view);
        }
    }


    public class AdapterTabs extends RecyclerView.Adapter<ViewHolder> {

        private LayoutInflater mInflater;
        private List<String> mData;
        private int mFocusItemPs = -1;
        private OnTabClickListener mOnTabClickListener;
        private boolean mIsLeft;

        public AdapterTabs(OnTabClickListener listener, boolean isLeft) {
            mInflater = LayoutInflater.from(getContext());
            this.mOnTabClickListener = listener;
            this.mIsLeft = isLeft;
        }

        public int getFocusedTab() {
            return mFocusItemPs;
        }

        public void setFocusedTab(int tab) {
            this.mFocusItemPs = tab;
        }


        public void setData(List<String> data) {
            this.mData = data;
        }


        @Override
        public int getItemCount() {
            return mData == null ? 0 : mData.size();
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View view = mInflater.inflate(R.layout.list_item_top_tab, viewGroup, false);
            ViewHolder viewHolder = new ViewHolder(view);
            viewHolder.contentView = view.findViewById(R.id.ll_tab);
            viewHolder.tvName = (TextView) view.findViewById(android.R.id.title);
            return viewHolder;
        }


        @Override
        public void onBindViewHolder(ViewHolder holder, final int position) {
            final String keyText = mData.get(position);
            int res = R.drawable.selector_tab_center;
            if (getItemCount() <= 1) {
                res = R.drawable.selector_tab_single;
            } else {
                if (position == 0) {
                    res = R.drawable.selector_tab_left;
                } else if (position == getItemCount() - 1) {
                    res = R.drawable.selector_tab_right;
                }
            }
            holder.tvName.setBackgroundResource(res);
            holder.tvName.setText(keyText);
            if (mIsLeft) {
                holder.contentView.setSelected(mFocusItemPs == position);
            } else {
                if (mIsRightClickSelect)
                    holder.contentView.setSelected(mFocusItemPs == position);
            }
            holder.contentView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mOnTabClickListener != null) {
                        mOnTabClickListener.onTabClick(position);
                    }
                    mFocusItemPs = position;
                    notifyDataSetChanged();
                }
            });


            final String name = mData.get(position);
            holder.tvName.setText(name);
            if (mIsLeft) {
                holder.tvName.setSelected(mFocusItemPs == position);
            } else {
                if (mIsRightClickSelect)
                    holder.tvName.setSelected(mFocusItemPs == position);
            }
            holder.tvName.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mOnTabClickListener != null) {
                        mOnTabClickListener.onTabClick(position);
                    }
                    mFocusItemPs = position;
                    notifyDataSetChanged();
                }
            });
        }
    }
}
