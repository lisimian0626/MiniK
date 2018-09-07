package com.beidousat.karaoke.ui.dlg;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.InputFilter;
import android.text.Selection;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.beidousat.karaoke.R;
import com.beidousat.karaoke.biz.QueryOrderHelper;
import com.beidousat.karaoke.biz.SupportQueryOrder;
import com.beidousat.karaoke.data.BoughtMeal;
import com.beidousat.libbns.model.Common;
import com.beidousat.karaoke.model.Meal;
import com.beidousat.karaoke.model.PayResult;
import com.beidousat.karaoke.ui.Main;
import com.beidousat.karaoke.util.ToastUtils;
import com.beidousat.libbns.model.ServerConfigData;
import com.beidousat.libbns.net.request.RequestMethod;
import com.beidousat.libbns.net.request.StoreHttpRequest;
import com.beidousat.libbns.net.request.StoreHttpRequestListener;
import com.beidousat.libbns.util.ListUtil;
import com.beidousat.libbns.util.Logger;
import com.beidousat.libbns.util.QrCodeUtil;
import com.beidousat.libwidget.recycler.HorizontalDividerItemDecoration;
import com.beidousat.libwidget.recycler.VerticalDividerItemDecoration;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * author: Hanson
 * date:   2017/3/30
 * dscribe:
 */

public class FmPayCard extends FmBaseDialog implements View.OnClickListener,SupportQueryOrder{
//    private TextView mTvMoney;
    private TextView mTvMeal;
    private RecyclerView mRvKeyboard;
    private EditText mEtInput;
//    private TextView mTvPrompt;
    private ImageView mIvback;
    private AlertDialog mConfirmDlg;
    private Meal mSelectedMeal;
    private RelativeLayout rel_paycard_input;
//    private LinearLayout lin_paycard_succed;
    private QueryOrderHelper mQueryOrderHelper;
//
    private Timer mQueryTimer = new Timer();
//
    private final static int HTTP_REQUEST_MSG = 1;
    private final static int CLOSE_DIALOG = 2;

    private Handler mRequestHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case HTTP_REQUEST_MSG:
                    mQueryOrderHelper.queryOrder(mSelectedMeal).post();
                    break;
                case CLOSE_DIALOG:
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

    private TimerTask mQueryTask = new TimerTask() {
        @Override
        public void run() {
            mRequestHandler.sendEmptyMessage(HTTP_REQUEST_MSG);
        }
    };

    public final static String MEAL_TAG = "SelectedMeal";
    public final static String TYPE_TAG = "type";
//
    private String mType;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.fm_pay_card, container, false);
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    void initData() {
        mSelectedMeal = (Meal) getArguments().getSerializable(MEAL_TAG);
        mType = getArguments().getString(TYPE_TAG);
        mQueryOrderHelper = new QueryOrderHelper(getActivity(),this);
    }

    @Override
    void initView() {
        mIvback=findViewById(R.id.paycard_iv_back);
        mIvback.setOnClickListener(this);
        mTvMeal = findViewById(R.id.tv_selected_meal);
        mTvMeal.setText(getResources().getString(R.string.text_selected_pay_meal,
                mSelectedMeal.getAmount(), mSelectedMeal.getUnit()));
        rel_paycard_input=findViewById(R.id.paycard_input);
//        lin_paycard_succed=findViewById(R.id.lin_paycard_succed);
        initEdittext();
        initKeyboard();

    }

    private void initEdittext() {
        mEtInput = (EditText) findViewById(android.R.id.input);
        mEtInput.setFilters(new InputFilter[]{new InputFilter.LengthFilter(19)});
        mEtInput.addTextChangedListener(new TextWatcher() {
            int beforeTextLength = 0;
            int onTextLength = 0;
            boolean isChanged = false;
            int location = 0;// 记录光标的位置
            private char[] tempChar;
            private StringBuffer buffer = new StringBuffer();
            int konggeNumberB = 0;
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                beforeTextLength = charSequence.length();
                if (buffer.length() > 0) {
                    buffer.delete(0, buffer.length());
                }
                konggeNumberB = 0;
                for (int ii = 0; ii < charSequence.length(); ii++) {
                    if (charSequence.charAt(ii) == ' ') {
                        konggeNumberB++;
                    }
                }
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                onTextLength = charSequence.length();
                buffer.append(charSequence.toString());
                if (onTextLength == beforeTextLength || onTextLength <= 3 || isChanged) {
                    isChanged = false;
                    return;
                }
                isChanged = true;
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (isChanged) {
                    location = mEtInput.getSelectionEnd();
                    int index = 0;
                    while (index < buffer.length()) {
                        if (buffer.charAt(index) == ' ') {
                            buffer.deleteCharAt(index);
                        } else {
                            index++;
                        }
                    }

                    index = 0;
                    int konggeNumberC = 0;
                    while (index < buffer.length()) {
                        // if (index % 5 == 4) {
                        //      buffer.insert(index, ' ');
                        //      konggeNumberC++;
                        // }
                        if (index == 4 || index == 9 || index == 14 || index == 19) {
                            buffer.insert(index, ' ');
                            konggeNumberC++;
                        }
                        index++;
                    }

                    if (konggeNumberC > konggeNumberB) {
                        location += (konggeNumberC - konggeNumberB);
                    }

                    tempChar = new char[buffer.length()];
                    buffer.getChars(0, buffer.length(), tempChar, 0);
                    String str = buffer.toString();
                    if (location > str.length()) {
                        location = str.length();
                    } else if (location < 0) {
                        location = 0;
                    }

                    mEtInput.setText(str);
                    Editable etable = mEtInput.getText();
                    Selection.setSelection(etable, location);
                    isChanged = false;
                }
            }
        });
    }

    private void initKeyboard() {
        mRvKeyboard = (RecyclerView) findViewById(android.R.id.keyboardView);
        HorizontalDividerItemDecoration horDivider = new HorizontalDividerItemDecoration.Builder(getContext())
                .color(Color.TRANSPARENT).size(15).margin(15, 15)
                .build();
        VerticalDividerItemDecoration verDivider = new VerticalDividerItemDecoration.Builder(getContext().getApplicationContext())
                .color(Color.TRANSPARENT).size(15).margin(15, 15)
                .build();

        mRvKeyboard.setLayoutManager(new GridLayoutManager(getContext(), 3));

        mRvKeyboard.addItemDecoration(horDivider);

        mRvKeyboard.addItemDecoration(verDivider);

        AdapterNumber adapter = new AdapterNumber();
        mRvKeyboard.setAdapter(adapter);
        adapter.setData(ListUtil.array2List(getContext().getResources().getStringArray(R.array.keyboard_numbers)));
    }

    @Override
    void setListener() {
//        mBtnBack.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                showConfirmDialog();
//            }
//        });
    }



    private void showConfirmDialog() {
        mConfirmDlg = DialogFactory.showCancelOrderDialog(getContext(),
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
                });
    }

    @Override
    public void onResume() {
        super.onResume();
//
        mQueryTimer.schedule(mQueryTask, 100, Common.Order_query_limit); //请求超时为5s
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
//
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
            case RequestMethod.ORDER_CANCEL:
                if (isSucced) {
                    mRequestHandler.sendEmptyMessage(CLOSE_DIALOG);
                }
          /*      else {
                    if (data != null && TextUtils.isEmpty(data.toString())) {
                        DialogFactory.showErrorDialog(getContext(), data.toString(),
                                new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                mAttached.dismiss();
                            }
                        });
                    }
                }*/
                break;
        }
    }

    @Override
    public void onClick(View v) {
     switch (v.getId()){
         case R.id.paycard_iv_back:
             CommonDialog dialog = CommonDialog.getInstance();
             dialog.onBackPressed();
             break;
     }
    }

    public class AdapterNumber extends RecyclerView.Adapter<AdapterNumber.ViewHolder> {

        private LayoutInflater mInflater;
        private List<String> mData = new ArrayList<String>();

        public AdapterNumber() {
            mInflater = LayoutInflater.from(getContext());
        }

        public void setData(List<String> data) {
            this.mData = data;
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            private TextView tvKey;

            public ViewHolder(View view) {
                super(view);
            }
        }

        @Override
        public int getItemCount() {
            return mData.size();
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View view = mInflater.inflate(R.layout.list_item_coupon_keyboard, viewGroup, false);
            ViewHolder viewHolder = new ViewHolder(view);
            viewHolder.tvKey = (TextView) view.findViewById(android.R.id.button1);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, final int position) {
            final String keyText = mData.get(position);
            holder.tvKey.setText(keyText);
            holder.tvKey.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        if (!TextUtils.isEmpty(keyText)) {
                            if (keyText.equals(getContext().getString(R.string.delete))) {
                                String text = mEtInput.getText().toString();
                                if (!TextUtils.isEmpty(text)) {
                                    String txt = text.substring(0, text.length() - 1);
                                    mEtInput.setText(txt);
                                }
                            } else if(keyText.equals("确定")){
                                if(mSelectedMeal==null){
                                    return;
                                }else if(mEtInput.length()!=19){
                                    if(getContext()!=null){
                                        ToastUtils.toast(getContext(),getString(R.string.input_limit));
                                        return;
                                    }
                                }else{
                                    payCard(mSelectedMeal.getOrderSn(),mEtInput.getText().toString().trim().replace(" ",""));
                                }
                            }else {
                                mEtInput.setText(mEtInput.getText() + keyText);
                            }
                        }
                    } catch (Exception e) {
                        Logger.e(getClass().getSimpleName(), e.toString());
                    }
                }
            });
        }
    }

    private void payCard(String orderSn,String cardCode) {
        StoreHttpRequest request = new StoreHttpRequest(ServerConfigData.getInstance().getServerConfig().getStore_web(), RequestMethod.PAY_CARD);
        request.setStoreHttpRequestListener(this);
        request.addParam("order_sn", orderSn);
        request.addParam("card_code", cardCode);
        request.setConvert2Class(PayResult.class);
        request.post();
    }
    @Override
    public void onStoreStart(String method) {
        if(getActivity()==null)
            return;
        LoadingUtil.showLoadingDialog(getActivity());
        super.onStoreStart(method);
    }

    @Override
    public void onStoreSuccess(String url, Object object) {
        LoadingUtil.closeLoadingDialog();
        PayResult payResult= (PayResult) object;
        if(payResult.getIs_pay()==0){
            CommonDialog dialog = CommonDialog.getInstance();
            dialog.onBackPressed();
        }

////        Log.e("test","obj:"+object.toString());
//        rel_paycard_input.setVisibility(View.GONE);
//        lin_paycard_succed.setVisibility(View.VISIBLE);
        super.onStoreSuccess(url, object);
    }

    @Override
    public void onStoreFailed(String url, String error) {
        LoadingUtil.closeLoadingDialog();
//        Log.e("test","error:"+error);
        if(getContext()!=null){
            ToastUtils.toast(getContext(),error);
        }
        super.onStoreFailed(url, error);
    }
}
