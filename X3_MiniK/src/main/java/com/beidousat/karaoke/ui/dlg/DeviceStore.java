package com.beidousat.karaoke.ui.dlg;

import android.app.Dialog;
import android.content.Context;
import android.os.Handler;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.beidousat.karaoke.R;
import com.beidousat.libbns.model.ServerConfigData;
import com.beidousat.karaoke.ui.BaseActivity;
import com.beidousat.libbns.model.StoreBaseModel;
import com.beidousat.libbns.net.request.BaseHttpRequest;
import com.beidousat.libbns.net.request.BaseHttpRequestListener;
import com.beidousat.libbns.net.request.RequestMethod;
import com.beidousat.libbns.util.DeviceUtil;
import com.beidousat.libbns.util.Logger;
import com.beidousat.libwidget.image.RecyclerImageView;
import com.google.gson.Gson;

/**
 * Created by J Wong on 2017/5/15.
 */

public class DeviceStore extends BaseDialog {

    private Context mContext;
    private TextView mTvMsg, mTvStatus;
    private ProgressBar mPgbLoading;
    private RecyclerImageView mRivStatus;
    private String mBoxCode;
    private final static String TAG = DeviceStore.class.getSimpleName();

    public DeviceStore(Context context, String boxCode) {
        super(context, R.style.MyDialog);
        mBoxCode = boxCode;
        init();
    }

    public void init() {
        this.setContentView(R.layout.dlg_device_store);
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.width = 450;
        lp.height = 450;
        lp.gravity = Gravity.CENTER;

        getWindow().setAttributes(lp);
        setCanceledOnTouchOutside(false);

        mTvMsg = (TextView) findViewById(R.id.tv_msg);
        mTvStatus = (TextView) findViewById(R.id.tv_status);
        mTvStatus.setGravity(Gravity.CENTER);
        mPgbLoading = (ProgressBar) findViewById(R.id.pgb_progress);
        mRivStatus = (RecyclerImageView) findViewById(R.id.iv_status);

    }

    @Override
    public void show() {
        super.show();
        post();
    }

    private void post() {
        mPgbLoading.setVisibility(View.VISIBLE);
        mTvMsg.setVisibility(View.VISIBLE);
        String devSn = DeviceUtil.getCupChipID();
        String url = ServerConfigData.getInstance().getServerConfig().getStore_web() + RequestMethod.DEVICE_STORE;

        BaseHttpRequest baseHttpRequest = new BaseHttpRequest();
        baseHttpRequest.setBaseHttpRequestListener(new BaseHttpRequestListener() {
            @Override
            public void onRequestCompletion(String url, String body) {
                String responseUrl = body;
                Logger.d(TAG, "onResponse body :" + responseUrl);
                StoreBaseModel baseModel = convert2BaseModel(responseUrl);
                if ("0".equals(baseModel.error)) {
                    String data = "";
                    try {
                        data = baseModel.data.getAsString();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    showSuccess(data);
                } else {
                    showFail(baseModel.message);
                }
            }

            @Override
            public void onRequestFail(String url, String err) {
                showFail(err);
            }
        });
        baseHttpRequest.addParam("dev_sn", devSn).addParam("dev_type", "box").addParam("nonce", mBoxCode);
        baseHttpRequest.postForm(url);
    }


    private void showFail(final String errMsg) {
        mPgbLoading.post(new Runnable() {
            @Override
            public void run() {
                mTvStatus.setText(getContext().getString(R.string.enter_store_fail, errMsg));
                mRivStatus.setImageResource(R.drawable.dlg_fail);
                mPgbLoading.setVisibility(View.GONE);
                mTvMsg.setVisibility(View.GONE);
                mTvStatus.setVisibility(View.VISIBLE);
                mRivStatus.setVisibility(View.VISIBLE);
            }
        });


        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                dismiss();
            }
        }, 3000);
    }

    private void showSuccess(final String message) {
        mPgbLoading.post(new Runnable() {
            @Override
            public void run() {
                mPgbLoading.setVisibility(View.GONE);
                mTvMsg.setVisibility(View.GONE);
                mTvStatus.setText(getContext().getString(R.string.enter_store_success) + "\n" + message);
                mRivStatus.setImageResource(R.drawable.dlg_buy_successful);
                mTvStatus.setVisibility(View.VISIBLE);
                mRivStatus.setVisibility(View.VISIBLE);
            }
        });


        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                dismiss();
            }
        }, 3000);
    }

    private Handler handler = new Handler();


    private StoreBaseModel convert2BaseModel(String response) {
        StoreBaseModel baseModel = null;
        try {
            Gson gson = new Gson();
            baseModel = gson.fromJson(response, StoreBaseModel.class);
        } catch (Exception e) {
            Logger.e(TAG, "convert2BaseModel ex:" + e.toString());
        }
        return baseModel;
    }

}
