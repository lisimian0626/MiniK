package com.beidousat.karaoke.ui.presentation;

import android.graphics.Bitmap;
import android.net.Uri;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebView;

import com.beidousat.libbns.model.Common;
import com.github.lzyzsd.jsbridge.BridgeWebView;
import com.github.lzyzsd.jsbridge.BridgeWebViewClient;
import com.github.lzyzsd.jsbridge.CallBackFunction;

/**
 * MyWebChromeClient
 */
public class MyWebChromeClient extends BridgeWebViewClient {
    BridgeWebView mWebview;

    public MyWebChromeClient(BridgeWebView webView) {
        super(webView);
        this.mWebview=webView;
    }

    @Override
    public void onPageStarted(WebView view, String url, Bitmap favicon) {
        super.onPageStarted(view, url, favicon);
        mWebview.callHandler(Common.INTERFACE_LOADSTART, "loadStart", new CallBackFunction() {
            @Override
            public void onCallBack(String s) {

            }
        });
    }

    @Override
    public void onPageFinished(WebView view, String url) {
        super.onPageFinished(view, url);
        mWebview.callHandler(Common.INTERFACE_LOADFINISH, "loadFinish", new CallBackFunction() {
            @Override
            public void onCallBack(String s) {

            }
        });
    }

    @Override
    public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
        super.onReceivedError(view, errorCode, description, failingUrl);
        mWebview.callHandler(Common.INTERFACE_LOADFINISH, "loadFinish", new CallBackFunction() {
            @Override
            public void onCallBack(String s) {

            }
        });
    }
}