package com.acmezon.acmezon_dash;

import com.acmezon.acmezon_dash.bluetooth.Connecting.DeviceConnector;

/**
 * Created by alesanmed on 8/06/16.
 */
public class Application extends android.app.Application {
    private DeviceConnector connection;

    @Override
    public void onCreate() {
        super.onCreate();
    }

    public synchronized DeviceConnector getConnection() {
        if (connection == null) {
            return null;
        }

        return connection;
    }

    public synchronized  void setConnection(DeviceConnector connection) {
        this.connection = connection;
    }
}
