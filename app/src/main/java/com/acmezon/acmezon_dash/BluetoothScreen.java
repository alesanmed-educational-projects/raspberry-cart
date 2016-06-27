package com.acmezon.acmezon_dash;

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
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.TextView;
import android.widget.Toast;

import com.acmezon.acmezon_dash.bluetooth.BluetoothResponseHandler;
import com.acmezon.acmezon_dash.bluetooth.ConnectDialog;
import com.acmezon.acmezon_dash.bluetooth.Connecting.DeviceConnector;
import com.acmezon.acmezon_dash.bluetooth.Connecting.DeviceData;
import com.acmezon.acmezon_dash.bluetooth.DeviceItem;
import com.acmezon.acmezon_dash.bluetooth.DeviceItemListAdapter;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;

import java.util.ArrayList;
import java.util.Set;

public class BluetoothScreen extends ListActivity implements ConnectDialog.ConnectDialogListener{
    private BluetoothAdapter BTAdapter;
    private DeviceItemListAdapter mAdapter;
    private ArrayList<DeviceItem> deviceItemList;
    private DeviceConnector connector;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private static BluetoothResponseHandler mHandler;
    private ProgressDialog loadingDialog;
    static BluetoothScreen instance;

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
        instance = this;
        setContentView(R.layout.activity_home_screen);

        deviceItemList = new ArrayList<>();
        BTAdapter = BluetoothAdapter.getDefaultAdapter();
        mAdapter = new DeviceItemListAdapter(this, android.R.layout.simple_list_item_1, deviceItemList);

        mSwipeRefreshLayout = (SwipeRefreshLayout) this.findViewById(R.id.devices_list_swipe);
        mSwipeRefreshLayout.setColorSchemeResources(R.color.loading_blue, R.color.loading_purple,
                                                    R.color.loading_lime, R.color.loading_tea);

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

            if (mHandler == null){
                mHandler = new BluetoothResponseHandler(){
                    @Override
                    public void onDeviceName(String deviceName) {
                        super.onDeviceName(deviceName);
                    }

                    @Override
                    public void onMessageRead(int bytes, String data) {
                        //Do nothing, just connect;
                    }

                    @Override
                    public void onMessageWritten(byte[] messageSended) {
                        //Do nothing, just connect;
                    }

                    @Override
                    public void onStateChange(int state) {
                        super.onStateChange(state);
                        Log.d("BLUETOOTH", "MESSAGE_STATE_CHANGE: " + state);
                        switch (state) {
                            case DeviceConnector.STATE_CONNECTED:
                                Log.d("BLUETOOTH", "Connected");
                                Thread connectedThread = new Thread(){
                                    @Override
                                    public void run() {
                                        loadingDialog.dismiss();
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                Toast.makeText(getApplicationContext(),
                                                        getResources().getString(R.string.bluetooth_connected),
                                                        Toast.LENGTH_LONG).show();
                                                Intent mainMenu = new Intent(BluetoothScreen.this, MainMenu.class);
                                                startActivity(mainMenu);
                                                BluetoothScreen.this.unregisterReceiver(bReciever);
                                                //finish();
                                            }
                                        });
                                    }
                                };

                                connectedThread.start();
                                break;
                            case DeviceConnector.STATE_CONNECTING:
                                Log.d("BLUETOOTH", "Connecting");
                                break;
                            case DeviceConnector.STATE_NONE:
                                Log.d("BLUETOOTH", "Not Connected");
                                Thread notConnectedThread = new Thread(){
                                    @Override
                                    public void run() {
                                        loadingDialog.dismiss();
                                        ((Application) getApplication()).setConnection(null);
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                Toast.makeText(getApplicationContext(),
                                                        getResources().getString(R.string.bluetooth_cant_connect),
                                                        Toast.LENGTH_LONG).show();
                                            }
                                        });
                                    }
                                };
                                notConnectedThread.start();
                                break;
                        }
                    }

                    @Override
                    public void onToast(Bundle data) {
                        super.onToast(data);
                    }
                };
            }

            setListAdapter(mAdapter);
            mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    Log.d("DEVICELISTSWIPE", "Has arrastrao el dedito. Mu bien miarma.");

                    updateFromSwipe();
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

        loadingDialog = ProgressDialog.show(this, "",
                getResources().getString(R.string.bluetooth_loading), true);

        loadingDialog.show();
        BluetoothDevice device = BTAdapter.getRemoteDevice(((ConnectDialog) dialog).getdMac());

        String emptyName = getString(R.string.empty_device_name);
        DeviceData data = new DeviceData(device, emptyName);
        connector = new DeviceConnector(data);
        connector.setHandler(mHandler);
        ((Application) getApplication()).setConnection(connector);
        ((Application) getApplication()).getConnection().connect();
    }

    @Override
    public void onDialogNegativeClick(DialogFragment dialog) {
        Log.d("DEVICECONNETION", "Cancel");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu items for use in the action bar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.bluetooth_menu, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.bluetooth_menu_refresh:
                Log.i("DEVICELISTSWIPE", "Ya ni con er deito...");

                if (!mSwipeRefreshLayout.isRefreshing()) {
                    mSwipeRefreshLayout.setRefreshing(true);
                }

                updateFromSwipe();

                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void updateFromSwipe() {
        TextView swipe_indicator = (TextView) this.findViewById(R.id.swipe_please);
        swipe_indicator.setVisibility(View.INVISIBLE);
        Set<BluetoothDevice> pairedDevices = BTAdapter.getBondedDevices();
        mAdapter.clear();
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

        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        this.registerReceiver(bReciever, filter);
        BTAdapter.startDiscovery();

        Log.d("DEVICELIST", "Adapter created\n");
        this.getListView().setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                BTAdapter.cancelDiscovery();
                DeviceItem device = (DeviceItem) parent.getItemAtPosition(position);
                Log.d("DEVICELIST", "DEVICE ITEM CLICKED: " + device.getDeviceName());

                FragmentManager fm = getFragmentManager();
                ConnectDialog bluetoothDialog = ConnectDialog.newInstance(device);

                bluetoothDialog.show(fm, "Connect dialog");
            }
        });

        mSwipeRefreshLayout.setRefreshing(false);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        BTAdapter.cancelDiscovery();
        if(connector != null)
            connector.stop();
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "BluetoothScreen Page", // TODO: Define a title for the content shown.
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
                "BluetoothScreen Page", // TODO: Define a title for the content shown.
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
