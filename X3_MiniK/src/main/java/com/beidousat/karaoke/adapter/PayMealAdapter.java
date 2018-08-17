package com.beidousat.karaoke.adapter;

import android.content.Context;
import android.graphics.Paint;
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
//        private final TextView mBuy;
        private final ImageView mIvCouponTag;
        private final TextView mTime,mPrice;

        private Meal mMeal;


        public PayMealViewHolder(View itemView) {
            super(itemView);

            mIvMeal = (ImageView) itemView.findViewById(R.id.iv_pay_meal);
            mTvTitle = (TextView) itemView.findViewById(R.id.tv_pay_meal_title);
            mIvCouponTag=(ImageView)itemView.findViewById(R.id.iv_pay_tab);
//            mBuy = (TextView) itemView.findViewById(R.id.tv_buy);
//            mTvTitle.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG); //中间横线
            mTvTitle.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG | Paint.ANTI_ALIAS_FLAG);  // 设置中划线并加清晰
            mTvTitle.getPaint().setAntiAlias(true);//抗锯齿

            mTime = (TextView) itemView.findViewById(R.id.tv_real_time);
            mPrice = (TextView) itemView.findViewById(R.id.tv_real_price);
            itemView.setOnClickListener(this);
//            mBuy.setOnClickListener(this);
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
            mTime.setText(meal.getTime());
            mPrice.setText(meal.getPriceTitle());
            mTvTitle.setText(meal.getTitle());
            mTvTitle.setVisibility(meal.getPrice() != meal.getRealPrice() ? View.VISIBLE : View.INVISIBLE);
            if (meal.getDrawable() != null) {
                mIvMeal.setImageDrawable(meal.getDrawable());
            }
            if(meal.getUser_card()==1){
                mIvCouponTag.setVisibility(View.VISIBLE);
            }else{
                mIvCouponTag.setVisibility(View.GONE);
            }
        }
    }
}
