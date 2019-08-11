package com.beidousat.libbns.evenbus;

public class EventBusId {

    public static final class id {

        /**
         * 火警警报开关
         */
        public static final int FIRE_ACTION = 10001;


        /**
         * 门口屏广告
         */
        public static final int PUSH_DOOR_AD = 3001;


        public static final int CURRENT_SCREEN_AD = 2003;

        /***
         * 切歌
         */
        public static final int PLAYER_NEXT = 1;

        /***
         * 切歌
         */
        public static final int PLAYER_NEXT_DELAY = 4;
        /**
         * 重唱
         */
        public static final int PLAYER_REPLAY = 3;

        /***
         * 音乐音量
         */
        public static final int PLAYER_SCORE_ON_OFF = 6;

        /***
         * 滑动
         */
        public static final int PLAYER_SEEK_TO = 12;

        /**
         *
         */
        public static final int ADD_FRAGMENT = 16;

        /**
         *
         */
        public static final int PLAYER_PLAY_SONG = 17;

        /**
         *
         */
        public static final int CHOOSE_SONG_CHANGED = 18;

        /**
         *
         */
        public static final int PLAYER_PLAY_BEGIN = 19;

        public static final int PLAYER_ADS_BEIGIN = 20;

//        public static final int PLAYER_SHOW_SERVICE = 21;

        public static final int SERVICE_MODE = 21;

        /***
         * 原唱
         */
        public static final int PLAYER_ORIGINAL = 22;

        /***
         * 伴唱唱
         */
        public static final int PLAYER_ACCOM = 23;

        /***
         * 取消静音
         */
        public static final int PLAYER_VOL_ON = 24;

        /***
         * 静音
         */
        public static final int PLAYER_VOL_OFF = 25;

        /***
         * 暂停
         */
        public static final int PLAYER_PAUSE2 = 26;

        /***
         * 播放
         */
        public static final int PLAYER_PLAY = 27;

        /**
         *
         */
        public static final int PLAYER_STATUS_CHANGED = 28;

        public static final int PLAYER_PLAY_LIVE = 29;

        public static final int PLAYER_PLAY_MOVIE = 30;

        public static final int TONE_MUTE=35;

        public static final int TONE_DEFAULT = 36;

        public static final int TONE_DOWN = 37;

        public static final int TONE_UP = 38;

        public static final int MIC_UP = 39;

        public static final int MIC_DOWN = 40;

        public static final int VOL_DOWN = 41;

        public static final int VOL_UP = 42;

        /**
         * 0:自动 1：明亮 ；2：柔和 ；3：抒情 4：动感
         */
        public static final int LIGHT_MODE = 43;


        /**
         *
         */
        public static final int SUNG_SONG_CHANGED = 45;

        public static final int SERIAL_RECEIVED_SERVICE_FLASHING = 46;

        public static final int SERIAL_RECEIVED_SERVICE_CANCEL = 47;

        public static final int SERIAL_RECEIVED_LightsBright = 48;

        public static final int SERIAL_RECEIVED_LightsSoft = 49;

        public static final int SERIAL_RECEIVED_LightsLyric = 50;

        public static final int SERIAL_RECEIVED_LightsDynamic = 51;

        public static final int SERIAL_RECEIVED_LightsFullOpen = 52;

        public static final int SERIAL_RECEIVED_LightsFullClose = 53;

        public static final int ADD_DANMAKU = 54;

        public static final int SHOW_EMOJI = 55;

        public static final int SHOW_QR_CODE = 56;

        public static final int GAME_LEVEL_PASS_CHANGED = 57;

        public static final int MAIN_PLAYER_STOP = 58;

        public static final int MAIN_PLAYER_RESUME = 59;

        public static final int PK_PROGRESS_SCORE = 60;

        public static final int PK_RESULT = 61;

        public static final int PK_GIVE_UP = 62;

        public static final int PK_DANMAKU = 63;

        public static final int ROOM_USER_CHANGED = 64;

        public static final int ERP_OPEN_ROOM = 65;

        public static final int ERP_CLOSE_ROOM = 66;

        public static final int ERP_PUSH_MSG = 67;

        public static final int PK_DUEL_CHANGED = 68;

        public static final int BACK_FRAGMENT = 69;

        public static final int SETTING_MARQUEE_MARGIN_CHANGED = 70;

        public static final int SETTING_AD_CORNER_MARGIN_CHANGED = 71;

        public static final int TV_AD_CORNER_SHOW = 72;

        public static final int SYSTEM_DANMAKU = 73;


        public static final int GAME_EXIT = 74;


        public static final int GAME_CLOSE = 75;

        public static final int GAME_BUTTON_STATUS = 76;

        public static final int GAME_PAUSE_PLAY = 77;


        public static final int GAME_RESULT = 78;

        public static final int GAME_START = 79;

        public static final int PK_GIVE_UP_2_MAIN = 80;

        public static final int PK_CLOSE = 81;


        public static final int PROJECTOR_PLAY_URL = 82;

        public static final int PROJECTOR_PLAY_PAUSE = 83;

        public static final int PROJECTOR_PLAY_PLAY = 84;


        public static final int SCENES_TYPE_CHANGED = 85;

        public static final int LOAD_RANKING = 86;

        public static final int SELECT_RANKING = 87;

        public static final int CURRENT_SECENCE_MODE_CHANGED = 88;

        public static final int HDMI_BLACK = 89;


        public static final int HDMI_SHOT = 90;

        public static final int SKIN_CHANGED = 91;

        public static final int PAY_SUCCEED = 92; //支付成功

        public static final int MEAL_EXPIRE = 93; //套餐过期

        public static final int ROOM_CLOSE = 94; //欢唱结束


        public static final int REQUEST_MEAL = 95; //重新请求套餐信息


        public static final int SERIAL_REVERB_UP = 96;
        public static final int SERIAL_REVERB_DOWN = 97;

        public static final int GIFT_DETAIL=98;
        public static final int GIFT_SUCCESSED=99;
        public static final int GIFT_FAIL=100;

        /**
         * 获取配置信息成功
         */
        public static final int GET_CONFIG_SUCCESS = 122;

        public static final int GET_BANNER= 123;

        public static final int UPDATA_SERIAL_SUCCED=124;
        public static final int TOAST=200;
    }


    /**
     * Download id start at 0x9901
     */
    public static final class Download {
        public static final int START = 0x9901;
        public static final int FINISH = 0x9902;
        public static final int PROGRESS = 0x9903;
        public static final int SPACE_NOT_ENOUGH = 0x9904;
        public static final int ERROR = 0x9905;
    }


    public static final class SOCKET {
        public static final int KBOX_STATUS = 10000;
        public static final int KBOX_STATUS_CHECKING = 10001;

    }

    public static final class SERIAL {
        public static final int SERIAL_MIC_VOL = 20000;
        public static final int SERIAL_EFF_VOL = 20001;

    }

    public static final class Dialog {
        public static final int CHECKROOM= 30000;
        public static final int PAYSERVICE= 30001;
    }

    public static final class INFARAED {
        public static final int RECEIVE_CODE = 40000;
    }

    public static final class Ost {
        public static final int RECEIVE_CODE = 50000;
        public static final int CHECK_CODE = 50001;
    }

    public static final class Ict {
        public static final int RECEIVE_CODE = 60000;
        public static final int CHECK_CODE = 60001;
    }

    public static final class Udp{
        public static final int SUCCESS = 70000;
        public static final int ERROR = 70001;
    }
    public static final class MCU{
        public static final int RECEIVE_CODE = 80000;
    }
}
