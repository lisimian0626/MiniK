package com.beidousat.karaoke.ui.dlg;

import android.app.Activity;
import android.app.Dialog;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager.LayoutParams;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.beidousat.karaoke.R;
import com.beidousat.karaoke.data.KBoxInfo;
import com.beidousat.libbns.util.QrCodeUtil;


public class PromptDialog extends BaseDialog implements OnClickListener {

    private final String TAG = PromptDialog.class.getSimpleName();

    private TextView mTvMsg,tv_close;
    private Button button, button2;
    private View.OnClickListener mPositiveListener;
//    private Activity outerActivity;

    public PromptDialog(Activity context) {
        super(context, R.style.MyDialog);
//        outerActivity = (Activity) context;
        init();
    }

    //    @Override
//    public boolean onTouchEvent(MotionEvent event) {
//        return outerActivity.dispatchTouchEvent(event);
//    }
    void init() {
        this.setContentView(R.layout.prompt_dialog);
        LayoutParams lp = getWindow().getAttributes();
        lp.width = 500;
        lp.height = 300;
        lp.gravity = Gravity.CENTER;

        getWindow().setAttributes(lp);
        mTvMsg = (TextView) findViewById(android.R.id.text1);
        tv_close=(TextView)findViewById(R.id.close);
        tv_close.setOnClickListener(this);
        button = (Button) findViewById(android.R.id.button1);
        button.setOnClickListener(this);

        button2 = (Button) findViewById(android.R.id.button2);
        button2.setOnClickListener(this);

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

    public void setShowButton2(boolean show) {
        button2.setVisibility(show ? View.VISIBLE : View.GONE);
    }
    public void setClose(boolean show) {
        tv_close.setVisibility(show ? View.VISIBLE : View.GONE);
    }
    public void setNotClose() {
        setCanceledOnTouchOutside(false);
        button.setVisibility(View.INVISIBLE);
        button.setEnabled(false);
        button.setClickable(false);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case android.R.id.button1:
                dismiss();
                if (mPositiveListener != null) {
                    mPositiveListener.onClick(v);
                }
                break;
            case android.R.id.button2:
                dismiss();
                break;
            case R.id.close:
                dismiss();
                break;
        }
    }
}
