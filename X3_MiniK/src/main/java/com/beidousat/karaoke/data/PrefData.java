package com.beidousat.karaoke.data;

import android.content.Context;
import android.util.Log;

import com.beidousat.libbns.model.Common;
import com.beidousat.libbns.util.PreferenceUtil;

/**
 * Created by J Wong on 2017/5/8.
 */

public class PrefData {

    private final static String KEY_MNG_PWD = "key_mng_pwd";

    private final static String KEY_ROOM_CODE = "key_room_code";
    private final static String KEY_SN_CODE = "key_sn_code";
    private final static String SERIAL_BAUD_RATE = "serial_baud_rate";
    private final static String SERIAL_MIC_DOWN = "serial_mic_down";//麦克风音量加
    private final static String SERIAL_MIC_UP = "serial_mic_up";//麦克风 音量减

    private final static String SERIAL_MICMUTE = "serial_mic_mute";//麦克风静音
    private final static String SERIAL_MICUNMUTE = "serial_mic_unmute";//取消麦克风静音
    private final static String SERIAL_QMICVOL = "serial_mic_vol";//询问麦克风值
    private final static String SERIAL_RESETMIC = "serial_reset_mic";//询问麦克风值


    private final static String SERIAL_REVERB_DOWN = "serial_reverb_down";//混响减
    private final static String SERIAL_REVERB_UP = "serial_reverb_up";//混响加
    private final static String SERIAL_QERRECT = "serial_query_effect";//查询混响
    private final static String SERIAL_RESETERRECT = "serial_reset_effect";//查询混响

    private final static String SERIAL_RESET = "serial_reset";
    private final static String LAST_TIME = "last_time";
    private final static String LANGUAGE = "language";
    private final static String AUTH = "auth";

    private final static String ISSINGLE = "isSingle";//是否为单机版本
    //Infrared
    private final static String ISOPEN = "isopen";
    private final static String INFRARED_OPEN = "infrared_open";
    private final static String INFRARED_CLOSE = "infrared_close";
    private final static String INFRARED_TEMP21 = "infrared_temp21";
    private final static String INFRARED_TEMP22 = "infrared_temp22";
    private final static String INFRARED_TEMP23 = "infrared_temp23";
    private final static String INFRARED_TEMP24 = "infrared_temp24";
    private final static String INFRARED_TEMP25 = "infrared_temp25";
    private final static String INFRARED_TEMP26 = "infrared_temp26";
    private final static String INFRARED_TEMP27 = "infrared_temp27";
    private final static String INFRARED_TEMP28 = "infrared_temp28";
    private final static String INFRARED_TEMP29 = "infrared_temp29";
    private final static String INFRARED_WIND_AUTO = "infrared_wind_auto";
    private final static String INFRARED_WIND_HIGH = "infrared_wind_high";
    private final static String INFRARED_WIND_MID = "infrared_wind_mid";
    private final static String INFRARED_WIND_LOW = "infrared_wind_low";
    private final static String CURTEMPVALUE = "Curtempvalue";
    private final static String CURWINDVALUE = "Curwindvalue";
    private final static String GETAUTOCEPHALOUS = "getautocephalous";

    private final static String SERIAL_RJ45 = "serial_rj45";
    private final static String SERIAL_UP = "serial_up";
    private final static String SERIAL_DOWN = "serial_down";
    private final static String NODISK = "nodisk";
    private final static String SOUNDEFFECTSBRAND = "sound_effects_brand";//音效器品牌名称


    /**
     * 读取分析是否为单机版本
     */
    public static boolean getIsSingle(Context context) {
        return PreferenceUtil.getBoolean(context, ISSINGLE, false);
    }

    /**
     * 设置是否为单机版本
     */
    public static void setIsSingle(Context context, boolean mode) {
        PreferenceUtil.setBoolean(context, ISSINGLE, mode);
    }

    /**
     * 读取是否为无盘配置
     */
    public static int Nodisk(Context context) {
        return PreferenceUtil.getInt(context, NODISK, 0);
    }

    /**
     * 设置为无盘
     */
    public static void setNodisk(Context context, int nodisk) {
        PreferenceUtil.setInt(context, NODISK, nodisk);
    }

    /**
     * 读取RJ45口的设备类型
     */
    public static int getSERIAL_RJ45(Context context) {
        return PreferenceUtil.getInt(context, SERIAL_RJ45, 0);
    }

    /**
     * 读取RJ45口的设备类型
     */
    public static void setSERIAL_RJ45(Context context, int mode) {
        PreferenceUtil.setInt(context, SERIAL_RJ45, mode);
    }

    /**
     * 读取3.5mm音频口(上位)串口 的设备类型
     */
    public static int getSERIAL_UP(Context context) {
        return PreferenceUtil.getInt(context, SERIAL_UP, 2);
    }

    /**
     * 设置3.5mm音频口(上位)串口 的设备类型
     */
    public static void setSERIAL_UP(Context context, int mode) {
        PreferenceUtil.setInt(context, SERIAL_UP, mode);
    }

    /**
     * 读取3.5mm音频口(下位)串口的设备类型
     */
    public static int getSERIAL_DOWN(Context context) {
        return PreferenceUtil.getInt(context, SERIAL_DOWN, 1);
    }

    /**
     * 设置3.5mm音频口(下位)串口 的设备类型
     */
    public static void setSERIAL_DOWN(Context context, int mode) {
        PreferenceUtil.setInt(context, SERIAL_DOWN, mode);
    }

    /**
     * 读取音效器品牌名称
     */
    public static int getSoundEffectsBrand(Context context) {
        return PreferenceUtil.getInt(context, SOUNDEFFECTSBRAND, -1);
    }

    /**
     * 设置音效器品牌名称
     */
    public static void setSoundEffectsBrand(Context context, int mode) {
        //防止用户设置的数值在保存时给误改
        if (getSoundEffectsBrand(context) == mode) return;
        if (mode == 1) {
            setSerilBaudrate(context, 115200);
            setSerilMicUp(context, "55AA1302");
            setSerilMicDown(context, "55AA1303");
            setSerilQMicVol(context, "55AAFF0A");
            setSerilResetMic(context, "55AA0A07");

            setSerilMicMute(context, "55AA0A00");
            setSerilMicUnmute(context, "55AA0A07");

            setSerilReverbUp(context, "55AA1304");
            setSerilReverbDown(context, "55AA1305");
            setSerilQueryErrect(context, "55AAFF08");
            setSerilResetErrect(context, "55AA0805");

            setSerilReset(context, "");
        } else {
            setSerilBaudrate(context, 9600);

            setSerilMicUp(context, "E0A206B70A0D0202B2");
            setSerilMicDown(context, "E0A206B70A0D0201B1");
            setSerilQMicVol(context, "E0A204B00A0DA7");
            setSerilResetMic(context, "E0A206B70A0D3200E0");

            setSerilMicMute(context, "E0A206B70A0D0000AE");
            setSerilMicUnmute(context, "E0A206B70A0D3200E0");


            setSerilReverbUp(context, "E0A206B70A0E0202B3");
            setSerilReverbDown(context, "E0A206B70A0E0201B2");
            setSerilQueryErrect(context, "E0A204B00A0EA8");
            setSerilResetErrect(context, "E0A206B70A0E1600C5");

            setSerilReset(context, "E0A206B70A500000F1");
        }
        PreferenceUtil.setInt(context, SOUNDEFFECTSBRAND, mode);
    }

    /**
     * 通过设备名称，跟据前配置取得物理串口位
     */
    public static String getDeviceSerial(Context context, int device_type) {
        //RJ45串口
        int use_rj45_int = getSERIAL_RJ45(context);
        //3.5音频口上 串口
        int use_audio_top_int = getSERIAL_UP(context);
        //3.5音频口下 串口
        int use_audio_down_int = getSERIAL_DOWN(context);

        //原来保存的设置不合规范，全部重置后使用默认设置
        if (use_rj45_int > 2 || use_audio_top_int > 2 || use_audio_down_int > 2) {
            setSERIAL_RJ45(context, 0);
            setSERIAL_UP(context, 0);
            setSERIAL_DOWN(context, 0);
            use_rj45_int = 0;
            use_audio_top_int = 1;
            use_audio_down_int = 2;
        }

        String Serial_str = "";
        if (device_type == use_rj45_int) {
            Serial_str = Common.Rj45Port;
        } else if (device_type == use_audio_top_int) {
            Serial_str = Common.AudioTopPort;
        } else if (device_type == use_audio_down_int) {
            Serial_str = Common.AudioDownPort;
        }
        return Serial_str;
    }


    /**
     * 是否关闭计费
     */
    public static String getAutocephalous(Context context) {
        return PreferenceUtil.getString(context, GETAUTOCEPHALOUS, "0");
    }

    /**
     * 是否关闭计费
     */
    public static void setAutocephalous(Context context, String temp) {
        PreferenceUtil.setString(context, GETAUTOCEPHALOUS, temp);
    }

    public static int getCurTemp(Context context) {
        return PreferenceUtil.getInt(context, CURTEMPVALUE, 25);
    }

    public static void setCurTemp(Context context, int temp) {
        PreferenceUtil.setInt(context, CURTEMPVALUE, temp);
    }

    public static int getCurWind(Context context) {
        return PreferenceUtil.getInt(context, CURWINDVALUE, -2);
    }

    public static void setCurWind(Context context, int wind) {
        PreferenceUtil.setInt(context, CURWINDVALUE, wind);
    }

    public static boolean Is_INFRARED_OPEN(Context context) {
        return PreferenceUtil.getBoolean(context, ISOPEN, false);
    }

    public static void setIs_INFRARED_OPEN(Context context, boolean isopen) {
        PreferenceUtil.setBoolean(context, ISOPEN, isopen);
    }

    public static boolean Is_Close_Set(Context context) {
        return PreferenceUtil.getBoolean(context, INFRARED_CLOSE, false);
    }

    public static void setIs_Close_Set(Context context, boolean isopen) {
        PreferenceUtil.setBoolean(context, INFRARED_CLOSE, isopen);
    }

    public static boolean Is_TEMP21_Set(Context context) {
        return PreferenceUtil.getBoolean(context, INFRARED_TEMP21, false);
    }

    public static void setIs_TEMP21_Set(Context context, boolean isopen) {
        PreferenceUtil.setBoolean(context, INFRARED_TEMP21, isopen);
    }

    public static boolean Is_TEMP22_Set(Context context) {
        return PreferenceUtil.getBoolean(context, INFRARED_TEMP22, false);
    }

    public static void setIs_TEMP22_Set(Context context, boolean isopen) {
        PreferenceUtil.setBoolean(context, INFRARED_TEMP22, isopen);
    }

    public static boolean Is_TEMP23_Set(Context context) {
        return PreferenceUtil.getBoolean(context, INFRARED_TEMP23, false);
    }

    public static void setIs_TEMP23_Set(Context context, boolean isopen) {
        PreferenceUtil.setBoolean(context, INFRARED_TEMP23, isopen);
    }

    public static boolean Is_TEMP24_Set(Context context) {
        return PreferenceUtil.getBoolean(context, INFRARED_TEMP24, false);
    }

    public static void setIs_TEMP24_Set(Context context, boolean isopen) {
        PreferenceUtil.setBoolean(context, INFRARED_TEMP24, isopen);
    }

    public static boolean Is_TEMP25_Set(Context context) {
        return PreferenceUtil.getBoolean(context, INFRARED_TEMP25, false);
    }

    public static void setIs_TEMP25_Set(Context context, boolean isopen) {
        PreferenceUtil.setBoolean(context, INFRARED_TEMP25, isopen);
    }

    public static boolean Is_TEMP26_Set(Context context) {
        return PreferenceUtil.getBoolean(context, INFRARED_TEMP26, false);
    }

    public static void setIs_TEMP26_Set(Context context, boolean isopen) {
        PreferenceUtil.setBoolean(context, INFRARED_TEMP26, isopen);
    }

    public static boolean Is_TEMP27_Set(Context context) {
        return PreferenceUtil.getBoolean(context, INFRARED_TEMP27, false);
    }

    public static void setIs_TEMP27_Set(Context context, boolean isopen) {
        PreferenceUtil.setBoolean(context, INFRARED_TEMP27, isopen);
    }

    public static boolean Is_TEMP28_Set(Context context) {
        return PreferenceUtil.getBoolean(context, INFRARED_TEMP28, false);
    }

    public static void setIs_TEMP28_Set(Context context, boolean isopen) {
        PreferenceUtil.setBoolean(context, INFRARED_TEMP28, isopen);
    }

    public static boolean Is_TEMP29_Set(Context context) {
        return PreferenceUtil.getBoolean(context, INFRARED_TEMP29, false);
    }

    public static void setIs_TEMP29_Set(Context context, boolean isopen) {
        PreferenceUtil.setBoolean(context, INFRARED_TEMP29, isopen);
    }

    public static boolean Is_WINDHIGH_Set(Context context) {
        return PreferenceUtil.getBoolean(context, INFRARED_WIND_HIGH, false);
    }

    public static void setIs_WINDHIGH_Set(Context context, boolean isopen) {
        PreferenceUtil.setBoolean(context, INFRARED_WIND_HIGH, isopen);
    }

    public static boolean Is_WINDMID_Set(Context context) {
        return PreferenceUtil.getBoolean(context, INFRARED_WIND_MID, false);
    }

    public static void setIs_WINDMID_Set(Context context, boolean isopen) {
        PreferenceUtil.setBoolean(context, INFRARED_WIND_MID, isopen);
    }

    public static boolean Is_WINDLOW_Set(Context context) {
        return PreferenceUtil.getBoolean(context, INFRARED_WIND_LOW, false);
    }

    public static void setIs_WINDLOW_Set(Context context, boolean isopen) {
        PreferenceUtil.setBoolean(context, INFRARED_WIND_LOW, isopen);
    }

    public static boolean Is_WINDAuto_Set(Context context) {
        return PreferenceUtil.getBoolean(context, INFRARED_WIND_AUTO, false);
    }

    public static void setIs_WINDAuto_Set(Context context, boolean isopen) {
        PreferenceUtil.setBoolean(context, INFRARED_WIND_AUTO, isopen);
    }

    public static boolean Is_Open_Set(Context context) {
        return PreferenceUtil.getBoolean(context, INFRARED_OPEN, false);
    }

    public static void setIs_Open_Set(Context context, boolean isopen) {
        PreferenceUtil.setBoolean(context, INFRARED_OPEN, isopen);
    }

    /**
     * 获取房间编号
     *
     * @param context
     * @return
     */
    public static String getRoomCode(Context context) {
        return PreferenceUtil.getString(context, KEY_ROOM_CODE, "");
    }

    /**
     * 设置房间编号
     *
     * @param context
     * @param roomCode
     */
    public static void setRoomCode(Context context, String roomCode) {
        PreferenceUtil.setString(context, KEY_ROOM_CODE, roomCode);
    }

    /**
     * 获取SN号
     *
     * @param context
     * @return
     */
    public static String getSNCode(Context context) {
        return PreferenceUtil.getString(context, KEY_SN_CODE, "");
    }

    /**
     * 设置SN号
     *
     * @param context
     * @param roomCode
     */
    public static void setSNCode(Context context, String roomCode) {
        PreferenceUtil.setString(context, KEY_SN_CODE, roomCode);
    }

    /**
     * 获取管理密码，默认值666888
     *
     * @param context
     * @return
     */
    public static String getPassword(Context context) {
        return PreferenceUtil.getString(context, KEY_MNG_PWD, "666888");
    }

    /**
     * 设置管理密码
     *
     * @param context
     * @param password
     */
    public static void setPassword(Context context, String password) {
        PreferenceUtil.setString(context, KEY_MNG_PWD, password);
    }


    /**
     * 获取中控波特率
     *
     * @param context
     * @return
     */
    public static int getSerilBaudrate(Context context) {
        int defValue = 9600;
        if (getSoundEffectsBrand(context) == 1) {
            defValue = 115200;
        }
        return PreferenceUtil.getInt(context, SERIAL_BAUD_RATE, defValue);
    }

    /**
     * 设置中控波特率
     *
     * @param context
     * @param baudrate
     */
    public static void setSerilBaudrate(Context context, int baudrate) {
        PreferenceUtil.setInt(context, SERIAL_BAUD_RATE, baudrate);
    }


    /**
     * 获取中控波麦克风+码值
     *
     * @param context
     * @return
     */
    public static String getSerilMicUp(Context context) {
        String defValue = "E0A206B70A0D0202B2";
        if (getSoundEffectsBrand(context) == 1) {
            defValue = "55AA1302";
        }
        return PreferenceUtil.getString(context, SERIAL_MIC_UP, defValue);
    }

    /**
     * 设置中控波麦克风+码值
     *
     * @param context
     * @param code
     */
    public static void setSerilMicUp(Context context, String code) {
        PreferenceUtil.setString(context, SERIAL_MIC_UP, code);
    }


    /**
     * 获取中控波麦克风-码值
     *
     * @param context
     * @return
     */
    public static String getSerilMicDown(Context context) {
        String defValue = "E0A206B70A0D0201B1";
        if (getSoundEffectsBrand(context) == 1) {
            defValue = "55AA1303";
        }
        return PreferenceUtil.getString(context, SERIAL_MIC_DOWN, defValue);
    }

    /**
     * 设置中控波麦克风-码值
     *
     * @param context
     * @param code
     */
    public static void setSerilMicDown(Context context, String code) {
        PreferenceUtil.setString(context, SERIAL_MIC_DOWN, code);
    }

    //--------------------------------------------------------------

    /**
     * 获取中控 重置麦克风码值
     *
     * @param context
     * @return
     */
    public static String getSerilResetMic(Context context) {
        String defValue = "E0A206B70A0D3200E0";
        if (getSoundEffectsBrand(context) == 1) {
            defValue = "55AA0A07";
        }
        return PreferenceUtil.getString(context, SERIAL_RESETMIC, defValue);
    }

    /**
     * 设置中控 重置麦克风码值
     *
     * @param context
     * @param code
     */
    public static void setSerilResetMic(Context context, String code) {
        PreferenceUtil.setString(context, SERIAL_RESETMIC, code);
    }

    /**
     * 获取中控 重置混响码值
     *
     * @param context
     * @return
     */
    public static String getSerilResetErrect(Context context) {
        String defValue = "E0A206B70A0E1600C5";
        if (getSoundEffectsBrand(context) == 1) {
            defValue = "55AA0805";
        }
        return PreferenceUtil.getString(context, SERIAL_RESETERRECT, defValue);
    }

    /**
     * 设置中控 重置混响码值
     *
     * @param context
     * @param code
     */
    public static void setSerilResetErrect(Context context, String code) {
        PreferenceUtil.setString(context, SERIAL_RESETERRECT, code);
    }

    /**
     * 获取中控 麦克风静音码值
     *
     * @param context
     * @return
     */
    public static String getSerilQMicVol(Context context) {
        String defValue = "E0A204B00A0DA7";
        if (getSoundEffectsBrand(context) == 1) {
            defValue = "55AAFF0A";
        }
        return PreferenceUtil.getString(context, SERIAL_QMICVOL, defValue);
    }

    /**
     * 设置中控 麦克风静音码值
     *
     * @param context
     * @param code
     */
    public static void setSerilQMicVol(Context context, String code) {
        PreferenceUtil.setString(context, SERIAL_QMICVOL, code);
    }


    /**
     * 获取中控 麦克风静音码值
     *
     * @param context
     * @return
     */
    public static String getSerilMicMute(Context context) {
        String defValue = "E0A206B70A0D0000AE";
        if (getSoundEffectsBrand(context) == 1) {
            defValue = "55AA0A00";
        }
        return PreferenceUtil.getString(context, SERIAL_MICMUTE, defValue);
    }

    /**
     * 设置中控 麦克风静音码值
     *
     * @param context
     * @param code
     */
    public static void setSerilMicMute(Context context, String code) {
        PreferenceUtil.setString(context, SERIAL_MICMUTE, code);
    }


    /**
     * 获取中控 取消麦克风静音码值
     *
     * @param context
     * @return
     */
    public static String getSerilMicUnmute(Context context) {
        String defValue = "E0A206B70A0D3200E0";
        if (getSoundEffectsBrand(context) == 1) {
            defValue = "55AA0A07";
        }
        return PreferenceUtil.getString(context, SERIAL_MICUNMUTE, defValue);
    }

    /**
     * 设置中控波 取消麦克风静音码值
     *
     * @param context
     * @param code
     */
    public static void setSerilMicUnmute(Context context, String code) {
        PreferenceUtil.setString(context, SERIAL_MICUNMUTE, code);
    }

    /**
     * 获取中控查询混响的码值
     *
     * @param context
     * @return
     */
    public static String getSerilQueryErrect(Context context) {
        String defValue = "E0A204B00A0EA8";
        if (getSoundEffectsBrand(context) == 1) {
            defValue = "55AAFF08";
        }
        return PreferenceUtil.getString(context, SERIAL_QERRECT, defValue);
    }

    /**
     * 设置中控查询混响的码值
     *
     * @param context
     * @param code
     */
    public static void setSerilQueryErrect(Context context, String code) {
        PreferenceUtil.setString(context, SERIAL_QERRECT, code);
    }


    /**
     * 获取中控波混响-码值
     *
     * @param context
     * @return
     */
    public static String getSerilReverbDown(Context context) {
        String defValue = "E0A206B70A0E0201B2";
        if (getSoundEffectsBrand(context) == 1) {
            defValue = "55AA1305";
        }
        return PreferenceUtil.getString(context, SERIAL_REVERB_DOWN, defValue);
    }

    /**
     * 设置中控波混响-码值
     *
     * @param context
     * @param code
     */
    public static void setSerilReverbDown(Context context, String code) {
        PreferenceUtil.setString(context, SERIAL_REVERB_DOWN, code);
    }


    /**
     * 获取中控波混响+码值
     *
     * @param context
     * @return
     */
    public static String getSerilReverbUp(Context context) {
        String defValue = "E0A206B70A0E0202B3";
        if (getSoundEffectsBrand(context) == 1) {
            defValue = "55AA1304";
        }
        return PreferenceUtil.getString(context, SERIAL_REVERB_UP, defValue);
    }

    /**
     * 设置中控波混响-码值
     *
     * @param context
     * @param code
     */
    public static void setSerilReverbUp(Context context, String code) {

        PreferenceUtil.setString(context, SERIAL_REVERB_UP, code);
    }


    /**
     * 获取中控重置码值
     *
     * @param context
     * @return
     */
    public static String getSerilReset(Context context) {
        String defValue = "E0A206B70A500000F1";
        if (getSoundEffectsBrand(context) == 1) {
            defValue = "";
        }
        return PreferenceUtil.getString(context, SERIAL_RESET, defValue);
    }

    /**
     * 设置中控重置-码值
     *
     * @param context
     * @param code
     */
    public static void setSerilReset(Context context, String code) {
        PreferenceUtil.setString(context, SERIAL_RESET, code);
    }

    /**
     * 获取上次心跳最后时间
     *
     * @param context
     * @return
     */
    public static long getLastTime(Context context) {
        return PreferenceUtil.getLong(context, LAST_TIME, -1);
    }

    /**
     * 设置上次心跳最后时间
     *
     * @param context
     * @param time
     */
    public static void setLastTime(Context context, long time) {
        PreferenceUtil.setLong(context, LAST_TIME, time);
    }

    /**
     * 获取上次语言
     *
     * @param context
     * @return
     */
    public static String getLastLanguage(Context context) {
        return PreferenceUtil.getString(context, LANGUAGE, "zh");
    }

    /**
     * 设置语言
     *
     * @param context
     * @param language
     */
    public static void setLanguage(Context context, String language) {
        PreferenceUtil.setString(context, LANGUAGE, language);
    }

    /**
     * 获取上次授权
     *
     * @param context
     * @return
     */
    public static boolean getLastAuth(Context context) {
        return PreferenceUtil.getBoolean(context, AUTH, false);
    }

    /**
     * 设置授权
     *
     * @param context
     * @param auth
     */
    public static void setAuth(Context context, boolean auth) {
        PreferenceUtil.setBoolean(context, AUTH, auth);
    }
}
