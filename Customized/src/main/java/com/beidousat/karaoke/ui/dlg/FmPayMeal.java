package com.beidousat.karaoke.ui.dlg;

import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.beidousat.karaoke.R;
import com.beidousat.karaoke.adapter.StripViewPagerAdapter;
import com.beidousat.karaoke.biz.QueryKboxHelper;
import com.beidousat.karaoke.data.BoughtMeal;
import com.beidousat.karaoke.data.KBoxStatusInfo;
import com.beidousat.karaoke.data.PrefData;
import com.beidousat.karaoke.data.ServerConfigData;
import com.beidousat.karaoke.model.Meal;
import com.beidousat.karaoke.ui.Main;
import com.beidousat.karaoke.ui.fragment.FmSetting;
import com.beidousat.karaoke.widget.CountDownTextView;
import com.beidousat.karaoke.widget.PagerSlidingTabStrip;
import com.beidousat.libbns.evenbus.EventBusUtil;
import com.beidousat.libbns.util.FragmentUtil;
import com.beidousat.libbns.util.Logger;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


/**
 * author: Hanson
 * date:   2017/3/30
 * describe:
 */

public class FmPayMeal extends FmBaseDialog {
    private ViewPager mViewPager;
    private List<Fragment> mFragments;
    private PagerSlidingTabStrip mPagerStrip;
    private View mCountLayout;
    private CountDownTextView mCountDown;
    private List<String> mPagerTitles = new ArrayList<>();

    private int mPageType = TYPE_NORMAL;

    public static final String TAG_PAGE_TYPE = "page_type";
    /**
     * 普通类型页面
     */
    public static final int TYPE_NORMAL = 0;
    /**
     * 普通续费类型页面
     */
    public static final int TYPE_NORMAL_RENEW = 1;
    /**
     * 带倒计时的续费类型
     */
    public static final int TYPE_CNT_RENEW = 2;


    private static final int MSG_SUCESS = 1;

    /**
     * 新建套餐支付页面
     *
     * @param type 页面类型:
     *             TYPE_NORMAL=普通类型；
     *             TYPE_NORMAL_RENEW=普通续费类型，只能选择当前已选套餐类型；
     *             TYPE_CNT_RENEW=带倒计时的续费类型,当倒计时结束后会发送ROOM_CLOSE消息；
     * @return
     */
    public static FmPayMeal createMealFragment(int type) {
        FmPayMeal payMeal = new FmPayMeal();
        Bundle bundle = new Bundle();
        //如果当前套餐未过期则创建续费套餐页面；否者创建普通套餐页面
        bundle.putInt(FmPayMeal.TAG_PAGE_TYPE, type);
        payMeal.setArguments(bundle);

        return payMeal;
    }

    private Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            if (msg.what == MSG_SUCESS) {
                setupPager();
                mAttached.registerCloseListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        handleWhenIsAutoCloseMode();
                    }
                });
            }

            return true;
        }
    });

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.fm_pay_meal, container, false);

        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    void initData() {
        mPageType = getArguments().getInt(TAG_PAGE_TYPE, TYPE_NORMAL);
    }

    @Override
    void initView() {
        mViewPager = findViewById(R.id.view_pager);
        mPagerStrip = findViewById(R.id.pager_strip);
        mCountLayout = findViewById(R.id.countdown_layout);
        mCountDown = findViewById(R.id.countdown);

        findViewById(R.id.tv_charges_desc).setOnClickListener(onChangeDescListener);
    }

    @Override
    void setListener() {
        mCountDown.setFinishedListener(new CountDownTextView.TimerFinished() {
            @Override
            public void onFinish() {
                handleWhenIsAutoCloseMode();
            }
        });
    }

    @Override
    public void onSuccess(String method, Object object) {
        super.onSuccess(method, object);
    }

    @Override
    public void onFailed(String method, String error) {
        super.onFailed(method, error);
    }

    @Override
    public void onResume() {
        super.onResume();
        setupCountDown();
        requestKBoxInfo();
//        mHandler.sendEmptyMessage(MSG_SUCESS);
    }

    @Override
    public void onPause() {
        super.onPause();
        mCountDown.stop();
        mAttached.unregisterCloseListener(this);
    }

    private void setupPager() {
        if (isAdded()) {
            initFragmentAndTitle();

            StripViewPagerAdapter mAdapter = new StripViewPagerAdapter(
                    getChildFragmentManager(), mFragments, mPagerTitles);
            mPagerStrip.setTextSize(getResources().getDimensionPixelSize(R.dimen.tab_font_size));
            mViewPager.setAdapter(mAdapter);
            mPagerStrip.setViewPager(mViewPager);
        }
    }

    private void setupCountDown() {
        if (mPageType == TYPE_CNT_RENEW) {
            mCountLayout.setVisibility(View.VISIBLE);
            mCountDown.restart();
        } else {
            mCountLayout.setVisibility(View.INVISIBLE);
        }
    }


    private void initFragmentAndTitle() {
        mFragments = new ArrayList<>();

        FmMeal timeMeal = new FmMeal();
        Bundle bundle1 = new Bundle();
        bundle1.putInt(FmMeal.FM_TAG, Meal.TIME);
        timeMeal.setArguments(bundle1);

        FmMeal songMeal = new FmMeal();
        Bundle bundle2 = new Bundle();
        bundle2.putInt(FmMeal.FM_TAG, Meal.SONG);
        songMeal.setArguments(bundle2);

        Meal boughtMeal = BoughtMeal.getInstance().getTheFirstMeal();
        if (mPageType != TYPE_NORMAL && !BoughtMeal.getInstance().isMealExpire() && boughtMeal != null) {
            if (boughtMeal.getType() == Meal.SONG) {
                mPagerTitles.add(getContext().getResources().getString(R.string.text_buy_songs));
                mFragments.add(songMeal);
            } else if (boughtMeal.getType() == Meal.TIME) {
                mPagerTitles.add(getContext().getResources().getString(R.string.text_buy_time));
                mFragments.add(timeMeal);
            }
        } else {
            mPagerTitles.addAll(Arrays.asList(Main.mMainActivity.getResources().getStringArray(R.array.paymeal_tabs)));
            mFragments.add(timeMeal);
            mFragments.add(songMeal);
        }

    }

    /**
     * 倒计时续费模式，倒计时结束后发送ROOM_CLOSE消息
     */
    private void handleWhenIsAutoCloseMode() {
        if (mPageType == TYPE_CNT_RENEW && BoughtMeal.getInstance().isMealExpire()) {
            EventBusUtil.postRoomClose(null);
        }
        mAttached.dismiss();
    }

    private boolean isShowErro = false;

    private void requestKBoxInfo() {
        if (KBoxStatusInfo.getInstance().getKBoxStatus() != null && KBoxStatusInfo.getInstance().getKBoxStatus().status == 1) {
            new QueryKboxHelper(getContext(), new QueryKboxHelper.QueryKboxFeedback() {
                @Override
                public void onStart() {
                    if (isAdded())
                        LoadingUtil.showLoadingDialog(Main.mMainActivity);
                }

                @Override
                public void onFeedback(boolean suceed, String msg) {
                    Logger.d("FmPayMeal", "onFeedback suceed:" + suceed + "  msg:" + msg);
                    if (isAdded())
                        LoadingUtil.closeLoadingDialog();
                    if (!suceed) {
                        mAttached.dismiss();
                        if (!isShowErro) {
                            DialogFactory.showErrorDialog(Main.mMainActivity, msg, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    isShowErro = false;
                                }
                            });
                            isShowErro = true;
                        }
                    } else {
                        mHandler.sendEmptyMessage(MSG_SUCESS);
                    }
                }
            }).getBoxInfo();
        } else {
            mAttached.dismiss();

            PromptDialog promptDialog = new PromptDialog(Main.mMainActivity);
            if (KBoxStatusInfo.getInstance().getKBoxStatus() != null) {
                promptDialog.setMessage("无法使用：" + KBoxStatusInfo.getInstance().getKBoxStatus().msg);
            } else {
                promptDialog.setMessage("未授权无法使用！");
            }
            promptDialog.show();

//            PromptDialog mDialogAuth = new PromptDialog(Main.mMainActivity);
//            mDialogAuth.setCanceledOnTouchOutside(false);
//            if (KBoxStatusInfo.getInstance().getKBoxStatus() != null) {
//                if (KBoxStatusInfo.getInstance().getKBoxStatus().code == 3001 || KBoxStatusInfo.getInstance().getKBoxStatus().code == 2001) {
//                    mDialogAuth.setPositiveButton(getString(R.string.setting), new View.OnClickListener() {
//                        @Override
//                        public void onClick(View view) {
//                            showMngPass(0);
//                        }
//                    });
//                }
//            } else {
//                mDialogAuth.setMessage("设备未授权无法使用！");
//            }
//            mDialogAuth.setMessage(KBoxStatusInfo.getInstance().getKBoxStatus().msg);
//            mDialogAuth.show();
        }
    }

//    private MngPwdDialog mDlgPass;
//
//    private void showMngPass(final int open) {
//        if (mDlgPass == null || !mDlgPass.isShowing()) {
//            mDlgPass = new MngPwdDialog(Main.mMainActivity);
//            mDlgPass.setOnMngPwdListener(new MngPwdDialog.OnMngPwdListener() {
//                @Override
//                public void onPass() {
//                    FragmentUtil.addFragment(FmSetting.newInstance(open));
//                }
//            });
//            mDlgPass.setOnDismissListener(new DialogInterface.OnDismissListener() {
//                @Override
//                public void onDismiss(DialogInterface dialogInterface) {
//                    FragmentUtil.addFragment(FmSetting.newInstance(open));
//                }
//            });
//            mDlgPass.show();
//        }
//    }

    private DlgWebView dlgWebView;
    private View.OnClickListener onChangeDescListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (dlgWebView == null || !dlgWebView.isShowing()) {
                dlgWebView = new DlgWebView(Main.mMainActivity,
                        ServerConfigData.getInstance().getServerConfig().getStore_web() + "package/?kbox_sn=" + PrefData.getRoomCode(getContext())
                );
                dlgWebView.show();
            }
        }
    };
}
