package com.beidousat.karaoke.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.TextView;

import com.beidousat.karaoke.R;
import com.beidousat.karaoke.data.BoughtMeal;
import com.beidousat.karaoke.model.Meal;
import com.beidousat.libbns.util.Logger;

import java.util.Observable;
import java.util.Observer;

/**
 * author: Hanson
 * date:   2017/4/12
 * describe:
 */

public class MealInfoTextView extends TextView implements Observer {
    private static final String TAG = MealInfoTextView.class.getSimpleName();

    public MealInfoTextView(Context context) {
        super(context);
    }

    public MealInfoTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MealInfoTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public MealInfoTextView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    public void update(Observable o, Object arg) {
        Meal meal = BoughtMeal.getInstance().getTheFirstMeal();
        if (meal != null && !BoughtMeal.getInstance().isMealExpire()) {
//            Logger.d(TAG, "meal 未过期");
            setSelected(true);
            switch (meal.getType()) {
                case Meal.SONG:
                    setText(getResources().getString(R.string.text_left_songs, BoughtMeal.getInstance().getLeftSongs()));
                    break;
                case Meal.TIME:
                    setText(getResources().getString(R.string.text_left_time, BoughtMeal.getInstance().getLeftMinite()));
                    break;
            }

        } else {
            Log.d("MealInfoTextView", "-------------过期");
            setText("");
            setSelected(false);
        }
        if (BoughtMeal.getInstance().isMealExpire()) {
            BoughtMeal.getInstance().clearMealInfoSharePreference();
            Log.e("test","清空套餐");
        } else {
            BoughtMeal.getInstance().saveMealInfoToSharePreference();
        }
    }

}
