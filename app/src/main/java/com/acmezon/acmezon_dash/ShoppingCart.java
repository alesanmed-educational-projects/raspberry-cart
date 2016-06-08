package com.acmezon.acmezon_dash;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

import com.acmezon.acmezon_dash.bluetooth.ConnectThread;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Iterator;
import java.util.Timer;
import java.util.TimerTask;

public class ShoppingCart extends AppCompatActivity {
    private BufferedReader bluetoothReader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shopping_cart);

        ConnectThread connection = ((Application) getApplication()).getConnection();

        Log.d("SHOPPINGCART", connection.getbTDevice().getAddress());
        InputStream tmpStream;
        InputStreamReader tmpReader;
        try {
            tmpStream = connection.getbTSocket().getInputStream();
            tmpReader = new InputStreamReader(tmpStream);
            bluetoothReader = new BufferedReader(tmpReader);
        } catch (IOException e) {
            close(bluetoothReader);
        }

        if(bluetoothReader != null) {
            final TextView bluetoothText = (TextView) findViewById(R.id.bluetoothInput);

            Thread updateThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    assert bluetoothText != null;
                    while(true) {
                        try {
                            final String new_line = bluetoothReader.readLine();
                            if (new_line.isEmpty()) {
                                continue;
                            }

                            JSONObject productsJSON = new JSONObject(new_line);
                            String text = "";

                            JSONObject products = ((JSONObject) productsJSON.get("products"));
                            JSONObject barcodes = ((JSONObject) productsJSON.get("barcodes"));

                            Iterator<String> productKeys = products.keys();
                            Iterator<String> barcodesKeys = barcodes.keys();

                            while(productKeys.hasNext()) {
                                String key = productKeys.next();
                                text += key + " - " + products.get(key) + "\n";
                            }

                            while(barcodesKeys.hasNext()) {
                                String key = barcodesKeys.next();
                                text += key + " - " + barcodes.get(key) + "\n";
                            }

                            final String new_text = text;
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    bluetoothText.setText(new_text);
                                }
                            });
                        } catch (IOException e) {
                            Log.d("SHOPPINGCART", e.getMessage());
                        } catch (JSONException e) {
                            Log.d("SHOPPINGCART", e.getMessage());
                        }
                    }
                }
            });

            updateThread.start();
        }
    }

    private void close(Closeable object) {
        if(object == null) return;

        try {
            object.close();
        } catch (IOException e) { }

        object = null;
    }
}
