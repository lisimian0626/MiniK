package com.beidousat.karaoke.ui.dlg;

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
import com.beidousat.karaoke.data.KBoxInfo;
import com.beidousat.karaoke.data.PrefData;
import com.beidousat.karaoke.ui.Main;
import com.beidousat.karaoke.model.RoomInfo;
import com.beidousat.karaoke.player.ChooseSongs;
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

public class FmDlgRoom extends FmBaseDialog implements View.OnTouchListener, View.OnClickListener,StoreHttpRequestListener {
    private ListView mLvRoom;
    private RecyclerView mLvItems;
    private EditTextEx mEt_phone;
    private EditTextEx mEtFocus;
    private Button mButton;
    private RoomListAdapter adapter;
    private String Kbox_sn;
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
        mRootView = inflater.inflate(R.layout.dlg_room, container, false);
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    void initData() {

    }

    @Override
    void initView() {
        mLvItems = (RecyclerView) findViewById(R.id.rv_items);
        mLvRoom=(ListView)findViewById(R.id.room_list);
        mEt_phone = (EditTextEx) findViewById(android.R.id.edit);
        mButton=(Button)findViewById(R.id.dlg_room_btn_yes);
    }

    @Override
    void setListener() {
        findViewById(R.id.iv_close).setOnClickListener(this);
        mEt_phone.setOnTouchListener(this);
        mButton.setOnClickListener(this);
        init();
        mEtFocus = mEt_phone;
        mEtFocus.setSelected(true);
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        return false;
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


    }

    private void setDetail(List<RoomInfo> roomInfoList){
        final  List<RoomInfo> roomInfos=new ArrayList<>();
        for (RoomInfo info:roomInfoList){
            if(info.getIs_online()!=1){
                roomInfos.add(info);
            }
        }
        if(roomInfos!=null&&roomInfos.size()>0){
            roomInfos.get(0).setIscheck(true);
            Kbox_sn=roomInfos.get(0).getId();
        }
        mLvItems.setVisibility(View.GONE);
        mEt_phone.setVisibility(View.GONE);
        mButton.setVisibility(View.VISIBLE);
        mLvRoom.setVisibility(View.VISIBLE);
        adapter=new RoomListAdapter(roomInfos);
        mLvRoom.setAdapter(adapter);
        mLvRoom.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                int currentNum = -1;
                for(RoomInfo info : roomInfos){ //遍历list集合中的数据
                    info.setIscheck(false);//全部设为未选中
                }
                if(currentNum == -1){ //选中
                    roomInfos.get(i).setIscheck(true);
                    currentNum = i;
                }else if(currentNum == i){ //同一个item选中变未选中
                    for(RoomInfo info : roomInfos){
                        info.setIscheck(false);
                    }
                    currentNum = -1;
                }else if(currentNum != i){ //不是同一个item选中当前的，去除上一个选中的
                    for(RoomInfo info : roomInfos){
                        info.setIscheck(false);
                    }
                    roomInfos.get(i).setIscheck(true);
                    currentNum = i;
                }
                Kbox_sn=roomInfos.get(i).getId();
//                Toast.makeText(parent.getContext(),datas.get(position).getTitle(),Toast.LENGTH_SHORT).show();
                adapter.notifyDataSetChanged();
            }
        });
    }
    private void requestDetail(String phone_num) {
        StoreHttpRequest request = new StoreHttpRequest(KBoxInfo.STORE_WEB, RequestMethod.ROOM_LIST);
        request.setStoreHttpRequestListener(this);
        request.addParam("mobile_phone", phone_num);
        request.setConvert2Token(new TypeToken<List<RoomInfo>>(){});
        request.post();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.dlg_room_btn_yes:
                new QueryKboxHelper(getContext().getApplicationContext(), null, new QueryKboxHelper.QueryKboxFeedback() {
                    @Override
                    public void onStart() {

                    }

                    @Override
                    public void onFeedback(boolean suceed, String msg, Object obj) {
                          if(suceed){
                              ChooseSongs.getInstance(getContext().getApplicationContext()).cleanChoose();
//                              BoughtMeal.getInstance().clearMealInfo();
//                              BoughtMeal.getInstance().clearMealInfoSharePreference();
//                              BoughtMeal.getInstance().notifyMealObervers();
//                              PrefData.setAuth(getContext().getApplicationContext(), false);
//                              KBoxStatusInfo.getInstance().setKBoxStatus(null);
                              PrefData.setAuth(getContext().getApplicationContext(), false);
                              BoughtMeal.getInstance().clearMealInfo();
                              BoughtMeal.getInstance().clearMealInfoSharePreference();
                              PrefData.setRoomCode(Main.mMainActivity,Kbox_sn);
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

                          }else{
                              if(getContext()!=null){
                                  ToastUtils.toast(getContext(),msg);
                              }
                          }
                    }
                }).getBoxInfo(Kbox_sn, DeviceUtil.getCupChipID());
//                mAttached.dismiss();
                break;
        }
    }

    public class RoomListAdapter extends BaseAdapter {
        private LayoutInflater inflater;
        private List<RoomInfo> roomInfoList;

        public RoomListAdapter(List<RoomInfo> roomInfoList) {
            this.roomInfoList = roomInfoList;
            inflater=LayoutInflater.from(getContext());
        }

        @Override
        public int getCount() {
            return roomInfoList!=null?roomInfoList.size():0;
        }

        @Override
        public Object getItem(int i) {
            return roomInfoList.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            ViewHolder holder = null;
            if (view == null) {
                holder = new ViewHolder();
                view=inflater.inflate(R.layout.listroom_item,null);
                holder.tv_room_num=(TextView) view.findViewById(R.id.roomlist_tv_room_num);
                holder.tv_lable=(TextView)view.findViewById(R.id.roomlist_tv_lable);
                holder.tv_room_type=(TextView)view.findViewById(R.id.roomlist_tv_type);
                holder.iv_select_img=(ImageView)view.findViewById(R.id.roomlist_iv_check);
                view.setTag(holder);
            } else {
                holder = (ViewHolder) view.getTag();
            }
            final RoomInfo roomInfo=roomInfoList.get(i);
                holder.tv_room_num.setText(roomInfo.getId());
                holder.tv_room_type.setText(roomInfo.getStatus_txt());
                holder.tv_lable.setText(roomInfo.getLabel());
                if(roomInfo.isIscheck()){
                    holder.iv_select_img.setVisibility(View.VISIBLE);
                }else {
                    holder.iv_select_img.setVisibility(View.GONE);
                }
            return view;
        }
    }
    class ViewHolder{
        TextView tv_room_num,tv_lable,tv_room_type;
        ImageView iv_select_img;
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
        public AdapterNumber.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View view = mInflater.inflate(R.layout.list_item_room_keyboard, viewGroup, false);
            AdapterNumber.ViewHolder viewHolder = new AdapterNumber.ViewHolder(view);
            viewHolder.tvKey = (TextView) view.findViewById(android.R.id.button1);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(final AdapterNumber.ViewHolder holder, final int position) {
            final String keyText = mData.get(position);
            holder.tvKey.setText(keyText);
            holder.tvKey.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        if (position == getItemCount() - 1) {
                            String str=mEt_phone.getText().toString().trim();
                            requestDetail(str);
                        } else {
                            if (!TextUtils.isEmpty(keyText)) {
                                if (keyText.equals(getActivity().getString(R.string.delete))) {
                                    String text = mEtFocus.getText().toString();
                                    if (!TextUtils.isEmpty(text)) {
                                        String txt = text.substring(0, text.length() - 1);
                                        mEtFocus.setText(txt);
                                    }
                                } else {
                                    mEtFocus.setText(mEtFocus.getText() + keyText);
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
    @Override
    public void onStoreSuccess(String method, Object object) {
        List<RoomInfo> roomInfoList= (List<RoomInfo>) object;
        setDetail(roomInfoList);
    }

    @Override
    public void onStoreFailed(String method, String error) {
        Toast.makeText(getActivity(),error,Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onStoreStart(String method) {

    }
    private void exitApp() {
        EventBusUtil.postSticky(EventBusId.id.MAIN_PLAYER_STOP, null);
        android.os.Process.killProcess(android.os.Process.myPid());//获取PID
        System.exit(0);//直接结束程序
    }
}
