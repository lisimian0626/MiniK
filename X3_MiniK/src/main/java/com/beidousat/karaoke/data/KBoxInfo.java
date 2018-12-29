package com.beidousat.karaoke.data;

import com.beidousat.karaoke.model.KBox;
import com.beidousat.karaoke.model.Notecode;
import com.beidousat.karaoke.model.Package;
import com.beidousat.karaoke.model.PayMent;
import com.beidousat.libbns.util.Logger;

import java.util.List;

/**
 * author: Hanson
 * date:   2017/5/5
 * describe:
 */

public class KBoxInfo {
        public static final String STORE_WEB="http://box.imtbox.com/";
    public static  String WEBVIEW = "https://www.imtbox.com/";
//    public static final String STORE_WEB="http://ktv.mesong.me/";
//    public static final String STORE_WEB="http://k.mesong.me/";
//    public static final String STORE_WEB = "http://minorder.beidousat.com/";
    private static final KBoxInfo mInstance = new KBoxInfo();
    private KBox mKBox;
    private List<PayMent> mPayMentlist;
    private KBoxInfo() {

    }

    public static KBoxInfo getInstance() {
        return mInstance;
    }

    public void setKBox(KBox kbox) {
        mKBox = kbox;
    }

    public KBox getKBox() {
        return mKBox;
    }

    public List<Package> getMealPackages() {
        if (mKBox == null)
            return null;

        return mKBox.getPackages();
    }
    public List<Notecode> getNotecode() {
        if (mKBox == null)
            return null;

        return mKBox.getBanknote_code();
    }
    public List<PayMent> getmPayMentlist() {
        return mPayMentlist;
    }

    public void setmPayMentlist(List<PayMent> mPayMentlist) {
        this.mPayMentlist = mPayMentlist;
    }
}
