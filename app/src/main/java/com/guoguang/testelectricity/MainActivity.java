package com.guoguang.testelectricity;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.guoguang.ioctl.IoctlExec;

public class MainActivity extends AppCompatActivity {

    private TextView textView;

    private BatteryBroadCastReceiver castReceiver;

    private IoctlExec ioctlExec;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textView=(TextView)findViewById(R.id.testView);


    }

    @Override
    protected void onResume() {
        super.onResume();
        if(castReceiver==null){
            castReceiver=new BatteryBroadCastReceiver();
            IntentFilter filter=new IntentFilter();
            filter.addAction(Intent.ACTION_BATTERY_CHANGED);
            filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
            registerReceiver(castReceiver,filter);
        }
        if(ioctlExec==null){
            ioctlExec=new IoctlExec();
        }
        int flag=ioctlExec.openPort("/dev/ghgpiosel");
        Log.d(getClass().getName(),"flag=="+flag);
        BluetoothAdapter bluetoothAdapter=BluetoothAdapter.getDefaultAdapter();
        if(bluetoothAdapter.isEnabled()){
            ioctlExec.setLightOn(5,0);
        }else {
            ioctlExec.setLightOn(5,1);
        }

    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(castReceiver);
    }

    private class BatteryBroadCastReceiver extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.getAction().equals(Intent.ACTION_BATTERY_CHANGED)){
                int level=intent.getIntExtra("level",0);
                int scale=intent.getIntExtra("scale",0);
                int percent=(level*100)/scale;
                textView.setText("当前电量为："+percent+"%");
                if(percent>80){
                    ioctlExec.setLightOn(0,0);
                    ioctlExec.setLightOn(1,0);
                    ioctlExec.setLightOn(2,0);
                    ioctlExec.setLightOn(3,0);
                }else if(percent>60) {
                    ioctlExec.setLightOn(0,1);
                    ioctlExec.setLightOn(1,0);
                    ioctlExec.setLightOn(2,0);
                    ioctlExec.setLightOn(3,0);
                }else if(percent>46) {
                    ioctlExec.setLightOn(0,1);
                    ioctlExec.setLightOn(1,1);
                    ioctlExec.setLightOn(2,0);
                    ioctlExec.setLightOn(3,0);
                }else if(percent>20) {
                    ioctlExec.setLightOn(0,1);
                    ioctlExec.setLightOn(1,1);
                    ioctlExec.setLightOn(2,1);
                    ioctlExec.setLightOn(3,0);
                }else {
                    ioctlExec.setLightOn(0,1);
                    ioctlExec.setLightOn(1,1);
                    ioctlExec.setLightOn(2,1);
                    ioctlExec.setLightOn(3,1);
                }
            }

            //wangjr
            if(intent.getAction().equals(BluetoothAdapter.ACTION_STATE_CHANGED)){
               int blueState=intent.getIntExtra(BluetoothAdapter.EXTRA_STATE,0);
                if(blueState==BluetoothAdapter.STATE_ON){
                    ioctlExec.setLightOn(5,0);
                }else if (blueState==BluetoothAdapter.STATE_OFF){
                    ioctlExec.setLightOn(5,1);
                }
            }
        }
    }
}
