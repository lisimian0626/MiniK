package com.beidousat.libwidget.text;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.os.Handler;
import android.os.Message;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.WindowManager;

import com.beidousat.libwidget.R;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;


public class MarqueeView extends SurfaceView implements SurfaceHolder.Callback {
    public Context mContext;

    private float mTextSize = 48; //字体大小

    private int mTextColor = Color.WHITE; //字体的颜色

//    private int mBackgroundColor = Color.TRANSPARENT;//背景色

    private boolean mIsRepeat;//是否重复滚动

    private int mStartPoint;// 开始滚动的位置  0是从最左面开始    1是从最末尾开始

    private int mDirection;//滚动方向 0 向左滚动   1向右滚动

//    private int mSpeed = 10;//滚动速度

    private SurfaceHolder holder;

    private TextPaint mTextPaint;

//    private MarqueeViewThread mThread;

    private String marqueeString;

    private int textWidth = 0, textHeight = 0;

    public int currentX = 0;// 当前x的位置

    public int sepX = 8;//每一步滚动的距离

    //清理背景画笔
    private Paint clearPaint;

    private ScheduledExecutorService mScheduledExecutorService;

    private int ShadowColor = Color.BLACK;

    private float mStrokeWidth = 0.0f;

//    private Paint mPaintEnter, mPaintExit;
//    private int mShaderWidth = 20, mShaderHeight = 80;


    public MarqueeView(Context context) {
        this(context, null);
    }

    public MarqueeView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MarqueeView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mContext = context;
        init(attrs, defStyleAttr);
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

    }
//    private RectF rectFLeft;

    private void init(AttributeSet attrs, int defStyleAttr) {
//        mPaintExit = new Paint();
//        LinearGradient gradient = new LinearGradient(0, 0, mShaderWidth, mShaderHeight, Color.BLACK, Color.TRANSPARENT, Shader.TileMode.MIRROR);
//        mPaintExit.setShader(gradient);
//
//        mPaintEnter = new Paint();
//        LinearGradient gradient2 = new LinearGradient(1280 - 80, 0, mShaderWidth, mShaderHeight, Color.TRANSPARENT, Color.BLACK, Shader.TileMode.MIRROR);
//        mPaintEnter.setShader(gradient2);

        clearPaint = new Paint();
        clearPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));

        TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.MarqueeView, defStyleAttr, 0);
        mTextColor = a.getColor(R.styleable.MarqueeView_textcolor, Color.WHITE);
        mTextSize = a.getDimension(R.styleable.MarqueeView_textSize, 48);
        mIsRepeat = a.getBoolean(R.styleable.MarqueeView_isRepeat, false);
        mStartPoint = a.getInt(R.styleable.MarqueeView_startPoint, 0);
        mDirection = a.getInt(R.styleable.MarqueeView_direction, 0);
        sepX = a.getInt(R.styleable.MarqueeView_speed, 10);
        mStrokeWidth = a.getFloat(R.styleable.MarqueeView_marqueeStrokeWidth, 0.0f);

        a.recycle();
        holder = this.getHolder();
        holder.setFormat(PixelFormat.TRANSPARENT);
        holder.addCallback(this);
        mTextPaint = new TextPaint();
        mTextPaint.setFlags(Paint.ANTI_ALIAS_FLAG);
        mTextPaint.setTextAlign(Paint.Align.LEFT);
    }

    public void setIsRepeat(boolean isRepeat) {
        mIsRepeat = isRepeat;
    }

    public void setText(String msg) {
        if (!TextUtils.isEmpty(msg)) {
            measurementsText(msg);
        }
    }

    public void setText(int color, String text) {
        if (!TextUtils.isEmpty(text)) {
            mTextColor = color;
            measurementsText(text);
        }
    }

    public String getText() {
        return marqueeString;
    }

    public void setTextSize(float textSize) {
        mTextSize = textSize;
        mTextPaint.setTextSize(mTextSize);
    }

    protected void measurementsText(String msg) {
        mPlaying = true;
        marqueeString = msg;
        mTextPaint.setTextSize(mTextSize);
        mTextPaint.setColor(mTextColor);
        Log.d("MarqueeView", "measurementsText mStrokeWidth:" + mStrokeWidth);
        if (mStrokeWidth > 0) {
            mTextPaint.setStrokeWidth(mStrokeWidth);
            mTextPaint.setFakeBoldText(false);
            mTextPaint.setStyle(Paint.Style.FILL_AND_STROKE);
            // 设定阴影(柔边, X 轴位移, Y 轴位移, 阴影颜色)
            mTextPaint.setShadowLayer(2, 0, 0, ShadowColor);
        }
        textWidth = (int) mTextPaint.measureText(marqueeString);
        Paint.FontMetrics fontMetrics = mTextPaint.getFontMetrics();
        textHeight = (int) fontMetrics.bottom;
        WindowManager wm = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        int width = wm.getDefaultDisplay().getWidth();
        if (mStartPoint == 0)
            currentX = 0;
        else
            currentX = width - getPaddingLeft() - getPaddingRight();

    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        this.holder = holder;
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        startScroll();
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        stopScroll();
    }


    /**
     * 开始滚动
     */
    public void startScroll() {
//        if (mThread != null && mThread.isRun)
//            return;
//        mThread = new MarqueeViewThread(holder);//创建一个绘图线程
//        mThread.start();

        if (mScheduledExecutorService != null && !mScheduledExecutorService.isShutdown())
            return;
        mScheduledExecutorService = Executors.newScheduledThreadPool(1);
        scheduleAtFixedRate(mScheduledExecutorService);
    }

    /**
     * 停止滚动
     */
    public void stopScroll() {
//        if (mThread != null) {
//            mThread.isRun = false;
//            mThread.interrupt();
//        }
//        mThread = null;
        mPlaying = false;
        if (mScheduledExecutorService != null) {
            mScheduledExecutorService.shutdownNow();
            mScheduledExecutorService = null;
        }
    }

    private boolean mPlaying;

    private void scheduleAtFixedRate(ScheduledExecutorService service) {
        service.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                if (mPlaying) {
                    onDrawView();
                }
            }
        }, 5000, 20, TimeUnit.MILLISECONDS);
    }

    private void clearPaint() {
        Canvas canvas = null;
        try {
            synchronized (holder) {
                canvas = holder.lockCanvas();
                if (null == canvas) {
                    return;
                }
                canvas.drawPaint(clearPaint);
            }
        } catch (Exception e) {
            Log.w("MarqueeView", "Exception ex:" + e.toString());
        } finally {
            if (canvas != null) {
                try {//结束锁定画图，并提交改变。
                    holder.unlockCanvasAndPost(canvas);
                } catch (Exception e) {
//                    Log.w("MarqueeView", "Exception ex:" + e.toString());
                }
            }
        }
    }

    private void onDrawView() {
        Canvas canvas = null;
        try {
            synchronized (holder) {

                canvas = holder.lockCanvas();
                if (null == canvas) {
                    return;
                }
                canvas.drawPaint(clearPaint);

                if (TextUtils.isEmpty(marqueeString)) {
                    return;
                }

                int paddingLeft = getPaddingLeft();
                int paddingTop = getPaddingTop();
                int paddingRight = getPaddingRight();
                int paddingBottom = getPaddingBottom();

                int contentWidth = getWidth() - paddingLeft - paddingRight;

                int contentHeight = getHeight() - paddingTop - paddingBottom;

                int centerYLine = paddingTop + contentHeight / 2;//中心线

                if (mDirection == 0) {//向左滚动
                    if (currentX <= -textWidth) {
                        if (!mIsRepeat) {//如果是不重复滚动
                            mHandler.sendEmptyMessage(ROLL_OVER);
                        }
                        currentX = contentWidth;
                    } else {
                        currentX -= sepX;
                    }
                } else {//  向右滚动
                    if (currentX >= contentWidth) {
                        if (!mIsRepeat) {//如果是不重复滚动
                            mHandler.sendEmptyMessage(ROLL_OVER);
                        }
                        currentX = -textWidth;
                    } else {
                        currentX += sepX;
                    }
                }
                canvas.save();
                canvas.drawText(marqueeString, currentX, centerYLine + dip2px(getContext(), textHeight) / 2, mTextPaint);

//                if (mPaintEnter != null) {
//                    canvas.drawRect(1280 - mShaderWidth, 0, mShaderWidth, mShaderHeight, mPaintEnter);
//                }
//                if (mPaintExit != null) {
//                    canvas.drawRect(0, 0, mShaderWidth, mShaderHeight, mPaintExit);
//                }
                canvas.restore();
                //  holder.unlockCanvasAndPost(canvas);//结束锁定画图，并提交改变。
            }
        } catch (IllegalArgumentException e) {
            setText("");
            stopScroll();
        } catch (Exception e) {
            Log.w("MarqueeView", "Exception ex:" + e.toString());
        } finally {
            if (canvas != null) {
                try {//结束锁定画图，并提交改变。
                    holder.unlockCanvasAndPost(canvas);
                } catch (Exception e) {
//                    Log.w("MarqueeView", "Exception ex:" + e.toString());
                }
            }
        }
    }
//    /**
//     * 线程
//     */
//    class MarqueeViewThread extends Thread {
//
//        private SurfaceHolder holder;
//
//        public boolean isRun;//是否在运行
//
//
//        public MarqueeViewThread(SurfaceHolder holder) {
//            this.holder = holder;
//            isRun = true;
//        }
//
//        public void onDraw() {
//            try {
//                synchronized (holder) {
//                    if (TextUtils.isEmpty(marqueeString)) {
//                        Thread.sleep(1000);//睡眠时间为1秒
//                        return;
//                    }
//                    Canvas canvas = holder.lockCanvas();
//                    if (null == canvas) {
//                        return;
//                    }
//                    canvas.drawPaint(clearPaint);
//
//                    int paddingLeft = getPaddingLeft();
//                    int paddingTop = getPaddingTop();
//                    int paddingRight = getPaddingRight();
//                    int paddingBottom = getPaddingBottom();
//
//                    int contentWidth = getWidth() - paddingLeft - paddingRight;
//
//                    int contentHeight = getHeight() - paddingTop - paddingBottom;
//
//                    int centerYLine = paddingTop + contentHeight / 2;//中心线
//
//                    if (mDirection == 0) {//向左滚动
//                        if (currentX <= -textWidth) {
//                            if (!mIsRepeat) {//如果是不重复滚动
//                                mHandler.sendEmptyMessage(ROLL_OVER);
//                            }
//                            currentX = contentWidth;
//                        } else {
//                            currentX -= sepX;
//                        }
//                    } else {//  向右滚动
//                        if (currentX >= contentWidth) {
//                            if (!mIsRepeat) {//如果是不重复滚动
//                                mHandler.sendEmptyMessage(ROLL_OVER);
//                            }
//                            currentX = -textWidth;
//                        } else {
//                            currentX += sepX;
//                        }
//                    }
//                    canvas.save();
//                    canvas.drawText(marqueeString, currentX, centerYLine + dip2px(getContext(), textHeight) / 2, mTextPaint);
//                    canvas.restore();
//                    holder.unlockCanvasAndPost(canvas);//结束锁定画图，并提交改变。
////                    int a = textWidth / margueeString.trim().length();
////                    int b = a / sepX;
////                    int c = mSpeed / b == 0 ? 1 : mSpeed / b;
////                    Log.d(getClass().getSimpleName(), "sleep c :" + c);
//                    try {
//                        Thread.sleep(40);//睡眠时间为移动的频率
//                    } catch (InterruptedException e) {
//                        Log.w("MarqueeView", "InterruptedException ex:" + e.toString());
//                    }
//                }
//            } catch (IllegalArgumentException e) {
//                setText("");
//                stopScroll();
//            } catch (Exception e) {
//                Log.w("MarqueeView", "Exception ex:" + e.toString());
//            }
//        }
//
//        @Override
//        public void run() {
//            while (isRun) {
//                onDraw();
//            }
//        }
//    }

    public static final int ROLL_OVER = 100;

    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case ROLL_OVER:
                    mPlaying = false;
                    setText("");
                    if (mOnMarqueeListener != null) {
                        mOnMarqueeListener.onRollOver();
                    }
//                    mHandler.removeCallbacks(runnableCallbackOver);
//                    mHandler.postDelayed(runnableCallbackOver, 100);
                    break;
            }
        }
    };

//    private Runnable runnableCallbackOver = new Runnable() {
//        @Override
//        public void run() {
//            if (mOnMarqueeListener != null) {
//                mOnMarqueeListener.onRollOver();
//            }
//        }
//    };

    /**
     * dip转换为px
     *
     * @param context
     * @param dpValue
     * @return
     */
    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    public void reset() {
        int contentWidth = getWidth() - getPaddingLeft() - getPaddingRight();
        if (mStartPoint == 0)
            currentX = 0;
        else
            currentX = contentWidth;
        clearPaint();
    }


    OnMarqueeListener mOnMarqueeListener;

    public void setOnMarqueeListener(OnMarqueeListener listener) {
        this.mOnMarqueeListener = listener;
    }
}
