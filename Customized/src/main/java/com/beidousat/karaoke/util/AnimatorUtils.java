package com.beidousat.karaoke.util;

import android.animation.Animator;
import android.animation.TypeEvaluator;
import android.animation.ValueAnimator;
import android.graphics.Point;
import android.graphics.PointF;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;

import com.beidousat.karaoke.R;


/**
 * author: Hanson
 * date:   2017/5/10
 * describe:
 */

public class AnimatorUtils {
    private static final String TAG = "AnimatorUtils";

    private static void animateParabola(final ViewGroup parent, final View src, View target) {
        final int[] locTag = new int[2];
        final int[] locSrc = new int[2];
        target.getLocationInWindow(locTag);
        src.getLocationInWindow(locSrc);

        int pointX = (locSrc[0] + locTag[0]) / 2;
        int pointY = locSrc[1] - locTag[1];
        final Point controllPoint = new Point(pointX, pointY);

        ValueAnimator valueAnimator = new ValueAnimator();
        valueAnimator.setDuration(500);
        valueAnimator.setObjectValues(new PointF(locSrc[0], locSrc[1]), new PointF(locTag[0], locTag[1]));
        valueAnimator.setInterpolator(new LinearInterpolator());
        valueAnimator.setEvaluator(new TypeEvaluator<PointF>() {
            // fraction = t / duration
            @Override
            public PointF evaluate(float fraction, PointF startValue,
                                   PointF endValue) {
                //贝塞尔曲线
                int x = (int) ((1 - fraction) * (1 - fraction) * startValue.x + 2 * fraction * (1 - fraction) * controllPoint.x + fraction * fraction * endValue.x);
                int y = (int) ((1 - fraction) * (1 - fraction) * startValue.y + 2 * fraction * (1 - fraction) * controllPoint.y + fraction * fraction * endValue.y);
                PointF p =  new PointF(x, y);

                return p;
            }
        });

        valueAnimator.start();
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                PointF point = (PointF) animation.getAnimatedValue();
                src.setX(point.x);
                src.setY(point.y);
            }
        });
        valueAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                ((ViewGroup)parent.getParent()).removeView(src);
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
    }

    private static View addAnimatorView(ViewGroup parent) {
        View view = new View(parent.getContext());
        view.setX(parent.getWidth()/2);
        view.setY(parent.getHeight()/2);
        view.setBackgroundResource(R.drawable.bg_round_progress);
        ((ViewGroup)parent.getParent()).addView(view, 44, 44);

        return view;
    }

    public static void playParabolaAnimator(ViewGroup parent, View target) {
        View view = addAnimatorView(parent);
        animateParabola(parent, view, target);
    }
}
