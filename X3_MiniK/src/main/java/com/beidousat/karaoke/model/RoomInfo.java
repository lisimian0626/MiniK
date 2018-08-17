package com.beidousat.karaoke.model;

/**
 * Created by J Wong on 2018/2/23.
 */

public class RoomInfo {
    /**
     * id : B00027494
     * label : 01
     * status : 1
     * status_txt : 在营业
     * is_online : 0
     */

    private String id;
    private String label;
    private String status;
    private String status_txt;
    private int is_online;
    private boolean ischeck;

    public boolean isIscheck() {
        return ischeck;
    }

    public void setIscheck(boolean ischeck) {
        this.ischeck = ischeck;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getStatus_txt() {
        return status_txt;
    }

    public void setStatus_txt(String status_txt) {
        this.status_txt = status_txt;
    }

    public int getIs_online() {
        return is_online;
    }

    public void setIs_online(int is_online) {
        this.is_online = is_online;
    }


}
