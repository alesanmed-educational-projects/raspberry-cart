package com.acmezon.acmezon_dash;

import com.acmezon.acmezon_dash.bluetooth.ConnectThread;

/**
 * Created by donzok on 8/06/16.
 */
public class Application extends android.app.Application {
    private ConnectThread connection;

    @Override
    public void onCreate() {
        super.onCreate();
    }

    public synchronized ConnectThread getConnection() {
        if (connection == null) {
            return null;
        }

        return connection;
    }

    public synchronized  void setConnection(ConnectThread connection) {
        this.connection = connection;
    }
}
