package com.beidousat.karaoke.ui.dlg;

import android.annotation.SuppressLint;
import android.app.Instrumentation;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.beidousat.karaoke.R;
import com.beidousat.karaoke.biz.QueryOrderHelper;
import com.beidousat.karaoke.biz.SupportQueryOrder;
import com.beidousat.karaoke.data.BoughtMeal;
import com.beidousat.karaoke.data.KBoxInfo;
import com.beidousat.karaoke.model.KBox;
import com.beidousat.karaoke.model.Notecode;
import com.beidousat.karaoke.util.SerialController;
import com.beidousat.karaoke.util.ToastUtils;
import com.beidousat.libbns.evenbus.ICTEvent;
import com.beidousat.libbns.model.Common;
import com.beidousat.karaoke.model.Meal;
import com.beidousat.karaoke.model.PayStatus;
import com.beidousat.libbns.evenbus.EventBusId;
import com.beidousat.libbns.evenbus.EventBusUtil;
import com.beidousat.libbns.net.request.RequestMethod;
import com.beidousat.libbns.util.DeviceUtil;
import com.beidousat.libbns.util.Logger;
import com.hoho.android.usbserial.util.TBManager;

import java.text.DecimalFormat;
import java.util.List;

import de.greenrobot.event.EventBus;

/**
 * author: Hanson
 * date:   2017/3/30
 * dscribe:
 */

public class FmTBPayNumber extends FmBaseDialog implements SupportQueryOrder {
    private final String TAG = "TBPay";
    private TextView mTBNumber;
    private int totoal;
    private int papermoney = 0;
    private int needmoney = 0;
    private int addmoney = 0;
    private int tbmoney=0;
    private int diffence;
    private TextView mTvMeal;
    private ImageView mImage;
    public final static String MEAL_TAG = "SelectedMeal";
    private Meal mSelectedMeal;
    private final static int TOUBI_CHANGE_MSG = 1;
    private QueryOrderHelper mQueryOrderHelper;
    private AlertDialog mConfirmDlg;
    private TextView mBtnBack;
    private TextView tvMoneyUnit, tvCodeMeal;
    private int mNeedCoin;
    private String code;
    public static final byte[] acceptbyte = {0x02};
    private byte[] rejectbyte = {0x15};
    private byte[] holdbyte = {0x24};
    public static final byte[] closebyte = {0x30};
    private final String PaySucced = "10";
    private final String PayFail = "11";
    public static final String TypeON = "808F";
    public static final String REJECT = "292F";
    int rejecttimes = 0;
    private String Type0 = "FFFFFFFF";
    private String Type1 = "FFFFFFFF";
    private String Type2 = "FFFFFFFF";
    private String Type3 = "FFFFFFFF";
    private String Type4 = "FFFFFFFF";
    private int Coin0 = 1;
    private int Coin1 = 5;
    private int Coin2 = 10;
    private int Coin3 = 20;
    private int Coin4 = 50;
    private String unit = "元";
    DecimalFormat df = new DecimalFormat("0.00");
    Handler myHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case TOUBI_CHANGE_MSG:
                    if (Common.isICT) {
                        if (needmoney == 0)
                            return;
                        int TbCount = KBoxInfo.getInstance().getKBox().getCoin_exchange_rate();
                        tbmoney=Common.TBcount*TbCount;
                        totoal= papermoney +tbmoney+Common.lastMoney;
                        double f1 = needmoney / 100f;
                        double f2 = totoal / 100f;
                        mTBNumber.setText(String.valueOf(df.format(f1)) + "/" + String.valueOf(df.format(f2)) + unit);
                        if (totoal >= needmoney) {

                            paySuccess();
                        }
                    } else {
                        mTBNumber.setText(mNeedCoin + "/" + Common.TBcount + getResources().getString(R.string.coin));
                        if (Common.TBcount >= mNeedCoin) {
                            //支付成功
                            paySuccess();
                        }
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
        mQueryOrderHelper.reportCoinPayFinish(mSelectedMeal);

        //设置当前购买的套餐
        BoughtMeal.getInstance().setBoughtMeal(mSelectedMeal, payStatus);
        EventBusUtil.postPaySucceed(isMealExpire);
        if(Common.isICT){
            Common.lastMoney=totoal-needmoney;
            Common.TBcount=0;
        }else{
            Common.TBcount=Common.TBcount-mNeedCoin;
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        EventBus.getDefault().register(this);
        mRootView = inflater.inflate(R.layout.fm_pay_tb_number, container, false);
        mAttached.getDialog().setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                final int action = event.getAction();
                final boolean isDown = action == KeyEvent.ACTION_DOWN;
                if (isDown && keyCode == 62) {
                    Common.TBcount++;
                    myHandler.sendEmptyMessage(TOUBI_CHANGE_MSG);
                }
                return true;
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
        mImage = findViewById(R.id.iv_pay_qrcode);
        if (Common.isEn) {
            mImage.setImageResource(R.drawable.pay_token_en);
        } else {
            mImage.setImageResource(R.drawable.pay_token);
        }
//        mImage.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                sendBack();
//            }
//        });
        tvCodeMeal = findViewById(R.id.tv_codeMeal);
        mTvMeal.setText(getResources().getString(R.string.text_selected_pay_meal,
                mSelectedMeal.getAmount(), mSelectedMeal.getUnit()));
        KBox kBox = KBoxInfo.getInstance().getKBox();
        if (kBox != null && !TextUtils.isEmpty(kBox.getCoin_unit())) {
            Common.isICT = true;
            List<Notecode> list_code = kBox.getBanknote_code();
            if (list_code != null && list_code.size() > 0) {
                String codeMeal = "";
                for (int i = 0; i < list_code.size(); i++) {
                    if (i == 0) {
                        Type0 = list_code.get(0).getCode();
                        Coin0 = Integer.valueOf(list_code.get(0).getUnit()) * 100;
                    } else if (i == 1) {
                        Type1 = list_code.get(1).getCode();
                        Coin1 = Integer.valueOf(list_code.get(1).getUnit()) * 100;
                    } else if (i == 2) {
                        Type2 = list_code.get(2).getCode();
                        Coin2 = Integer.valueOf(list_code.get(2).getUnit()) * 100;
                    } else if (i == 3) {
                        Type3 = list_code.get(3).getCode();
                        Coin3 = Integer.valueOf(list_code.get(3).getUnit()) * 100;
                    } else if (i == 4) {
                        Type4 = list_code.get(4).getCode();
                        Coin4 = Integer.valueOf(list_code.get(4).getUnit()) * 100;
                    }
                    codeMeal += list_code.get(i).getUnit() + "  ";
                }
//                tvCodeMeal.setText(getString(R.string.ICT_codeMeal)+codeMeal+" "+unit);
//                tvCodeMeal.setVisibility(View.VISIBLE);
            } else {
//                tvCodeMeal.setVisibility(View.VISIBLE);
            }
            Logger.d(TAG, "needmoney:" + mSelectedMeal.getIntPrice());
            needmoney = mSelectedMeal.getIntPrice();
//            needmoney = Math.round(mSelectedMeal.getPrice());
            unit = kBox.getCoin_unit();
            double f = needmoney / 100f;
            int TbCount = KBoxInfo.getInstance().getKBox().getCoin_exchange_rate();
            tbmoney=Common.TBcount*TbCount;
            totoal=papermoney+tbmoney+Common.lastMoney;
            if(totoal>=needmoney){
                paySuccess();
            }
            mTBNumber.setText(String.valueOf(df.format(f)) + "/"+df.format(totoal/100f) + kBox.getCoin_unit());
        } else {
            Common.isICT = false;
            mNeedCoin = getBiCount();
            Logger.d(getClass().getSimpleName(), "initView mNeedCoin:" + mNeedCoin +
                    "  Cion_exchange_rate:" + KBoxInfo.getInstance().getKBox().getCoin_exchange_rate());
            if(Common.TBcount>=mNeedCoin){
                paySuccess();
            }
            mTBNumber.setText(mNeedCoin + "/"+Common.TBcount + getResources().getString(R.string.coin));
        }

//        if (Common.isICT) {
//
//            List<Notecode> list_code=kBox.getBanknote_code();
//            if(list_code!=null&&list_code.size()>0){
//                String codeMeal = "";
//                for (int i=0;i<list_code.size();i++){
//                    if(i==0){
//                        Type0=list_code.get(0).getCode();
//                        Coin0=Integer.valueOf(list_code.get(0).getUnit());
//                    }else if(i==1){
//                        Type1=list_code.get(1).getCode();
//                        Coin1=Integer.valueOf(list_code.get(1).getUnit());
//                    }else if(i==2){
//                        Type2=list_code.get(2).getCode();
//                        Coin2=Integer.valueOf(list_code.get(2).getUnit());
//                    }else if(i==3){
//                        Type3=list_code.get(3).getCode();
//                        Coin3=Integer.valueOf(list_code.get(3).getUnit());
//                    }else if(i==4){
//                        Type4=list_code.get(4).getCode();
//                        Coin4=Integer.valueOf(list_code.get(4).getUnit());
//                    }
//                    codeMeal+=list_code.get(i).getUnit()+"  ";
//                }
////                tvCodeMeal.setText(getString(R.string.ICT_codeMeal)+codeMeal+" "+unit);
////                tvCodeMeal.setVisibility(View.VISIBLE);
//            }else{
////                tvCodeMeal.setVisibility(View.VISIBLE);
//            }
//            Logger.d(TAG,"needmoney:"+mSelectedMeal.getPrice());
//            needmoney = Math.round(mSelectedMeal.getPrice());
//            if(kBox!=null&&!TextUtils.isEmpty(kBox.getCoin_unit())) {
//                unit=kBox.getCoin_unit();
//                mTBNumber.setText(needmoney + "/0 " + kBox.getCoin_unit());
//            }else{
//                mTBNumber.setText(mNeedCoin + "/0 " + getResources().getString(R.string.coin));
//            }
//        } else {
//            mNeedCoin = getBiCount();
//            Logger.d(getClass().getSimpleName(), "initView mNeedCoin:" + mNeedCoin +
//                    "  Cion_exchange_rate:" + KBoxInfo.getInstance().getKBox().getCoin_exchange_rate());
//            mTBNumber.setText(mNeedCoin + "/0 " + getResources().getString(R.string.coin));
//        }

        TBManager.getInstance().start(getContext(), new TBManager.TBManagerListener() {
            @Override
            public void onNewBi() {
                Common.TBcount++;
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
        float needMoney = mSelectedMeal.getIntPrice();
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
        SerialController.getInstance(getSupportedContext()).sendbyteICT(closebyte);
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }


    @SuppressLint("StringFormatMatches")
    private void showConfirmDialog() {
        if (Common.isICT) {
            if (totoal > 0) {//已投币提示损失
                mConfirmDlg = DialogFactory.showCancelCoinDialog(getContext(),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                mQueryOrderHelper.cancelOrder(mSelectedMeal);
                                Common.lastMoney=0;
                                Common.TBcount=0;
                            }
                        }, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        }, getString(R.string.text_cancel_order_coin2) + String.valueOf(df.format(totoal /100f)) + unit);
            } else {
                mQueryOrderHelper.cancelOrder(mSelectedMeal);
            }
        } else {
            if (Common.TBcount > 0) {//已投币提示损失
                mConfirmDlg = DialogFactory.showCancelCoinDialog(getContext(),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                mQueryOrderHelper.cancelOrder(mSelectedMeal);
                                Common.lastMoney=0;
                                Common.TBcount=0;
                            }
                        }, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        }, getString(R.string.text_cancel_order_coin, Common.TBcount));
            } else {
                mQueryOrderHelper.cancelOrder(mSelectedMeal);
            }
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

    public void onEventMainThread(ICTEvent event) {
//        Logger.i(TAG, "OnSerialReceive FmTbPay:" + event.data + "");

        String str = (String) event.data;
        Log.i(TAG, str);
        if (TextUtils.isEmpty(str))
            return;
        switch (event.id) {
            case EventBusId.Ict.RECEIVE_CODE:
//                Logger.d(TAG,"diffence:"+diffence);
                diffence = needmoney - totoal;
                code += str;

                if (code.replace(" ", "").toUpperCase().contains(Type4)) {
                    Logger.d(TAG, "paytype4");
                    if (diffence >= Coin4) {
                        SerialController.getInstance(getSupportedContext()).sendbyteICT(acceptbyte);
                        addmoney = Coin4;
                    } else {
                        Logger.d(TAG, "rejectbyte");
                        if (getContext() != null) {
                            ToastUtils.toast(getContext(), getString(R.string.ICT_ERROR));
                        }
                        SerialController.getInstance(getSupportedContext()).sendbyteICT(rejectbyte);
                    }
//                    papermoney += Coin4;
//                    mTBNumber.setText(needmoney + "/ " + papermoney + getResources().getString(R.string.TWD));
                    code = "";
                } else if (code.replace(" ", "").toUpperCase().contains(Type3)) {
                    Logger.d(TAG, "paytype3");
                    if (diffence >= Coin3) {
                        SerialController.getInstance(getSupportedContext()).sendbyteICT(acceptbyte);
                        addmoney = Coin3;
                    } else {
                        if (getContext() != null) {
                            ToastUtils.toast(getContext(), getString(R.string.ICT_ERROR));
                        }
                        SerialController.getInstance(getSupportedContext()).sendbyteICT(rejectbyte);
                    }
                    code = "";
                } else if (code.replace(" ", "").toUpperCase().contains(Type2)) {
                    Logger.d(TAG, "paytype2");
                    if (diffence >= Coin2) {
                        SerialController.getInstance(getSupportedContext()).sendbyteICT(acceptbyte);
                        addmoney = Coin2;
                    } else {
                        if (getContext() != null) {
                            ToastUtils.toast(getContext(), getString(R.string.ICT_ERROR));
                        }
                        SerialController.getInstance(getSupportedContext()).sendbyteICT(rejectbyte);
                    }
                    code = "";
                } else if (code.replace(" ", "").toUpperCase().contains(Type1)) {
                    Logger.d(TAG, "paytype1");
                    if (diffence >= Coin1) {
                        SerialController.getInstance(getSupportedContext()).sendbyteICT(acceptbyte);
                        addmoney = Coin1;
                    } else {
                        if (getContext() != null) {
                            ToastUtils.toast(getContext(), getString(R.string.ICT_ERROR));
                        }
                        SerialController.getInstance(getSupportedContext()).sendbyteICT(rejectbyte);
                    }
                    code = "";
                } else if (code.replace(" ", "").toUpperCase().contains(Type0)) {
                    Logger.d(TAG, "paytype0");
                    if (diffence >= Coin0) {
                        SerialController.getInstance(getSupportedContext()).sendbyteICT(acceptbyte);
                        addmoney = Coin0;
                    } else {
                        if (getContext() != null) {
                            ToastUtils.toast(getContext(), getString(R.string.ICT_ERROR));
                        }
                        SerialController.getInstance(getSupportedContext()).sendbyteICT(rejectbyte);
                    }
                    code = "";
                } else if (code.replace(" ", "").toUpperCase().contains(PaySucced)) {
                    papermoney += addmoney;
                    code = "";
                    if (needmoney == 0)
                        return;
                    totoal=papermoney+tbmoney+Common.lastMoney;
                    mTBNumber.setText(String.valueOf(df.format(needmoney / 100f)) + "/ " + String.valueOf(df.format(totoal / 100f)) + unit);
                    if (totoal >= needmoney) {
                        paySuccess();
                    }
                } else if (code.replace(" ", "").toUpperCase().contains(PayFail)) {
                    code = "";
                } else if (code.replace(" ", "").toUpperCase().contains(FmTBPayNumber.TypeON)) {
                    Logger.d(TAG, "TypeON");
                    SerialController.getInstance(getSupportedContext()).sendbyteICT(acceptbyte);
                    code = "";
                } else if (code.replace(" ", "").toUpperCase().contains(FmTBPayNumber.REJECT)) {
                    rejecttimes++;
                    Logger.d(TAG, "REJECT");
                    if (rejecttimes >= 3) {
                        SerialController.getInstance(getSupportedContext()).sendbyteICT(acceptbyte);
                        rejecttimes = 0;
                    }
                    code = "";
                } else {

                }


                break;
        }
    }
    public void sendBack(){
        new Thread(){
            public void run() {
                try{
                    Instrumentation inst = new Instrumentation();
                    inst.sendKeyDownUpSync(KeyEvent.KEYCODE_SPACE);
                }
                catch (Exception e) {
                }
            }
        }.start();
    }
}
