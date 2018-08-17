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
import com.beidousat.libbns.net.request.HttpRequest;
import com.beidousat.libbns.net.request.RequestMethod;
import com.beidousat.libbns.net.request.SSLHttpRequest;
import com.beidousat.libbns.util.DeviceUtil;
import com.beidousat.libbns.util.HttpParamsUtils;
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
    }

    @Override
    void initView() {
        mRecyclerView = findViewById(R.id.recycler_view);

        List<Meal> meals = null;
        if (mFmTag == Meal.TIME) {
            meals = MealDataFactory.createTimeMeal();
        } else {
            meals = MealDataFactory.createSongMeal();
    }

        mAdapter = new PayMealAdapter(getContext(), meals, mItemClickListener);
        mRecyclerView.setAdapter(mAdapter);
        FlexboxLayoutManager manager = new FlexboxLayoutManager(ROW);
        manager.setJustifyContent(JustifyContent.SPACE_BETWEEN);
        mRecyclerView.setLayoutManager(manager);
    }

    @Override
    void setListener() {

    }

    private View.OnClickListener mItemClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            mMeal = (Meal) v.getTag();
            switch (v.getId()) {
                case R.id.tv_buy:
                    initRequest(RequestMethod.CREATE_ORDER).doPost(0);
                    break;
            }
        }
    };

    private void showQrCode(Meal meal) {
        CommonDialog dialog = CommonDialog.getInstance();
        dialog.setShowClose(true);
        FmPayQrCode qrCode = new FmPayQrCode();
        Bundle bundle = new Bundle();
        bundle.putSerializable(FmPayQrCode.MEAL_TAG, meal);
        qrCode.setArguments(bundle);
        dialog.setContent(qrCode);
        if (!dialog.isAdded()) {
            dialog.show(getChildFragmentManager(), "commonDialog");
        }
    }

    @Override
    public HttpRequest initRequest(String method) {
        SSLHttpRequest request = new SSLHttpRequest(getActivity().getApplicationContext(), method);
        request.setHttpRequestListener(this);
        request.addParam(HttpParamsUtils.initCreateOrderParams(
                mMeal.getType(),
                mMeal.getAmount(),
                DeviceUtil.getCupChipID(getContext()),
                ""));
        request.setConvert2Class(Meal.class);
        return request;
    }

    @Override
    public void onStart(String method) {
        super.onStart(method);
        LoadingUtil.showLoadingDialog(getContext());
    }

    @Override
    public void onSuccess(String method, Object object) {
        super.onSuccess(method, object);

        LoadingUtil.closeLoadingDialog();
        switch (method) {
            case RequestMethod.CREATE_ORDER:
                if (object instanceof Meal) {
                    Meal meal = (Meal) object;
                    showQrCode(meal);
                }
                break;
        }
    }

    @Override
    public void onFailed(String method, String error) {
        super.onFailed(method, error);

        LoadingUtil.closeLoadingDialog();
    }
}
