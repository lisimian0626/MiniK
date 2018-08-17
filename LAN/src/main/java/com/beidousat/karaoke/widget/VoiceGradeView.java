package com.beidousat.karaoke.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import com.beidousat.karaoke.R;
import com.beidousat.libbns.util.Logger;
import com.beidousat.score.KeyInfo;
import com.beidousat.score.NoteInfo;
import java.util.concurrent.ConcurrentLinkedQueue;


/**
 * author: Hanson
 * date:   2016/6/14
 * describe:
 */
public class VoiceGradeView extends SurfaceView implements SurfaceHolder.Callback2, ReceiveMicData {
    private static String TAG = VoiceGradeView.class.getSimpleName();

    private VoiceDataSources dataSources; //源数据
    private ConcurrentLinkedQueue<NoteInfo> dataComparison; //对比数据

    private Bitmap mCursor;
    private int width;
    private int height;

    //标准线画笔
    private Paint standardPanit;
    //对比线画笔
    private Paint comparsonPaint;
    //清理背景画笔
    private Paint clearPaint;
    //基准线画笔
    private Paint baseLinePaint;

    //1秒钟滚动的距离
    private float perSecW;

    //线条起始位置
    private float startX;
    //线条结束位置
    private float endX;
    //key最大波动值
    private float yDif;
    private float drawStartH = 30;
    //
    private float perY;
    //
    private float drawHeight;
    //游标位置
    private float cursorPos;
    //记录当前屏幕第一线条的所在dataSources的下标值
    private int currentIndex;

    private static final float COMPARSON_LEN = 0.1f;
    //线条高度
    private static final int LINE_HEIGHT = 10;
    private static final int CURSOR_HEIGHT = 20;

    //线条滚动满屏需要花费多少秒
    private float MAX_DURATION_SCREEN = 9f;
    private float CURRENT_LEFT_DURATION = MAX_DURATION_SCREEN / 3;
    private static final float BASE_LINE_W_PX = 430;

    //音乐当前播放时间(s)
    public float currentTime = 0;


    public VoiceGradeView(Context context) {
        super(context);
        init();
    }

    public VoiceGradeView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public VoiceGradeView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        standardPanit = new Paint();
        standardPanit.setColor(getResources().getColor(R.color.voic_source));
        standardPanit.setStyle(Paint.Style.FILL);
        standardPanit.setAntiAlias(true);

        comparsonPaint = new Paint();
        comparsonPaint.setColor(getResources().getColor(R.color.voic_mic));
        comparsonPaint.setStyle(Paint.Style.FILL);
        comparsonPaint.setAntiAlias(true);

        clearPaint = new Paint();
        clearPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));

        baseLinePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        baseLinePaint.setColor(Color.GREEN);
        baseLinePaint.setStrokeWidth(2);

        dataComparison = new ConcurrentLinkedQueue<NoteInfo>();

        setZOrderOnTop(true);
        getHolder().setFormat(PixelFormat.TRANSPARENT);
        getHolder().addCallback(this);

        mCursor = BitmapFactory.decodeResource(getResources(), R.drawable.tv_staff_cursor);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        width = getMeasuredWidth();
        height = getMeasuredHeight();
        //预留空间画游标
        drawHeight = height - 2 * drawStartH;

        CURRENT_LEFT_DURATION = MAX_DURATION_SCREEN * BASE_LINE_W_PX / getWidth();
    }


    private void drawLine(Canvas canvas, NoteInfo one, Paint paint) {
        if (one == null) {
            return;
        }

        startX = (one.startPos - currentTime + CURRENT_LEFT_DURATION) * perSecW;
        startX = startX < 0 ? 0 : startX;
        endX = (one.startPos + one.len - currentTime + CURRENT_LEFT_DURATION) > MAX_DURATION_SCREEN ?
                width : (one.startPos + one.len - currentTime + CURRENT_LEFT_DURATION) * perSecW;

        RectF rect = new RectF(startX, (dataSources.getMaxY() - one.key) * perY + drawStartH - LINE_HEIGHT / 2,
                endX, (dataSources.getMaxY() - one.key) * perY + LINE_HEIGHT / 2 + drawStartH);

        canvas.drawRoundRect(rect, 5, 5, paint);
    }

    private void drawSourceLines(Canvas canvas) {
        if (dataSources != null) {
            NoteInfo one = null;
            int i = 0;

            for (i = currentIndex; i < dataSources.size(); i++) {
                one = dataSources.get(i);

                if (one.startPos + one.len >= currentTime - CURRENT_LEFT_DURATION) {
                    break;
                }
            }
            currentIndex = (i - 1) > 0 ? (i - 1) : 0;

            for (i = currentIndex; i < dataSources.size(); i++) {
                one = dataSources.get(i);
                if (one.startPos > currentTime + MAX_DURATION_SCREEN - CURRENT_LEFT_DURATION) {
                    break;
                }
                drawLine(canvas, one, standardPanit);
            }

        }
    }

    private void drawComparsonLine(Canvas canvas) {

        if (dataSources != null && dataComparison.size() > 0) {

            NoteInfo sourceItem = null;

            for (int i = currentIndex; i < dataSources.size(); i++) {
                sourceItem = dataSources.get(i);

                for (NoteInfo comp : dataComparison) {
                    if (comp.startPos < currentTime - CURRENT_LEFT_DURATION) {
                        dataComparison.poll();
                        continue;
                    }
                    if (comp.endPos > currentTime) {
                        break;
                    }
                    if (comp.startPos >= sourceItem.startPos
                            && comp.startPos <= sourceItem.endPos) {
                        comp.key = sourceItem.key;
                        cursorPos = comp.key;
                        comp.endPos = comp.endPos > sourceItem.endPos ? sourceItem.endPos : comp.endPos;
                        comp.len = comp.endPos - comp.startPos;
                        drawLine(canvas, comp, comparsonPaint);
                    }
                }
            }

            /*for (NoteInfo comp : dataComparison) {
                drawLine(canvas, comp, comparsonPaint);
            }*/
        }
    }

    private void resetXYSection() {
        if (dataSources != null) {
            for (NoteInfo info : dataSources) {
                float min = Math.min(dataSources.getMinY(), info.key);
                float max = Math.max(dataSources.getMaxY(), info.key);
                dataSources.setMaxY(max);
                dataSources.setMinY(min);
                cursorPos = min;

                if (info.endPos > currentTime + MAX_DURATION_SCREEN - CURRENT_LEFT_DURATION) {
                    break;
                }
            }
        }
    }

    private void drawVoiceCursor(Canvas canvas) {
        if (dataSources != null) {
            float top = (dataSources.getMaxY() - cursorPos) * perY - mCursor.getHeight() / 2 + drawStartH;
            float left = BASE_LINE_W_PX - mCursor.getWidth();
            canvas.drawBitmap(mCursor, left, top, comparsonPaint);
            canvas.drawBitmap(mCursor, left, top, comparsonPaint);
        }
    }

    public void setDataSources(VoiceDataSources dataSources) {
        this.dataSources = dataSources;
        cursorPos = dataSources.getMinY();

        resetData();
    }

    private void resetData() {
        dataComparison.clear();
        currentTime = 0;
        cursorPos = 0;
        currentIndex = 0;

        perSecW = width / MAX_DURATION_SCREEN;
        yDif = dataSources.getMaxY() - dataSources.getMinY();
        perY = drawHeight / yDif;
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        mLoaded = true;
        mSurfaceHolder = holder;
    }

    private SurfaceHolder mSurfaceHolder;

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        mLoaded = false;
        mSurfaceHolder = null;
    }

    @Override
    public void surfaceRedrawNeeded(SurfaceHolder holder) {

    }

    @Override
    public void onReceiveMicData(KeyInfo[] infos) {
        for (int i = 0; i < infos.length; i++) {
            NoteInfo item = new NoteInfo(infos[i].time, COMPARSON_LEN, infos[i].key, 0);
            dataComparison.add(item);
        }
//        Logger.d(TAG, "len="+infos.length+";pt="+currentTime+";st="+infos[0].time+";et="+infos[infos.length-1].time);
    }

    private boolean mLoaded = false;

    public void drawView() {
        if (!mLoaded || mSurfaceHolder == null)
            return;
        Canvas canvas = mSurfaceHolder.lockCanvas();
        try {
            if (canvas != null) {
                int w = canvas.getWidth();
                if (w <= 0)
                    Logger.d(TAG, "canvas <=0   =====================>");

                synchronized (mSurfaceHolder) {
//                getHolder().unlockCanvasAndPost(canvas);
                    canvas.drawPaint(clearPaint);
//            drawBaseLine(canvas);
                    resetXYSection();
                    canvas.save();
                    drawSourceLines(canvas);
                    drawComparsonLine(canvas);
                    drawVoiceCursor(canvas);
                    canvas.restore();
//                mSurfaceHolder.unlockCanvasAndPost(canvas);
                }
            }
        } catch (Exception e) {
            Logger.w(TAG, "drawView ex:" + e.toString());
        } finally {
            if (canvas != null)
                mSurfaceHolder.unlockCanvasAndPost(canvas);
        }
    }

}
