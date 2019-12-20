package com.beidousat.karaoke.ui.dlg;

import android.content.Context;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.beidousat.karaoke.R;
import com.beidousat.karaoke.data.BoughtMeal;
import com.beidousat.karaoke.data.PrefData;
import com.beidousat.karaoke.ui.Main;
import com.beidousat.libbns.model.Common;
import com.beidousat.libbns.model.ServerConfigData;
import com.beidousat.karaoke.model.Meal;
import com.beidousat.karaoke.model.Song;

/**
 * Created by J Wong on 2017/4/10.
 */

public class DlgLogin extends BaseDialog implements View.OnClickListener {

    private Button btnRetry;
    private String Tag = "DlgLogin";

    public DlgLogin(Context context) {
        super(context, R.style.MyDialog);
        initView();
    }

    private void initView() {
        this.setContentView(R.layout.dlg_login);
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.width = 500;
        lp.height = 460;
        lp.gravity = Gravity.CENTER;
        getWindow().setAttributes(lp);
        btnRetry = (Button) findViewById(android.R.id.button1);
        btnRetry.setOnClickListener(this);
        findViewById(R.id.iv_close).setOnClickListener(this);
    }

    private void init() {
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_close:
                dismiss();
                break;
            case android.R.id.button1:
                break;
        }
    }

    @Override
    public void dismiss() {
        super.dismiss();
    }
}
