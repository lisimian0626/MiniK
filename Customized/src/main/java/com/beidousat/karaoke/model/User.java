package com.beidousat.karaoke.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * author: Hanson
 * date:   2017/4/14
 * describe:
 */

public class User implements Serializable {
    @SerializedName("user_id")
    private int UserId;
    @SerializedName("nick_name")
    private String NickName;    //用户昵称
    @SerializedName("sex")
    private int Sex;    //用户性别。值为1时是男性，值为2时是女性，值为0时是未知
    @SerializedName("avatar")
    private String Avatar;    //用户头像
    @SerializedName("language")
    private String Language;    //用户所用语言

    public String getNickName() {
        return NickName;
    }

    public int getSex() {
        return Sex;
    }

    public int getUserId() {
        return UserId;
    }

    public String getAvatar() {
        return Avatar;
    }

    public String getLanguage() {
        return Language;
    }


    public static boolean isEmpty(User user) {
        if (user == null) return true;

        return user.UserId == 0;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        User other = (User) obj;

        return  UserId == other.UserId;
    }
}
