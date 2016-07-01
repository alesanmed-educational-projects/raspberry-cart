package com.acmezon.acmezon_dash.bluetooth.Connecting;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.ParcelUuid;
import android.util.Log;

import java.lang.reflect.Method;
import java.util.ArrayList;

public class BluetoothUtils {
    private static final String TAG = "BluetoothUtils";
    private static final boolean D = true;

    public static ArrayList<ParcelUuid> getDeviceUuids(BluetoothDevice device) {
        ArrayList<ParcelUuid> result = new ArrayList<>();

        try {
            Method method = device.getClass().getMethod("getUuids");
            ParcelUuid[] phoneUuids = (ParcelUuid[]) method.invoke(device);
            if (phoneUuids != null) {
                for (ParcelUuid uuid : phoneUuids) {
                    if (D) Log.d(TAG, device.getName() + ": " + uuid.toString());
                    result.add(uuid);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            if (D) Log.e(TAG, "getDeviceUuids() failed", e);
        }

        return result;
    }

    /**
     * see http://habrahabr.ru/post/144547/
     */
    public static BluetoothSocket createRfcommSocket(BluetoothDevice device) {
        BluetoothSocket tmp = null;
        try {
            Class<?> class1 = device.getClass();
            Class aclass[] = new Class[1];
            aclass[0] = Integer.TYPE;
            Method method = class1.getMethod("createRfcommSocket", aclass);
            Object aobj[] = new Object[1];
            aobj[0] = 1;

            tmp = (BluetoothSocket) method.invoke(device, aobj);
        } catch (Exception e) {
            e.printStackTrace();
            if (D) Log.e(TAG, "createRfcommSocket() failed", e);
        }
        return tmp;
    }
}
