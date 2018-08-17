package com.beidousat.karaoke.ui.dlg;

import android.app.Activity;
import android.app.Dialog;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager.LayoutParams;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.beidousat.karaoke.R;

import org.w3c.dom.Text;


public class DlgInitLoading extends BaseDialog implements OnClickListener {

    private final String TAG = DlgInitLoading.class.getSimpleName();
    private ProgressBar mProgress;
    private TextView mTvMsg,mTvError;
    private Button button;
    private View.OnClickListener mPositiveListener;
    private String error;
    public DlgInitLoading(Activity context) {
        super(context, R.style.MyDialog);
        init();
    }
    public void setError(String text) {
        error=text;
    }
    void init() {
        this.setContentView(R.layout.dlg_init_loading);
        LayoutParams lp = getWindow().getAttributes();
        lp.width = 500;
        lp.height = 300;
        lp.gravity = Gravity.CENTER;
        getWindow().setAttributes(lp);
        setCanceledOnTouchOutside(false);
        mProgress = (ProgressBar) findViewById(R.id.pgb_progress);
        mTvMsg = (TextView) findViewById(R.id.tv_text);
        mTvError=(TextView)findViewById(R.id.tv_error);
        button = (Button) findViewById(android.R.id.button1);
        button.setOnClickListener(this);
        button.setVisibility(View.GONE);
        button.postDelayed(new Runnable() {//20s没有初始化成功提示设置网络
            @Override
            public void run() {
                button.setVisibility(View.VISIBLE);
                mTvError.setText(error);
                mTvError.setVisibility(View.VISIBLE);
                mTvMsg.setVisibility(View.GONE);
                mProgress.setVisibility(View.GONE);

            }
        }, 10 * 1000);
    }

    public void setMessage(CharSequence text) {
        mTvMsg.setText(text);
    }

    public void setMessage(int resId) {
        mTvMsg.setText(resId);
    }

    public void setPositiveButton(String text, View.OnClickListener listener) {
        this.button.setText(text);
        this.mPositiveListener = listener;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case android.R.id.button1:
                if (mPositiveListener != null) {
                    mPositiveListener.onClick(v);
                }
                break;
        }
    }
}
