package com.beidousat.karaoke.udp;

import android.content.Context;

import com.beidousat.karaoke.data.KBoxInfo;
import com.beidousat.karaoke.ui.Main;
import com.beidousat.libbns.util.DeviceUtil;
import com.beidousat.libbns.util.PackageUtil;
import com.google.gson.Gson;

import java.io.Serializable;

public class SignUp implements Serializable {
   public String event;
   public String eventkey;
   public String kbox_sn;
   public String device_sn;
   public String os_version;
   public String version;
   public String hsn;


    @Override
    public String toString() {
        return toJson();
    }

    private String toJson() {
        try {
            Gson gson = new Gson();
            String json = gson.toJson(this);
            return json;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    void setSign(Context context,long hsn){
        event="sign";
        eventkey="1";
        if(KBoxInfo.getInstance()!=null&&KBoxInfo.getInstance().getKBox()!=null){
            kbox_sn=KBoxInfo.getInstance().getKBox().getKBoxSn();
        }
        device_sn= DeviceUtil.getCupChipID();
        os_version= String.valueOf(PackageUtil.getSystemVersionCode());
        version=String.valueOf(PackageUtil.getVersionCode(context));
        this.hsn=String.valueOf(hsn);
    }

}
