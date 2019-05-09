package com.beidousat.karaoke.ui.dlg;

import android.app.Activity;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.beidousat.karaoke.R;
import com.beidousat.karaoke.data.PrefData;
import com.beidousat.karaoke.ui.Main;
import com.beidousat.libbns.model.Common;
import com.beidousat.libbns.util.PackageUtil;
import com.beidousat.libwidget.image.RecyclerImageView;

/**
 * Created by J Wong on 2015/10/12 15:52.
 */
public class PopVersionInfo extends BaseDialog {

    private Activity mContext;
    private RecyclerImageView logo;
    private TextView tv_sn;
    public PopVersionInfo(Activity context) {
        super(context, R.style.MyDialog);
        init(context);
    }


    public void init(Activity context) {
        mContext = context;
        this.setContentView(R.layout.pop_version_info);
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.width = 600;
        lp.height = 300;
        lp.gravity = Gravity.CENTER;

        getWindow().setAttributes(lp);
        ((TextView) findViewById(R.id.tv_system_version)).setText(mContext.getString(R.string.sys_version_x, PackageUtil.getVersionName(mContext.getApplication())+" / "+PackageUtil.getSystemVersionCode()));
        tv_sn= (TextView) findViewById(R.id.tv_sn);
        String sn= PrefData.getSNCode(Main.mMainActivity);
        if(!TextUtils.isEmpty(sn)){
            tv_sn.setText("SN:"+sn);
            tv_sn.setVisibility(View.VISIBLE);
        }else{
            tv_sn.setVisibility(View.GONE);

        }
         logo=(RecyclerImageView) findViewById(R.id.iv_logo);
         if(Common.isEn){
             logo.setImageResource(R.drawable.logo_en);
         }

    }

}
