package com.beidousat.libbns.evenbus;

import com.beidousat.libbns.util.Logger;

import de.greenrobot.event.EventBus;

/**
 * Created by J Wong on 2016/6/22.
 */
public class EventBusUtil {

    public static void postSticky(int id, Object ogj) {
        EventBus.getDefault().postSticky(BusEvent.getEvent(id, ogj));
    }

    public static void postPaySucceed(Object obj) {
        Logger.d("EventBus", "----PAY_SUCCEED----");
        EventBus.getDefault().post(BusEvent.getEvent(EventBusId.id.PAY_SUCCEED, obj));
    }

    public static void postMealExpire(Object obj) {
        Logger.d("EventBus", "----MEAL_EXPIRE----");
        EventBus.getDefault().post(BusEvent.getEvent(EventBusId.id.MEAL_EXPIRE, obj));
    }

    public static void postRoomClose(Object obj) {
        Logger.d("EventBus", "----ROOM_CLOSE----");
        EventBus.getDefault().post(BusEvent.getEvent(EventBusId.id.ROOM_CLOSE, obj));
    }

    public static void postRequestMeal() {
        EventBus.getDefault().post(BusEvent.getEvent(EventBusId.id.REQUEST_MEAL));
    }

    public static void postDownloadStart(String url, String path) {
        EventBus.getDefault().post(DownloadBusEvent.getEvent(EventBusId.Download.START, url, path, 0));
    }

    public static void postDownloadFinish(String url, String path) {
        EventBus.getDefault().postSticky(DownloadBusEvent.getEvent(EventBusId.Download.FINISH, url, path, 100));
    }

    public static void postDownloadProgress(String url, String path, float percent) {
        EventBus.getDefault().postSticky(DownloadBusEvent.getEvent(EventBusId.Download.PROGRESS, url, path, percent));
    }

    public static void postDownloadSpaceNotEnough(String url, String path) {
        EventBus.getDefault().post(DownloadBusEvent.getEvent(EventBusId.Download.SPACE_NOT_ENOUGH, url, path, 0));
    }
    public static void postDownloadError(String url, String path, String msg) {
        EventBus.getDefault().post(DownloadBusEvent.getEvent(EventBusId.Download.ERROR, url, path, msg));
    }
    public static void postGiftDetail(Object obj){
        EventBus.getDefault().post(BusEvent.getEvent(EventBusId.id.GIFT_DETAIL, obj));
    }
    public static void postGiftSuccessed(Object obj){
        EventBus.getDefault().post(BusEvent.getEvent(EventBusId.id.GIFT_SUCCESSED, obj));
    }
    public static void postGiftFail(String msg){
        EventBus.getDefault().post(BusEvent.getEvent(EventBusId.id.GIFT_FAIL, msg));
    }
    public static void postCheckRoom(String kbox_sn){
        EventBus.getDefault().post(BusEvent.getEvent(EventBusId.Dialog.CHECKROOM,kbox_sn));
    }
    public static void postPayService(){
        EventBus.getDefault().post(BusEvent.getEvent(EventBusId.Dialog.PAYSERVICE,""));
    }
    public static void postInfraredCode(String code){
        EventBus.getDefault().post(BusEvent.getEvent(EventBusId.INFARAED.RECEIVE_CODE,code));
    }
}
