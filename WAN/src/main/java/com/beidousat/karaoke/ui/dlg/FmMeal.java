package com.beidousat.karaoke.ui.dlg;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.beidousat.karaoke.R;
import com.beidousat.karaoke.adapter.PayMealAdapter;
import com.beidousat.karaoke.model.Meal;
import com.beidousat.karaoke.ui.Main;
import com.beidousat.libbns.util.Logger;
import com.google.android.flexbox.FlexboxLayoutManager;
import com.google.android.flexbox.JustifyContent;

import java.util.List;

import static com.google.android.flexbox.FlexDirection.ROW;

/**
 * author: Hanson
 * date:   2017/3/30
 * describe:
 */

public class FmMeal extends FmBaseDialog {
    private int mFmTag;
    private PayMealAdapter mAdapter;
    private RecyclerView mRecyclerView;
    private Meal mMeal;

    public static final String FM_TAG = "FrgamentTag";
    public static final String FM_CARDCODE="Fm_CardCode";

    private String fm_cardcode;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.fm_meal, container, false);
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    void initData() {
        Bundle bundle = getArguments();
        mFmTag = bundle.getInt(FM_TAG);
        fm_cardcode=bundle.getString(FM_CARDCODE);
    }

    @Override
    void initView() {
        mRecyclerView = findViewById(R.id.recycler_view);

        List<Meal> meals = MealDataFactory.getMeal(mFmTag);

        FlexboxLayoutManager manager = new FlexboxLayoutManager(ROW);
        manager.setJustifyContent(JustifyContent.SPACE_BETWEEN);
//        LinearLayoutManager manager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
//        VerticalDividerItemDecoration verDivider = new VerticalDividerItemDecoration.Builder(getContext())
//                .color(Color.TRANSPARENT).size(DensityUtil.dip2px(getContext(), 120)).build();
//        mRecyclerView.addItemDecoration(verDivider);
        mRecyclerView.setLayoutManager(manager);

        mAdapter = new PayMealAdapter(getContext(), meals, mItemClickListener);
        mRecyclerView.setAdapter(mAdapter);
    }

    @Override
    void setListener() {

    }

    private View.OnClickListener mItemClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            mMeal = (Meal) v.getTag();

//            switch (v.getId()) {
//                case R.id.tv_buy:
                    if(mMeal.getUse_online() == 1){
                        CommonDialog dialog = CommonDialog.getInstance();
                        dialog.setShowClose(true);
                        FmChoosePay qrCode = new FmChoosePay();
                        Bundle bundle = new Bundle();
                        bundle.putSerializable(FmChoosePay.MEAL_TAG, mMeal);
                        bundle.putString(FmChoosePay.MEAL_CARDCODE,fm_cardcode);
                        qrCode.setArguments(bundle);
                        dialog.setContent(qrCode);
                        if (!dialog.isAdded()) {
                            dialog.show(getChildFragmentManager(), "commonDialog");
                        }
//                        break;
                    }else {
                        showTBPayNumber(mMeal);
                    }

//            }
        }
    };

    private void showTBPayNumber(Meal meal) {
        float nowPrice = meal.getPrice();
        float preRealPrice = mMeal.getRealPrice();

        Logger.d("FmChoosePay", "nowRealPrice:" + nowPrice + "  preRealPrice:" + preRealPrice);

        CommonDialog dialog = CommonDialog.getInstance();
        dialog.setShowClose(true);
        FmTBPayNumber qrCode = new FmTBPayNumber();
        Bundle bundle = new Bundle();
        bundle.putSerializable(FmTBPayNumber.MEAL_TAG, meal);
//        bundle.putString(FmPayQrCode.TYPE_TAG, mPayment);
//        bundle.putBoolean(FmPayQrCode.TAG_PRICE_CHANGE, nowPrice != preRealPrice);

        qrCode.setArguments(bundle);
        dialog.setContent(qrCode);
        if (!dialog.isAdded()) {
            dialog.show(getChildFragmentManager(), "commonDialog");
        }
    }

//    private void showQrCode(Meal meal) {
//        CommonDialog dialog = CommonDialog.getInstance();
//        dialog.setShowClose(true);
//        FmChoosePay qrCode = new FmChoosePay();
//        Bundle bundle = new Bundle();
//        bundle.putSerializable(FmPayQrCode.MEAL_TAG, meal);
//        qrCode.setArguments(bundle);
//        dialog.setContent(qrCode);
//        if (!dialog.isAdded()) {
//            dialog.show(getChildFragmentManager(), "commonDialog");
//        }
//    }
//
//    @Override
//    public HttpRequest initRequest(String method) {
//        SSLHttpRequest request = new SSLHttpRequest(getActivity().getApplicationContext(), method);
//        request.setHttpRequestListener(this);
//        request.addParam(HttpParamsUtils.initCreateOrderParams(
//                mMeal.getType(),
//                mMeal.getAmount(),
//                DeviceUtil.getCupChipID(),
//                KBoxInfo.getInstance().getKBox().getKBoxSn()));
//        request.setConvert2Class(Meal.class);
//        return request;
//    }

    @Override
    public void onStart(String method) {
        super.onStart(method);
        LoadingUtil.showLoadingDialog(Main.mMainActivity);
    }

//    @Override
//    public void onSuccess(String method, Object object) {
//        super.onSuccess(method, object);
//
//        LoadingUtil.closeLoadingDialog();
//        switch (method) {
//            case RequestMethod.CREATE_ORDER:
//                if (object instanceof Meal) {
//                    Meal meal = (Meal) object;
//                    showQrCode(meal);
//                }
//                break;
//        }
//    }
//
//    @Override
//    public void onFailed(String method, String error) {
//        super.onFailed(method, error);
//
//        LoadingUtil.closeLoadingDialog();
//        DialogFactory.showErrorDialog(getContext(), error, new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                dialog.dismiss();
//            }
//        });
//    }

}
