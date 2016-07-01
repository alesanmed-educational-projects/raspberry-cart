package com.acmezon.acmezon_dash.bluetooth;

import android.os.Bundle;
import android.util.Log;

/*
 * Created by alesanmed on 20/06/2016.
 */
public class BluetoothResponseHandler {
    public BluetoothResponseHandler() {
    }

    public void onStateChange(int state) {

    }

    public void onMessageRead(int bytes, String data) {
        if(data != null) {
            Log.d("BLUETOOTH", data);
        }
    }

    public void onMessageWritten(byte[] messageSended) { }

    public void onDeviceName(String deviceName) { }

    public void onToast(Bundle data) { }
}
