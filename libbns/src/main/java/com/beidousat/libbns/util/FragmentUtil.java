package com.beidousat.libbns.util;

import android.support.v4.app.Fragment;

import com.beidousat.libbns.evenbus.BusEvent;
import com.beidousat.libbns.evenbus.EventBusId;
import com.beidousat.libbns.model.FragmentModel;

import de.greenrobot.event.EventBus;

/**
 * Created by J Wong on 2016/6/20.
 */
public class FragmentUtil {

    public static void addFragment(Fragment fragment) {
        EventBus.getDefault().postSticky(BusEvent.getEvent(EventBusId.id.ADD_FRAGMENT, new FragmentModel(fragment)));
    }
}
