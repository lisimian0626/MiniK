package com.beidousat.karaoke.ui.dlg;

import android.app.Activity;
import android.content.Context;

import com.beidousat.karaoke.model.Song;

/**
 * Created by J Wong on 2017/6/26.
 */

public class DialogHelper {

    private static DlgShare mDlgShare;

    public static void showShareDialog(Context context, Song song) {
        if (mDlgShare == null || !mDlgShare.isShowing()) {
            mDlgShare = new DlgShare(context, song);
            mDlgShare.show();
        } else {
            mDlgShare.setSong(song);
        }
    }

}
