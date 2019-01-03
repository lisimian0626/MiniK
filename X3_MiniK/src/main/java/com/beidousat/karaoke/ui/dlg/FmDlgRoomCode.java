package com.beidousat.karaoke.ui.dlg;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.beidousat.karaoke.R;
import com.beidousat.karaoke.biz.QueryKboxHelper;
import com.beidousat.karaoke.data.BoughtMeal;
import com.beidousat.karaoke.data.KBoxStatusInfo;
import com.beidousat.karaoke.data.PrefData;
import com.beidousat.libbns.model.ServerConfigData;
import com.beidousat.karaoke.model.RoomInfo;
import com.beidousat.karaoke.player.ChooseSongs;
import com.beidousat.karaoke.ui.Main;
import com.beidousat.karaoke.ui.OnDlgListener;
import com.beidousat.karaoke.util.ToastUtils;
import com.beidousat.karaoke.widget.EditTextEx;
import com.beidousat.libbns.evenbus.EventBusId;
import com.beidousat.libbns.evenbus.EventBusUtil;
import com.beidousat.libbns.net.request.RequestMethod;
import com.beidousat.libbns.net.request.StoreHttpRequest;
import com.beidousat.libbns.net.request.StoreHttpRequestListener;
import com.beidousat.libbns.util.DeviceUtil;
import com.beidousat.libbns.util.ListUtil;
import com.beidousat.libbns.util.Logger;
import com.beidousat.libwidget.recycler.HorizontalDividerItemDecoration;
import com.beidousat.libwidget.recycler.VerticalDividerItemDecoration;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2018/4/17.
 */

public class FmDlgRoomCode extends FmBaseDialog{
    private TextView mTvRoomCode;
    private RecyclerView mLvItems;
    private EditTextEx mEtPwd;
    private OnDlgListener onDlgListener;

    public OnDlgListener getOnDlgListener() {
        return onDlgListener;
    }

    public void setOnDlgListener(OnDlgListener onDlgListener) {
        this.onDlgListener = onDlgListener;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.dlg_room_code, container, false);
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    void initData() {

    }

    @Override
    void initView() {
        mLvItems = (RecyclerView) findViewById(R.id.rv_items);
        mEtPwd = (EditTextEx) findViewById(android.R.id.input);
        mTvRoomCode = (TextView) findViewById(R.id.tv_room_no);
        init();
    }

    @Override
    void setListener() {

    }

    private void init() {

        HorizontalDividerItemDecoration horDivider = new HorizontalDividerItemDecoration.Builder(getActivity().getApplicationContext())
                .color(Color.TRANSPARENT).size(12).margin(12, 12)
                .build();

        VerticalDividerItemDecoration verDivider = new VerticalDividerItemDecoration.Builder(getActivity().getApplicationContext())
                .color(Color.TRANSPARENT).size(12).margin(12, 12)
                .build();

        mLvItems.setLayoutManager(new GridLayoutManager(getActivity().getApplicationContext(), 3));
        mLvItems.addItemDecoration(verDivider);
        mLvItems.addItemDecoration(horDivider);

        AdapterNumber adapter = new AdapterNumber();
        mLvItems.setAdapter(adapter);

        adapter.setData(ListUtil.array2List(getActivity().getResources().getStringArray(R.array.mng_pwd_texts)));


        String roomCode = PrefData.getRoomCode(getContext().getApplicationContext());
        if (TextUtils.isEmpty(roomCode)) {
            mTvRoomCode.setText(getContext().getString(R.string.room_code_x, "未设置"));
        } else {
            mTvRoomCode.setText(getContext().getString(R.string.room_code_x, roomCode));
        }
    }


    public class AdapterNumber extends RecyclerView.Adapter<AdapterNumber.ViewHolder> {

        private LayoutInflater mInflater;
        private List<String> mData = new ArrayList<String>();

        public AdapterNumber() {
            mInflater = LayoutInflater.from(getActivity());
        }

        public void setData(List<String> data) {
            this.mData = data;
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            private TextView tvKey;

            public ViewHolder(View view) {
                super(view);
            }
        }

        @Override
        public int getItemCount() {
            return mData.size();
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View view = mInflater.inflate(R.layout.list_item_room_keyboard, viewGroup, false);
            ViewHolder viewHolder = new ViewHolder(view);
            viewHolder.tvKey = (TextView) view.findViewById(android.R.id.button1);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, final int position) {
            final String keyText = mData.get(position);
            holder.tvKey.setText(keyText);
            holder.tvKey.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        if (position == getItemCount() - 1) {
                            String pwd = mEtPwd.getText().toString().trim();
                            if (!TextUtils.isEmpty(pwd)) {
                                final String kbox_sn="B" + pwd;
                                new QueryKboxHelper(getContext().getApplicationContext(), null, new QueryKboxHelper.QueryKboxFeedback() {
                                    @Override
                                    public void onStart() {

                                    }

                                    @Override
                                    public void onFeedback(boolean suceed, String msg, Object obj) {
                                        if(suceed){
                                            if(getContext()==null){
                                                return;
                                            }
                                            ChooseSongs.getInstance(getContext().getApplicationContext()).cleanChoose();
                                            BoughtMeal.getInstance().clearMealInfo();
                                            BoughtMeal.getInstance().clearMealInfoSharePreference();
//                                            BoughtMeal.getInstance().notifyMealObervers();
                                            PrefData.setAuth(getContext().getApplicationContext(), false);
//                                            KBoxStatusInfo.getInstance().setKBoxStatus(null);
                                            PrefData.setRoomCode(getContext().getApplicationContext(),kbox_sn);
                                            if(onDlgListener!=null){
                                                onDlgListener.onDissmiss();
                                            }
                                            PromptDialog promptDialog = new PromptDialog(getActivity());
                                            promptDialog.setMessage(R.string.change_room_tip);
                                            promptDialog.setPositiveButton(getString(R.string.reboot), new View.OnClickListener() {
                                                @Override
                                                public void onClick(View view) {
                                                    exitApp();
                                                }
                                            });
                                            promptDialog.show();
//                                            ToastUtils.toast(getContext().getApplicationContext(),getString(R.string.room_num_success));
                                        }else{
                                            if(getContext()!=null){
                                                ToastUtils.toast(getContext(),msg);
                                            }
                                        }
                                    }
                                }).getBoxInfo(kbox_sn, DeviceUtil.getCupChipID());
                            } else {
                                if(getContext()!=null) {
                                    ToastUtils.toast(getContext(), getString(R.string.roomcode_input));
                                }
                            }
                        } else {
                            if (!TextUtils.isEmpty(keyText)) {
                                if (keyText.equals(getActivity().getString(R.string.delete))) {
                                    String text = mEtPwd.getText().toString();
                                    if (!TextUtils.isEmpty(text)) {
                                        String txt = text.substring(0, text.length() - 1);
                                        mEtPwd.setText(txt);
                                    }
                                } else {
                                    mEtPwd.setText(mEtPwd.getText() + keyText);
                                }
                            }
                        }
                    } catch (Exception e) {
                        Logger.e(getClass().getSimpleName(), e.toString());
                    }
                }
            });
        }
    }
    private void exitApp() {
        EventBusUtil.postSticky(EventBusId.id.MAIN_PLAYER_STOP, null);
        android.os.Process.killProcess(android.os.Process.myPid());//获取PID
        System.exit(0);//直接结束程序
    }
}
