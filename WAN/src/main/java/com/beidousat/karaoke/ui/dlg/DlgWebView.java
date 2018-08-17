package com.beidousat.karaoke.ui.dlg;

import android.app.Activity;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager.LayoutParams;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.beidousat.karaoke.R;
import com.beidousat.karaoke.widget.ProgressWebView;
import com.beidousat.libbns.util.Logger;


public class DlgWebView extends BaseDialog implements OnClickListener {

    private final String TAG = DlgWebView.class.getSimpleName();
    private ProgressWebView mWebView;
    private String mUrl;

    public DlgWebView(Activity context, String url) {
        super(context, R.style.MyDialog);
        mUrl = url;
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

        WebSettings settings = mWebView.getSettings();
        settings.setJavaScriptEnabled(true);


        mWebView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);

        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                // TODO Auto-generated method stub
                //返回值是true的时候控制去WebView打开，为false调用系统浏览器或第三方浏览器
                view.loadUrl(url);
                return true;
            }
        });
    }

    @Override
    public void show() {
        super.show();
        Logger.d(TAG, "WebView loadUrl:" + mUrl);
        mWebView.loadUrl(mUrl);
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
