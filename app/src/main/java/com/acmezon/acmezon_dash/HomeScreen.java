package com.acmezon.acmezon_dash;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DialogFragment;
import android.app.FragmentManager;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;

import com.acmezon.acmezon_dash.bluetooth.ConnectDialog;
import com.acmezon.acmezon_dash.bluetooth.ConnectThread;
import com.acmezon.acmezon_dash.bluetooth.DeviceItem;
import com.acmezon.acmezon_dash.bluetooth.DeviceItemListAdapter;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;

import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;

public class HomeScreen extends ListActivity implements ConnectDialog.ConnectDialogListener{
    private BluetoothAdapter BTAdapter;
    private DeviceItemListAdapter mAdapter;
    private ArrayList<DeviceItem> deviceItemList;
    private ConnectThread connection;

    public static int REQUEST_BLUETOOTH = 1;
    private final BroadcastReceiver bReciever = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                Log.d("DEVICELIST", "Bluetooth device found\n");
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                // Create a new device item
                DeviceItem newDevice = new DeviceItem(device.getName(), device.getAddress(), "false");
                // Add it to our adapter
                deviceItemList.add(newDevice);
                mAdapter.notifyDataSetChanged();
            }
        }
    };

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen);

        deviceItemList = new ArrayList<DeviceItem>();
        BTAdapter = BluetoothAdapter.getDefaultAdapter();
        mAdapter = new DeviceItemListAdapter(this, android.R.layout.simple_list_item_1, deviceItemList);
        // Phone does not support Bluetooth so let the user know and exit.
        if (BTAdapter == null) {
            new AlertDialog.Builder(this)
                    .setTitle("Not compatible")
                    .setMessage("Your phone does not support Bluetooth")
                    .setPositiveButton("Exit", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            System.exit(0);
                        }
                    })
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
        } else {
            if (!BTAdapter.isEnabled()) {
                Intent enableBT = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBT, REQUEST_BLUETOOTH);
            }
            BTAdapter.startDiscovery();

            Set<BluetoothDevice> pairedDevices = BTAdapter.getBondedDevices();
            if (pairedDevices.size() > 0) {
                for (BluetoothDevice device : pairedDevices) {
                    DeviceItem newDevice= new DeviceItem(device.getName(),device.getAddress(),"false");
                    Log.d("DEVICELIST", "DEVICE:" + newDevice.getDeviceName() + "\n");
                    deviceItemList.add(newDevice);
                    mAdapter.notifyDataSetChanged();
                }
            }

            // If there are no devices, add an item that states so. It will be handled in the view.
            if(deviceItemList.size() == 0) {
                Log.d("DEVICELIST", "NO DEVICES\n");
            }


            Log.d("DEVICELIST", "Adapter created\n");
            setListAdapter(mAdapter);
            this.getListView().setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    DeviceItem device = (DeviceItem) parent.getItemAtPosition(position);
                    Log.d("DEVICELIST", "DEVICE ITEM CLICKED: " + device.getDeviceName());

                    FragmentManager fm = getFragmentManager();
                    ConnectDialog bluetoothDialog = ConnectDialog.newInstance(device);

                    bluetoothDialog.show(fm, "Connect dialog");
                }
            });
        }

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    @Override
    public void onDialogPositiveClick(final DialogFragment dialog) {
        Log.d("DEVICECONNECTION", "Connecting to device: " + ((ConnectDialog) dialog).getdName());
        final ProgressDialog loadingDialog = ProgressDialog.show(this, "",
                getResources().getString(R.string.bluetooth_loading), true);

        loadingDialog.show();

        Thread bluetoothThread = new Thread() {
            @Override
            public void run() {
                BluetoothDevice device = BTAdapter.getRemoteDevice(((ConnectDialog) dialog).getdMac());

                Log.d("DEVICECONNECTION", "Device to connect: " + device.getName() + " - " + device.getAddress());
                connection = new ConnectThread(device,
                        UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"));

                BTAdapter.cancelDiscovery();
                Boolean res = connection.connect();

                Log.d("DEVICECONNETION", "Connected to device " + ((ConnectDialog) dialog).getdName() + ": " + res);
                loadingDialog.dismiss();

                if (res) {
                    Toast.makeText(getApplicationContext(),
                            getResources().getString(R.string.bluetooth_connected),
                            Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getApplicationContext(),
                            getResources().getString(R.string.bluetooth_cant_connect),
                            Toast.LENGTH_LONG).show();
                }
            }
        };

        bluetoothThread.start();
    }

    @Override
    public void onDialogNegativeClick(DialogFragment dialog) {
        Log.d("DEVICECONNETION", "Cancel");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        BTAdapter.cancelDiscovery();
        if (connection != null) {
            connection.cancel();
        }
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "HomeScreen Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app URL is correct.
                Uri.parse("android-app://com.acmezon.acmezon_dash/http/host/path")
        );
        AppIndex.AppIndexApi.start(client, viewAction);
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "HomeScreen Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app URL is correct.
                Uri.parse("android-app://com.acmezon.acmezon_dash/http/host/path")
        );
        AppIndex.AppIndexApi.end(client, viewAction);
        client.disconnect();
    }
}
