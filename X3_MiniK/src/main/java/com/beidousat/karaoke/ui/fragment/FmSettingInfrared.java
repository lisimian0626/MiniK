package com.beidousat.karaoke.ui.fragment;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.beidousat.karaoke.R;
import com.beidousat.karaoke.data.PrefData;
import com.beidousat.karaoke.ui.dlg.DialogFactory;
import com.beidousat.karaoke.ui.dlg.DlgInfraredLoading;
import com.beidousat.karaoke.ui.dlg.PromptDialog;
import com.beidousat.karaoke.util.SerialController;
import com.beidousat.karaoke.util.ToastUtils;
import com.beidousat.karaoke.widget.EditTextEx;
import com.beidousat.libbns.evenbus.BusEvent;
import com.beidousat.libbns.evenbus.EventBusId;
import com.beidousat.libbns.evenbus.EventBusUtil;
import com.beidousat.libbns.util.DensityUtil;
import com.beidousat.libbns.util.ListUtil;
import com.beidousat.libbns.util.Logger;
import com.beidousat.libwidget.recycler.VerticalDividerItemDecoration;

import java.util.ArrayList;
import java.util.List;

import de.greenrobot.event.EventBus;

/**
 * Created by J Wong on 2017/5/12.
 */

public class FmSettingInfrared extends BaseFragment implements View.OnClickListener{
    private final static String TAG = "SerialController";
    private TextView mOpen, mclose, mTemp21,mTemp22,mTemp23,mTemp24,mTemp25,mTemp26,mTemp27,mTemp28,mWindAuto,mWindHight,mWindMid,mWindLow;
    private View currentview;
    private List<Button> list_button=new ArrayList<>();
    private DlgInfraredLoading mDlgLoading;
    private com.beidousat.karaoke.ui.dlg.AlertDialog mConfirmDlg;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.fm_setting_infrared2, null);
        mRootView.findViewById(R.id.iv_back).setOnClickListener(this);
        mRootView.findViewById(R.id.infrared_tv_reset).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showConfirmDialog();
            }
        });
        mOpen= (TextView) mRootView.findViewById(R.id.infrared_tv_open);
        mclose=(TextView) mRootView.findViewById(R.id.infrared_tv_close);
        mTemp21=(TextView)mRootView.findViewById(R.id.infrared_tv_21) ;
        mTemp22=(TextView)mRootView.findViewById(R.id.infrared_tv_22) ;
        mTemp23=(TextView)mRootView.findViewById(R.id.infrared_tv_23) ;
        mTemp24=(TextView)mRootView.findViewById(R.id.infrared_tv_24) ;
        mTemp25=(TextView)mRootView.findViewById(R.id.infrared_tv_25) ;
        mTemp26=(TextView)mRootView.findViewById(R.id.infrared_tv_26) ;
        mTemp27=(TextView)mRootView.findViewById(R.id.infrared_tv_27) ;
        mTemp28=(TextView)mRootView.findViewById(R.id.infrared_tv_28) ;
        mWindAuto=(TextView)mRootView.findViewById(R.id.infrared_tv_auto) ;
        mWindHight=(TextView)mRootView.findViewById(R.id.infrared_tv_high) ;
        mWindMid=(TextView)mRootView.findViewById(R.id.infrared_tv_mid) ;
        mWindLow=(TextView)mRootView.findViewById(R.id.infrared_tv_low) ;
        mOpen.setOnClickListener(this);
        mclose.setOnClickListener(this);
        mTemp21.setOnClickListener(this);
        mTemp22.setOnClickListener(this);
        mTemp23.setOnClickListener(this);
        mTemp24.setOnClickListener(this);
        mTemp25.setOnClickListener(this);
        mTemp26.setOnClickListener(this);
        mTemp27.setOnClickListener(this);
        mTemp28.setOnClickListener(this);
        mWindAuto.setOnClickListener(this);
        mWindHight.setOnClickListener(this);
        mWindMid.setOnClickListener(this);
        mWindLow.setOnClickListener(this);
//        list_button.add(mOpen);
//        list_button.add(mclose);
//        list_button.add(mTemp21);
//        list_button.add(mTemp22);
//        list_button.add(mTemp23);
//        list_button.add(mTemp24);
//        list_button.add(mTemp25);
//        list_button.add(mTemp26);
//        list_button.add(mTemp27);
//        list_button.add(mTemp28);
//        list_button.add(mTemp29);
//        list_button.add(mWindAuto);
//        list_button.add(mWindHight);
//        list_button.add(mWindMid);
//        list_button.add(mWindLow);
        init();
        return mRootView;
    }

    private void init() {
        if(PrefData.Is_Open_Set(getContext())){
            mOpen.setBackgroundResource(R.drawable.bg_infrared_orange);
        }else{
            mOpen.setBackgroundResource(R.drawable.bg_infrared_gray);
        }
        if(PrefData.Is_Close_Set(getContext())){
            mclose.setBackgroundResource(R.drawable.bg_infrared_orange);
        }else{
            mclose.setBackgroundResource(R.drawable.bg_infrared_gray);
        }
        if(PrefData.Is_TEMP21_Set(getContext())){
            mTemp21.setBackgroundResource(R.drawable.bg_infrared_orange);
        }else{
            mTemp21.setBackgroundResource(R.drawable.bg_infrared_gray);
        }
        if(PrefData.Is_TEMP22_Set(getContext())){
            mTemp22.setBackgroundResource(R.drawable.bg_infrared_orange);
        }else{
            mTemp22.setBackgroundResource(R.drawable.bg_infrared_gray);
        }
        if(PrefData.Is_TEMP23_Set(getContext())){
            mTemp23.setBackgroundResource(R.drawable.bg_infrared_orange);
        }else{
            mTemp23.setBackgroundResource(R.drawable.bg_infrared_gray);
        }
        if(PrefData.Is_TEMP24_Set(getContext())){
            mTemp24.setBackgroundResource(R.drawable.bg_infrared_orange);
        }else{
            mTemp24.setBackgroundResource(R.drawable.bg_infrared_gray);
        }
        if(PrefData.Is_TEMP25_Set(getContext())){
            mTemp25.setBackgroundResource(R.drawable.bg_infrared_orange);
        }else{
            mTemp25.setBackgroundResource(R.drawable.bg_infrared_gray);
        }
        if(PrefData.Is_TEMP26_Set(getContext())){
            mTemp26.setBackgroundResource(R.drawable.bg_infrared_orange);
        }else{
            mTemp26.setBackgroundResource(R.drawable.bg_infrared_gray);
        }
        if(PrefData.Is_TEMP27_Set(getContext())){
            mTemp27.setBackgroundResource(R.drawable.bg_infrared_orange);
        }else{
            mTemp27.setBackgroundResource(R.drawable.bg_infrared_gray);
        }
        if(PrefData.Is_TEMP28_Set(getContext())){
            mTemp28.setBackgroundResource(R.drawable.bg_infrared_orange);
        }else{
            mTemp28.setBackgroundResource(R.drawable.bg_infrared_gray);
        }
        if(PrefData.Is_WINDAuto_Set(getContext())){
            mWindAuto.setBackgroundResource(R.drawable.bg_infrared_orange);
        }else{
            mWindAuto.setBackgroundResource(R.drawable.bg_infrared_gray);
        }
        if(PrefData.Is_WINDHIGH_Set(getContext())){
            mWindHight.setBackgroundResource(R.drawable.bg_infrared_orange);
        }else{
            mWindHight.setBackgroundResource(R.drawable.bg_infrared_gray);
        }
        if(PrefData.Is_WINDMID_Set(getContext())){
            mWindMid.setBackgroundResource(R.drawable.bg_infrared_orange);
        }else{
            mWindMid.setBackgroundResource(R.drawable.bg_infrared_gray);
        }
        if(PrefData.Is_WINDLOW_Set(getContext())){
            mWindLow.setBackgroundResource(R.drawable.bg_infrared_orange);
        }else{
            mWindLow.setBackgroundResource(R.drawable.bg_infrared_gray);
        }
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        EventBus.getDefault().register(this);
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }


    @Override
    public void onClick(View view) {
//        setButtonDisable();
        currentview=view;
        switch (view.getId()) {
            case R.id.iv_back:
                EventBusUtil.postSticky(EventBusId.id.BACK_FRAGMENT, "");
                break;
            case R.id.infrared_tv_open:
                study((byte)0x00,(byte)0x88);
                break;
            case R.id.infrared_tv_close:
                study((byte)0x01,(byte)0x89);
                break;
            case R.id.infrared_tv_21:
                study((byte)0x02,(byte)0x8A);
                break;
            case R.id.infrared_tv_22:
                study((byte)0x03,(byte)0x8B);
                break;
            case R.id.infrared_tv_23:
                study((byte)0x04,(byte)0x8C);
                break;
            case R.id.infrared_tv_24:
                study((byte)0x05,(byte)0x8D);
                break;
            case R.id.infrared_tv_25:
                study((byte)0x06,(byte)0x8E);
                break;
            case R.id.infrared_tv_26:
                study((byte)0x07,(byte)0x8F);
                break;
            case R.id.infrared_tv_27:
                study((byte)0x08,(byte)0x80);
                break;
            case R.id.infrared_tv_28:
                study((byte)0x09,(byte)0x81);
                break;
            case R.id.infrared_tv_auto:
                study((byte)0x11,(byte)0x99);
                break;
            case R.id.infrared_tv_high:
                study((byte)0x12,(byte)0x9A);
                break;
            case R.id.infrared_tv_mid:
                study((byte)0x13,(byte)0x9B);
                break;
            case R.id.infrared_tv_low:
                study((byte)0x14,(byte)0x9C);
                break;
        }
    }

    private void Infraredreset() {
        PrefData.setIs_Open_Set(getContext(),false);
        PrefData.setIs_Close_Set(getContext(),false);
        PrefData.setIs_TEMP21_Set(getContext(),false);
        PrefData.setIs_TEMP22_Set(getContext(),false);
        PrefData.setIs_TEMP23_Set(getContext(),false);
        PrefData.setIs_TEMP24_Set(getContext(),false);
        PrefData.setIs_TEMP25_Set(getContext(),false);
        PrefData.setIs_TEMP26_Set(getContext(),false);
        PrefData.setIs_TEMP27_Set(getContext(),false);
        PrefData.setIs_TEMP28_Set(getContext(),false);
        PrefData.setIs_WINDAuto_Set(getContext(),false);
        PrefData.setIs_WINDHIGH_Set(getContext(),false);
        PrefData.setIs_WINDMID_Set(getContext(),false);
        PrefData.setIs_WINDLOW_Set(getContext(),false);
        init();
    }

//    private void setButtonDisable() {
//        for (Button b:list_button){
//            b.setEnabled(false);
//        }
//    }
//
//    private void setButtonable() {
//        for (Button b:list_button){
//            b.setEnabled(true);
//        }
//    }
    public void onEventMainThread(BusEvent event) {
        switch (event.id) {
            case EventBusId.INFARAED.RECEIVE_CODE:
                Logger.i(TAG, "OnSerialReceive FmSettingInfrared:" + event.data + "");
                String str=(String) event.data;
                if(str.equals("E0")){
                     switch (currentview.getId()){
                         case R.id.infrared_tv_open:
                             mOpen.setBackgroundResource(R.drawable.bg_infrared_gray);
                             PrefData.setIs_Open_Set(getContext(),false);
                             break;
                         case R.id.infrared_tv_close:
                             mclose.setBackgroundResource(R.drawable.bg_infrared_gray);
                             PrefData.setIs_Close_Set(getContext(),false);
                             break;
                         case R.id.infrared_tv_21:
                             mTemp21.setBackgroundResource(R.drawable.bg_infrared_gray);
                             PrefData.setIs_TEMP21_Set(getContext(),false);
                             break;
                         case R.id.infrared_tv_22:
                             mTemp22.setBackgroundResource(R.drawable.bg_infrared_gray);
                             PrefData.setIs_TEMP22_Set(getContext(),false);
                             break;
                         case R.id.infrared_tv_23:
                             mTemp23.setBackgroundResource(R.drawable.bg_infrared_gray);
                             PrefData.setIs_TEMP23_Set(getContext(),false);
                             break;
                         case R.id.infrared_tv_24:
                             mTemp24.setBackgroundResource(R.drawable.bg_infrared_gray);
                             PrefData.setIs_TEMP24_Set(getContext(),false);
                             break;
                         case R.id.infrared_tv_25:
                             mTemp25.setBackgroundResource(R.drawable.bg_infrared_gray);
                             PrefData.setIs_TEMP25_Set(getContext(),false);
                             break;
                         case R.id.infrared_tv_26:
                             mTemp26.setBackgroundResource(R.drawable.bg_infrared_gray);
                             PrefData.setIs_TEMP26_Set(getContext(),false);
                             break;
                         case R.id.infrared_tv_27:
                             mTemp27.setBackgroundResource(R.drawable.bg_infrared_gray);
                             PrefData.setIs_TEMP27_Set(getContext(),false);
                             break;
                         case R.id.infrared_tv_28:
                             mTemp28.setBackgroundResource(R.drawable.bg_infrared_gray);
                             PrefData.setIs_TEMP28_Set(getContext(),false);
                             break;
                         case R.id.infrared_tv_auto:
                             mWindAuto.setBackgroundResource(R.drawable.bg_infrared_gray);
                             PrefData.setIs_WINDAuto_Set(getContext(),false);
                             break;
                         case R.id.infrared_tv_high:
                             mWindHight.setBackgroundResource(R.drawable.bg_infrared_gray);
                             PrefData.setIs_WINDHIGH_Set(getContext(),false);
                             break;
                         case R.id.infrared_tv_mid:
                             mWindMid.setBackgroundResource(R.drawable.bg_infrared_gray);
                             PrefData.setIs_WINDMID_Set(getContext(),false);
                             break;
                         case R.id.infrared_tv_low:
                             mWindLow.setBackgroundResource(R.drawable.bg_infrared_gray);
                             PrefData.setIs_WINDLOW_Set(getContext(),false);
                             break;
                     }
                     if(getContext()!=null){
                         ToastUtils.toast(getContext(),getString(R.string.infrared_study_fail));
                     }
                }else{
                    switch (currentview.getId()){
                        case R.id.infrared_tv_open:
                            mOpen.setBackgroundResource(R.drawable.bg_infrared_orange);
                            PrefData.setIs_Open_Set(getContext(),true);
                            break;
                        case R.id.infrared_tv_close:
                            mclose.setBackgroundResource(R.drawable.bg_infrared_orange);
                            PrefData.setIs_Close_Set(getContext(),true);
                            break;
                        case R.id.infrared_tv_21:
                            mTemp21.setBackgroundResource(R.drawable.bg_infrared_orange);
                            PrefData.setIs_TEMP21_Set(getContext(),true);
                            break;
                        case R.id.infrared_tv_22:
                            mTemp22.setBackgroundResource(R.drawable.bg_infrared_orange);
                            PrefData.setIs_TEMP22_Set(getContext(),true);
                            break;
                        case R.id.infrared_tv_23:
                            mTemp23.setBackgroundResource(R.drawable.bg_infrared_orange);
                            PrefData.setIs_TEMP23_Set(getContext(),true);
                            break;
                        case R.id.infrared_tv_24:
                            mTemp24.setBackgroundResource(R.drawable.bg_infrared_orange);
                            PrefData.setIs_TEMP24_Set(getContext(),true);
                            break;
                        case R.id.infrared_tv_25:
                            mTemp25.setBackgroundResource(R.drawable.bg_infrared_orange);
                            PrefData.setIs_TEMP25_Set(getContext(),true);
                            break;
                        case R.id.infrared_tv_26:
                            mTemp26.setBackgroundResource(R.drawable.bg_infrared_orange);
                            PrefData.setIs_TEMP26_Set(getContext(),true);
                            break;
                        case R.id.infrared_tv_27:
                            mTemp27.setBackgroundResource(R.drawable.bg_infrared_orange);
                            PrefData.setIs_TEMP27_Set(getContext(),true);
                            break;
                        case R.id.infrared_tv_28:
                            mTemp28.setBackgroundResource(R.drawable.bg_infrared_orange);
                            PrefData.setIs_TEMP28_Set(getContext(),true);
                            break;
                        case R.id.infrared_tv_auto:
                            mWindAuto.setBackgroundResource(R.drawable.bg_infrared_orange);
                            PrefData.setIs_WINDAuto_Set(getContext(),true);
                            break;
                        case R.id.infrared_tv_high:
                            mWindHight.setBackgroundResource(R.drawable.bg_infrared_orange);
                            PrefData.setIs_WINDHIGH_Set(getContext(),true);
                            break;
                        case R.id.infrared_tv_mid:
                            mWindMid.setBackgroundResource(R.drawable.bg_infrared_orange);
                            PrefData.setIs_WINDMID_Set(getContext(),true);
                            break;
                        case R.id.infrared_tv_low:
                            mWindLow.setBackgroundResource(R.drawable.bg_infrared_orange);
                            PrefData.setIs_WINDLOW_Set(getContext(),true);
                            break;
                    }
//                    ToastUtils.toast(getActivity(),getString(R.string.infrared_study_success));
                }
//                setButtonable();
                dismissInfraredLoading();
                break;
        }
    }

    private void study(byte address,byte xor) {
        try {
            byte[] cmd=new byte[5];
            cmd[0]=(byte) 0x88;
            cmd[1]=address;
            cmd[2]=(byte)0x00;
            cmd[3]=(byte)0x00;
            cmd[4]=xor;
            SerialController.getInstance(getContext()).sendbyte(cmd);
            Logger.i(TAG, "study:" + "adress:"+Byte.toString(address));
//            setButtonDisable();

            showInfraredDialog(getString(R.string.infrared_studing),"");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void showInfraredDialog(final String text,final String error) {
        if (mDlgLoading == null || !mDlgLoading.isShowing()) {
            mDlgLoading = new DlgInfraredLoading(getActivity());
            mDlgLoading.setMessage(text);
            mDlgLoading.setError(error);
            mDlgLoading.show();
        } else {
            mDlgLoading.setMessage(text);
        }
    }
    private void dismissInfraredLoading() {
        if (mDlgLoading != null && mDlgLoading.isShowing()) {
            mDlgLoading.dismiss();
        }
    }
    private void showConfirmDialog() {
            mConfirmDlg = DialogFactory.showConfirmDialog(getContext(),
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Infraredreset();
                            dialog.dismiss();
                        }
                    }, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    }, getString(R.string.infrared_confirm_tips));

    }
}
