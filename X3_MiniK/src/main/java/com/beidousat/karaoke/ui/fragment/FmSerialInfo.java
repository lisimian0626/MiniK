package com.beidousat.karaoke.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.beidousat.karaoke.R;
import com.beidousat.karaoke.data.PrefData;

public class FmSerialInfo extends BaseFragment implements View.OnClickListener, AdapterView.OnItemSelectedListener {
    private Spinner spinner_rj45,spinner_down,spinner_up;
    private String[] spinner_item;
//    private ArrayAdapter<String> arrayAdapter;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.fm_setting_serial_info, null);
        mRootView.findViewById(R.id.iv_back).setOnClickListener(this);
        spinner_rj45= (Spinner) mRootView.findViewById(R.id.setting_spinner_rj45);
        spinner_down= (Spinner) mRootView.findViewById(R.id.setting_spinner_down);
        spinner_up= (Spinner) mRootView.findViewById(R.id.setting_spinner_up);
        spinner_item = getResources().getStringArray(R.array.spinner_item);
//        arrayAdapter=new ArrayAdapter<String>(getContext(),android.R.layout.simple_spinner_item,spinner_item);
//        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//        spinner_rj45.setAdapter(arrayAdapter);
//        spinner_down.setAdapter(arrayAdapter);
//        spinner_up.setAdapter(arrayAdapter);
//        spinner_rj45.setOnItemSelectedListener(this);
//        spinner_down.setOnItemSelectedListener(this);
//        spinner_up.setOnItemSelectedListener(this);

        return mRootView;
    }

    @Override
    public void onClick(View v) {

    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        switch (view.getId()){
            case R.id.setting_spinner_rj45:
                if(spinner_item.length>0)
                PrefData.setSERIAL_RJ45(getContext(),spinner_item[position]);
                break;
            case R.id.setting_spinner_down:
                if(spinner_item.length>0)
                PrefData.setSERIAL_DOWN(getContext(),spinner_item[position]);
                break;
            case R.id.setting_spinner_up:
                if(spinner_item.length>0)
                PrefData.setSERIAL_UP(getContext(),spinner_item[position]);
                break;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
