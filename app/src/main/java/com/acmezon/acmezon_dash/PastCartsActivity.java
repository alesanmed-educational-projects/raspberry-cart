package com.acmezon.acmezon_dash;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.acmezon.acmezon_dash.adapters.DateListAdapter;
import com.acmezon.acmezon_dash.bluetooth.BluetoothResponseHandler;
import com.acmezon.acmezon_dash.bluetooth.Commands;
import com.acmezon.acmezon_dash.bluetooth.Connecting.DeviceConnector;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

public class PastCartsActivity extends ListActivity {
    private TextView pastCartsEmpty;
    private List<Date> pastCarts;
    private DeviceConnector connection;
    private ProgressDialog loadingDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_past_carts);

        pastCartsEmpty = (TextView) findViewById(R.id.past_carts_list_title);
        pastCarts = new ArrayList<>();

        final DateListAdapter cartsAdapter = new DateListAdapter(
                this, R.layout.shopping_cart_row, pastCarts);

        setListAdapter(cartsAdapter);

        connection = ((Application) getApplication()).getConnection();
        BluetoothResponseHandler bluetoothHandler = new BluetoothResponseHandler() {
            @Override
            public void onDeviceName(String deviceName) {
                super.onDeviceName(deviceName);
            }

            @Override
            public void onMessageRead(int bytes, String data) {
                try {
                    JSONObject cartsJSON = new JSONObject(data);
                    String carts = cartsJSON.getString("carts");
                    String[] cartsDates = carts.split(";");
                    DateFormat format = new SimpleDateFormat("yyyy.M.d.H.mm.ss", Locale.ENGLISH);
                    for (String cartsDate : cartsDates) {
                        try {
                            pastCarts.add(format.parse(cartsDate));
                        } catch (ParseException e) {
                            Log.d("PASTCARTS", e.getMessage());
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(PastCartsActivity.this,
                                            getString(R.string.past_cart_parse_error),
                                            Toast.LENGTH_LONG).show();
                                }
                            });
                        }
                    }

                    Collections.reverse(pastCarts);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            cartsAdapter.notifyDataSetChanged();
                            loadingDialog.dismiss();
                        }
                    });
                }catch (JSONException e) {
                    Log.d("PASTCARTS", e.getMessage());
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(PastCartsActivity.this,
                                    getString(R.string.past_cart_error),
                                    Toast.LENGTH_LONG).show();
                            finish();
                        }
                    });
                }
            }

            @Override
            public void onMessageWritten(byte[] messageSended) {
                Log.d("BLUETOOTH", "Message sended: " + new String(messageSended));
            }

            @Override
            public void onStateChange(int state) {
                //Do Nothing, just read/write
            }

            @Override
            public void onToast(Bundle data) {
                super.onToast(data);
            }
        };

        connection.setHandler(bluetoothHandler);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                loadingDialog = ProgressDialog.show(PastCartsActivity.this, "",
                        getResources().getString(R.string.past_carts_loading), true);

                Log.d("PASTCARTS", "Loading dialog created");
                loadingDialog.show();
            }
        });

        connection.write(Commands.GET_OLD_CARTS.getBytes());
    }
}
