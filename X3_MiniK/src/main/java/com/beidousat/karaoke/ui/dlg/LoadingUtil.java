package com.beidousat.karaoke.ui.dlg;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.beidousat.karaoke.R;
import com.bumptech.glide.Glide;

/**
 * author: Hanson
 * date:   2017/4/10
 * describe:
 */

public class LoadingUtil {
    private static Dialog mDialog;

    public static void showLoadingDialog(Context context) {
        if (mDialog != null && mDialog.isShowing())
            return;

        mDialog = new Dialog(context, R.style.MyDialog);
        mDialog.setCanceledOnTouchOutside(false);

        View view = LayoutInflater.from(context).inflate(R.layout.dlg_loading, null);
        mDialog.setContentView(view);

        ImageView mIvLoading = (ImageView) view.findViewById(R.id.iv_loading);
        Glide.with(context).load(R.drawable.loading).asGif().into(mIvLoading);

        mDialog.show();
    }

    public static void showLoadingDialog(Context context, String msg) {
        if (mDialog != null && mDialog.isShowing())
            return;

        mDialog = new Dialog(context, R.style.MyDialog);
        mDialog.setCanceledOnTouchOutside(false);

        View view = LayoutInflater.from(context).inflate(R.layout.dlg_loading, null);
        mDialog.setContentView(view);

        ImageView mIvLoading = (ImageView) view.findViewById(R.id.iv_loading);
        Glide.with(context).load(R.drawable.loading).asGif().into(mIvLoading);
        TextView tvMsg = (TextView) view.findViewById(R.id.tv_msg);
        tvMsg.setText(msg);
        mDialog.show();
    }


    public static void closeLoadingDialog() {
        if (mDialog != null && mDialog.isShowing()) {
            mDialog.dismiss();
            mDialog = null;
        }
    }
}
