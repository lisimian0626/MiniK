package com.beidousat.karaoke.model;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * author: Hanson
 * date:   2017/5/5
 * describe:
 */

public class KBox {

    @SerializedName("kbox_sn") //房间编号
    String KBoxSn;
    @SerializedName("store_id") //店家ID
    int StoreID;
    @SerializedName("store_code") //店家编号
    String StoreCode;
    @SerializedName("label") //标签
    String Label;
    @SerializedName("province")  //省
    String Province;
    @SerializedName("city") //市
    String City;
    @SerializedName("area") //区域
    String Area;
    @SerializedName("address") //地址明细
    String Address;
    @SerializedName("status")//状态
    String  status;
    @SerializedName("skin")//皮肤
    String  skin;
    @SerializedName("start_use_time")//开始营业时间
    String start_use_time;
    @SerializedName(" update_time")//更新时间
    String  update_time;
    @SerializedName("add_time")//添加时间
    String add_time;
    @SerializedName("is_pause")//是否停机
    int is_pause;

    @SerializedName("use_online") //是否使用在线支付方案，1=是，0=否
    int use_online;

    @SerializedName("use_coin") //是否使用投币方案，1=是，0=否
    int use_coin;
    @SerializedName("use_gift_card") //是否使用礼品卡方案，1=是，0=否
            int use_gift_card;
    @SerializedName("coin_exchange_rate") //汇率，一个币的价格
    float coin_exchange_rate;
    @SerializedName("service_telphone")//客服电话
    String service_desc;
    @SerializedName("service_qrcode_str")//客服二维码
    String service_qrcode_str;
    @SerializedName("packages")
    List<Package> Packages;
    @SerializedName("banknote_code")
    List<Notecode>  banknote_code;
    @SerializedName("vod_thumbnail_server")
    String kbox_ip;
    @SerializedName("vod_http_server")
    String SERVER_ADDRESS;
    @SerializedName("socket_server")
    String store_ip;
    @SerializedName("ad_server")
    String ad_web;
    @SerializedName("autocephalous")
    String autocephalous;
    @SerializedName("baseplay")
    List<BasePlay> basePlayList;
    @SerializedName("use_pos")
    int use_pos;
    @SerializedName("coin_unit")
    String coin_unit;

    public List<BasePlay> getBasePlayList() {
        return basePlayList;
    }

    public void setBasePlayList(List<BasePlay> basePlayList) {
        this.basePlayList = basePlayList;
    }

    public String getKBoxSn() {
        return KBoxSn;
    }

    public int getStoreID() {
        return StoreID;
    }

    public String getStoreCode() {
        return StoreCode;
    }

    public String getLabel() {
        return Label;
    }

    public String getProvince() {
        return Province;
    }

    public String getCity() {
        return City;
    }

    public String getArea() {
        return Area;
    }

    public String getAddress() {
        return Address;
    }

    public List<Package> getPackages() {
        return Packages;
    }

    public int getUse_online() {
        return use_online;
    }

    public void setUse_online(int use_online) {
        this.use_online = use_online;
    }


    public int getUse_coin() {
        return use_coin;
    }

    public void setUse_coin(int use_coin) {
        this.use_coin = use_coin;
    }

    public float getCoin_exchange_rate() {
        return coin_exchange_rate;
    }

    public void setCoin_exchange_rate(float coin_exchange_rate) {
        this.coin_exchange_rate = coin_exchange_rate;
    }

    public void setKBoxSn(String KBoxSn) {
        this.KBoxSn = KBoxSn;
    }

    public void setStoreID(int storeID) {
        StoreID = storeID;
    }

    public void setStoreCode(String storeCode) {
        StoreCode = storeCode;
    }

    public void setLabel(String label) {
        Label = label;
    }

    public void setProvince(String province) {
        Province = province;
    }

    public void setCity(String city) {
        City = city;
    }

    public void setArea(String area) {
        Area = area;
    }

    public void setAddress(String address) {
        Address = address;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getSkin() {
        return skin;
    }

    public void setSkin(String skin) {
        this.skin = skin;
    }

    public String getStart_use_time() {
        return start_use_time;
    }

    public void setStart_use_time(String start_use_time) {
        this.start_use_time = start_use_time;
    }

    public String getUpdate_time() {
        return update_time;
    }

    public void setUpdate_time(String update_time) {
        this.update_time = update_time;
    }

    public String getAdd_time() {
        return add_time;
    }

    public void setAdd_time(String add_time) {
        this.add_time = add_time;
    }

    public int getIs_pause() {
        return is_pause;
    }

    public void setIs_pause(int is_pause) {
        this.is_pause = is_pause;
    }

    public String getService_desc() {
        return service_desc;
    }

    public void setService_desc(String service_desc) {
        this.service_desc = service_desc;
    }

    public String getService_qrcode_str() {
        return service_qrcode_str;
    }

    public void setService_qrcode_str(String service_qrcode_str) {
        this.service_qrcode_str = service_qrcode_str;
    }

    public void setPackages(List<Package> packages) {
        Packages = packages;
    }

    public String getKbox_ip() {
        return kbox_ip;
    }

    public void setKbox_ip(String kbox_ip) {
        this.kbox_ip = kbox_ip;
    }

    public String getSERVER_ADDRESS() {
        return SERVER_ADDRESS;
    }

    public void setSERVER_ADDRESS(String SERVER_ADDRESS) {
        this.SERVER_ADDRESS = SERVER_ADDRESS;
    }

    public String getStore_ip() {
        return store_ip;
    }

    public void setStore_ip(String store_ip) {
        this.store_ip = store_ip;
    }

    public String getAd_web() {
        return ad_web;
    }

    public void setAd_web(String ad_web) {
        this.ad_web = ad_web;
    }

    public String getAutocephalous() {
        return autocephalous;
    }

    public void setAutocephalous(String autocephalous) {
        this.autocephalous = autocephalous;
    }

    public int getUse_gift_card() {
        return use_gift_card;
    }

    public void setUse_gift_card(int use_gift_card) {
        this.use_gift_card = use_gift_card;
    }

    public int getUse_pos() {
        return use_pos;
    }

    public void setUse_pos(int use_pos) {
        this.use_pos = use_pos;
    }

    public List<Notecode> getBanknote_code() {
        return banknote_code;
    }

    public void setBanknote_code(List<Notecode> banknote_code) {
        this.banknote_code = banknote_code;
    }

    public String getCoin_unit() {
        return coin_unit;
    }

    public void setCoin_unit(String coin_unit) {
        this.coin_unit = coin_unit;
    }
    public String basePlaytoJsonStr(List<BasePlay> basePlayList){

        return toJson(basePlayList);
    }
    private String toJson(List<BasePlay> basePlayList) {
        try {
            Gson gson = new Gson();
            return gson.toJson(basePlayList);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }
}
