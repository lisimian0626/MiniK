package com.beidousat.karaoke.ui.dlg;

import com.beidousat.karaoke.model.Meal;

import java.util.ArrayList;
import java.util.List;


/**
 * author: Hanson
 * date   2017/4/10
 * describe:
 */

public class MealDataFactory {
    public static List<Meal> createTimeMeal() {
        List<Meal> array = new ArrayList<>();
        Meal meal1 = new Meal();
        meal1.setID(0x01);
        meal1.setAmount(15);
        meal1.setPrice(1200f);
        meal1.setType(Meal.TIME);
        array.add(meal1);

        Meal meal2 = new Meal();
        meal2.setID(0x02);
        meal2.setAmount(30);
        meal2.setPrice(2000f);
        meal2.setType(Meal.TIME);
        array.add(meal2);

        Meal meal3 = new Meal();
        meal3.setID(0x03);
        meal3.setAmount(60);
        meal3.setPrice(3800f);
        meal3.setType(Meal.TIME);
        array.add(meal3);

        return array;
    }

    public static List<Meal> createSongMeal() {
        List<Meal> array = new ArrayList<>();
        Meal meal1 = new Meal();
        meal1.setID(0x11);
        meal1.setAmount(1);
        meal1.setPrice(500f);
        meal1.setType(Meal.SONG);
        array.add(meal1);

        Meal meal2 = new Meal();
        meal2.setID(0x12);
        meal2.setAmount(5);
        meal2.setPrice(2000f);
        meal2.setType(Meal.SONG);
        array.add(meal2);

        Meal meal3 = new Meal();
        meal3.setID(0x13);
        meal3.setAmount(10);
        meal3.setPrice(3800f);
        meal3.setType(Meal.SONG);
        array.add(meal3);

        return array;
    }
}
