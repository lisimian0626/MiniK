package com.beidousat.karaoke.udp;

public class UDPComment {
    public static boolean isSign=false;
    public static String token="";
    public static int sendhsn=1;
    public static String QRcode="";

    public static String getUDPErrorMsg(String errorCode){
        String msg="";
        switch (errorCode){
            case "X1001":
                msg="缺少event字段";
            case "X1002":
                msg="缺少eventkey";
                break;
            case "X1003":
                msg="缺少hsn或hsn值错误";
                break;
            case "X1004":
                msg="缺少token";
                break;
            case "X1005":
                msg="缺少device_sn";
                break;
            case "X1006":
                msg="缺少kbox_sn";
                break;
            case "X1007":
                msg="缺少os_version";
                break;
            case "X1008":
                msg="缺少version";
                break;
            case "X4000":
                msg="token出错，或签到数据过期（需要重签）";
                break;
            case "X5000":
                msg="无法签到，芯片串号不存在";
                break;
        }
        return msg;
    }
}
