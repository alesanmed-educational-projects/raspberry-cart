package com.acmezon.acmezon_dash;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.acmezon.acmezon_dash.bluetooth.BluetoothResponseHandler;
import com.acmezon.acmezon_dash.bluetooth.Commands;
import com.acmezon.acmezon_dash.bluetooth.Connecting.DeviceConnector;
import com.acmezon.acmezon_dash.image_url.LazyImageLoadAdapter;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ShoppingCart extends Activity {
    private Button btnGetCart;
    private DeviceConnector connection;
    private ProgressDialog loadingDialog;

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

        BluetoothResponseHandler mHandler = new BluetoothResponseHandler(){
            @Override
            public void onDeviceName(String deviceName) {
                super.onDeviceName(deviceName);
            }

            @Override
            public void onMessageRead(int bytes, String data) {
                data = data.trim();
                Log.d("BLUETOOTH", data.trim());
                switch (data){
                    case Commands.LAST_CART_REQUESTED:
                        getLastCart();
                        break;
                    default:
                        receiveProducts(data);
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

        connection.setHandler(mHandler);
    }

    public void getLastCart(){
        Thread getCart = new Thread(new Runnable() {
            @Override
            public void run() {
                if (Looper.myLooper() == null) {
                    Looper.prepare();
                }
                loadingDialog = ProgressDialog.show(ShoppingCart.this, "",
                        getResources().getString(R.string.shopping_cart_loading), true);

                Log.d("SHOPPINGCART", "Loading dialog created");
                loadingDialog.show();
            }
        });
        getCart.start();
        connection.write(Commands.GET_LAST.getBytes());
    }

    @Override
    public void onDestroy()
    {
        if(list != null)
            list.setAdapter(null);

        super.onDestroy();
    }

    public JSONObject[] getProducts(List<JSONObject> barcodes, List<JSONObject> names) {
        JSONObject[] res = new JSONObject[barcodes.size() + names.size()];
        int i = 0;
        for (JSONObject p : barcodes) {
            URL url = null;
            try {
                url = new URL(getString(R.string.domain).concat(String.format("/api/product/barcode/%s", p.getString("barcode"))));
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
                url = new URL(getString(R.string.domain).concat(String.format("/api/product/text/search/%s", p.getString("name"))));
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

    private void receiveProducts(String stringProducts) {
        JSONObject[] completedProducts = null;
        try {
            JSONObject productsJSON = new JSONObject(stringProducts);

            JSONObject names = null;
            try {
                names = ((JSONObject) productsJSON.get("products"));
            } catch (JSONException e) {
                Log.d("SHOPPINGCART", e.getMessage());
            }

            JSONObject barcodes = null;
            try {
                barcodes = ((JSONObject) productsJSON.get("barcodes"));
            } catch (JSONException e) {
                Log.d("SHOPPINGCART", e.getMessage());
            }

            List<JSONObject> namesArray = new ArrayList<>();
            if (names != null) {
                Iterator<String> namesKeys = names.keys();
                while (namesKeys.hasNext()) {
                    String key = namesKeys.next();
                    JSONObject tmp = new JSONObject();
                    tmp.put("name", key);
                    tmp.put("quantity", names.get(key));
                    namesArray.add(tmp);
                }
            }

            List<JSONObject> barcodesArray = new ArrayList<>();
            if (barcodes != null) {
                Iterator<String> barcodesKeys = barcodes.keys();
                while (barcodesKeys.hasNext()) {
                    String key = barcodesKeys.next();
                    JSONObject tmp = new JSONObject();
                    tmp.put("barcode", key);
                    tmp.put("quantity", barcodes.get(key));
                    barcodesArray.add(tmp);
                }
            }

            completedProducts = getProducts(barcodesArray, namesArray);
        }catch (JSONException e) {
            Log.d("SHOPPINGCART", e.getMessage());
        }

        final JSONObject[] finalProducts = completedProducts;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                assert finalProducts != null;

                list=(ListView)findViewById(R.id.products_list);
                assert list != null;
                adapter=new LazyImageLoadAdapter(ShoppingCart.this, finalProducts, ShoppingCart.this);
                list.setItemsCanFocus(false);
                list.setLongClickable(true);
                list.setAdapter(adapter);

                Button btn_pay = (Button)findViewById(R.id.btn_pay);
                assert btn_pay != null;
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

                loadingDialog.dismiss();

                TextView shpTitle = (TextView) findViewById(R.id.shopping_cart_title);
                assert shpTitle != null;

                shpTitle.setVisibility(View.VISIBLE);
                list.setVisibility(View.VISIBLE);
                btn_pay.setVisibility(View.VISIBLE);
            }
        });
    }

    private void close(Closeable object) {
        if(object == null) return;

        try {
            object.close();
        } catch (IOException e) { }

        object = null;
    }
}
