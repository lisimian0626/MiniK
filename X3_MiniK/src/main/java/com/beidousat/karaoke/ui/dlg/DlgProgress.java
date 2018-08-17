package com.beidousat.karaoke.ui.dlg;

import android.app.Activity;
import android.app.Dialog;
import android.view.Gravity;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.beidousat.karaoke.R;

/**
 * Created by J Wong on 2016/12/9.
 */

public class DlgProgress extends BaseDialog {

    private TextView tvTitle;
    private TextView tvTip;
    private TextView tvProgress;
    private ProgressBar mProgressBar;

    public DlgProgress(Activity context) {
        super(context, R.style.MyDialog);
        init();
    }

    public void setTitle(String title) {
        tvTitle.setText(title);
    }

    public void setTip(String tip) {
        tvTip.setText(tip);
    }

    public void setProgress(long progress, long total) {
        tvProgress.setText(progress + "/" + total);
        mProgressBar.setProgress((int) ((100 * progress) / total));
    }

    void init() {
        this.setContentView(R.layout.dlg_progress);
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.width = 500;
        lp.height = 300;
        lp.gravity = Gravity.CENTER;

        getWindow().setAttributes(lp);
        tvTitle = (TextView) findViewById(R.id.tv_title);
        tvTip = (TextView) findViewById(R.id.tv_tip);
        tvProgress = (TextView) findViewById(R.id.tv_progress);
        mProgressBar = (ProgressBar) findViewById(R.id.pgb_progress);
        setCanceledOnTouchOutside(false);
    }

}