package com.acmezon.acmezon_dash;

import android.app.Activity;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.acmezon.acmezon_dash.bluetooth.Commands;
import com.acmezon.acmezon_dash.bluetooth.Connecting.ConnectThread;
import com.acmezon.acmezon_dash.image_url.LazyImageLoadAdapter;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Iterator;

public class ShoppingCart extends Activity {
    private BufferedReader bluetoothReader;
    private OutputStream bluetoothWriter;
    private Button btnGetCart;
    private ConnectThread connection;

    ListView list;
    LazyImageLoadAdapter adapter;

    JSONObject[] products;
    Button add,sub;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shopping_cart);

        btnGetCart = (Button) findViewById(R.id.get_cart);

        assert btnGetCart != null;

        btnGetCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getLastCart();
            }
        });

        connection = ((Application) getApplication()).getConnection();

        Log.d("SHOPPINGCART", connection.getbTDevice().getAddress());

        bluetoothListen();
        //bluetoothWrite(connection);
    }

    public void getLastCart(){
        if (Looper.myLooper() == null) {
            Looper.prepare();
        }
        final ProgressDialog loadingDialog = ProgressDialog.show(this, "",
                getResources().getString(R.string.shopping_cart_loading), true);

        loadingDialog.show();

        Thread shoppingCartThread = new Thread() {
            @Override
            public void run() {
                waitForBluetoothProducts();
                loadingDialog.dismiss();
            }
        };

        shoppingCartThread.start();
        //sendCommand(Commands.GET_LAST);
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

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                list=(ListView)findViewById(R.id.products_list);
                adapter=new LazyImageLoadAdapter(ShoppingCart.this, products, ShoppingCart.this);
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

                btnGetCart.setVisibility(View.INVISIBLE);

                TextView shpTitle = (TextView) findViewById(R.id.shopping_cart_title);
                assert shpTitle != null;

                ListView productsList = (ListView) findViewById(R.id.products_list);
                assert productsList != null;

                Button btnShpNext = (Button) findViewById(R.id.btn_pay);
                assert btnShpNext != null;

                shpTitle.setVisibility(View.VISIBLE);
                productsList.setVisibility(View.VISIBLE);
                btnShpNext.setVisibility(View.VISIBLE);
            }
        });
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
                    //TODO: Completar o eliminar el else
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
                    //TODO: Completar o eliminar el else
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

    private void sendCommand(String command) {
        try {
            bluetoothWriter = connection.getbTSocket().getOutputStream();

            try {
                bluetoothWriter.write(command.getBytes());
            } catch (Exception e) {
                Log.d("SHOPPINGCART", e.getMessage());
            }
        } catch (IOException e) {
            close(bluetoothWriter);
            Log.d("SHOPPINGCART", e.getMessage());
        }
    }

    private void waitForBluetoothProducts() {
        Log.d("SHOPPINGCART", "ENTRA!!!!");
        final byte delimiter = 10; //This is the ASCII code for a newline character

        boolean stopWorker = false;
        int readBufferPosition = 0;
        byte[] readBuffer = new byte[1024];
        InputStream tmpStream = null;
        InputStreamReader tmpReader;
        try {
            tmpStream = connection.getbTSocket().getInputStream();
            tmpReader = new InputStreamReader(tmpStream);
            bluetoothReader = new BufferedReader(tmpReader);

            bluetoothWriter = connection.getbTSocket().getOutputStream();
        } catch (IOException e) {
            Log.d("SHOPPINGCART", e.getMessage());
            close(bluetoothReader);
        }


        Log.d("SHOPPINGCART", "IF!!!!");
        if(tmpStream != null) {
            try {
                bluetoothWriter.write(Commands.GET_LAST.getBytes());
            } catch (IOException e) {
                Log.d("SHOPPINGCART", e.getMessage());
            }
            while(!Thread.currentThread().isInterrupted() && !stopWorker)
            {
                try
                {
                    int bytesAvailable = tmpStream.available();
                    if(bytesAvailable > 0)
                    {
                        byte[] packetBytes = new byte[bytesAvailable];
                        tmpStream.read(packetBytes);
                        for(int i=0;i<bytesAvailable;i++)
                        {
                            byte b = packetBytes[i];
                            if(b == delimiter)
                            {
                                byte[] encodedBytes = new byte[readBufferPosition];
                                System.arraycopy(readBuffer, 0, encodedBytes, 0, encodedBytes.length);
                                final String data = new String(encodedBytes, "US-ASCII");
                                readBufferPosition = 0;

                                Log.d("SHOPPINGCART", data);
                            }
                            else
                            {
                                readBuffer[readBufferPosition++] = b;
                            }
                        }
                    }
                }
                catch (IOException ex)
                {
                    stopWorker = true;
                }
            }
            /*
            while (!Thread.currentThread().isInterrupted()) {
                Log.d("SHOPPINGCART", "WHILE!!!!");
                try {
                    Log.d("SHOPPINGCART", "Char: " + (char) bluetoothReader.read());
                    /*final String new_line = bluetoothReader.readLine();
                    Log.d("SHOPPINGCART", new_line);
                    if (new_line.isEmpty()) {
                        continue;
                    }


                    Log.d("SHOPPINGCART", new_line);
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
                    Log.d("SHOPPINGCART", new_text);
                    break;
                } catch (IOException e) {
                    Log.d("SHOPPINGCART", e.getMessage());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }*/
        } else {
            Log.d("SHOPPINGCART", "ELSE!!!!");
        }
    }

    private void bluetoothListen() {
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
            Thread updateThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    while(true) {
                        try {
                            final String new_line = bluetoothReader.readLine();
                            if (new_line.isEmpty() || !new_line.equals("get_last_cart")) {
                                continue;
                            }

                            getLastCart();
                        } catch (IOException e) {
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
