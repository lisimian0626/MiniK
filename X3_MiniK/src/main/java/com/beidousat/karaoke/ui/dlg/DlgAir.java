package com.beidousat.karaoke.ui.dlg;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;

import com.beidousat.karaoke.R;
import com.beidousat.karaoke.data.PrefData;
import com.beidousat.karaoke.util.SerialController;
import com.beidousat.karaoke.util.ToastUtils;
import com.beidousat.libbns.evenbus.BusEvent;
import com.beidousat.libbns.evenbus.EventBusId;
import com.beidousat.libbns.util.Logger;
import com.beidousat.libwidget.image.RecyclerImageView;

import de.greenrobot.event.EventBus;

public class DlgAir extends BaseDialog implements OnClickListener {
    private final static String TAG = "DlgAir";
    private RecyclerImageView iv_open,iv_temp_up,iv_temp_down,iv_wind_up,iv_wind_down;
    private TextView tv_temp,tv_wind;
    private Handler handler;
    private final int SEND_MSG = 0;
    private final int def_temp=25;
    private final int def_wind=2;
    private int cur_temp=-1;
    private int cur_wind=-1;
    private boolean isopen;
    public DlgAir(Context context) {
        super(context, R.style.MyDialog);
        init();
//        EventBus.getDefault().register(this);
    }

    void init() {
        this.setContentView(R.layout.dlg_air);
        cur_temp=PrefData.getCurTemp(getContext());
        cur_wind=PrefData.getCurWind(getContext());
        isopen=PrefData.Is_INFRARED_OPEN(getContext());
        LayoutParams lp = getWindow().getAttributes();
        lp.width = 790;
        lp.height = 460;
        lp.gravity = Gravity.CENTER;
        getWindow().setAttributes(lp);
        iv_open = (RecyclerImageView) findViewById(R.id.air_iv_open);
        tv_temp=(TextView)findViewById(R.id.tv_tempture);
        tv_wind=(TextView)findViewById(R.id.tv_wind);
        findViewById(R.id.iv_close).setOnClickListener(this);
        iv_temp_up=(RecyclerImageView) findViewById(R.id.air_iv_temp_up);
        iv_temp_down=(RecyclerImageView) findViewById(R.id.air_iv_temp_down);
        iv_wind_up=(RecyclerImageView) findViewById(R.id.air_iv_wind_up);
        iv_wind_down=(RecyclerImageView) findViewById(R.id.air_iv_wind_down);
        iv_temp_up.setOnClickListener(this);
        iv_temp_down.setOnClickListener(this);
        iv_wind_up.setOnClickListener(this);
        iv_wind_down.setOnClickListener(this);
        findViewById(R.id.tv_test).setOnClickListener(this);
        if(isopen){
            iv_open.setImageResource(R.drawable.ic_air_on);
            setAble(true);
        }else{
            iv_open.setImageResource(R.drawable.ic_air_off);
            setAble(false);
        }
        tv_temp.setText(getContext().getString(R.string.tempture)+" "+cur_temp+"℃");
        if(cur_wind==3){
            tv_wind.setText(getContext().getString(R.string.wind_speed)+" "+"高");
        }else if(cur_wind==2){
            tv_wind.setText(getContext().getString(R.string.wind_speed)+" "+"中");
        }else if(cur_wind==1){
            tv_wind.setText(getContext().getString(R.string.wind_speed)+" "+"低");
        }else{
            tv_wind.setText(getContext().getString(R.string.wind_speed)+" "+"自动");
        }
        iv_open.setOnClickListener(this);

        handler = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(Message msg) {
                switch (msg.what) {
                    case SEND_MSG:
                        try {
                            byte[] cmd = new byte[4];
                            cmd[0] = (byte) 0x88;
                            cmd[1] = (byte) 0x01;
                            cmd[2] = (byte) 0x00;
                            cmd[3] = (byte) 0x00;
                            cmd[4] = (byte) 0x89;
                            SerialController.getInstance(getContext()).sendbyte(cmd);
                            Toast.makeText(getContext(), "发送：" + cmd.toString(), Toast.LENGTH_LONG).show();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        break;
                }
                return true;
            }
        });
    }



    private void setAble(boolean b) {
        iv_temp_up.setEnabled(b);
        iv_temp_down.setEnabled(b);
        iv_wind_up.setEnabled(b);
        iv_wind_down.setEnabled(b);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_close:
                dismiss();
                break;
            case R.id.air_iv_open:
                if (!isopen) {
                    //开
                    iv_open.setImageResource(R.drawable.ic_air_on);
                    setAble(true);
                    PrefData.setIs_INFRARED_OPEN(getContext(), true);
                    try {
                        byte[] cmd = new byte[5];
                        cmd[0] = (byte) 0x86;
                        cmd[1] = (byte) 0x00;
                        cmd[2] = (byte) 0x00;
                        cmd[3] = (byte) 0x00;
                        cmd[4] = (byte) 0x86;
                        SerialController.getInstance(getContext()).sendbyte(cmd);
                        Log.e("test", "cmd:" + 00);

                        isopen=!isopen;
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    //关
                    iv_open.setImageResource(R.drawable.ic_air_off);
                    setAble(false);
                    PrefData.setIs_INFRARED_OPEN(getContext(), false);
                    try {
                        byte[] cmd = new byte[5];
                        cmd[0] = (byte) 0x86;
                        cmd[1] = (byte) 0x01;
                        cmd[2] = (byte) 0x00;
                        cmd[3] = (byte) 0x00;
                        cmd[4] = (byte) 0x87;
                        SerialController.getInstance(getContext()).sendbyte(cmd);
                        Log.e("test", "cmd:" + 01);
                        isopen=!isopen;
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                break;
            case R.id.air_iv_temp_up:
                if(cur_temp==-1){
                    cur_temp=25;
                }
                switch (cur_temp){
                    case 21:
                        setTemp22();
                        cur_temp++;
                        break;
                    case 22:
                        setTemp23();
                        cur_temp++;
                        break;
                    case 23:
                        setTemp24();
                        cur_temp++;
                        break;
                    case 24:
                        setTemp25();
                        cur_temp++;
                        break;
                    case 25:
                        setTemp26();
                        cur_temp++;
                        break;
                    case 26:
                        setTemp27();
                        cur_temp++;
                        break;
                    case 27:
                        setTemp28();
                        cur_temp++;
                        break;
                    case 28:
                        setTemp28();
                        break;
                }
                tv_temp.setText(getContext().getString(R.string.tempture)+" "+cur_temp+"℃");
                break;
            case R.id.air_iv_temp_down:
                if(cur_temp==-1){
                    cur_temp=25;
                }
                switch (cur_temp){
                    case 21:
                        setTemp21();
                        break;
                    case 22:
                        setTemp21();
                        cur_temp--;
                        break;
                    case 23:
                        setTemp22();
                        cur_temp--;
                        break;
                    case 24:
                        setTemp23();
                        cur_temp--;
                        break;
                    case 25:
                        setTemp24();
                        cur_temp--;
                        break;
                    case 26:
                        setTemp25();
                        cur_temp--;
                        break;
                    case 27:
                        setTemp26();
                        cur_temp--;
                        break;
                    case 28:
                        setTemp27();
                        cur_temp--;
                        break;
                    case 29:
                        setTemp28();
                        cur_temp--;
                        break;
                }
                tv_temp.setText(getContext().getString(R.string.tempture)+" "+cur_temp+"℃");
                break;
            case R.id.air_iv_wind_up:
                if(cur_wind==-1){
                    cur_wind=2;
                }
                switch (cur_wind) {
                    case 1:
                        setWindMid();
                        cur_wind++;
                        break;
                    case 2:
                        setWindHigh();
                        cur_wind++;
                        break;
                    case 3:
                        setWindHigh();
                        break;

                }
//                if(cur_wind==3){
//                    tv_wind.setText(getContext().getString(R.string.wind_speed)+" "+"高");
//                }else if(cur_wind==2){
//                    tv_wind.setText(getContext().getString(R.string.wind_speed)+" "+"中");
//                }else if(cur_wind==1){
//                    tv_wind.setText(getContext().getString(R.string.wind_speed)+" "+"低");
//                }else{
//                    tv_wind.setText(getContext().getString(R.string.wind_speed)+" "+"自动");
//                }
                break;
            case R.id.air_iv_wind_down:
                if(cur_wind==-1){
                    cur_wind=2;
                }
                switch (cur_wind) {
                    case 1:
                        setWindLow();
                        break;
                    case 2:
                        setWindLow();
                        cur_wind--;
                        break;
                    case 3:
                        setWindMid();
                        cur_wind--;
                        break;
                }

                break;

        }
    }

    @Override
    public void dismiss() {
        if(cur_temp!=-1){
            PrefData.setCurTemp(getContext(),cur_temp);
        }
        if(cur_wind!=-1) {
            PrefData.setCurWind(getContext(), cur_wind);
        }
        super.dismiss();
    }

    private void setTemp21(){
        byte[] cmd=new byte[5];
        cmd[0]=(byte)0x86;
        cmd[1]=(byte)0x02;
        cmd[2]=(byte)0x00;
        cmd[3]=(byte)0x00;
        cmd[4]=(byte)0x84;
        SerialController.getInstance(getContext()).sendbyte(cmd);
    }
    private void setTemp22(){
        byte[] cmd=new byte[5];
        cmd[0]=(byte)0x86;
        cmd[1]=(byte)0x03;
        cmd[2]=(byte)0x00;
        cmd[3]=(byte)0x00;
        cmd[4]=(byte)0x85;
        SerialController.getInstance(getContext()).sendbyte(cmd);
    }
    private void setTemp23(){
        byte[] cmd=new byte[5];
        cmd[0]=(byte)0x86;
        cmd[1]=(byte)0x04;
        cmd[2]=(byte)0x00;
        cmd[3]=(byte)0x00;
        cmd[4]=(byte)0x82;
        SerialController.getInstance(getContext()).sendbyte(cmd);
    }
    private void setTemp24(){
        byte[] cmd=new byte[5];
        cmd[0]=(byte)0x86;
        cmd[1]=(byte)0x05;
        cmd[2]=(byte)0x00;
        cmd[3]=(byte)0x00;
        cmd[4]=(byte)0x83;
        SerialController.getInstance(getContext()).sendbyte(cmd);
    }
    private void setTemp25(){
        byte[] cmd=new byte[5];
        cmd[0]=(byte)0x86;
        cmd[1]=(byte)0x06;
        cmd[2]=(byte)0x00;
        cmd[3]=(byte)0x00;
        cmd[4]=(byte)0x80;
        SerialController.getInstance(getContext()).sendbyte(cmd);
    }
    private void setTemp26(){
        byte[] cmd=new byte[5];
        cmd[0]=(byte)0x86;
        cmd[1]=(byte)0x07;
        cmd[2]=(byte)0x00;
        cmd[3]=(byte)0x00;
        cmd[4]=(byte)0x81;
        SerialController.getInstance(getContext()).sendbyte(cmd);
    }
    private void setTemp27(){
        byte[] cmd=new byte[5];
        cmd[0]=(byte)0x86;
        cmd[1]=(byte)0x08;
        cmd[2]=(byte)0x00;
        cmd[3]=(byte)0x00;
        cmd[4]=(byte)0x8E;
        SerialController.getInstance(getContext()).sendbyte(cmd);
    }
    private void setTemp28(){
        byte[] cmd=new byte[5];
        cmd[0]=(byte)0x86;
        cmd[1]=(byte)0x09;
        cmd[2]=(byte)0x00;
        cmd[3]=(byte)0x00;
        cmd[4]=(byte)0x8F;
        SerialController.getInstance(getContext()).sendbyte(cmd);
    }
//    private void setTemp29(){
//        byte[] cmd=new byte[5];
//        cmd[0]=(byte)0x86;
//        cmd[1]=(byte)0x10;
//        cmd[2]=(byte)0x00;
//        cmd[3]=(byte)0x00;
//        cmd[4]=(byte)0x96;
//        SerialController.getInstance(getContext()).sendbyte(cmd);
//    }
    private void setWindAuto(){
        byte[] cmd=new byte[5];
        cmd[0]=(byte)0x86;
        cmd[1]=(byte)0x11;
        cmd[2]=(byte)0x00;
        cmd[3]=(byte)0x00;
        cmd[4]=(byte)0x97;
        SerialController.getInstance(getContext()).sendbyte(cmd);
        tv_wind.setText(getContext().getString(R.string.wind_speed)+" "+"自动");
    }
    private void setWindHigh(){
        byte[] cmd=new byte[5];
        cmd[0]=(byte)0x86;
        cmd[1]=(byte)0x12;
        cmd[2]=(byte)0x00;
        cmd[3]=(byte)0x00;
        cmd[4]=(byte)0x94;
        SerialController.getInstance(getContext()).sendbyte(cmd);
        tv_wind.setText(getContext().getString(R.string.wind_speed)+" "+"高");
    }
    private void setWindMid(){
        byte[] cmd=new byte[5];
        cmd[0]=(byte)0x86;
        cmd[1]=(byte)0x13;
        cmd[2]=(byte)0x00;
        cmd[3]=(byte)0x00;
        cmd[4]=(byte)0x95;
        SerialController.getInstance(getContext()).sendbyte(cmd);
        tv_wind.setText(getContext().getString(R.string.wind_speed)+" "+"中");
    }
    private void setWindLow(){
        byte[] cmd=new byte[5];
        cmd[0]=(byte)0x86;
        cmd[1]=(byte)0x14;
        cmd[2]=(byte)0x00;
        cmd[3]=(byte)0x00;
        cmd[4]=(byte)0x92;
        SerialController.getInstance(getContext()).sendbyte(cmd);
        tv_wind.setText(getContext().getString(R.string.wind_speed)+" "+"低");
    }
}
