package com.beidousat.karaoke.udp;

public class UdpError {
    public static String translate(String code) {
        String result = "未知错误";
        switch (code.toUpperCase()){
            case "X1001":
                result="缺少event字段";
                break;
            case "X1002":
                result="缺少eventkey";
                break;
            case "X1003":
                result="缺少hsn或hsn值错误";
                break;
            case "X1004":
                result="缺少token";
                break;
            case "X1005":
                result="缺少device_sn";
                break;
            case "X1006":
                result="缺少kbox_sn";
                break;
            case "X1007":
                result="缺少os_version";
                break;
            case "X1008":
                result="缺少version";
                break;
            case "X4000":
                result="token出错，或签到数据过期（需要重签）";
                break;
            case "X5000":
                result="无法签到，芯片串号不存在";
                break;
            case "X5002":
                result="无法签到，芯片串号不存在";
                break;
            case "X5003":
                result="非法签到（kbox_sn与芯片口号冲突";
                break;
        }
        return result;
    }
}
