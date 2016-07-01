package com.acmezon.acmezon_dash;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.acmezon.acmezon_dash.adapters.DateListAdapter;
import com.acmezon.acmezon_dash.bluetooth.BluetoothResponseHandler;
import com.acmezon.acmezon_dash.bluetooth.Commands;
import com.acmezon.acmezon_dash.bluetooth.Connecting.DeviceConnector;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileOutputStream;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class PastCartsActivity extends ListActivity {
    private TextView pastCartsEmpty;
    private final DateFormat format = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss", Locale.ENGLISH);
    private List<Date> pastCarts;
    private ProgressDialog loadingDialog;
    private ProgressDialog deleteDialog;
    private final String ACTION_CLOSE = "com.acmezon.acmezon_dash.ACTION_CLOSE";
    DeviceConnector connection;
    private Date toDelete;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_past_carts);
        pastCartsEmpty = (TextView) findViewById(R.id.past_carts_empty);
        pastCarts = new ArrayList<>();

        IntentFilter filter = new IntentFilter(ACTION_CLOSE);
        PastCartsReceiver pastCartsReceiver = new PastCartsReceiver();
        registerReceiver(pastCartsReceiver, filter);

        final DateListAdapter cartsAdapter = new DateListAdapter(
                this, R.layout.shopping_cart_row, pastCarts);

        setListAdapter(cartsAdapter);

        this.getListView().setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                deletePastCart(position);

                return true;
            }
        });

        connection = ((Application) getApplication()).getConnection();
        BluetoothResponseHandler bluetoothHandler = new BluetoothResponseHandler() {
            @Override
            public void onDeviceName(String deviceName) {
                super.onDeviceName(deviceName);
            }

            @Override
            public void onMessageRead(int bytes, String data) {
                switch (data.trim()) {
                    case "delete ok":
                        pastCarts.remove(toDelete);
                        toDelete = null;

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(PastCartsActivity.this,
                                        getString(R.string.past_cart_delete_ok),
                                        Toast.LENGTH_LONG).show();
                                cartsAdapter.notifyDataSetChanged();
                                deleteDialog.dismiss();
                            }
                        });
                        break;
                    default:
                        try {
                            JSONObject cartsJSON = new JSONObject(data);
                            String carts = cartsJSON.getString("carts");
                            if (!carts.equals("")) {
                                String[] cartsDates = carts.split(";");
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

                                Collections.sort(pastCarts, Collections.<Date>reverseOrder());
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        cartsAdapter.notifyDataSetChanged();
                                        loadingDialog.dismiss();
                                    }
                                });
                            } else {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        loadingDialog.dismiss();
                                        pastCartsEmpty.setVisibility(View.VISIBLE);
                                    }
                                });
                            }
                        } catch (JSONException e) {
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
                        break;
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

    private void deletePastCart(final int position) {
        AlertDialog.Builder alert = new AlertDialog.Builder(
                this);
        alert.setTitle(getString(R.string.delete_past_cart));
        alert.setMessage(getString(R.string.delete_past_cart_subtitle));
        alert.setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                toDelete = (Date) PastCartsActivity.this.getListView().getItemAtPosition(position);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        deleteDialog = ProgressDialog.show(PastCartsActivity.this, "",
                                getResources().getString(R.string.delete_past_cart_loading), true);
                    }
                });
                connection.write(String.format("[delete %s]", format.format(toDelete)).getBytes());
            }
        });
        alert.setNegativeButton(getString(R.string.no), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        alert.show();
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        Date selected = (Date) l.getItemAtPosition(position);

        Intent cartPreview = new Intent(PastCartsActivity.this, CartPreviewActivity.class);
        cartPreview.putExtra("cart", format.format(selected));
        startActivity(cartPreview);
    }

    class PastCartsReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.getAction().equals(ACTION_CLOSE)) {
                PastCartsActivity.this.finish();
            }
        }
    }
}
