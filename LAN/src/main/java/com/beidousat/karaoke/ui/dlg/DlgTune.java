package com.beidousat.karaoke.ui.dlg;

import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager.LayoutParams;
import android.widget.TextView;

import com.beidousat.karaoke.R;

public class DlgTune extends Dialog implements OnClickListener {

    private TextView mTvMusic, mTvTone;
    private OnTuneListener mOnTuneListener;

    public DlgTune(Context context) {
        super(context, R.style.MyDialog);
        init();
    }

    public void setOnTuneListener(OnTuneListener listener) {
        this.mOnTuneListener = listener;
    }

    public void setCurrentMusicVol(int vol) {
        mTvMusic.setText(getContext().getString(R.string.music_vol_x, vol));
    }

    public void setCurrentTone(int tone) {
        mTvTone.setText(getContext().getString(R.string.tone_x, tone));
    }


    void init() {
        this.setContentView(R.layout.dlg_tune);
        LayoutParams lp = getWindow().getAttributes();
        lp.width = 790;
        lp.height = 460;
        lp.gravity = Gravity.CENTER;
        getWindow().setAttributes(lp);
        mTvMusic = (TextView) findViewById(R.id.tv_music);
        mTvTone = (TextView) findViewById(R.id.tv_tone);
        findViewById(R.id.iv_close).setOnClickListener(this);
        findViewById(R.id.iv_mic_down).setOnClickListener(this);
        findViewById(R.id.iv_mic_up).setOnClickListener(this);
        findViewById(R.id.iv_music_down).setOnClickListener(this);
        findViewById(R.id.iv_music_up).setOnClickListener(this);
        findViewById(R.id.iv_tone_down).setOnClickListener(this);
        findViewById(R.id.iv_tone_up).setOnClickListener(this);

        findViewById(R.id.iv_default).setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_close:
                dismiss();
                break;
            case R.id.iv_mic_down:
                if (mOnTuneListener != null)
                    mOnTuneListener.onMicDown();
                break;
            case R.id.iv_mic_up:
                if (mOnTuneListener != null)
                    mOnTuneListener.onMicUp();
                break;
            case R.id.iv_music_down:
                if (mOnTuneListener != null)
                    mOnTuneListener.onMusicDown();
                break;
            case R.id.iv_music_up:
                if (mOnTuneListener != null)
                    mOnTuneListener.onMusicUp();
                break;
            case R.id.iv_tone_down:
                if (mOnTuneListener != null)
                    mOnTuneListener.onToneDown();
                break;
            case R.id.iv_tone_up:
                if (mOnTuneListener != null)
                    mOnTuneListener.onToneUp();
                break;

            case R.id.iv_default:
                if (mOnTuneListener != null)
                    mOnTuneListener.onReset();
                break;
        }
    }

    @Override
    public void dismiss() {
        super.dismiss();
    }


    public interface OnTuneListener {
        void onMicDown();

        void onMicUp();

        void onMusicDown();

        void onMusicUp();

        void onToneDown();

        void onToneUp();

        void onReset();
    }

}
