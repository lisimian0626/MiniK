package com.beidousat.karaoke.ui.fragment;

import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.beidousat.karaoke.R;
import com.beidousat.karaoke.data.KBoxInfo;
import com.beidousat.karaoke.data.PrefData;
import com.beidousat.libbns.model.ServerConfigData;
import com.beidousat.karaoke.ui.Main;
import com.beidousat.karaoke.ui.dlg.CommonDialog;
import com.beidousat.karaoke.ui.dlg.DlgPassword;
import com.beidousat.karaoke.ui.dlg.DlgRoomCode;
import com.beidousat.karaoke.ui.dlg.FmRoomSet;
import com.beidousat.karaoke.ui.dlg.PromptDialog;
import com.beidousat.karaoke.util.ToastUtils;
import com.beidousat.karaoke.util.UIUtils;
import com.beidousat.libbns.evenbus.EventBusId;
import com.beidousat.libbns.evenbus.EventBusUtil;
import com.beidousat.libbns.model.KBoxStatus;
import com.beidousat.libbns.net.socket.KBoxSocketHeart;
import com.beidousat.libbns.util.FragmentUtil;
import com.beidousat.libbns.util.PreferenceUtil;

/**
 * Created by J Wong on 2017/5/8.
 */

public class FmSetting extends BaseFragment implements View.OnClickListener {

    private int mOpenType;
    private TextView tv_room_no,tv_mng;

    public static FmSetting newInstance(int openType) {
        FmSetting fragment = new FmSetting();
        Bundle args = new Bundle();
        args.putInt("openType", openType);
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mOpenType = getArguments().getInt("openType", -1);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.fm_setting, null);
        mRootView.findViewById(R.id.tv_back).setOnClickListener(this);
        mRootView.findViewById(R.id.tv_system_setting).setOnClickListener(this);
        mRootView.findViewById(R.id.tv_calibration).setOnClickListener(this);
        mRootView.findViewById(R.id.tv_password).setOnClickListener(this);
        tv_room_no= (TextView) mRootView.findViewById(R.id.tv_room_no);
        tv_room_no.setOnClickListener(this);
        tv_mng= (TextView) mRootView.findViewById(R.id.tv_mng);
        tv_mng.setOnClickListener(this);
        String sn= PrefData.getSNCode(Main.mMainActivity);
        if(!TextUtils.isEmpty(sn)){
            tv_room_no.setVisibility(View.GONE);
            tv_mng.setVisibility(View.GONE);
        }else{
            tv_room_no.setVisibility(View.VISIBLE);
            tv_mng.setVisibility(View.VISIBLE);
        }
        mRootView.findViewById(R.id.tv_serial).setOnClickListener(this);
        mRootView.findViewById(R.id.tv_infrared).setOnClickListener(this);
        switch (mOpenType) {
            case 0:
                onClick(mRootView.findViewById(R.id.tv_room_no));
                break;
        }

        return mRootView;
    }

    private DlgRoomCode mDlgRoomCode;

//    private void showRoomCodeSet() {
//        if (mDlgRoomCode == null || !mDlgRoomCode.isShowing()) {
//            mDlgRoomCode = new DlgRoomCode(Main.mMainActivity);
//            mDlgRoomCode.setOnDismissListener(new DialogInterface.OnDismissListener() {
//                @Override
//                public void onDismiss(DialogInterface dialogInterface) {
//                    checkBoxId();
//                }
//            });
//            mDlgRoomCode.show();
//        }
//    }
    private void showRoomSet() {
        CommonDialog dialog = CommonDialog.getInstance();
        dialog.setShowClose(true);
        dialog.setContent(FmRoomSet.createRoomSetFragment());
        if (!dialog.isAdded()) {
            dialog.show(getFragmentManager(), "commonDialog");

        }
    }
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_back:
                EventBusUtil.postSticky(EventBusId.id.BACK_FRAGMENT, "");
                break;
            case R.id.tv_system_setting:
                EventBusUtil.postSticky(EventBusId.id.MAIN_PLAYER_STOP, null);
                Intent intent = new Intent("/");
                ComponentName cm = new ComponentName("com.android.settings", "com.android.settings.Settings");
                intent.setComponent(cm);
                intent.setAction("android.intent.action.VIEW");
                startActivity(intent);
                exitApp();
                break;
            case R.id.tv_calibration:
                toCalibrationActivity();
                exitApp();
                break;
            case R.id.tv_password:
                DlgPassword dlgPassword = new DlgPassword(getActivity());
                dlgPassword.show();
                break;
            case R.id.tv_room_no:
                showRoomSet();
                break;
            case R.id.tv_serial:
                FragmentUtil.addFragment(new FmSettingSerail());
                break;
            case R.id.tv_infrared:
                FragmentUtil.addFragment(new FmSettingInfrared());
                break;
            case R.id.tv_mng:
                if (ServerConfigData.getInstance().getServerConfig() != null) {
                    intent = new Intent();
                    intent.setAction("android.intent.action.VIEW");
//                Uri content_url = Uri.parse("http://test.beidousat.com/");
                    Uri content_url = Uri.parse(KBoxInfo.WEBVIEW+"#login");
                    intent.setData(content_url);
                    startActivity(intent);
                    exitApp();
                } else {
                    Toast.makeText(getContext().getApplicationContext(), "未连接服务器", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }


    private PromptDialog mDialogAuth;

//    private void checkBoxId() {
//        if (!TextUtils.isEmpty(PrefData.getRoomCode(getContext().getApplicationContext()))) {
//            KBoxSocketHeart kBoxSocketHeart = KBoxSocketHeart.getInstance(ServerConfigData.getInstance().getServerConfig().getStore_ip(),
//                    ServerConfigData.getInstance().getServerConfig().getStore_port());
//            kBoxSocketHeart.setKBoxId(PrefData.getRoomCode(getContext().getApplicationContext()));
//            kBoxSocketHeart.check();
//            kBoxSocketHeart.sendHeartPackage(PrefData.getRoomCode(getContext().getApplicationContext()), new KBoxSocketHeart.OnSetKBoxIDListener() {
//                @Override
//                public void callback(final KBoxStatus kBoxStatus) {
//                    Main.mMainActivity.runOnUiThread(new Runnable() {
//                        @Override
//                        public void run() {
//                            try {
//                                if (kBoxStatus.status == 1) {//授权通过
//                                    ToastUtils.toast(getContext().getApplicationContext(),getString(R.string.room_num_success));
////                                    Toast.makeText(getContext(), "房间号设置成功！", Toast.LENGTH_LONG).show();
//                                } else {//授权未通过
//                                    if ((mDlgRoomCode == null || !mDlgRoomCode.isShowing()) && (mDialogAuth == null || !mDialogAuth.isShowing())) {
//                                        mDialogAuth = new PromptDialog(Main.mMainActivity);
//                                        if (kBoxStatus.code == 3001 || kBoxStatus.code == 2001) {
//                                            mDialogAuth.setPositiveButton("设置", new View.OnClickListener() {
//                                                @Override
//                                                public void onClick(View view) {
//                                                    showRoomCodeSet();
//                                                }
//                                            });
//                                        }
//                                        mDialogAuth.setMessage(kBoxStatus.msg);
//                                        mDialogAuth.show();
//                                    }
//                                }
//                            } catch (Exception e) {
//                                e.printStackTrace();
//                            }
//                        }
//                    });
//                }
//            });
//        }
//    }

    private PromptDialog mDialogSetRoomTip;

    private void showSetRoomCodeDialog() {
        if (mDialogSetRoomTip == null || !mDialogSetRoomTip.isShowing()) {
            mDialogSetRoomTip = new PromptDialog(Main.mMainActivity);
            mDialogSetRoomTip.setMessage("未设置房间编号！请先设置房间编号。");
            mDialogSetRoomTip.show();
        }
    }


    private void exitApp() {
        EventBusUtil.postSticky(EventBusId.id.MAIN_PLAYER_STOP, null);
        hideSystemUI(false);
        getActivity().finish();
        android.os.Process.killProcess(android.os.Process.myPid());//获取PID
        System.exit(0);//直接结束程序
    }

    private void hideSystemUI(boolean hide) {
        UIUtils.hideNavibar(getContext(), hide);
    }

    private void toCalibrationActivity() {
        try {
            Intent intent = new Intent("/");
            ComponentName cm = new ComponentName("org.zeroxlab.util.tscal",
                    "org.zeroxlab.util.tscal.TSCalibration");
            intent.setComponent(cm);
            intent.setAction("android.intent.action.VIEW");
            startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
