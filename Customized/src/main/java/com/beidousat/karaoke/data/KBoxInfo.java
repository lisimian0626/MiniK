package com.beidousat.karaoke.data;

import com.beidousat.karaoke.model.KBox;
import com.beidousat.karaoke.model.Package;
import com.beidousat.libbns.util.Logger;

import java.util.List;

/**
 * author: Hanson
 * date:   2017/5/5
 * describe:
 */

public class KBoxInfo {
    private static final KBoxInfo mInstance = new KBoxInfo();
    private KBox mKBox;

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
}
