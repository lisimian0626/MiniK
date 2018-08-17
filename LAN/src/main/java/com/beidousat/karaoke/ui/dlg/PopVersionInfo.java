package com.beidousat.karaoke.ui.dlg;

import android.app.Activity;
import android.app.Dialog;
import android.view.Gravity;
import android.view.WindowManager;
import android.widget.TextView;
import com.beidousat.karaoke.R;
import com.beidousat.libbns.util.PackageUtil;

/**
 * Created by J Wong on 2015/10/12 15:52.
 */
public class PopVersionInfo extends Dialog {

    private Activity mContext;


    public PopVersionInfo(Activity context) {
        super(context, R.style.MyDialog);
        init(context);
    }


    public void init(Activity context) {
        mContext = context;
        this.setContentView(R.layout.pop_version_info);
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.width = 600;
        lp.height = 450;
        lp.gravity = Gravity.CENTER;

        getWindow().setAttributes(lp);

        ((TextView) findViewById(R.id.tv_apk_version)).setText(mContext.getString(R.string.version_x, PackageUtil.getVersionName(mContext.getApplication())));
        ((TextView) findViewById(R.id.tv_system_version)).setText(mContext.getString(R.string.sys_version_x, PackageUtil.getSystemVersionCode()));

    }

}
