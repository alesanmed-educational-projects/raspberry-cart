package com.acmezon.acmezon_dash;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.acmezon.acmezon_dash.bluetooth.BluetoothResponseHandler;
import com.acmezon.acmezon_dash.bluetooth.Commands;
import com.acmezon.acmezon_dash.bluetooth.Connecting.DeviceConnector;
import com.acmezon.acmezon_dash.bluetooth.security.Sha;
import com.acmezon.acmezon_dash.image_url.LazyImageLoadAdapter;
import com.acmezon.acmezon_dash.products.ProductUtils;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.security.NoSuchAlgorithmException;

public class ShoppingCart extends Activity {
    private Button btnGetCart;
    private Button btnDeleteCart;
    private Button btn_pay;
    private DeviceConnector connection;
    private ProgressDialog loadingDialog;
    private boolean cartReceived = false;
    private final String FILENAME = "shopping_cart";
    private String stringProducts;

    ListView list;
    LazyImageLoadAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shopping_cart);

        btnDeleteCart = (Button) findViewById(R.id.btn_del);

        assert btnDeleteCart != null;

        btnDeleteCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteCart();
            }
        });

        btn_pay = (Button) findViewById(R.id.btn_pay);
        assert btn_pay != null;
        btn_pay.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO: Redirigir
                Toast.makeText(getApplicationContext(),
                        "TODO: Redirección",
                        Toast.LENGTH_LONG).show();
            }
        });

        File shoppingCart = getBaseContext().getFileStreamPath(FILENAME);
        Log.d("SHOPPINGCART", "Exists: " + shoppingCart.exists());
        if (!cartReceived && !shoppingCart.exists()) {
            btnGetCart = (Button) findViewById(R.id.get_cart);

            assert btnGetCart != null;

            btnGetCart.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    getLastCart();
                }
            });

            connection = ((Application) getApplication()).getConnection();

            BluetoothResponseHandler mHandler = new BluetoothResponseHandler() {
                @Override
                public void onDeviceName(String deviceName) {
                    super.onDeviceName(deviceName);
                }

                @Override
                public void onMessageRead(int bytes, String data) {
                    data = data.trim();
                    Log.d("BLUETOOTH", data.trim());
                    switch (data) {
                        case Commands.LAST_CART_REQUESTED:
                            getLastCart();
                            break;
                        case Commands.CHECKSUM_VALID:
                            receiveProducts(stringProducts);
                            break;
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
                            break;
                        default:
                            checkProducts(data);
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
        } else {
            list = (ListView) findViewById(R.id.products_list);
            assert list != null;

            btnGetCart = (Button) findViewById(R.id.get_cart);
            assert btnGetCart != null;

            if (!cartReceived) {
                FileInputStream shoppingCartFile;
                try {
                    shoppingCartFile = this.getApplicationContext().openFileInput(FILENAME);
                    InputStreamReader isr = new InputStreamReader(shoppingCartFile);
                    BufferedReader bufferedReader = new BufferedReader(isr);
                    final StringBuilder sb = new StringBuilder();
                    String line;
                    while ((line = bufferedReader.readLine()) != null) {
                        sb.append(line);
                    }
                    Thread receiveProducts = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            receiveProducts(sb.toString());
                        }
                    });

                    receiveProducts.start();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                list.setVisibility(View.VISIBLE);

                btn_pay.setVisibility(View.VISIBLE);

                btnDeleteCart.setVisibility(View.VISIBLE);

                btnGetCart.setVisibility(View.INVISIBLE);

                TextView shpTitle = (TextView) findViewById(R.id.shopping_cart_title);
                assert shpTitle != null;
                shpTitle.setVisibility(View.VISIBLE);
            }
        }
    }

    private void deleteCart() {
        new AlertDialog.Builder(this)
            .setIcon(R.drawable.warning)
            .setTitle(R.string.delete_cart_dialog_title)
            .setMessage(R.string.delete_cart_dialog_text)
            .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener()
            {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    File dir = getFilesDir();
                    File shoppingCartFile = new File(dir, FILENAME);
                    boolean deleted = shoppingCartFile.delete();
                    if (deleted) {
                        if(((Application) getApplication()).getConnection() != null) {
                            ShoppingCart.this.recreate();
                        } else {
                            finish();
                        }
                    } else {
                        Toast.makeText(getApplicationContext(),
                                R.string.delete_cart_error,
                                Toast.LENGTH_LONG).show();
                    }
                }

            })
            .setNegativeButton(R.string.no, null)
            .show();
    }

    public void getLastCart(){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                loadingDialog = ProgressDialog.show(ShoppingCart.this, "",
                        getResources().getString(R.string.shopping_cart_loading), true);

                Log.d("SHOPPINGCART", "Loading dialog created");
                loadingDialog.show();
            }
        });
        connection.write(Commands.GET_LAST.getBytes());
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
                    adapter = new LazyImageLoadAdapter(ShoppingCart.this, finalProducts, ShoppingCart.this);
                    list.setItemsCanFocus(false);
                    list.setLongClickable(true);
                    list.setAdapter(adapter);

                    btnGetCart.setVisibility(View.INVISIBLE);

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

                    TextView shpTitle = (TextView) findViewById(R.id.shopping_cart_title);
                    assert shpTitle != null;

                    shpTitle.setVisibility(View.VISIBLE);
                    list.setVisibility(View.VISIBLE);
                    btn_pay.setVisibility(View.VISIBLE);
                    btnDeleteCart.setVisibility(View.VISIBLE);

                    cartReceived = true;
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

}
