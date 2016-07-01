package com.acmezon.acmezon_dash.bluetooth.Connecting;

import android.bluetooth.BluetoothDevice;
import android.os.ParcelUuid;

import java.util.ArrayList;


public class DeviceData {
    private String name = "";
    private String address = "";
    private ArrayList<ParcelUuid> uuids = null;

    public DeviceData(BluetoothDevice device, String emptyName) {
        name = device.getName();
        address = device.getAddress();

        if (name == null || name.isEmpty()) name = emptyName;
        uuids = BluetoothUtils.getDeviceUuids(device);
    }

    public String getName() {
        return name;
    }

    public void setName(String deviceName) {
        name = deviceName;
    }

    public String getAddress() {
        return address;
    }

    public ArrayList<ParcelUuid> getUuids() {
        return uuids;
    }
}
