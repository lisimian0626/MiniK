package com.beidousat.minidoor;

import android.app.Activity;
import android.app.Dialog;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager.LayoutParams;
import android.widget.EditText;

import com.beidousat.libbns.util.PreferenceUtil;


public class DlgBoxIdInput extends Dialog implements View.OnClickListener {

    private final String TAG = DlgBoxIdInput.class.getSimpleName();
    private EditText editTextEx;

    public DlgBoxIdInput(Activity context) {
        super(context, R.style.MyDialog);
        init();
    }

    void init() {
        this.setContentView(R.layout.dlg_boxid_input);
        LayoutParams lp = getWindow().getAttributes();
        lp.width = 500;
        lp.height = 300;
        lp.gravity = Gravity.CENTER;
        getWindow().setAttributes(lp);
        setCanceledOnTouchOutside(false);

        editTextEx = (EditText) findViewById(android.R.id.edit);
        findViewById(android.R.id.button1).setOnClickListener(this);
        findViewById(android.R.id.button2).setOnClickListener(this);

        editTextEx.setText(PreferenceUtil.getString(getContext().getApplicationContext(), Main.PREF_KEY_BOXID));

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case android.R.id.button1:
                dismiss();
                break;
            case android.R.id.button2:
                PreferenceUtil.setString(getContext().getApplicationContext(), Main.PREF_KEY_BOXID, editTextEx.getText().toString());
                dismiss();
                break;
        }
    }
}
