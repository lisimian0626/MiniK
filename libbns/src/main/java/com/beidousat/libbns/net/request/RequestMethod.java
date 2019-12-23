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
     * 歌曲详情
     */
    public final static String GET_SONGINFO = "getSongInfo";
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
     * 走马灯广告
     */
    public final static String GET_MARQUEE = "getAdTrot";
    /**
     * banner广告
     */
    public final static String GET_BANNER = "ad/pdetail";   //B1 Banner广告 J1角标广告 Z2走马灯（新）

    /**
     * 广告计费
     */
    public static final String AD_BILL = "BillDetails";


    public static final String GET_SONG_RANKING = "getSongRanking";

//    public static final String GET_GRADE_RECORD = "GradeRecord";//演唱得分排名
    public static final String GET_GRADE_RECORD = "play_song/score_order";//演唱得分排名

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
     * 排行榜
     * 参数：Type
     * 1总排行 2月排行 3周排行4日排行 5国语 6粤语
     */
    public static final String SONG_RANKING = "SongRanking";

    public static final String DEVICE_STORE = "device/push_store";//入库

    public static final String RECORD_UPLOAD = "Binary";//录音上传

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
    public static final String ORDER_ACCOUNT = "Order/change_account";
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
    public static final String ROOM_LIST = "Kbox/user_to_list ";//通过账号，查询当前账号下的所有可用房号

    /**
     * 支付方式
     */
    public static final String PAY_PAYMENT = "Pay/payment";

    public static final String GET_SERVER_CFG = "Index/getConfig";//获取配置文件

    public static final String UPLOAD_SONG = "Play_song/set_song";//演唱歌曲记录

    public static final String DOWNLOAD_TIMES = "Binary/download_times";//下载次数记录
}
