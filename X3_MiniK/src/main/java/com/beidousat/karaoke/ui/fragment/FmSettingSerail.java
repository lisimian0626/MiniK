package com.beidousat.karaoke.ui.fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.beidousat.karaoke.R;
import com.beidousat.karaoke.data.PrefData;
import com.beidousat.karaoke.ui.dlg.PromptDialog;
import com.beidousat.karaoke.widget.EditTextEx;
import com.beidousat.libbns.evenbus.EventBusId;
import com.beidousat.libbns.evenbus.EventBusUtil;
import com.beidousat.libbns.util.DensityUtil;
import com.beidousat.libbns.util.ListUtil;
import com.beidousat.libbns.util.Logger;
import com.beidousat.libwidget.recycler.VerticalDividerItemDecoration;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by J Wong on 2017/5/12.
 */

public class FmSettingSerail extends BaseFragment implements View.OnClickListener, View.OnTouchListener {

    private EditTextEx mEtBaudrate, mEtMicDown, mEtMicUp, mEtReverbDown, mEtReverbUp, mEtReset,mEtReverbVol,mEtMicVol;
    private RecyclerView mRvNum, mRvLine1, mRvLine2, mRvLine3;
    private AdapterKeyboard mAdtLine1, mAdtLine2, mAdtLine3, mAdtmRvNum;
    private EditTextEx mEtFocus;
    private int mPreBaudrate;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.fm_setting_serial, null);
        mRootView.findViewById(R.id.iv_back).setOnClickListener(this);
        mRootView.findViewById(R.id.tv_key_del).setOnClickListener(this);

        mEtBaudrate = (EditTextEx) mRootView.findViewById(R.id.et_baudrate);
        mEtBaudrate.setOnTouchListener(this);
        mEtMicDown = (EditTextEx) mRootView.findViewById(R.id.et_mic_down);
        mEtMicDown.setOnTouchListener(this);
        mEtMicUp = (EditTextEx) mRootView.findViewById(R.id.et_mic_up);
        mEtMicUp.setOnTouchListener(this);

        mEtReverbUp = (EditTextEx) mRootView.findViewById(R.id.et_reverb_up);
        mEtReverbUp.setOnTouchListener(this);
        mEtReverbDown = (EditTextEx) mRootView.findViewById(R.id.et_reverb_down);
        mEtReverbDown.setOnTouchListener(this);


        mEtReverbVol = (EditTextEx) mRootView.findViewById(R.id.et_reverb_vol);//读取值混响的值
        mEtReverbVol.setOnTouchListener(this);
        mEtMicVol = (EditTextEx) mRootView.findViewById(R.id.et_mic_vol);//读取值麦克风的值
        mEtMicVol.setOnTouchListener(this);

        mEtReset = (EditTextEx) mRootView.findViewById(R.id.et_reset);
        mEtReset.setOnTouchListener(this);

        mRvNum = (RecyclerView) mRootView.findViewById(R.id.rv_keyboard_num);
        mRvLine1 = (RecyclerView) mRootView.findViewById(R.id.rv_keyboard_line1);
        mRvLine2 = (RecyclerView) mRootView.findViewById(R.id.rv_keyboard_line2);
        mRvLine3 = (RecyclerView) mRootView.findViewById(R.id.rv_keyboard_line3);
        initKeyboard();

        mPreBaudrate = PrefData.getSerilBaudrate(getContext().getApplicationContext());
        mEtBaudrate.setText(String.valueOf(mPreBaudrate));

        mEtMicUp.setText(PrefData.getSerilMicUp(getContext().getApplicationContext()));
        mEtMicDown.setText(PrefData.getSerilMicDown(getContext().getApplicationContext()));

        mEtReverbDown.setText(PrefData.getSerilReverbDown(getContext().getApplicationContext()));
        mEtReverbUp.setText(PrefData.getSerilReverbUp(getContext().getApplicationContext()));

        mEtReset.setText(PrefData.getSerilReset(getContext().getApplicationContext()));

        onTouch(mEtBaudrate, null);


        return mRootView;
    }

    private void initKeyboard() {
        int dividerWidth = DensityUtil.dip2px(getContext(), 2);
        VerticalDividerItemDecoration verDivider = new VerticalDividerItemDecoration.Builder(getContext())
                .color(Color.TRANSPARENT).size(dividerWidth).margin(dividerWidth, dividerWidth)
                .build();

        LinearLayoutManager layoutManagerNum = new LinearLayoutManager(getContext());
        layoutManagerNum.setOrientation(LinearLayoutManager.HORIZONTAL);
        mRvNum.setLayoutManager(layoutManagerNum);
        mRvNum.addItemDecoration(verDivider);
        mAdtmRvNum = new AdapterKeyboard();
        mRvNum.setAdapter(mAdtmRvNum);
        mAdtmRvNum.setData(ListUtil.array2List(getResources().getStringArray(R.array.keyboard_numbers_serial)));


        LinearLayoutManager layoutManager3 = new LinearLayoutManager(getContext());
        layoutManager3.setOrientation(LinearLayoutManager.HORIZONTAL);
        mRvLine3.setLayoutManager(layoutManager3);
        mRvLine3.addItemDecoration(verDivider);
        mAdtLine3 = new AdapterKeyboard();
        mRvLine3.setAdapter(mAdtLine3);
        mAdtLine3.setData(ListUtil.array2List(getResources().getStringArray(R.array.keyboard_keys_line3)));


        LinearLayoutManager layoutManager2 = new LinearLayoutManager(getContext());
        layoutManager2.setOrientation(LinearLayoutManager.HORIZONTAL);
        mRvLine2.setLayoutManager(layoutManager2);
        mRvLine2.addItemDecoration(verDivider);
        mAdtLine2 = new AdapterKeyboard();
        mRvLine2.setAdapter(mAdtLine2);
        mAdtLine2.setData(ListUtil.array2List(getResources().getStringArray(R.array.keyboard_keys_line2)));


        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        mRvLine1.setLayoutManager(layoutManager);
        mRvLine1.addItemDecoration(verDivider);
        mAdtLine1 = new AdapterKeyboard();
        mRvLine1.setAdapter(mAdtLine1);
        mAdtLine1.setData(ListUtil.array2List(getResources().getStringArray(R.array.keyboard_keys_line1)));


    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        mEtBaudrate.setSelected(false);
        mEtMicDown.setSelected(false);
        mEtMicUp.setSelected(false);
        mEtReverbDown.setSelected(false);
        mEtReverbUp.setSelected(false);
        mEtReset.setSelected(false);

        mEtReverbVol.setSelected(false);
        mEtMicVol.setSelected(false);

        mEtFocus = (EditTextEx) view;
        mEtFocus.setSelected(true);
        return false;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                try {//波特率
                    int baudrate = Integer.valueOf(mEtBaudrate.getText().toString());
                    PrefData.setSerilBaudrate(getContext().getApplicationContext(), baudrate);
                } catch (Exception e) {
                    e.printStackTrace();
                    PromptDialog promptDialog = new PromptDialog(getActivity());
                    promptDialog.setMessage(R.string.baudrate_format);
                    promptDialog.show();
                    return;
                }
                try {//麦克风+
                    PrefData.setSerilMicUp(getContext().getApplicationContext(), mEtMicUp.getText().toString());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                try {//麦克风-
                    PrefData.setSerilMicDown(getContext().getApplicationContext(), mEtMicDown.getText().toString());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                try {//混响-
                    PrefData.setSerilReverbDown(getContext().getApplicationContext(), mEtReverbDown.getText().toString());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                try {//混响+
                    PrefData.setSerilReverbUp(getContext().getApplicationContext(), mEtReverbUp.getText().toString());
                } catch (Exception e) {
                    e.printStackTrace();
                }

                try {//重置
                    PrefData.setSerilReset(getContext().getApplicationContext(), mEtReset.getText().toString());
                } catch (Exception e) {
                    e.printStackTrace();
                }

                if (mPreBaudrate != PrefData.getSerilBaudrate(getContext().getApplicationContext())) {
                    PromptDialog promptDialog = new PromptDialog(getActivity());
                    promptDialog.setMessage(R.string.change_baudrate_tip);
                    promptDialog.setPositiveButton(getString(R.string.reboot), new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            exitApp();
                        }
                    });
                    promptDialog.show();
                } else {
                    EventBusUtil.postSticky(EventBusId.id.BACK_FRAGMENT, "");
                }
                break;
            case R.id.tv_key_del:
                try {
                    String text = mEtFocus.getText().toString();
                    if (!TextUtils.isEmpty(text)) {
                        String txt = text.substring(0, text.length() - 1);
                        mEtFocus.setText(txt);
                    }
                } catch (Exception e) {
                    Logger.e("WidgetKeyBoard", e.toString());
                }
                break;
        }
    }

    private void exitApp() {
        EventBusUtil.postSticky(EventBusId.id.MAIN_PLAYER_STOP, null);
        getActivity().finish();
        android.os.Process.killProcess(android.os.Process.myPid());//获取PID
        System.exit(0);//直接结束程序
    }

    public class AdapterKeyboard extends BaseAdapter {

        public AdapterKeyboard() {
            super();
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View view = mInflater.inflate(R.layout.list_item_keyboard_key, viewGroup, false);
            ViewHolder viewHolder = new ViewHolder(view);
            viewHolder.tvKey = (TextView) view.findViewById(android.R.id.button1);
            return viewHolder;
        }


        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            final String keyText = mData.get(position);
            holder.tvKey.setText(keyText);
            holder.tvKey.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        if (!TextUtils.isEmpty(keyText)) {
                            mEtFocus.setText(mEtFocus.getText() + keyText);
                        }
                    } catch (Exception e) {
                        Logger.e("WidgetKeyBoard", e.toString());
                    }
                }
            });
        }
    }

    public class BaseAdapter extends RecyclerView.Adapter<ViewHolder> {

        LayoutInflater mInflater;
        List<String> mData = new ArrayList<String>();

        public BaseAdapter() {
            mInflater = LayoutInflater.from(getContext());
        }

        public void setData(List<String> data) {
            this.mData = data;
        }

        public String getItem(int position) {
            if (mData == null) {
                return null;
            } else {
                return mData.get(position);
            }
        }

        @Override
        public int getItemCount() {
            return mData.size();
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            return null;
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView tvKey;

        public ViewHolder(View view) {
            super(view);
        }
    }

}
