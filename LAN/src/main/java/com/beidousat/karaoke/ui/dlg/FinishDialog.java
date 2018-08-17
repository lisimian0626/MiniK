package com.beidousat.karaoke.ui.dlg;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.beidousat.karaoke.R;
import com.beidousat.karaoke.widget.CountDownTextView;
import com.beidousat.libbns.evenbus.BusEvent;
import com.beidousat.libbns.evenbus.EventBusId;
import com.beidousat.libbns.evenbus.EventBusUtil;

import de.greenrobot.event.EventBus;

/**
 * author: Hanson
 * date:   2017/3/31
 * describe:
 */

public class FinishDialog extends DialogFragment {
    private View mRootView;
    private TextView mGotoShare;
    private TextView mGotoContinue;
    private CountDownTextView mCountdown;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(STYLE_NORMAL, R.style.CommonDialog);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        setCancelable(false);

        mRootView = inflater.inflate(R.layout.dlg_finish, container, false);

        initData();
        intView();
        setListener();

        return mRootView;
    }

    private void initData() {

    }

    private void intView() {
        mCountdown = (CountDownTextView) mRootView.findViewById(R.id.countdown);
        mGotoShare = (TextView) mRootView.findViewById(R.id.goto_share);
        mGotoContinue = (TextView) mRootView.findViewById(R.id.goto_continue);
    }

    private void setListener() {
        mGotoContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CommonDialog commonDialog = CommonDialog.getInstance();
                commonDialog.setContent(FmPayMeal.createMealFragment(FmPayMeal.TYPE_CNT_RENEW));
                commonDialog.setShowClose(true);

                if (!commonDialog.isAdded()) {
                    commonDialog.show(getFragmentManager(), "commondialog");
                }
                dismiss();
            }
        });

        mGotoShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CommonDialog commonDialog = CommonDialog.getInstance();
                commonDialog.setContent(new FmShare());
                commonDialog.setShowClose(false);

                if (!commonDialog.isAdded()) {
                    commonDialog.show(getFragmentManager(), "commondialog");
                }
                dismiss();
            }
        });

        mCountdown.setFinishedListener(new CountDownTextView.TimerFinished() {
            @Override
            public void onFinish() {
                dismiss();
                EventBusUtil.postRoomClose(null);
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        mCountdown.start();
    }

    @Override
    public void onPause() {
        super.onPause();
        mCountdown.stop();
    }
}
