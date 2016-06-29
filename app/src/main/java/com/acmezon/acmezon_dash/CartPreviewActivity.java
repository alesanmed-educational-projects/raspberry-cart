package com.acmezon.acmezon_dash;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.acmezon.acmezon_dash.bluetooth.BluetoothResponseHandler;
import com.acmezon.acmezon_dash.bluetooth.Commands;
import com.acmezon.acmezon_dash.bluetooth.Connecting.DeviceConnector;
import com.acmezon.acmezon_dash.bluetooth.security.Sha;
import com.acmezon.acmezon_dash.image_url.LazyImageLoadAdapter;
import com.acmezon.acmezon_dash.products.ProductUtils;

import org.json.JSONObject;

import java.io.FileOutputStream;
import java.security.NoSuchAlgorithmException;

public class CartPreviewActivity extends AppCompatActivity {
    private DeviceConnector connection;
    private ProgressDialog loadingDialog;
    private final String FILENAME = "shopping_cart";
    private String stringProducts;

    ListView list;
    LazyImageLoadAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart_preview);

        Button btnRestoreCart = (Button) findViewById(R.id.btn_restore);

        assert btnRestoreCart != null;

        btnRestoreCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                restore();
            }
        });

        connection = ((Application) getApplication()).getConnection();

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                loadingDialog = ProgressDialog.show(CartPreviewActivity.this, "",
                        getResources().getString(R.string.shopping_cart_loading), true);
            }
        });

        BluetoothResponseHandler mHandler = new BluetoothResponseHandler() {
            @Override
            public void onDeviceName(String deviceName) {
                super.onDeviceName(deviceName);
            }

            @Override
            public void onMessageRead(int bytes, String data) {
                data = data.trim();
                Log.d("BLUETOOTH", data.trim());
                switch (data.trim()){
                    case Commands.CHECKSUM_VALID:
                        receiveProducts(stringProducts);
                    case Commands.CHECKSUM_INVALID:
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                loadingDialog.dismiss();
                                Toast.makeText(getApplicationContext(),
                                        getString(R.string.error_receive_cart),
                                        Toast.LENGTH_LONG).show();
                            }
                        });
                        finish();
                    default:
                        checkProducts(data);
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
        connection.write("[get ]".getBytes());
    }

    @Override
    public void onDestroy() {
        if(list != null)
            list.setAdapter(null);

        super.onDestroy();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                loadingDialog.dismiss();
            }
        });
    }

    private void checkProducts(String products) {
        String checksum = null;
        stringProducts = products;
        try {
            checksum = Sha.hash256(products);
        } catch (NoSuchAlgorithmException e) {
            Log.d("SHOPPINGCART", e.getMessage());
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    loadingDialog.dismiss();
                    Toast.makeText(getApplicationContext(),
                            getString(R.string.error_receive_cart),
                            Toast.LENGTH_LONG).show();
                }
            });
        }

        String command = String.format("[checksum %s]", checksum);
        connection.write(command.getBytes());
    }

    private void receiveProducts(final String productsReceived) {
        final JSONObject[] finalProducts = ProductUtils.receiveProducts(this, productsReceived);
        this.stringProducts = null;

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (finalProducts != null && finalProducts.length > 0) {
                    list = (ListView) findViewById(R.id.products_list);
                    assert list != null;
                    adapter = new LazyImageLoadAdapter(CartPreviewActivity.this,
                            finalProducts, CartPreviewActivity.this);
                    list.setItemsCanFocus(false);
                    list.setLongClickable(true);
                    list.setAdapter(adapter);

                    if (loadingDialog != null)
                        loadingDialog.dismiss();

                    FileOutputStream outputStream;

                    try {
                        outputStream = openFileOutput(FILENAME, Context.MODE_PRIVATE);
                        outputStream.write(productsReceived.getBytes());
                        outputStream.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    Toast.makeText(getApplicationContext(),
                            getString(R.string.no_products),
                            Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    private void restore() {

    }
}
