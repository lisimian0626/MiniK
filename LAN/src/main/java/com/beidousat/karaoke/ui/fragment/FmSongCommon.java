package com.beidousat.karaoke.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.beidousat.karaoke.R;
import com.beidousat.karaoke.model.Songs;
import com.beidousat.libbns.net.request.HttpRequest;
import com.beidousat.libbns.net.request.RequestMethod;
import com.beidousat.libbns.util.Logger;

import java.util.Map;

/**
 * Created by J Wong on 2015/12/17 17:34.
 */
public class FmSongCommon extends FmSongBase {

    //    private List<Dictionary> mLanguages;
    private String[] mKeys, mValues;
    private String mLanguagesId;
    private String[] mSongLanguagesId, mSongLanguagesText;
    private int mWordCount = -1;

    private Map<String, String> mRequestParam;

    public static FmSongCommon newInstance(String[] keys, String[] vals) {
        FmSongCommon fragment = new FmSongCommon();
        Bundle args = new Bundle();
        args.putStringArray("keys", keys);
        args.putStringArray("vals", vals);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mKeys = getArguments().getStringArray("keys");
            mValues = getArguments().getStringArray("vals");
        }
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        mSongLanguagesId = getResources().getStringArray(R.array.api_song_language_id);
        mSongLanguagesText = getResources().getStringArray(R.array.api_song_language_text);
        mLanguagesId = mSongLanguagesId[0];
        mWidgetTopTabs.setLeftTabs(mSongLanguagesText);

        requestSongs();

        return mRootView;
    }


    private void requestSongs() {
        HttpRequest r = initRequest(RequestMethod.GET_SONG);
        r.addParam("Nums", String.valueOf(8));
        boolean isFirstSong = true;
        boolean isFirstScoreSong = false;
        boolean isFirstHdSong = false;
        if (mKeys != null && mKeys.length > 0 && mValues != null
                && mValues.length > 0 && mValues.length == mKeys.length) {
            for (int i = 0; i < mKeys.length; i++) {
                r.addParam(mKeys[i], mValues[i]);
                if ("IsGradeLib".equals(mKeys[i]) && "1".equals(mValues[i])) {
                    isFirstScoreSong = true;
                } else if ("IsClear".equals(mKeys[i]) && "1".equals(mValues[i])) {
                    isFirstHdSong = true;
                }
            }
        }
        if (!TextUtils.isEmpty(mLanguagesId)) {
            r.addParam("LanguageID", mLanguagesId);
            if (!mLanguagesId.equals(mSongLanguagesId[0])) {
                isFirstSong = false;
            }
        }
        if (!TextUtils.isEmpty(mSearchKeyword)) {
            r.addParam("Namesimplicity", mSearchKeyword);
            isFirstSong = false;
        }
        if (mWordCount > 0) {
            r.addParam("WordCount", String.valueOf(mWordCount));
            isFirstSong = false;
        }

        mRequestParam = r.getParams();
//        Songs songs;
//        if (isFirstSong && isFirstScoreSong && (songs = ScoreSongsTemp.getInstance(getContext().getApplicationContext()).getScoreSongs()) != null) {//评分 预加载歌曲
//            initSongPager(songs.totalPages, songs.list, mRequestParam);
//            mWidgetKeyboard.setWords(songs.NextWrod);
//            mWidgetKeyboard.setKeyboardKeyEnableText(songs.NextWrod);
//        } else if (isFirstSong && isFirstHdSong) {
//            r.setConvert2Class(Songs.class);
//            r.doPost(1);
//        } else if (isFirstSong && (songs = SpellSongsTemp.getInstance(getContext().getApplicationContext()).getSpellSongs()) != null) {
//            initSongPager(songs.totalPages, songs.list, mRequestParam);
//            mWidgetKeyboard.setWords(songs.NextWrod);
//            mWidgetKeyboard.setKeyboardKeyEnableText(songs.NextWrod);
//        } else {
        r.setConvert2Class(Songs.class);
        r.doPost(1);
//        }
    }


    @Override
    public void onLeftTabClick(int position) {
        mLanguagesId = mSongLanguagesId[position];
        requestSongs();
        super.onLeftTabClick(position);
    }

    @Override
    public void onInputTextChanged(String text) {
        mSearchKeyword = text;
        requestSongs();
        super.onInputTextChanged(text);
    }

    @Override
    public void onWordCountChanged(int count) {
        mWordCount = count;
        requestSongs();
        super.onWordCountChanged(count);
    }

    @Override
    public void onSuccess(String method, Object object) {
        if (RequestMethod.GET_SONG.equalsIgnoreCase(method)) {
            Songs songs = (Songs) object;
            if (songs != null) {
                if ((TextUtils.isEmpty(mSearchKeyword) && TextUtils.isEmpty(songs.Namesimplicity)) || songs.Namesimplicity.equals(mSearchKeyword)) {
                    initSongPager(songs.totalPages, songs.list, mRequestParam);
                    mWidgetKeyboard.setWords(songs.NextWrod);
                    mWidgetKeyboard.setKeyboardKeyEnableText(songs.NextWrod);
                }
            }
        }
        super.onSuccess(method, object);
    }

    @Override
    public void onFailed(String method, String error) {
        super.onFailed(method, error);
        if (RequestMethod.GET_SONG.equalsIgnoreCase(method)) {
            Logger.d("FmSongCommon", "GET_SONG onFailed ");
            initSongPager(0, null, mRequestParam);
        }
    }
}
