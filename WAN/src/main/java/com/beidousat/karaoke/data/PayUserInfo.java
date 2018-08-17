package com.beidousat.karaoke.data;

import com.beidousat.karaoke.model.User;
import com.beidousat.libbns.util.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;

/**
 * author: Hanson
 * date:   2017/4/14
 * describe:
 */
public class PayUserInfo extends Observable {
    private List<User> mUsers;

    private static PayUserInfo ourInstance = new PayUserInfo();

    public static PayUserInfo getInstance() {
        return ourInstance;
    }

    private PayUserInfo() {
        mUsers = new ArrayList<>();
    }

    public void addUser(User user) {
        if (!User.isEmpty(user)) {
            if (mUsers.contains(user)) {
                mUsers.remove(user);
            }
            mUsers.add(0, user);
            notifyUserObservers();
        }
        Logger.d("PayUserInfo", "当前用户="+mUsers.size());
    }

    public void removeUser(User user) {
        if (User.isEmpty(user)) return;

        for (User item : mUsers) {
            if (item.equals(user)) {
                mUsers.remove(user);
                notifyUserObservers();
                break;
            }
        }
    }

    public void removeAllUser() {
        mUsers.clear();
        notifyUserObservers();
    }

    public List<User> getUsers() {
        return mUsers;
    }

    private void notifyUserObservers() {
        setChanged();
        notifyObservers();
    }
}
