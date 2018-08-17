package com.beidousat.karaoke.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.beidousat.karaoke.R;
import com.beidousat.karaoke.data.PayUserInfo;
import com.beidousat.karaoke.model.User;
import com.beidousat.libbns.util.ServerFileUtil;
import com.bumptech.glide.Glide;

import java.util.Observable;
import java.util.Observer;

/**
 * author: Hanson
 * date:   2017/4/14
 * describe:
 */

public class UserInfoLayout extends LinearLayout implements Observer {
    TextView mTvUser;
    ImageView mIvAvatar;

    public UserInfoLayout(Context context) {
        super(context);
    }

    private void init() {
        mIvAvatar = (ImageView) findViewById(R.id.iv_avatar);
        mTvUser = (TextView) findViewById(R.id.tv_user);
    }

    public UserInfoLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public UserInfoLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();

        init();
    }

    @Override
    public void update(Observable o, Object arg) {
        if (isAttachedToWindow()) {
            User user = null;
            try {
                int size = PayUserInfo.getInstance().getUsers().size();
                if (size > 0) {
                    user = PayUserInfo.getInstance().getUsers().get(0);
                }

                if (!User.isEmpty(user)) {
                    setVisibility(VISIBLE);
                    if (mTvUser != null) {
                        mTvUser.setText(user.getNickName());
                    }
                } else {
                    setVisibility(GONE);
                }
                Log.d("BoughtMeal", user == null? "清除用户信息" : user.toString());
                String avatar = user == null ? "" : user.getAvatar();
                Glide.with(getContext()).load(ServerFileUtil.getImageUrl(avatar))
                        .error(R.drawable.ic_avatar).into(mIvAvatar);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
