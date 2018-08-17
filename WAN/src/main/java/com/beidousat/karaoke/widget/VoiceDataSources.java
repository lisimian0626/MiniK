package com.beidousat.karaoke.widget;

import com.beidousat.score.NoteInfo;

import java.util.LinkedList;

/**
 * author: Hanson
 * date:   2016/6/14
 * describe:
 */
public class VoiceDataSources extends LinkedList<NoteInfo> {
    private float minY = Float.MAX_VALUE;
    private float maxY = Float.MIN_VALUE;

    public float getMinY() {
        return minY;
    }

    public void setMinY(float minY) {
        this.minY = minY;
    }

    public float getMaxY() {
        return maxY;
    }

    public void setMaxY(float maxY) {
        this.maxY = maxY;
    }
}
