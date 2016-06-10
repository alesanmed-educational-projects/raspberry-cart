package com.acmezon.acmezon_dash;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ListView;

import com.acmezon.acmezon_dash.image_url.LazyImageLoadAdapter;

import java.io.BufferedReader;
import java.io.OutputStream;

public class ShoppingCart extends Activity {
    private BufferedReader bluetoothReader;
    private OutputStream bluetoothWriter;

    ListView list;
    LazyImageLoadAdapter adapter;

    private String[] names = {
            "Producto 1",
            "Producto 2",
            "Producto 3",
            "Producto 4",
            "Producto 5",
            "Producto 6",
            "Producto 7",
            "Producto 8",
            "Producto 9",
            "Producto 10"
    };
    private String[] image_urls = {
            "http://cdn.akamai.steamstatic.com/steam/apps/446050/header.jpg?t=1465548552",
            "http://cdn.akamai.steamstatic.com/steam/apps/268500/header.jpg?t=1463073148",
            "http://cdn.akamai.steamstatic.com/steam/apps/7760/header.jpg?t=1447351440",
            "http://cdn.akamai.steamstatic.com/steam/apps/370020/header.jpg?t=1463877514",
            "http://cdn.akamai.steamstatic.com/steam/apps/276810/header.jpg?t=1461256421",
            "http://cdn.akamai.steamstatic.com/steam/apps/341150/header.jpg?t=1455819921",
            "http://cdn.akamai.steamstatic.com/steam/apps/243470/header.jpg?t=1452887685",
            "http://cdn.akamai.steamstatic.com/steam/apps/377160/header.jpg?t=1458619195",
            "http://cdn.akamai.steamstatic.com/steam/apps/447040/header.jpg?t=1465546434",
            "http://cdn.akamai.steamstatic.com/steam/apps/271590/header.jpg?t=1465398269"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shopping_cart);

        list=(ListView)findViewById(R.id.listView);
        adapter=new LazyImageLoadAdapter(this, names, image_urls, this);
        list.setAdapter(adapter);

        //ConnectThread connection = ((Application) getApplication()).getConnection();

        //Log.d("SHOPPINGCART", connection.getbTDevice().getAddress());

        //bluetoothListen(connection);
        //bluetoothWrite(connection);
    }

    @Override
    public void onDestroy()
    {
        list.setAdapter(null);
        super.onDestroy();
    }

    /*private void bluetoothWrite(ConnectThread connection) {
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
    }*/

    /*private void bluetoothListen(ConnectThread connection) {
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
    }*/

    /*private void close(Closeable object) {
        if(object == null) return;

        try {
            object.close();
        } catch (IOException e) { }

        object = null;
    }*/
}
