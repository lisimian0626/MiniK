package com.beidousat.karaoke.biz;

import android.content.Context;
import android.support.v4.app.FragmentManager;

/**
 * author: Hanson
 * date:   2017/4/12
 * describe:
 */

public interface SupportQueryOrder {
    Context getSupportedContext();
    FragmentManager getSupportedFragmentManager();
    //todo for test
    void sendRequestMessage(boolean isSucced, String method, Object data);
}
