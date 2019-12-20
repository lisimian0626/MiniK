package com.beidousat.karaoke.ui.dlg;

import android.app.Activity;

import com.beidousat.karaoke.model.Song;

/**
 * Created by J Wong on 2017/6/26.
 */

public class DialogHelper {

    private static DlgShare mDlgShare;
    private static DlgLogin mDlgLogin;

    /**
     * 打开分享窗口
     *
     * */
    public static void showShareDialog(Activity activity, Song song) {
        if (mDlgShare == null || !mDlgShare.isShowing()) {
            mDlgShare = new DlgShare(activity, song);
            mDlgShare.show();
        } else {
            mDlgShare.setSong(song);
        }
    }

    /**
     * 打开用户扫码登陆
     * */
    public static void showLoginDialog(Activity activity) {

        if (mDlgLogin == null || !mDlgLogin.isShowing()) {
            mDlgLogin = new DlgLogin(activity);
            mDlgLogin.show();
        } else {
            mDlgLogin.show();
        }
    }

}
