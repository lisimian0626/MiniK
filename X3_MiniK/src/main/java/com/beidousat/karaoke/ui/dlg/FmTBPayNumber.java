package com.beidousat.karaoke.ui.dlg;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.beidousat.karaoke.R;
import com.beidousat.karaoke.biz.QueryOrderHelper;
import com.beidousat.karaoke.biz.SupportQueryOrder;
import com.beidousat.karaoke.data.BoughtMeal;
import com.beidousat.karaoke.data.KBoxInfo;
import com.beidousat.karaoke.data.PrefData;
import com.beidousat.libbns.model.ServerConfigData;
import com.beidousat.karaoke.model.Meal;
import com.beidousat.karaoke.model.PayStatus;
import com.beidousat.karaoke.ui.Main;
import com.beidousat.libbns.evenbus.BusEvent;
import com.beidousat.libbns.evenbus.EventBusId;
import com.beidousat.libbns.evenbus.EventBusUtil;
import com.beidousat.libbns.net.request.HttpRequest;
import com.beidousat.libbns.net.request.RequestMethod;
import com.beidousat.libbns.util.DeviceUtil;
import com.beidousat.libbns.util.Logger;
import com.beidousat.libbns.util.QrCodeUtil;
import com.beidousat.score.KeyInfo;
import com.hoho.android.usbserial.util.TBManager;

import java.util.Timer;
import java.util.TimerTask;

import de.greenrobot.event.EventBus;

/**
 * author: Hanson
 * date:   2017/3/30
 * dscribe:
 */

public class FmTBPayNumber extends FmBaseDialog implements SupportQueryOrder {

    private TextView mTBNumber;
    private int mTBCount = 0;
    private TextView mTvMeal;
    public final static String MEAL_TAG = "SelectedMeal";
    private Meal mSelectedMeal;
    private final static int TOUBI_CHANGE_MSG = 1;
    private QueryOrderHelper mQueryOrderHelper;
    private AlertDialog mConfirmDlg;
    private TextView mBtnBack;
    private TextView tvMoneyUnit;
    private int mNeedCoin;

    Handler myHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case TOUBI_CHANGE_MSG:
                    if (mTBCount >= mNeedCoin) {
                        //支付成功
                        mTBNumber.setText(mNeedCoin + "/ " + mTBCount + getResources().getString(R.string.coin));
                        paySuccess();
                    } else {
                        mTBNumber.setText(mNeedCoin + "/ " + mTBCount + getResources().getString(R.string.coin));
                    }
                    break;
            }
            super.handleMessage(msg);
        }
    };


    private void paySuccess() {
        PayStatus payStatus = new PayStatus();
        payStatus.setPayStatus(1);
        payStatus.setPayTime((int) System.currentTimeMillis());
        payStatus.setOrderSn(mSelectedMeal.getOrderSn());
        payStatus.setDeviceSn(DeviceUtil.getCupChipID());
        payStatus.setType(mSelectedMeal.getType());
        payStatus.setAmount(mSelectedMeal.getAmount());
        boolean isMealExpire = BoughtMeal.getInstance().isMealExpire();
        CommonDialog dialog = CommonDialog.getInstance();
        dialog.setShowClose(true);
        dialog.setContent(new FmPayResult());
        if (!dialog.isAdded()) {
            dialog.show(getFragmentManager(), "commonDialog");
        }
        //确保支付的套餐是正确的
        mSelectedMeal.setAmount(payStatus.getAmount());
        mSelectedMeal.setType(payStatus.getType());
        mQueryOrderHelper.reportCoinPayFinish(mSelectedMeal).post();

        //设置当前购买的套餐
        BoughtMeal.getInstance().setBoughtMeal(mSelectedMeal, payStatus);
        EventBusUtil.postPaySucceed(isMealExpire);

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.fm_pay_tb_number, container, false);
        mAttached.getDialog().setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                final int action = event.getAction();
                final boolean isDown = action == KeyEvent.ACTION_DOWN;
                if(isDown&&keyCode==62){
                    mTBCount++;
                    myHandler.sendEmptyMessage(TOUBI_CHANGE_MSG);
                }
                return false;
            }
        });
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    void initData() {

        mSelectedMeal = (Meal) getArguments().getSerializable(MEAL_TAG);

        mQueryOrderHelper = new QueryOrderHelper(this);

    }


    @Override
    void initView() {
        mBtnBack = findViewById(R.id.btn_back);
        tvMoneyUnit = (TextView) findViewById(R.id.tv_money_unit);
        mTBNumber = (TextView) findViewById(R.id.tv_money);
        mTvMeal = (TextView) findViewById(R.id.tv_selected_meal);

        mNeedCoin = getBiCount();

        Logger.d(getClass().getSimpleName(), "initView mNeedCoin:" + mNeedCoin +
                "  Cion_exchange_rate:" + KBoxInfo.getInstance().getKBox().getCoin_exchange_rate());

        mTvMeal.setText(getResources().getString(R.string.text_selected_pay_meal,
                mSelectedMeal.getAmount(), mSelectedMeal.getUnit()));

        mTBNumber.setText(mNeedCoin + "/0 "+getResources().getString(R.string.coin));


        TBManager.getInstance().start(getContext(), new TBManager.TBManagerListener() {
            @Override
            public void onNewBi() {
                mTBCount++;
                myHandler.sendEmptyMessage(TOUBI_CHANGE_MSG);
            }

            @Override
            public void onError(String error) {
//                tvMoneyUnit.setText(getString(R.string.coin_error));
//                mTBNumber.setVisibility(View.GONE);
            }
        });

    }

    public int getBiCount() {
        int count = 0;
        float needMoney = mSelectedMeal.getPrice() * 100;
        if (KBoxInfo.getInstance().getKBox().getCoin_exchange_rate() > 0) {
            if (needMoney % KBoxInfo.getInstance().getKBox().getCoin_exchange_rate() == 0) {
                count = (int) (needMoney / KBoxInfo.getInstance().getKBox().getCoin_exchange_rate());
            } else {
                count = (int) (needMoney / KBoxInfo.getInstance().getKBox().getCoin_exchange_rate()) + 1;
            }
        }
        return count;
    }

    @Override
    void setListener() {
        mBtnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showConfirmDialog();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        //注册对话框关闭事件
        mAttached.registerCloseListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showConfirmDialog();
            }
        });
    }

    @Override
    public void onPause() {
        super.onPause();
//        TBManager.getInstance().stop();
        //反注册对话框关闭事件
        mAttached.unregisterCloseListener(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }


    private void showConfirmDialog() {
        if (mTBCount == 0) {//已投币提示损失
            mConfirmDlg = DialogFactory.showCancelCoinDialog(getContext(),
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            mQueryOrderHelper.cancelOrder(mSelectedMeal).post();
                        }
                    }, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    }, getString(R.string.text_cancel_order_coin, mTBCount));
        } else {
            mQueryOrderHelper.cancelOrder(mSelectedMeal).post();
        }
    }

    @Override
    public Context getSupportedContext() {
        return getContext();
    }

    @Override
    public FragmentManager getSupportedFragmentManager() {
        return getChildFragmentManager();
    }

    @Override
    public void sendRequestMessage(boolean isSucced, String method, Object data) {
        switch (method) {
            case RequestMethod.ORDER_CANCEL:
                if (isSucced) {
                    mRequestHandler.sendEmptyMessage(10010);
                }
                break;
            case RequestMethod.ORDER_FINISH_PAY:

                break;
        }
    }


    private Handler mRequestHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case 10010:
                    if (mConfirmDlg != null)
                        mConfirmDlg.dismiss();
                    mAttached.dismiss();
                    break;
                default:
                    break;
            }
            return true;
        }
    });


}
