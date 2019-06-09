package com.beidousat.libbns.model;

public class BannerInfo {

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
