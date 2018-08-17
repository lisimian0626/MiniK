package com.beidousat.karaoke.ui.dlg;

import android.app.Activity;
import android.graphics.Paint;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager.LayoutParams;
import android.widget.Button;
import android.widget.TextView;

import com.beidousat.karaoke.R;


public class DlgPriceChange extends BaseDialog implements OnClickListener {

    private final String TAG = DlgPriceChange.class.getSimpleName();

    private TextView mTvMsg;
    private Button button, button2;
    private View.OnClickListener mPositiveListener, mCancelListener;
    private TextView mTvMsg2;


    public DlgPriceChange(Activity context) {
        super(context, R.style.MyDialog);
        init();
    }

    void init() {
        this.setContentView(R.layout.dlg_price_change);
        LayoutParams lp = getWindow().getAttributes();
        lp.width = 500;
        lp.height = 300;
        lp.gravity = Gravity.CENTER;

        getWindow().setAttributes(lp);
        mTvMsg = (TextView) findViewById(android.R.id.text1);
        mTvMsg2 = (TextView) findViewById(android.R.id.text2);
        button = (Button) findViewById(android.R.id.button1);
        button.setOnClickListener(this);
        mTvMsg2.getPaint().setAntiAlias(true);// 抗锯齿
        mTvMsg2.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
        button2 = (Button) findViewById(android.R.id.button2);
        button2.setOnClickListener(this);

    }

    public void setMessage(CharSequence text) {
        mTvMsg.setText(text);
    }

    public void setLinkMessage(CharSequence text, View.OnClickListener listener) {
        mTvMsg2.setText(text);
        mTvMsg2.setOnClickListener(listener);
    }

    public void setMessage(int resId) {
        mTvMsg.setText(resId);
    }

    public void setPositiveButton(String text, View.OnClickListener listener) {
        this.button.setText(text);
        this.mPositiveListener = listener;
    }

    public void setCancelButton(String text, View.OnClickListener listener) {
        this.button2.setText(text);
        this.mCancelListener = listener;
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case android.R.id.button1:
                if (mPositiveListener != null) {
                    mPositiveListener.onClick(v);
                }
                break;
            case android.R.id.button2:
                if (mCancelListener != null) {
                    mCancelListener.onClick(v);
                }
                break;
        }
    }
}
