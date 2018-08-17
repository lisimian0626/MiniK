package com.beidousat.karaoke.ui.dlg;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.beidousat.karaoke.R;
import com.beidousat.karaoke.biz.QueryOrderHelper;
import com.beidousat.karaoke.biz.SupportQueryOrder;
import com.beidousat.karaoke.model.Meal;
import com.beidousat.libbns.net.request.RequestMethod;
import com.beidousat.libbns.util.QrCodeUtil;

import java.util.Timer;
import java.util.TimerTask;

import static com.beidousat.karaoke.R.string.ad;

/**
 * author: Hanson
 * date:   2017/3/30
 * dscribe:
 */

public class FmPayQrCode extends FmBaseDialog implements SupportQueryOrder {
    private TextView mBtnBack;
    private TextView mTvMoney;
    private TextView mTvMeal;
    private ImageView mIvQrCode;
    private AlertDialog mConfirmDlg;
    private Meal mSelectedMeal;
    private QueryOrderHelper mQueryOrderHelper;

    private Timer mQueryTimer = new Timer();

    private final static int HTTP_REQUEST_MSG = 1;
    private final static int CLOSE_DIALOG = 2;

    private Handler mRequestHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case HTTP_REQUEST_MSG:
                    mQueryOrderHelper.queryOrder().doPost(0);
                    break;
                case CLOSE_DIALOG:
                    mConfirmDlg.dismiss();
                    mAttached.dismiss();
                    break;
                default:
                    break;
            }
            return true;
        }
    });

    private TimerTask mQueryTask = new TimerTask() {
        @Override
        public void run() {
            mRequestHandler.sendEmptyMessage(HTTP_REQUEST_MSG);
        }
    };

    public final static String MEAL_TAG = "SelectedMeal";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.fm_pay_qrcode, container, false);
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    void initData() {
        mSelectedMeal = (Meal) getArguments().getSerializable(MEAL_TAG);
        mQueryOrderHelper = new QueryOrderHelper(this, mSelectedMeal);
    }

    @Override
    void initView() {
        mBtnBack = findViewById(R.id.btn_back);
        mTvMoney = findViewById(R.id.tv_money);
        mTvMeal = findViewById(R.id.tv_selected_meal);
        mIvQrCode = findViewById(R.id.iv_pay_qrcode);

        mTvMeal.setText(getResources().getString(R.string.text_selected_pay_meal,
                mSelectedMeal.getAmount(), mSelectedMeal.getUnit()));
        mTvMoney.setText(String.format("%.2f", mSelectedMeal.getPrice()));
        mIvQrCode.setImageBitmap(QrCodeUtil.createQRCode(mSelectedMeal.getUrl()));
//        Glide.with(getContext()).load(mSelectedMeal.getUrl()).centerCrop().into(mIvQrCode);
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

    private void showConfirmDialog() {
        mConfirmDlg = new AlertDialog(getContext());
        mConfirmDlg.setTitle(R.string.text_cancel_order_title);
        mConfirmDlg.setMessage(getResources().getString(R.string.text_cancel_order_msg));
        mConfirmDlg.setPositiveButton(R.string.text_yes, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mQueryOrderHelper.cancelOrder().doPost(0);
            }
        });
        mConfirmDlg.setNegativeButton(R.string.text_no, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mConfirmDlg.dismiss();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();

        mQueryTimer.schedule(mQueryTask, 0, 6000); //请求超时为5s
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

        mQueryTimer.cancel();
        mQueryTask.cancel();
        //反注册对话框关闭事件
        mAttached.unregisterCloseListener(this);
        mRequestHandler.removeMessages(HTTP_REQUEST_MSG);
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
            case RequestMethod.CANCEL_ORDER:
                if (isSucced) {
                    mRequestHandler.sendEmptyMessage(CLOSE_DIALOG);
                }
                break;
        }
    }
}
