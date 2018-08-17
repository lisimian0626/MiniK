package com.beidousat.karaoke.ui.dlg;

import android.app.Activity;
import android.app.Dialog;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager.LayoutParams;
import android.widget.Button;
import android.widget.TextView;

import com.beidousat.karaoke.R;


public class PromptDialog extends Dialog implements OnClickListener {

    private final String TAG = PromptDialog.class.getSimpleName();

    private TextView mTvMsg;
    private Button button;
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
        button = (Button) findViewById(android.R.id.button1);
        button.setOnClickListener(this);
    }

    public void setMessage(CharSequence text) {
        mTvMsg.setText(text);
    }

    public void setMessage(int resId){
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
                dismiss();
                break;
        }
    }
}
