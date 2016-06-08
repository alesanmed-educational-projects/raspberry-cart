package com.acmezon.acmezon_dash;

import android.app.ListActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.acmezon.acmezon_dash.bluetooth.ConnectThread;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.Iterator;

public class ShoppingCart extends ListActivity {
    private BufferedReader bluetoothReader;
    private OutputStream bluetoothWriter;

    private String languages[]=new String[]{"Java","PHP","Python","JavaScript","Ruby","C",
            "Go","Perl","Pascal","Java","PHP","Python","JavaScript","Ruby","C","Java","PHP",
            "Python","JavaScript","Ruby","C"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shopping_cart);

        this.setListAdapter(new ArrayAdapter<String>(
                this, R.layout.product_row,
                R.id.product_name,languages));

        ConnectThread connection = ((Application) getApplication()).getConnection();

        Log.d("SHOPPINGCART", connection.getbTDevice().getAddress());

        bluetoothListen(connection);
        bluetoothWrite(connection);
    }

    private void bluetoothWrite(ConnectThread connection) {
        try {
            bluetoothWriter = connection.getbTSocket().getOutputStream();

            Thread writeThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    String text = "test\n";
                    try {
                        while (true) {
                            bluetoothWriter.write(text.getBytes());
                            Thread.sleep(1000L, 0);
                        }
                    } catch (IOException e) {
                        Log.d("SHOPPINGCART", e.getMessage());
                    } catch (InterruptedException e) {
                        Log.d("SHOPPINGCART", e.getMessage());
                    }
                }
            });

            writeThread.start();
        } catch (IOException e) {
            close(bluetoothWriter);
            Log.d("SHOPPINGCART", e.getMessage());
        }
    }

    private void bluetoothListen(ConnectThread connection) {
        InputStream tmpStream;
        InputStreamReader tmpReader;
        try {
            tmpStream = connection.getbTSocket().getInputStream();
            tmpReader = new InputStreamReader(tmpStream);
            bluetoothReader = new BufferedReader(tmpReader);

            bluetoothWriter = connection.getbTSocket().getOutputStream();
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

                            JSONObject products = null;
                            try{
                                products = ((JSONObject) productsJSON.get("products"));
                            } catch (JSONException e) {
                                Log.d("SHOPPINGCART", e.getMessage());
                            }

                            JSONObject barcodes = null;
                            try{
                                barcodes = ((JSONObject) productsJSON.get("barcodes"));
                            }catch (JSONException e){
                                Log.d("SHOPPINGCART", e.getMessage());
                            }

                            if(products != null) {
                                Iterator<String> productKeys = products.keys();
                                while (productKeys.hasNext()) {
                                    String key = productKeys.next();
                                    text += key + " - " + products.get(key) + "\n";
                                }
                            }

                            if(barcodes != null) {
                                Iterator<String> barcodesKeys = barcodes.keys();
                                while (barcodesKeys.hasNext()) {
                                    String key = barcodesKeys.next();
                                    text += key + " - " + barcodes.get(key) + "\n";
                                }
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
                            e.printStackTrace();
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
