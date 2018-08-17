package com.beidousat.karaoke.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.beidousat.karaoke.R;
import com.beidousat.karaoke.model.Meal;

import java.util.List;

/**
 * author: Hanson
 * date:   2017/3/31
 * describe:
 */

public class PayMealAdapter extends RecyclerView.Adapter<PayMealAdapter.PayMealViewHolder> {
    Context mContext;
    List<Meal> mPayMeals;
    View.OnClickListener mItemClickListener;

    public PayMealAdapter(Context context, List<Meal> payMeals, View.OnClickListener listener) {
        mContext = context;
        mPayMeals = payMeals;
        mItemClickListener = listener;
    }

    @Override
    public PayMealViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.list_item_pay_meal, parent, false);
        view.setOnClickListener(mItemClickListener);

        return new PayMealViewHolder(view);
    }

    @Override
    public void onBindViewHolder(PayMealViewHolder holder, int position) {
        holder.onBind(position, mPayMeals.get(position));
    }

    @Override
    public int getItemCount() {
        return mPayMeals == null ? 0 : mPayMeals.size();
    }

    public class PayMealViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final ImageView mIvMeal;
        private final TextView mTvTitle;
        private final TextView mBuy;

        private Meal mMeal;


        public PayMealViewHolder(View itemView) {
            super(itemView);

            mIvMeal = (ImageView) itemView.findViewById(R.id.iv_pay_meal);
            mTvTitle = (TextView) itemView.findViewById(R.id.tv_pay_meal_title);
            mBuy = (TextView) itemView.findViewById(R.id.tv_buy);

            mBuy.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            v.setTag(mMeal);
            if (mItemClickListener != null) {
                mItemClickListener.onClick(v);
            }
        }

        public void onBind(int position, Meal meal) {
            mMeal = meal;

            mTvTitle.setText(meal.getTitle());
            if (meal.getDrawable() != null) {
                mIvMeal.setImageDrawable(meal.getDrawable());
            }
        }
    }
}
