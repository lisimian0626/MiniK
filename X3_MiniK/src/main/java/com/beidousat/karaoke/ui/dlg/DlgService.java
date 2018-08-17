package com.beidousat.karaoke.ui.dlg;

import android.app.Activity;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager.LayoutParams;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.beidousat.karaoke.R;
import com.beidousat.karaoke.data.KBoxInfo;
import com.beidousat.libbns.util.QrCodeUtil;


public class DlgService extends BaseDialog implements OnClickListener {

    private final String TAG = DlgService.class.getSimpleName();
    private View.OnClickListener mPositiveListener;
    private ImageView iv_qrcode;

    public DlgService(Activity context) {
        super(context, R.style.MyDialog);
        init();
    }

    void init() {
        this.setContentView(R.layout.dlg_service);
        LayoutParams lp = getWindow().getAttributes();
        lp.width = getContext().getResources().getInteger(R.integer.preview_w);
        lp.height = getContext().getResources().getInteger(R.integer.preview_h);
        lp.gravity = Gravity.CENTER;
        getWindow().setAttributes(lp);
        setCanceledOnTouchOutside(true);
        iv_qrcode=(ImageView) findViewById(R.id.iv_service_qrcode);
        findViewById(R.id.riv_close).setOnClickListener(this);
        if(KBoxInfo.getInstance().getKBox()!=null&& !TextUtils.isEmpty(KBoxInfo.getInstance().getKBox().getService_qrcode_str())) {
            iv_qrcode.setImageBitmap(QrCodeUtil.createQRCode(KBoxInfo.getInstance().getKBox().getService_qrcode_str()));
        }
    }


//    public void setPositiveButton(String text, View.OnClickListener listener) {
//        this.button.setText(text);
//        this.mPositiveListener = listener;
//    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.riv_close:
                dismiss();
                break;
        }
    }
}
