package com.acmezon.acmezon_dash.bluetooth;

/*
 * Created by Matt on 5/12/2015.
 */
public class DeviceItem {

    private String deviceName;
    private String address;

    public String getDeviceName() {
        return deviceName;
    }

    public String getAddress() {
        return address;
    }

    public DeviceItem(String name, String address){
        this.deviceName = name;
        this.address = address;
    }
}
