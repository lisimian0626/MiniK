package com.beidousat.karaoke.ui;

import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import com.beidousat.karaoke.R;
import com.beidousat.karaoke.data.PrefData;
import com.beidousat.karaoke.util.Utils;
import com.beidousat.libbns.evenbus.EventBusId;
import com.beidousat.libbns.evenbus.EventBusUtil;
import com.beidousat.libbns.model.Common;
import com.beidousat.libbns.net.request.HttpRequestListener;
import com.trello.rxlifecycle2.LifecycleTransformer;
import com.trello.rxlifecycle2.components.support.RxAppCompatActivity;

import java.util.Locale;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.ObservableTransformer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;


/**
 * Created by J Wong on 2015/10/9 08:56.
 */
public class BaseActivity extends RxAppCompatActivity implements OnScreenAdListener, HttpRequestListener {

    private final static int SYS_UI = View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
            | View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
            | View.SYSTEM_UI_FLAG_IMMERSIVE;

    public static long mLastTouchTime;
    public static boolean mCanShowAd = true;

    private ScheduledExecutorService mScheduledExecutorService;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mLastTouchTime = 0;
        switchLanguage(PrefData.getLastLanguage(this));
        getWindow().getDecorView().setSystemUiVisibility(SYS_UI);
//        startScreenTimer();
    }

    @Override
    protected void onDestroy() {
//        stopScreenTimer();
        super.onDestroy();
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        mLastTouchTime = System.currentTimeMillis();
        return super.dispatchTouchEvent(ev);
    }


    private void startScreenTimer() {
        if (mScheduledExecutorService != null && !mScheduledExecutorService.isShutdown())
            return;
        mScheduledExecutorService = Executors.newScheduledThreadPool(1);
        scheduleAtFixedRate(mScheduledExecutorService);

    }

    private void stopScreenTimer() {
        if (mScheduledExecutorService != null) {
            mScheduledExecutorService.shutdownNow();
            mScheduledExecutorService = null;
        }
    }

    private void scheduleAtFixedRate(ScheduledExecutorService service) {
        service.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                if (mCanShowAd && System.currentTimeMillis() - mLastTouchTime > 5 * 60 * 1000) {//进入屏保5min
                    onEnterScreenAd();
                }
            }
        }, 60, 2, TimeUnit.SECONDS);
    }

    @Override
    public void onEnterScreenAd() {
    }


    @Override
    public void onStart(String method) {

    }

    @Override
    public void onSuccess(String method, Object object) {

    }

    @Override
    public void onFailed(String method, String error) {

    }
    /**
     * 线程调度
     */
    protected <T> ObservableTransformer<T, T> compose(final LifecycleTransformer<T> lifecycle) {
        return new ObservableTransformer<T, T>() {
            @Override
            public ObservableSource<T> apply(Observable<T> observable) {
                return observable
                        .subscribeOn(Schedulers.io())
                        .doOnSubscribe(new Consumer<Disposable>() {
                            @Override
                            public void accept(Disposable disposable) throws Exception {
                                // 可添加网络连接判断等
                                if (!Utils.isNetworkAvailable(BaseActivity.this)) {
                                    Toast.makeText(BaseActivity.this, R.string.toast_network_error, Toast.LENGTH_SHORT).show();
                                }
                            }
                        })
                        .observeOn(AndroidSchedulers.mainThread())
                        .compose(lifecycle);
            }
        };
    }
    /**
     *
     * <切换语言>
     *
     * @param language
     * @see [类、类#方法、类#成员]
     */
    protected void switchLanguage(String language)
    {
        // 设置应用语言类型
        Resources resources = getResources();
        Configuration config = resources.getConfiguration();
        DisplayMetrics dm = resources.getDisplayMetrics();
        if (language.equals("en"))
        {
            config.locale = Locale.ENGLISH;
            Common.isEn=true;
        }
        else if(language.equals("tw")){
            config.locale = Locale.TAIWAN;
            Common.isEn=false;
        }
        else {
            // 简体中文
            config.locale = Locale.SIMPLIFIED_CHINESE;
            Common.isEn=false;
        }
        resources.updateConfiguration(config, dm);

        // 保存设置语言的类型
        PrefData.setLanguage(this,language);
    }
    protected void exitApp() {
        EventBusUtil.postSticky(EventBusId.id.MAIN_PLAYER_STOP, null);
        android.os.Process.killProcess(android.os.Process.myPid());//获取PID
        System.exit(0);//直接结束程序
    }
}
