package com.beidousat.karaoke.ui.dlg;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager.LayoutParams;
import android.widget.TextView;

import com.beidousat.karaoke.R;
import com.beidousat.libbns.evenbus.BusEvent;
import com.beidousat.libbns.evenbus.EventBusId;

import de.greenrobot.event.EventBus;

public class DlgTune extends BaseDialog implements OnClickListener {

    private TextView mTvMusic, mTvTone, mTvMic, mTvEff;
    private OnTuneListener mOnTuneListener;

    public DlgTune(Context context) {
        super(context, R.style.MyDialog);
        init();

        EventBus.getDefault().register(this);

    }

    public void setOnTuneListener(OnTuneListener listener) {
        this.mOnTuneListener = listener;
    }

    public void setCurrentMusicVol(int vol) {
        int percent = (int) (100 * vol / 15);
        mTvMusic.setText(getContext().getString(R.string.music_vol_x, vol + ""));
    }

    public void setCurrentTone(int tone) {
        mTvTone.setText(getContext().getString(R.string.tone_x, tone - 100));
    }

    public void setCurrentMic(int vol) {
//        int percent = (int) (100 * vol / 82);
        mTvMic.setText(getContext().getString(R.string.mic_x, vol + ""));
    }


    public void setCurrentEff(int vol) {
//        int percent = (int) (100 * vol / 82);
        mTvEff.setText(getContext().getString(R.string.eff_x, vol + ""));
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
        mTvMic = (TextView) findViewById(R.id.tv_mic);
        mTvEff = (TextView) findViewById(R.id.tv_reverb);

        findViewById(R.id.iv_close).setOnClickListener(this);
        findViewById(R.id.iv_mic_down).setOnClickListener(this);
        findViewById(R.id.iv_mic_up).setOnClickListener(this);
        findViewById(R.id.iv_music_down).setOnClickListener(this);
        findViewById(R.id.iv_music_up).setOnClickListener(this);
        findViewById(R.id.iv_tone_down).setOnClickListener(this);
        findViewById(R.id.iv_tone_up).setOnClickListener(this);
        findViewById(R.id.iv_reverb_up).setOnClickListener(this);
        findViewById(R.id.iv_reverb_down).setOnClickListener(this);

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
            case R.id.iv_reverb_down:
                if (mOnTuneListener != null)
                    mOnTuneListener.onReverbDown();
                break;
            case R.id.iv_reverb_up:
                if (mOnTuneListener != null)
                    mOnTuneListener.onReverbUp();
                break;
        }
    }

    @Override
    public void dismiss() {
        EventBus.getDefault().unregister(this);
        super.dismiss();
    }

    public void onEventMainThread(BusEvent event) {
        switch (event.id) {
            case EventBusId.SERIAL.SERIAL_EFF_VOL:
                int effVol = Integer.valueOf(event.data.toString());
                setCurrentEff(effVol);
                break;
            case EventBusId.SERIAL.SERIAL_MIC_VOL:
                int micVol = Integer.valueOf(event.data.toString());
                setCurrentMic(micVol);
                break;
        }
    }

    public interface OnTuneListener {
        void onMicDown();

        void onMicUp();

        void onMusicDown();

        void onMusicUp();

        void onToneDown();

        void onToneUp();

        void onReset();

        void onReverbUp();

        void onReverbDown();
    }

}
