package com.acmezon.acmezon_dash;

import android.app.Activity;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.acmezon.acmezon_dash.image_url.LazyImageLoadAdapter;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class ShoppingCart extends Activity {
    private BufferedReader bluetoothReader;
    private OutputStream bluetoothWriter;

    ListView list;
    LazyImageLoadAdapter adapter;

    JSONObject[] products;
    Button add,sub;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shopping_cart);

        JSONObject p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11;
        try {
            p1 = new JSONObject()
                    .put("name", "Producto 1")
                    .put("image_url", "http://cdn.akamai.steamstatic.com/steam/apps/377160/header.jpg?t=1458619195")
                    .put("quantity", 1);
            p2 = new JSONObject()
                    .put("name", "Producto 2")
                    .put("image_url", "http://cdn.akamai.steamstatic.com/steam/apps/377160/header.jpg?t=1458619195")
                    .put("quantity", 1);
            p3 = new JSONObject()
                    .put("name", "Producto 3")
                    .put("image_url", "http://cdn.akamai.steamstatic.com/steam/apps/377160/header.jpg?t=1458619195")
                    .put("quantity", 1);
            p4 = new JSONObject()
                    .put("name", "Producto 4")
                    .put("image_url", "http://cdn.akamai.steamstatic.com/steam/apps/377160/header.jpg?t=1458619195")
                    .put("quantity", 1);
            p5 = new JSONObject()
                    .put("name", "Producto 5")
                    .put("image_url", "http://cdn.akamai.steamstatic.com/steam/apps/377160/header.jpg?t=1458619195")
                    .put("quantity", 1);
            p6 = new JSONObject()
                    .put("name", "Producto 6")
                    .put("image_url", "http://cdn.akamai.steamstatic.com/steam/apps/377160/header.jpg?t=1458619195")
                    .put("quantity", 1);
            p7 = new JSONObject()
                    .put("name", "Producto 7")
                    .put("image_url", "http://cdn.akamai.steamstatic.com/steam/apps/377160/header.jpg?t=1458619195")
                    .put("quantity", 1);
            p8 = new JSONObject()
                    .put("name", "Producto 8")
                    .put("image_url", "http://cdn.akamai.steamstatic.com/steam/apps/377160/header.jpg?t=1458619195")
                    .put("quantity", 1);
            p9 = new JSONObject()
                    .put("name", "Producto 9")
                    .put("image_url", "http://cdn.akamai.steamstatic.com/steam/apps/377160/header.jpg?t=1458619195")
                    .put("quantity", 1);
            p10 = new JSONObject()
                    .put("name", "Producto 10")
                    .put("image_url", "http://cdn.akamai.steamstatic.com/steam/apps/377160/header.jpg?t=1458619195")
                    .put("quantity", 1);
            p11 = new JSONObject()
                    .put("name", "Producto 11")
                    .put("image_url", "http://cdn.akamai.steamstatic.com/steam/apps/377160/header.jpg?t=1458619195")
                    .put("quantity", 1);
        } catch (JSONException e) {
            p1 = p2 = p3 = p4 = p5 = p6 = p7 = p8 = p9 = p10 = p11 = new JSONObject();
        }

        products = new JSONObject[]{p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11};

        list=(ListView)findViewById(R.id.listView);
        adapter=new LazyImageLoadAdapter(this, products, this);
        list.setItemsCanFocus(false);
        list.setLongClickable(true);
        list.setAdapter(adapter);

        Button btn_pay = (Button)findViewById(R.id.btn_pay);
        btn_pay.setOnClickListener( new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO: Redirigir
                Toast.makeText(getApplicationContext(),
                        "TODO: Redirección",
                        Toast.LENGTH_LONG).show();
            }
        });

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

    public JSONObject[] getProducts(JSONObject[] barcodes, JSONObject[] names) {
        JSONObject[] res = new JSONObject[barcodes.length + names.length];
        int i = 0;
        for (JSONObject p : barcodes) {
            URL url = null;
            try {
                url = new URL(String.format("http://DOMINIO/api/product/barcode/%s", p.getString("barcode")));
            } catch (MalformedURLException | JSONException e) {
                e.printStackTrace();
            }

            HttpURLConnection connection = null;
            try {
                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                // Expirar a los 10 segundos si la conexión no se establece
                connection.setConnectTimeout(10000);
                // Esperar solo 15 segundos para que finalice la lectura
                connection.setReadTimeout(15000);
                connection.connect();

                int response = connection.getResponseCode();
                BufferedReader br = null;
                if (response >= 200 && response <=399) {
                    br = new BufferedReader(new InputStreamReader((connection.getInputStream())));
                    StringBuilder responseStrBuilder = new StringBuilder();
                    String inputStr;
                    while ((inputStr = br.readLine()) != null)
                        responseStrBuilder.append(inputStr);
                    JSONObject obj = new JSONObject(responseStrBuilder.toString());
                    obj.put("quantity", p.getInt("quantity"));
                    res[i] = obj;
                    i++;
                } else {

                }
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            } catch (JSONException e) {
                e.printStackTrace();
            }


        }

        for (JSONObject p: names) {
            URL url = null;
            try {
                url = new URL(String.format("http://DOMINIO//api/product/text/search/%s", p.getString("name")));
            } catch (MalformedURLException | JSONException e) {
                e.printStackTrace();
            }

            HttpURLConnection connection = null;
            try {
                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                // Expirar a los 10 segundos si la conexión no se establece
                connection.setConnectTimeout(10000);
                // Esperar solo 15 segundos para que finalice la lectura
                connection.setReadTimeout(15000);
                connection.connect();

                int response = connection.getResponseCode();
                BufferedReader br = null;
                if (response >= 200 && response <=399) {
                    br = new BufferedReader(new InputStreamReader((connection.getInputStream())));
                    StringBuilder responseStrBuilder = new StringBuilder();
                    String inputStr;
                    while ((inputStr = br.readLine()) != null)
                        responseStrBuilder.append(inputStr);
                    JSONObject obj = new JSONObject(responseStrBuilder.toString());
                    obj.put("quantity", p.getInt("quantity"));
                    res[i] = obj;
                    i++;
                } else {

                }
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return res;
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
