package com.beidousat.karaoke.ui.dlg;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.InputFilter;
import android.text.Selection;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager.LayoutParams;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.beidousat.karaoke.R;
import com.beidousat.karaoke.data.BoughtMeal;
import com.beidousat.karaoke.data.PrefData;
import com.beidousat.libbns.model.ServerConfigData;
import com.beidousat.karaoke.model.CouponDetail;
import com.beidousat.karaoke.model.GiftDetail;
import com.beidousat.karaoke.util.DateUtil;
import com.beidousat.libbns.evenbus.EventBusUtil;
import com.beidousat.libbns.net.request.RequestMethod;
import com.beidousat.libbns.net.request.StoreHttpRequest;
import com.beidousat.libbns.net.request.StoreHttpRequestListener;
import com.beidousat.libbns.util.DeviceUtil;
import com.beidousat.libbns.util.HttpParamsUtils;
import com.beidousat.libbns.util.ListUtil;
import com.beidousat.libbns.util.Logger;
import com.beidousat.libwidget.image.RecyclerImageView;
import com.beidousat.libwidget.recycler.HorizontalDividerItemDecoration;
import com.beidousat.libwidget.recycler.VerticalDividerItemDecoration;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class DlgCoupon extends BaseDialog implements View.OnClickListener, StoreHttpRequestListener {

    private final String TAG = DlgCoupon.class.getSimpleName();
    private View mViewInput, mViewDetail, mViewProgress;
    private TextView tvDlgTitle;
    private EditText mEtInput;
    private Button button;
    private TextView tvRmb, tvValue, tvUnit, tvCouponName, tvLimit, tvNotice, tvTitle, tvDetail, tvTime;
    private RecyclerView mRvKeyboard;
    private TextView mTvErrTip, mTvProgress;
    private ProgressBar mProgressBar;
    private RecyclerImageView mRivStatus;
    //优惠券类型 1.折扣卷 ,代金卷 使用后转到支付界面支付时候抵扣;2.礼品卷 使用后直接生效
    private int ticket_type=-1;
    private CouponDetail couponDetail;
    public DlgCoupon(Context context) {
        super(context, R.style.MyDialog);
        init();
    }



    void init() {

        this.setContentView(R.layout.dlg_coupon);
        LayoutParams lp = getWindow().getAttributes();
        lp.width = getContext().getResources().getInteger(R.integer.preview_w);
        lp.height = getContext().getResources().getInteger(R.integer.preview_h);
        lp.gravity = Gravity.CENTER;
        getWindow().setAttributes(lp);
        tvDlgTitle = (TextView) findViewById(android.R.id.title);
        mViewInput = findViewById(android.R.id.inputArea);
        mViewDetail = findViewById(android.R.id.extractArea);
        mEtInput = (EditText) findViewById(android.R.id.input);
        button = (Button) findViewById(android.R.id.button1);
        button.setOnClickListener(this);
        findViewById(R.id.riv_close).setOnClickListener(this);
        mViewDetail.setOnClickListener(this);
        mRvKeyboard = (RecyclerView) findViewById(android.R.id.keyboardView);
        tvRmb = (TextView) findViewById(R.id.tv_rmb);
        tvValue = (TextView) findViewById(R.id.tv_value);
        tvUnit = (TextView) findViewById(R.id.tv_unit);
        tvCouponName = (TextView) findViewById(R.id.tv_coupon_name);
        tvLimit = (TextView) findViewById(R.id.tv_limit);
        tvNotice = (TextView) findViewById(R.id.tv_notice);
        tvTitle = (TextView) findViewById(R.id.tv_title);
        tvDetail = (TextView) findViewById(R.id.tv_detail);
        tvTime = (TextView) findViewById(R.id.tv_time);
        mTvErrTip = (TextView) findViewById(R.id.tv_err_tip);
        mViewProgress = findViewById(android.R.id.progress);
        mTvProgress = (TextView) findViewById(android.R.id.text1);
        mProgressBar = (ProgressBar) findViewById(R.id.progressBar);
        mRivStatus = (RecyclerImageView) findViewById(R.id.riv_status);

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
                mTvErrTip.setText("");
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
    //显示卡卷信息
    private void setCouponDetail(CouponDetail detail) {
        if (detail != null) {
            mViewProgress.setVisibility(View.GONE);
            mViewInput.setVisibility(View.GONE);
            tvDlgTitle.setVisibility(View.VISIBLE);
            mViewDetail.setVisibility(View.VISIBLE);

            tvTitle.setText(detail.title);
            tvNotice.setText(detail.notice);
            tvDetail.setText(detail.description);

            button.setText("马上使用");
            if (!TextUtils.isEmpty(detail.use_date_str))
                tvTime.setText("有效时间:"+detail.use_date_str);

            if (!TextUtils.isEmpty(detail.limit)){
                if(detail.limit.equalsIgnoreCase("0")){
                    tvLimit.setText("无限制");
                }else {
                    tvLimit.setText("最低消费"+BigDecimal.valueOf(Long.valueOf(detail.limit)).divide(new BigDecimal(100)).toString()+"元");
                }
            }


            if ("CASH".equalsIgnoreCase(detail.card_type)) {//代金券
                tvCouponName.setText("代金券");
                tvDetail.setText(detail.description);
                if(!TextUtils.isEmpty(detail.show_product)) {
                    tvValue.setText(BigDecimal.valueOf(Long.valueOf(detail.show_product)).divide(new BigDecimal(100)).toString());
                }
                tvRmb.setText("¥");
                tvUnit.setText("元");
                ticket_type=1;
            } else if ("DISCOUNT".equalsIgnoreCase(detail.card_type)) {//折扣券
                tvCouponName.setText("折扣券");
                tvValue.setText(detail.show_product);
                tvRmb.setText("");
                tvUnit.setText("折");
                ticket_type=1;
            } else if ("GIFT".equalsIgnoreCase(detail.card_type)) {//礼品券
                tvRmb.setText("");
                tvCouponName.setText("兑换券");
                tvValue.setText(detail.show_product);
                ticket_type=2;
                if ("music".equalsIgnoreCase(detail.pre_product)) {//歌曲
                    tvUnit.setText("首");
                } else if ("time".equalsIgnoreCase(detail.pre_product)) {//分钟
                    tvUnit.setText("分钟");
                }
            }else if("GENERAL_CARD".equalsIgnoreCase(detail.card_type)){ //礼品卷
                tvCouponName.setText("礼品卡");
                tvDetail.setText(detail.description);
                tvValue.setText(BigDecimal.valueOf(Long.valueOf(detail.show_product)).divide(new BigDecimal(100)).toString());
                tvRmb.setText("¥");
                tvUnit.setText("元");
            }

        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.riv_close:
                dismiss();
                break;
            case android.R.id.extractArea:
//                if (mViewInput.isShown()) {//查看详情
//                    if (!TextUtils.isEmpty(mEtInput.getText())&&mEtInput.getText().length()==19) {
//                        requestDetail(mEtInput.getText().toString());
//                    } else {
//                        mTvErrTip.setText("请输入16位有效卡券号码进行兑换");
//                    }
//                } else
                    if (mViewDetail.isShown()) {
//                    mViewInput.setVisibility(View.VISIBLE);
//                    tvDlgTitle.setVisibility(View.GONE);
//                    mViewDetail.setVisibility(View.GONE);
                    if(couponDetail!=null){
                        if(!TextUtils.isEmpty(couponDetail.use_date_str)){
                            boolean isStart=false;
                            try {
                                String[] data=couponDetail.use_date_str.split("~");
                                isStart=DateUtil.DateCompare(data[0],data[1]);
                            }catch (Exception e){
                                e.printStackTrace();
                                isStart=false;
                            }
                            if(isStart){
                                if(couponDetail.card_type.toUpperCase().equals("GIFT")){
                                    int play_type=-1;
                                    if(couponDetail.pre_product.equalsIgnoreCase("music")){
                                        play_type=1;
                                    }else if(couponDetail.pre_product.equalsIgnoreCase("time")){
                                        play_type=2;
                                    }
                                    if(BoughtMeal.getInstance().getTheFirstMeal()==null||play_type== BoughtMeal.getInstance().getTheFirstMeal().getType()){
                                        EventBusUtil.postGiftDetail(couponDetail);
                                        requestGift(mEtInput.getText().toString().replace(" ",""),1);
                                    }else{

                                        EventBusUtil.postGiftFail("卡卷与当前套餐不一致，不能使用");
                                    }
                                }else{
                                    couponDetail.setCard_code(mEtInput.getText().toString().replace(" ",""));
                                    EventBusUtil.postGiftDetail(couponDetail);
                                }
                            }else{
                                EventBusUtil.postGiftFail("卡券过期或未到生效日期");
                            }
                        }


                        dismiss();
                    }


                }
                break;
        }
    }

    @Override
    public void dismiss() {
        super.dismiss();
    }

    private void requestDetail(String cardCode) {
        StoreHttpRequest request = new StoreHttpRequest(ServerConfigData.getInstance().getServerConfig().getStore_web(), RequestMethod.CARD_DETAIL);
        request.setStoreHttpRequestListener(this);
        request.addParam("card_code", cardCode);
        request.addParam("static_code", String.valueOf(1));
        request.setConvert2Class(CouponDetail.class);
        request.post();
    }

    private void requestGift(String inputCode,int static_code) {
        StoreHttpRequest request = new StoreHttpRequest(ServerConfigData.getInstance().getServerConfig().getStore_web(), RequestMethod.GIFT_CREATE);
        request.setStoreHttpRequestListener(this);
        request.addParam(HttpParamsUtils.initGiftParams(DeviceUtil.getCupChipID(), PrefData.getRoomCode(getContext()),
                inputCode,1));
        request.setConvert2Class(GiftDetail.class);
        request.post();
    }
//    private void requestGift_Order(int pay_type,int pay_count,String inputCode,int static_code) {
//        StoreHttpRequest request = new StoreHttpRequest(ServerConfigData.getInstance().getServerConfig().getStore_web(), RequestMethod.ORDER_CREATE);
//        request.setStoreHttpRequestListener(this);
//        request.addParam(HttpParamsUtils.initCreateOrderParams(pay_type,pay_count,null,DeviceUtil.getCupChipID(), PrefData.getRoomCode(getContext()),
//                inputCode,1));
//        request.setConvert2Class(GiftDetail.class);
//        request.post();
//    }
    @Override
    public void onStoreSuccess(String method, Object object) {
        mProgressBar.setVisibility(View.INVISIBLE);
        mRivStatus.setVisibility(View.VISIBLE);
        if (RequestMethod.CARD_DETAIL.equalsIgnoreCase(method)) {
            if (object != null && object instanceof CouponDetail) {
                mTvProgress.setText("读取卡券成功！");
                mRivStatus.setImageResource(R.drawable.dlg_buy_successful);
                couponDetail = (CouponDetail) object;
                setCouponDetail(couponDetail);
            }
        } else if (RequestMethod.GIFT_CREATE.equalsIgnoreCase(method)) {
            if (object != null && object instanceof GiftDetail) {
                mTvProgress.setText("使用优惠卷成功！");
                mRivStatus.setImageResource(R.drawable.dlg_buy_successful);
                GiftDetail giftDetail = (GiftDetail) object;
                EventBusUtil.postGiftSuccessed(giftDetail);
//                gift_successed(giftDetail);
            }
        }
    }

    @Override
    public void onStoreFailed(String method, String error) {
        mProgressBar.setVisibility(View.INVISIBLE);
        mRivStatus.setVisibility(View.VISIBLE);
        if (RequestMethod.CARD_DETAIL.equalsIgnoreCase(method)) {
            mTvProgress.setText(error);
            mRivStatus.setImageResource(R.drawable.dlg_fail);
            mViewProgress.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mViewProgress.setVisibility(View.GONE);
                }
            }, 2000);
        }else if (RequestMethod.GIFT_CREATE.equalsIgnoreCase(method)) {
              EventBusUtil.postGiftFail(error);
//            mTvProgress.setText("使用优惠卷失败！" + error);
//            mRivStatus.setImageResource(R.drawable.dlg_fail);
//            mViewProgress.postDelayed(new Runnable() {
//                @Override
//                public void run() {
//                    mViewProgress.setVisibility(View.GONE);
//                }
//            }, 2000);
        }
    }

    @Override
    public void onStoreStart(String method) {
        mProgressBar.setVisibility(View.VISIBLE);
        mViewProgress.setVisibility(View.VISIBLE);
        mRivStatus.setVisibility(View.GONE);
        if (RequestMethod.CARD_DETAIL.equalsIgnoreCase(method)) {
            mTvProgress.setText("正在读取卡券信息......");
        }else if (RequestMethod.GIFT_CREATE.equalsIgnoreCase(method)) {
            mTvProgress.setText("正在读取优惠卷信息......");
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
                                if (!TextUtils.isEmpty(mEtInput.getText())&&mEtInput.getText().length()==19) {
                                    requestDetail(mEtInput.getText().toString());
                                } else {
                                    mTvErrTip.setText("请输入16位有效卡券号码进行兑换");
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


}
