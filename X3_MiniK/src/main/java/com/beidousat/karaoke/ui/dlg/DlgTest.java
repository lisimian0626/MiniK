package com.beidousat.karaoke.ui.dlg;

import android.app.Activity;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager.LayoutParams;
import android.widget.Button;

import com.beidousat.karaoke.R;
import com.beidousat.karaoke.util.SerialController;
import com.beidousat.libbns.model.Common;


public class DlgTest extends BaseDialog implements OnClickListener {
    private Button open,send,close;
    private final String TAG = DlgTest.class.getSimpleName();
    public DlgTest(Activity context) {
        super(context, R.style.MyDialog);
        init();
    }

    void init() {
        this.setContentView(R.layout.dlg_test);
        LayoutParams lp = getWindow().getAttributes();
        lp.width = getContext().getResources().getInteger(R.integer.preview_w);
        lp.height = getContext().getResources().getInteger(R.integer.preview_h);
        lp.gravity = Gravity.CENTER;
        getWindow().setAttributes(lp);
        setCanceledOnTouchOutside(true);
        open=(Button) findViewById(R.id.btn_test_open);
        send=(Button)findViewById(R.id.btn_test_send);
        close=(Button)findViewById(R.id.btn_test_close);
        open.setOnClickListener(this);
        send.setOnClickListener(this);
        close.setOnClickListener(this);
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
            case R.id.btn_test_open:
//                SerialController controller=SerialController.getInstance(getContext());
                try {
                    SerialController.getInstance(getContext()).openInfrared(Common.mInfraredPort,Common.mInfraredBaudRate);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                break;
            case R.id.btn_test_send:
                try {
                    SerialController.getInstance(getContext()).send("hello");
                }catch (Exception e){
                    e.printStackTrace();
                }

                break;
            case R.id.btn_test_close:
                try {
                    SerialController.getInstance(getContext()).close();
                }catch (Exception e){
                    e.printStackTrace();
                }

                break;
        }
    }
}
