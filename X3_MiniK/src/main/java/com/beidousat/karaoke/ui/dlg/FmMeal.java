package com.beidousat.karaoke.ui.dlg;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.beidousat.karaoke.R;
import com.beidousat.karaoke.adapter.PayMealAdapter;
import com.beidousat.karaoke.data.KBoxInfo;
import com.beidousat.karaoke.model.KBox;
import com.beidousat.karaoke.model.Meal;
import com.beidousat.karaoke.ui.Main;
import com.beidousat.karaoke.util.ToastUtils;
import com.beidousat.libbns.model.ServerConfigData;
import com.beidousat.libbns.net.request.HttpRequest;
import com.beidousat.libbns.net.request.RequestMethod;
import com.beidousat.libbns.net.request.StoreHttpRequest;
import com.beidousat.libbns.util.DeviceUtil;
import com.beidousat.libbns.util.HttpParamsUtils;
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
    public static final String FM_CARDCODE = "Fm_CardCode";

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
        fm_cardcode = bundle.getString(FM_CARDCODE);
    }

    @Override
    void initView() {
        mRecyclerView = findViewById(R.id.recycler_view);

        List<Meal> meals = MealDataFactory.getMeal(mFmTag);

        FlexboxLayoutManager manager = new FlexboxLayoutManager(ROW);
        manager.setJustifyContent(JustifyContent.SPACE_BETWEEN);
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
            initStoreRequest(ServerConfigData.getInstance().getServerConfig().getStore_web(), RequestMethod.ORDER_CREATE2).post();
        }
    };

    private void dealAfterCreateOrder(Meal meal) {
        KBox kBox=KBoxInfo.getInstance().getKBox();
        if(kBox.getUse_coin()==1&&kBox.getUse_online()==0&&kBox.getUse_gift_card()==0&&kBox.getUse_pos()==0){
            showTBPayNumber(meal);
        }else if(kBox.getUse_coin()==0&&kBox.getUse_online()==0&&kBox.getUse_gift_card()==1&&kBox.getUse_pos()==0){
            showCardPay(meal);
        }else if(kBox.getUse_coin()==0&&kBox.getUse_online()==0&&kBox.getUse_gift_card()==0&&kBox.getUse_pos()==1){
            showOctPayNumber(meal);
        }else{
            CommonDialog dialog = CommonDialog.getInstance();
            dialog.setShowClose(true);
            FmChoosePay choosePay = new FmChoosePay();
            Bundle bundle = new Bundle();
            bundle.putSerializable(FmChoosePay.MEAL_TAG, meal);
            bundle.putString(FmChoosePay.MEAL_CARDCODE, fm_cardcode);
            choosePay.setArguments(bundle);
            dialog.setContent(choosePay);
            if (!dialog.isAdded()) {
                dialog.show(getChildFragmentManager(), "commonDialog");
            }
        }
    }

    @Override
    public StoreHttpRequest initStoreRequest(String urlHost, String method) {
        StoreHttpRequest request = new StoreHttpRequest(urlHost,method);
        request.setStoreHttpRequestListener(this);
        if (TextUtils.isEmpty(fm_cardcode)) {
            request.addParam(HttpParamsUtils.initCreateOrder2Params(mMeal.getType(), mMeal.getAmount(),
                    DeviceUtil.getCupChipID(), KBoxInfo.getInstance().getKBox().getKBoxSn(), "", 1));

        } else {
            request.addParam(HttpParamsUtils.initCreateOrder2Params(mMeal.getType(), mMeal.getAmount(), DeviceUtil.getCupChipID(), KBoxInfo.getInstance().getKBox().getKBoxSn(), fm_cardcode, 1));
        }
        request.setConvert2Class(Meal.class);
        return request;
    }

    @Override
    public void onStoreStart(String method) {
        LoadingUtil.showLoadingDialog(Main.mMainActivity);
        super.onStoreStart(method);
    }

    @Override
    public void onStoreSuccess(String url, Object object) {
        LoadingUtil.closeLoadingDialog();
        Meal meal= (Meal) object;
        meal.setLable(mMeal.getLable());
        dealAfterCreateOrder(meal);
        super.onStoreSuccess(url, object);
    }

    @Override
    public void onStoreFailed(String url, String error) {
        LoadingUtil.closeLoadingDialog();
        if(getContext()==null)
            return;
        ToastUtils.toast(getContext(),error);
        super.onStoreFailed(url, error);
    }
    private void showTBPayNumber(Meal meal) {
        Logger.d(getClass().getSimpleName(), "showTBPayNumber  meal:" + meal.getPrice() + "  ");
        CommonDialog dialog = CommonDialog.getInstance();
        dialog.setShowClose(true);
        FmTBPayNumber qrCode = new FmTBPayNumber();
        Bundle bundle = new Bundle();
        bundle.putSerializable(FmTBPayNumber.MEAL_TAG, meal);
        qrCode.setArguments(bundle);
        dialog.setContent(qrCode);
        if (!dialog.isAdded()) {
            dialog.show(getChildFragmentManager(), "commonDialog");
        }
    }
    private void showOctPayNumber(Meal meal) {
//        Logger.d(getClass().getSimpleName(), "showTBPayNumber  meal:" + mMeal.getPrice() + "  ");
        CommonDialog dialog = CommonDialog.getInstance();
        dialog.setShowClose(true);
        FmOctoNumber fmOctoNumber = new FmOctoNumber();
        Bundle bundle = new Bundle();
        bundle.putSerializable(FmOctoNumber.MEAL_TAG, meal);
        fmOctoNumber.setArguments(bundle);
        dialog.setContent(fmOctoNumber);
        if (!dialog.isAdded()) {
            dialog.show(getChildFragmentManager(), "commonDialog");
        }
    }
    private void showCardPay(Meal meal) {
        Logger.d(getClass().getSimpleName(), "showPayCard  meal:" + meal.getPrice() + "  ");
        CommonDialog dialog = CommonDialog.getInstance();
        dialog.setShowClose(true);
        FmPayCard payCard = new FmPayCard();
        Bundle bundle = new Bundle();
        bundle.putSerializable(FmPayCard.MEAL_TAG, meal);
        payCard.setArguments(bundle);
        dialog.setContent(payCard);
        if (!dialog.isAdded()) {
            dialog.show(getChildFragmentManager(), "commonDialog");
        }
    }
}
