package com.beidousat.karaoke.ui.dlg;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.annotation.DrawableRes;
import android.support.annotation.StringRes;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.beidousat.karaoke.R;
import com.beidousat.libbns.util.DensityUtil;

public class AlertDialog implements DialogInterface {
    Context context;
    android.app.AlertDialog ad;
    TextView titleView;
    TextView messageView;
    LinearLayout buttonLayout;

    public AlertDialog(Context context) {
        this(context, 0, 0);
    }

    public AlertDialog(Context context, int width, int height) {
        this.context = context;
        ad = new android.app.AlertDialog.Builder(context).create();
        ad.setCancelable(false);
        ad.show();
        //关键在下面的两行,使用window.setContentView,替换整个对话框窗口的布局
        Window window = ad.getWindow();
        window.setContentView(R.layout.dlg_alert);
        titleView = (TextView) window.findViewById(R.id.title);
        messageView = (TextView) window.findViewById(R.id.message);
        buttonLayout = (LinearLayout) window.findViewById(R.id.buttonLayout);

        if (width != 0 && height != 0) {
            window.setLayout(width, height);
        }
    }

    public void setTitle(int resId) {
        titleView.setText(resId);
    }

    public void setTitle(String title) {
        titleView.setText(title);
    }

    public void setMessage(int resId) {
        messageView.setText(resId);
    }

    public void setMessage(String message) {
        messageView.setText(message);
    }



    /**
     * 设置按钮
     *
     * @param text
     * @param listener
     */
    public void setPositiveButton(String text, Drawable drawable, final View.OnClickListener listener) {
        Button button = new Button(context);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.width = context.getResources().getDimensionPixelSize(R.dimen.dlg_button_width);
        params.height = context.getResources().getDimensionPixelSize(R.dimen.dlg_button_height);
        button.setLayoutParams(params);
        if (drawable == null) {
            button.setBackgroundResource(R.drawable.selector_dlg_back);
        } else {
            button.setBackground(drawable);
        }
        button.setText(text);
        button.setTextColor(Color.WHITE);
        button.setTextSize(context.getResources().getDimensionPixelSize(R.dimen.dlg_countdown_small));
        button.setOnClickListener(listener);
        buttonLayout.addView(button);
    }

    public void setPositiveButton(@StringRes int text, final View.OnClickListener listener) {
        setPositiveButton(context.getResources().getString(text), null, listener);
    }

    public void setPositiveButton(@StringRes int text, @DrawableRes int drawable, final View.OnClickListener listener) {
        setNegativeButton(context.getResources().getString(text), context.getDrawable(drawable), listener);
    }

    /**
     * 设置按钮
     *
     * @param text
     * @param listener
     */
    public void setNegativeButton(String text, Drawable drawable, final View.OnClickListener listener) {
        Button button = new Button(context);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.width = context.getResources().getDimensionPixelSize(R.dimen.dlg_button_width);
        params.height = context.getResources().getDimensionPixelSize(R.dimen.dlg_button_height);
        button.setLayoutParams(params);
        if (drawable == null) {
            button.setBackgroundResource(R.drawable.selector_dlg_continue);
        } else {
            button.setBackground(drawable);
        }
        button.setText(text);
        button.setTextColor(Color.WHITE);
        button.setTextSize(context.getResources().getDimensionPixelSize(R.dimen.dlg_countdown_small));
        button.setOnClickListener(listener);
        if (buttonLayout.getChildCount() > 0) {
            params.setMargins(DensityUtil.dip2px(context, 60), 0, 0, 0);
            button.setLayoutParams(params);
            buttonLayout.addView(button, 1);
        } else {
            button.setLayoutParams(params);
            buttonLayout.addView(button);
        }
    }

    public void setNegativeButton(@StringRes int text, final View.OnClickListener listener) {
        setNegativeButton(context.getResources().getString(text), null, listener);
    }

    public void setNegativeButton(@StringRes int text, @DrawableRes int drawable, final View.OnClickListener listener) {
        setNegativeButton(context.getResources().getString(text), context.getDrawable(drawable), listener);
    }

    @Override
    public void cancel() {
//        dismiss();
    }

    /**
     * 关闭对话框
     */
    public void dismiss() {
        ad.dismiss();
    }

}