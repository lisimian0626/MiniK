package com.beidousat.karaoke.ui.dlg;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager.LayoutParams;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.TextView;

import com.beidousat.karaoke.R;
import com.beidousat.karaoke.widget.ProgressWebView;
import com.beidousat.libbns.util.Logger;

import java.util.HashMap;
import java.util.Map;


public class DlgWebView extends BaseDialog implements OnClickListener {

    private final String TAG = DlgWebView.class.getSimpleName();
    private ProgressWebView mWebView;
    private String mUrl;
    private Context mContext;
    private Map<String, String> Headers;
    public DlgWebView(Context context, String url) {
        super(context, R.style.MyDialog);
        mUrl = url;
        this.mContext=context;
        init();
    }

    void init() {
        this.setContentView(R.layout.dlg_webview);
        LayoutParams lp = getWindow().getAttributes();
        lp.width = 960;
        lp.height = 600;
        lp.gravity = Gravity.CENTER;
        getWindow().setAttributes(lp);
        findViewById(R.id.riv_close).setOnClickListener(this);
        mWebView = (ProgressWebView) findViewById(R.id.webview);

        Headers = new HashMap<String, String>();
        Headers.put("mink", "minkbox");//设备标识(前面是key，后面是value)
        WebSettings settings = mWebView.getSettings();
        settings.setJavaScriptEnabled(true);//开启支持javascript
        settings.setCacheMode(WebSettings.LOAD_NO_CACHE);
        settings.setUserAgentString("minkbox");
        settings.setAllowFileAccess(true);//支持文件流
        settings.setSupportZoom(false);//不支持缩放
        settings.setBuiltInZoomControls(false);//不支持缩放
        settings.setUseWideViewPort(false);// 调整到适合webview大小
        settings.setLoadWithOverviewMode(false);//  调整到适合webview大小
        settings.setBlockNetworkImage(true);////提高网页加载速度，暂时阻塞图片加载，然后网页加载好了，在进行加载图片
        //settings.setAppCacheEnabled(true);//开启缓存机制
        settings.setDomStorageEnabled(true);//开启DOM
        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                // TODO Auto-generated method stub
                //返回值是true的时候控制去WebView打开，为false调用系统浏览器或第三方浏览器
                view.loadUrl(url,Headers);
                return true;
            }
        });
    }

    @Override
    public void show() {
        super.show();
        Logger.d(TAG, "WebView loadUrl:" + mUrl);
        mWebView.loadUrl(mUrl,Headers);
    }

    @Override
    public void dismiss() {
        mWebView.destroy();
        super.dismiss();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.riv_close:
                dismiss();
                break;
        }
    }
}
