package com.beidousat.libbns.net.request;

import android.text.TextUtils;

import com.beidousat.libbns.model.ServerConfigData;
import com.beidousat.libbns.util.BnsConfig;

/**
 * Created by J Wong on 2015/10/19 11:41.
 */
public class RequestMethod {

    /**
     * 歌曲列表
     */
    public final static String GET_SONG = "getSong";


    /**
     * 歌星
     */
    public static final String GET_SINGER = "getSinger";

    /**
     * 版本信息
     */
    public final static String GET_VERSION = "getVersion";

    /**
     * 贴片广告
     */
    public final static String GET_PASTER = "getAdPaster";
    /**
     * 角标广告
     */
    public final static String GET_CORNER = "getAdCorner";
    /**
     * 走马灯广告
     */
    public final static String GET_MARQUEE = "getAdTrot";
    /**
     * banner广告
     */
    public final static String GET_BANNER = "getAdKiosk";
    /**
     * 广告计费
     */
    public static final String AD_BILL = "BillDetails";


    public static final String GET_SONG_RANKING = "getSongRanking";

    public static final String GET_GRADE_RECORD = "GradeRecord";

    public static final String MUZIKLAND_LEVEL = "MuziklandLevel";

    public static final String GET_SINGER_DETAIL = "getSingerDetail";

    /**
     * 公益广告
     */
    public final static String GET_AD_BENEFIT = "getAdBenefit";

    /**
     * 暂停广告
     */
    public final static String GET_AD_PAUSE = "getAdPause";

    /**
     * 片头片尾广告
     */
    public final static String GET_AD_START_END = "getAdHeadTail";


    /**
     * 创建支付订单
     */
//    public static final String CREATE_ORDER = "CreateOrder";

    /**
     * 查询订单是否支付成功
     */
//    public static final String QUERY_ORDER = "QueryOrder";

    /**
     * 查询用户信息
     */
//    public static final String QUERY_USER = "QueryUser";

    /**
     * 取消订单
     */
//    public static final String CANCEL_ORDER = "OrderCancel";

    /**
     * 录音文件上传
     */
    public static final String UPLOAD_RECORD = ServerConfigData.getInstance().getServerConfig().getVod_url() + "m=netbar&a=Record";

    /**
     * 排行榜
     * 参数：Type
     * 1总排行 2月排行 3周排行4日排行 5国语 6粤语
     */
    public static final String SONG_RANKING = "SongRanking";

//    public static final String GET_KBOX = "StoreKbox";
//public static final String DEVICE_STORE = "Localstore/warehouse";
    public static final String DEVICE_STORE = "device/push_store";

    public static final String GET_CONFIG = "getConfig";

    public static final String GET_AD_DOOR_MINI = "getAdDoorMini";

    public static final String RECORD_UPLOAD = "Binary";

    public static final String SHARE_HTML_URL = "/share/recording.html?query=";


    /**
     * 屏保广告
     */
    public static final String GET_AD_SCREEN = "getAdScreen";


    /**
     * 读取具体包房信息——机顶盒专用
     */
    public static final String STORE_KBOX = "Kbox/detail";


    /**
     * 创建订单
     */
    public static final String ORDER_CREATE = "Order/Create";

    /**
     * 创建订单(新)
     */
    public static final String ORDER_CREATE2 = "Order/Create2";
    /**
     * 查询订单
     */
    public static final String ORDER_QUERY = "Order/query";

    /**
     * 取消订单
     */
    public static final String ORDER_CANCEL = "Order/Cancel";

    /**
     * 發起支付
     */
    public static final String PAY_CREATE = "Pay/Create";
    /**
     * 禮品卡支付
     */
    public static final String PAY_CARD = "Pay/card_consume";
    /**
     * 获取支付服务费二维码
     */
    public static final String ORDER_ACCOUNT="Order/change_account";
    /**
     * 查询支付后的用户信息
     */
    public static final String USER_QUERY = "User/query";

    /**
     * 投币完成
     */
    public static final String ORDER_FINISH_PAY = "Order/finish_pay";

    /**
     * 卡券详情
     */
    public static final String CARD_DETAIL = "Card/detail";

    /**
     * 礼品券兑换
     */
    public static final String GIFT_CREATE = "Order/use_gift_create";


    /**
     * 房间列表
     */
    public static final String ROOM_LIST = "Kbox/user_to_list ";

    /**
     * 支付方式
     */
    public static final String PAY_PAYMENT = "Pay/payment";

    public static final String GET_SERVER_CFG="Index/getConfig";

    public static final String UPLOAD_SONG="Play_song/set_song";
}
