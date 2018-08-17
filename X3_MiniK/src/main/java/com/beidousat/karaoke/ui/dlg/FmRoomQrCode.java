package com.beidousat.karaoke.ui.dlg;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.beidousat.karaoke.R;

/**
 * author: Hanson
 * date:   2017/3/30
 * describe:
 */

public class FmRoomQrCode extends FmBaseDialog {
    TextView mRoomName;
    ImageView mQrCode;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.fm_room_qrcode, container, false);

        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    void initData() {
        initRequest("scanRoomQrCode");
    }

    @Override
    void initView() {
        mRoomName = findViewById(R.id.room_name);
        mQrCode = findViewById(R.id.qrcode);
    }

    @Override
    void setListener() {

    }

    @Override
    public void onSuccess(String method, Object object) {
        super.onSuccess(method, object);
    }

    @Override
    public void onFailed(String method, String error) {
        super.onFailed(method, error);
    }
}
