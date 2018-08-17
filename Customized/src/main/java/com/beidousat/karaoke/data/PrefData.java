package com.beidousat.karaoke.data;

import android.content.Context;

import com.beidousat.libbns.util.PreferenceUtil;

/**
 * Created by J Wong on 2017/5/8.
 */

public class PrefData {

    private final static String KEY_MNG_PWD = "key_mng_pwd";

    private final static String KEY_ROOM_CODE = "key_room_code";

    private final static String SERIAL_BAUD_RATE = "serial_baud_rate";
    private final static String SERIAL_MIC_DOWN = "serial_mic_down";
    private final static String SERIAL_MIC_UP = "serial_mic_up";

    private final static String SERIAL_REVERB_DOWN = "serial_reverb_down";
    private final static String SERIAL_REVERB_UP = "serial_reverb_up";

    private final static String SERIAL_RESET = "serial_reset";


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
        return PreferenceUtil.getInt(context, SERIAL_BAUD_RATE, 9600);
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
        return PreferenceUtil.getString(context, SERIAL_MIC_UP, "E0A206B70A0D0202B2");
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
        return PreferenceUtil.getString(context, SERIAL_MIC_DOWN, "E0A206B70A0D0201B1");
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

    /**
     * 获取中控波混响-码值
     *
     * @param context
     * @return
     */
    public static String getSerilReverbDown(Context context) {
        return PreferenceUtil.getString(context, SERIAL_REVERB_DOWN, "E0A206B70A0E0201B2");
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
        return PreferenceUtil.getString(context, SERIAL_REVERB_UP, "E0A206B70A0E0202B3");
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
        return PreferenceUtil.getString(context, SERIAL_RESET, "E0A206B70A500000F1");
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
}
