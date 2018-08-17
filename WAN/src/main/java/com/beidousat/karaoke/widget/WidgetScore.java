package com.beidousat.karaoke.widget;

import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.beidousat.karaoke.R;
import com.beidousat.karaoke.widget.visualizer.RecordVisualizerView;
import com.beidousat.libwidget.image.RecyclerImageView;
import com.nineoldandroids.animation.ObjectAnimator;
import com.nineoldandroids.view.ViewHelper;

/**
 * Created by J Wong on 2015/12/12 13:50.
 */
public class WidgetScore extends RelativeLayout {


    private RecordVisualizerView mVisualizerView;
    private TextView widgetScoreNum;
    private FlakeView mFlakeView;
    private RecyclerImageView mIvMode;

    public WidgetScore(Context context) {
        super(context);
        init();
    }

    public WidgetScore(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        View rootView = LayoutInflater.from(getContext()).inflate(R.layout.widget_score, this);
        mFlakeView = new FlakeView(getContext());

        FrameLayout view = (FrameLayout) rootView.findViewById(R.id.fl_flake);
        view.addView(mFlakeView, 0);

        mVisualizerView = (RecordVisualizerView) rootView.findViewById(R.id.visualizer);

        ViewTreeObserver observer = mVisualizerView.getViewTreeObserver();
        observer.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                mVisualizerView.setBaseY(71 * mVisualizerView.getHeight() / 100);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    mVisualizerView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                } else {
                    mVisualizerView.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                }
            }
        });
        widgetScoreNum = (TextView) rootView.findViewById(R.id.tv_num);

//        mFlakeView = (FlakeView) rootView.findViewById(R.id.fv_flakeView);


        mFlakeView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);

        mIvMode = (RecyclerImageView) rootView.findViewById(R.id.iv_mode);


    }


    public RecordVisualizerView getVisualizerView() {
        return mVisualizerView;
    }

    private int mCurScore;

    public void setScore(int score) {
        if (mCurScore != score) {
            mCurScore = score;
            ViewHelper.setPivotX(widgetScoreNum, widgetScoreNum.getWidth() / 2f);
            ViewHelper.setPivotY(widgetScoreNum, widgetScoreNum.getHeight() / 2f);
            ObjectAnimator.ofFloat(widgetScoreNum, "alpha", 1, 0, 1).setDuration(1500).start();
            ObjectAnimator.ofFloat(widgetScoreNum, "rotationY", 0, 180, 0).setDuration(1500).start();
            widgetScoreNum.postDelayed(new Runnable() {
                @Override
                public void run() {
                    widgetScoreNum.setText(String.valueOf(mCurScore));
                }
            }, 750);
        }
    }

    public void setVisualizerData(int decibel) {
        mVisualizerView.receive(decibel);
    }

    public void startFlake() {
        mFlakeView.resume();
    }

    public void stopFlake() {
        mFlakeView.pause();
    }

//    public void setMode(int mode) {
//        int resId = 0;
//        if (mode == 1) {
//            resId = R.drawable.tv_score_mode_n;
//        } else if (mode == 2) {
//            resId = R.drawable.tv_score_mode_p;
//        } else {
//            resId = 0;
//        }
//        mIvMode.setImageResource(resId);
//    }

    public void release() {
        mVisualizerView.receive(0);
        stopFlake();
    }
}
