package com.beidousat.karaoke.ui.dlg;

import android.content.Context;
import android.content.DialogInterface;
import android.view.View;

import com.beidousat.karaoke.R;
import com.beidousat.libbns.util.DensityUtil;
import com.beidousat.libbns.util.DiskFileUtil;


/**
 * author: Hanson
 * date:   2017/5/9
 * describe:
 */

public class DialogFactory {

    /**
     * 创建下载对话框
     *
     * @param context
     * @param abortListener    放弃事件
     * @param downloadListener 后台加载 事件
     * @return
     */
    public static void showDownloadDialog(final Context context,
                                          final DialogInterface.OnClickListener abortListener,
                                          final DialogInterface.OnClickListener downloadListener) {
        final AlertDialog dialog = new AlertDialog(context,
                DensityUtil.dip2px(context, 400), DensityUtil.dip2px(context, 240));
        dialog.setTitle(R.string.prompt);
        dialog.setMessage(R.string.download_prompt);
        dialog.setPositiveButton(R.string.abort, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (abortListener != null) {
                    abortListener.onClick(dialog, android.app.AlertDialog.BUTTON_NEGATIVE);
                }
            }
        });
        dialog.setNegativeButton(R.string.download_background, R.drawable.selector_dlg_share, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //todo 检测是否有硬盘
                if (!DiskFileUtil.hasDiskStorage()) {
                    dialog.dismiss();
                    DialogFactory.showErrorDialog(context, "检测到没有硬盘，请插入硬盘！", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                } else {
                    if (downloadListener != null) {
                        downloadListener.onClick(dialog, android.app.AlertDialog.BUTTON_POSITIVE);
                    }
                }
            }
        });
    }

    public static AlertDialog showCancelOrderDialog(Context context,
                                                    final DialogInterface.OnClickListener okListener,
                                                    final DialogInterface.OnClickListener cancelListener) {
        final AlertDialog dialog = new AlertDialog(context,
                DensityUtil.dip2px(context, 450), DensityUtil.dip2px(context, 260));
        dialog.setTitle(R.string.text_cancel_order_title);
        dialog.setMessage(R.string.text_cancel_order_msg);
        dialog.setPositiveButton(R.string.text_yes, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (okListener != null) {
                    okListener.onClick(dialog, android.app.AlertDialog.BUTTON_POSITIVE);
                }
            }
        });
        dialog.setNegativeButton(R.string.text_no, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (cancelListener != null) {
                    cancelListener.onClick(dialog, android.app.AlertDialog.BUTTON_NEGATIVE);
                }
            }
        });

        return dialog;
    }

    public static void showErrorDialog(Context context, String msg, final DialogInterface.OnClickListener listener) {
        final AlertDialog dialog = new AlertDialog(context);
        dialog.setTitle(R.string.text_error_title);
        dialog.setMessage(msg);
        dialog.setPositiveButton(R.string.ok, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                if (listener != null) {
                    listener.onClick(dialog, android.app.AlertDialog.BUTTON_POSITIVE);
                }
            }
        });
    }


    public static AlertDialog showCancelCoinDialog(Context context,
                                                   final DialogInterface.OnClickListener okListener,
                                                   final DialogInterface.OnClickListener cancelListener, String tipMsg) {
        final AlertDialog dialog = new AlertDialog(context,
                DensityUtil.dip2px(context, 450), DensityUtil.dip2px(context, 260));
        dialog.setTitle(R.string.text_cancel_order_title);
        dialog.setMessage(tipMsg);
        dialog.setPositiveButton(R.string.text_yes, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (okListener != null) {
                    okListener.onClick(dialog, android.app.AlertDialog.BUTTON_POSITIVE);
                }
            }
        });
        dialog.setNegativeButton(R.string.text_no, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (cancelListener != null) {
                    cancelListener.onClick(dialog, android.app.AlertDialog.BUTTON_NEGATIVE);
                }
            }
        });

        return dialog;
    }

    public static AlertDialog showConfirmDialog(Context context,
                                                   final DialogInterface.OnClickListener okListener,
                                                   final DialogInterface.OnClickListener cancelListener, String tipMsg) {
        final AlertDialog dialog = new AlertDialog(context,
                DensityUtil.dip2px(context, 450), DensityUtil.dip2px(context, 260));
        dialog.setTitle(R.string.infrared_confirm);
        dialog.setMessage(tipMsg);
        dialog.setPositiveButton(R.string.text_yes, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (okListener != null) {
                    okListener.onClick(dialog, android.app.AlertDialog.BUTTON_POSITIVE);
                }
            }
        });
        dialog.setNegativeButton(R.string.text_no, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (cancelListener != null) {
                    cancelListener.onClick(dialog, android.app.AlertDialog.BUTTON_NEGATIVE);
                }
            }
        });

        return dialog;
    }
}
