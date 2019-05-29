package com.beidousat.libbns.model;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class BannerInfo {

    /**
     * data : {"action_type":"1","action_url":"https://www.imtbox.com/","media_type":"1","img_url":"http://f.imtbox.com/ad/201905252019-05-25/5ce82939f3978.png"}
     * message : success
     * error : 0
     */

    private DataBean data;
    private String message;
    private int error;

    public static List<BannerInfo> arrayBannerInfoFromData(String str) {

        Type listType = new TypeToken<ArrayList<BannerInfo>>() {
        }.getType();

        return new Gson().fromJson(str, listType);
    }

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getError() {
        return error;
    }

    public void setError(int error) {
        this.error = error;
    }

    public static class DataBean {
        /**
         * action_type : 1
         * action_url : https://www.imtbox.com/
         * media_type : 1
         * img_url : http://f.imtbox.com/ad/201905252019-05-25/5ce82939f3978.png
         */

        private String action_type;
        private String action_url;
        private String media_type;
        private String img_url;

        public static List<DataBean> arrayDataBeanFromData(String str) {

            Type listType = new TypeToken<ArrayList<DataBean>>() {
            }.getType();

            return new Gson().fromJson(str, listType);
        }

        public String getAction_type() {
            return action_type;
        }

        public void setAction_type(String action_type) {
            this.action_type = action_type;
        }

        public String getAction_url() {
            return action_url;
        }

        public void setAction_url(String action_url) {
            this.action_url = action_url;
        }

        public String getMedia_type() {
            return media_type;
        }

        public void setMedia_type(String media_type) {
            this.media_type = media_type;
        }

        public String getImg_url() {
            return img_url;
        }

        public void setImg_url(String img_url) {
            this.img_url = img_url;
        }
    }
}
