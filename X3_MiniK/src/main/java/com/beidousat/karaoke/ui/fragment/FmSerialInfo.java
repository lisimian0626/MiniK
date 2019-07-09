package com.beidousat.karaoke.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.Toast;

import com.beidousat.karaoke.R;
import com.beidousat.karaoke.data.PrefData;
import com.beidousat.libbns.evenbus.EventBusId;
import com.beidousat.libbns.evenbus.EventBusUtil;

public class FmSerialInfo extends BaseFragment implements View.OnClickListener {
    private Spinner spinner_rj45, spinner_down, spinner_up;
    private String[] spinner_item;
    private int select_rj45, select_up, select_down;

    //    private ArrayAdapter<String> arrayAdapter;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.fm_setting_serial_info, null);
        mRootView.findViewById(R.id.iv_back).setOnClickListener(this);
        mRootView.findViewById(R.id.setting_spinner_tv_save).setOnClickListener(this);
        spinner_rj45 = (Spinner) mRootView.findViewById(R.id.setting_spinner_rj45);
        spinner_down = (Spinner) mRootView.findViewById(R.id.setting_spinner_down);
        spinner_up = (Spinner) mRootView.findViewById(R.id.setting_spinner_up);
        spinner_item = getResources().getStringArray(R.array.spinner_item);
        spinner_rj45.setSelection(PrefData.getSERIAL_RJ45(getContext()));
        spinner_rj45.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                select_rj45 = position;

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        spinner_up.setSelection(PrefData.getSERIAL_UP(getContext()));
        spinner_up.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                select_up = position;

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        spinner_down.setSelection(PrefData.getSERIAL_DOWN(getContext()));
        spinner_down.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                select_down = position;

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        return mRootView;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_back:
                EventBusUtil.postSticky(EventBusId.id.BACK_FRAGMENT, "");
                break;
            case R.id.setting_spinner_tv_save:
                if (select_rj45 == select_up || select_rj45 == select_down || select_up == select_down) {
                    Toast.makeText(getContext(), "串口不能重复,请重新选择", Toast.LENGTH_SHORT).show();
                } else {
                    save();
                }
                break;
        }
    }

    private void save() {
        PrefData.setSERIAL_RJ45(getContext(), select_rj45);
        PrefData.setSERIAL_UP(getContext(), select_up);
        PrefData.setSERIAL_DOWN(getContext(), select_down);
        EventBusUtil.postSticky(EventBusId.id.UPDATA_SERIAL_SUCCED, "");
    }
}
