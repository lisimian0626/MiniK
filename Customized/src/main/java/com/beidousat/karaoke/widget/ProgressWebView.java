package com.beidousat.karaoke.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.webkit.WebView;
import android.widget.ProgressBar;

import com.beidousat.karaoke.R;

/**
 * Created by J Wong on 2017/9/4.
 */

public class ProgressWebView extends WebView {

    private ProgressBar progressbar;
    private float vRadius = 10;
    private int vWidth;
    private int vHeight;
    private int x;
    private int y;
    private Paint paint1;
    private Paint paint2;

    public ProgressWebView(Context context, AttributeSet attrs) {
        super(context, attrs);

        progressbar = new ProgressBar(context, null,
                android.R.attr.progressBarStyleHorizontal);
        progressbar.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, 10, 0, 0));

        Drawable drawable = context.getResources().getDrawable(R.drawable.progress_bar_states);
        progressbar.setProgressDrawable(drawable);
        addView(progressbar);
        setWebChromeClient(new WebChromeClient());
        //是否可以缩放
        getSettings().setSupportZoom(true);
        getSettings().setBuiltInZoomControls(true);
//        getSettings().setJavaScriptEnabled(true);
//        getSettings().setUseWideViewPort(true);
//        getSettings().setLoadWithOverviewMode(true);
        init(context);
    }

    public class WebChromeClient extends android.webkit.WebChromeClient {
        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            if (newProgress == 100) {
                progressbar.setVisibility(GONE);
            } else {
                if (progressbar.getVisibility() == GONE)
                    progressbar.setVisibility(VISIBLE);
                progressbar.setProgress(newProgress);
            }
            super.onProgressChanged(view, newProgress);
        }

    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        LayoutParams lp = (LayoutParams) progressbar.getLayoutParams();
        lp.x = l;
        lp.y = t;
        progressbar.setLayoutParams(lp);
        super.onScrollChanged(l, t, oldl, oldt);
    }


    private void init(Context context) {
        paint1 = new Paint();
        paint1.setColor(Color.WHITE);
        paint1.setAntiAlias(true);
        paint1.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_OUT));
        //
        paint2 = new Paint();
        paint2.setXfermode(null);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        vWidth = getMeasuredWidth();
        vHeight = getMeasuredHeight();
    }

    @Override
    public void draw(Canvas canvas) {
        x = this.getScrollX();
        y = this.getScrollY();
        Bitmap bitmap = Bitmap.createBitmap(x + vWidth, y + vHeight, Bitmap.Config.ARGB_8888);
        Canvas canvas2 = new Canvas(bitmap);
        super.draw(canvas2);
        drawLeftUp(canvas2);
        drawRightUp(canvas2);
        drawLeftDown(canvas2);
        drawRightDown(canvas2);
        canvas.drawBitmap(bitmap, 0, 0, paint2);
        bitmap.recycle();
    }


    private void drawLeftUp(Canvas canvas) {
        Path path = new Path();
        path.moveTo(x, vRadius);
        path.lineTo(x, y);
        path.lineTo(vRadius, y);
        path.arcTo(new RectF(x, y, x + vRadius * 2, y + vRadius * 2), -90, -90);
        path.close();
        canvas.drawPath(path, paint1);
    }


    private void drawLeftDown(Canvas canvas) {
        Path path = new Path();
        path.moveTo(x, y + vHeight - vRadius);
        path.lineTo(x, y + vHeight);
        path.lineTo(x + vRadius, y + vHeight);
        path.arcTo(new RectF(x, y + vHeight - vRadius * 2, x + vRadius * 2, y + vHeight), 90, 90);
        path.close();
        canvas.drawPath(path, paint1);
    }


    private void drawRightDown(Canvas canvas) {
        Path path = new Path();
        path.moveTo(x + vWidth - vRadius, y + vHeight);
        path.lineTo(x + vWidth, y + vHeight);
        path.lineTo(x + vWidth, y + vHeight - vRadius);
        path.arcTo(new RectF(x + vWidth - vRadius * 2, y + vHeight - vRadius * 2, x + vWidth, y + vHeight), 0, 90);
        path.close();
        canvas.drawPath(path, paint1);
    }


    private void drawRightUp(Canvas canvas) {
        Path path = new Path();
        path.moveTo(x + vWidth, y + vRadius);
        path.lineTo(x + vWidth, y);
        path.lineTo(x + vWidth - vRadius, y);
        path.arcTo(new RectF(x + vWidth - vRadius * 2, y, x + vWidth, y + vRadius * 2), -90, 90);
        path.close();
        canvas.drawPath(path, paint1);
    }
}
